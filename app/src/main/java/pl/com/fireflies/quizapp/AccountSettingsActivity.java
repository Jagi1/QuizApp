package pl.com.fireflies.quizapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView loginText, emailText;
    private EditText newEmail;
    private Button changePassword, changeEmail, logout_button, changeAvatar;
    private ImageView avatar_image;
    private ProgressDialog progressDialog;
    public static final int PICK_IMAGE = 1, STORAGE_PERMISSION_CODE = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        initViews();

        if (DataHolder.getInstance().firebaseUser != null) {
            String name = DataHolder.getInstance().firebaseUser.getDisplayName();
            String email = DataHolder.getInstance().firebaseUser.getEmail();

            // Check if user's email is verified
            boolean emailVerified = DataHolder.getInstance().firebaseUser.isEmailVerified();

            loginText.setText(name);
            emailText.setText(email + " " + emailVerified);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_email:
                String stringNewEmail = String.valueOf(newEmail.getText());

                DataHolder.getInstance().firebaseUser.updateEmail(stringNewEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountSettingsActivity.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.logout_button:
                DataHolder.getInstance().firebaseAuth.signOut();
                DataHolder.getInstance().firebaseUser = null;
                finish();
                break;
            case R.id.change_avatar:
                if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    requestStoragePermission();
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Wybierz zdjęcie"),PICK_IMAGE);
                break;
        }
    }

    private void requestStoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Wymagane pozwolenie")
                    .setMessage("Pozwolenie jest potrzebne aby ustawić własny avatar.")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Pozwolenie przyznane.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Pozwolenie nie zostało przyznane.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE)
        {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            final Uri uri = data.getData();
            final StorageReference filepath = DataHolder.getInstance().storageReference.child("user")
                    .child(DataHolder.getInstance().firebaseUser.getUid())
                    .child(uri.getLastPathSegment());
            UploadTask uploadTask = filepath.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            /**
                             * TODO:
                             * Zmiana avatara dziala ale przy zmianie aktywnosci avatar wraca do normalnego.
                             * Trzeba zapisac jakos pobrany avatar z firebase storage na dysk i ustawic to jako
                             * avatar.
                             * */
                            Picasso.get()
                                    .load(uri)
                                    .resize(64,64)
                                    .centerCrop()
                                    .into(avatar_image);
                        }
                    });
                    Toast.makeText(AccountSettingsActivity.this,"Upload done...",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AccountSettingsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    protected void initViews() {
        changeEmail = (Button) findViewById(R.id.change_email);
        changePassword = (Button) findViewById(R.id.change_password);
        logout_button = (Button) findViewById(R.id.logout_button);
        changeAvatar = (Button) findViewById(R.id.change_avatar);
        avatar_image = (ImageView) findViewById(R.id.avatar);
        changeEmail.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        logout_button.setOnClickListener(this);
        changeAvatar.setOnClickListener(this);
        loginText = (TextView) findViewById(R.id.login);
        emailText = (TextView) findViewById(R.id.email);
        newEmail = (EditText) findViewById(R.id.new_email);
        progressDialog = new ProgressDialog(this);
    }
}
