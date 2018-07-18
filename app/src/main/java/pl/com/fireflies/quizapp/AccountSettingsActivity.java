package pl.com.fireflies.quizapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText newEmail;
    private ImageView avatar_image;
    private ProgressDialog progressDialog;
    private Uri uriFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_account_settings);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.change_email:
                String stringNewEmail = String.valueOf(newEmail.getText());
                DataHolder.firebaseUser.updateEmail(stringNewEmail)
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
                DataHolder.firebaseAuth.signOut();
                DataHolder.firebaseUser = null;
                finish();
                break;

            case R.id.change_avatar:
                if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) DataHolder.requestStoragePermission(this);
                else chooseImage();
                break;
        }
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), DataHolder.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == DataHolder.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Pozwolenie przyznane.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pozwolenie nie zostało przyznane.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DataHolder.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFilePath = data.getData();
            try {
                // ustawianie awataru lokalnie
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFilePath);
                avatar_image.setImageBitmap(bitmap);
                // wysylanie awataru do storage
                uploadImage();
                DataHolder.setAvatarImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (uriFilePath != null) {
            if(DataHolder.getInstance().dark_theme) progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
            else progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // sciezka do folderu uzytkownika :
            final StorageReference refStoragePath = DataHolder.storageReference.child("user")
                    .child(DataHolder.firebaseUser.getUid()).child("avatarImage.jpg");
            UploadTask uploadTask = refStoragePath.putFile(uriFilePath);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountSettingsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountSettingsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    protected void initViews() {
        ArrayList<CardView> cards = new ArrayList<CardView>()
        {{
           add((CardView) findViewById(R.id.card1));
           add((CardView) findViewById(R.id.card2));
           add((CardView) findViewById(R.id.card3));
           add((CardView) findViewById(R.id.card4));
        }};
        for (CardView card : cards) card.setOnClickListener(this);
        ArrayList<Button> buttons = new ArrayList<Button>()
        {{
           add((Button) findViewById(R.id.change_email));
           add((Button) findViewById(R.id.change_password));
           add((Button) findViewById(R.id.logout_button));
           add((Button) findViewById(R.id.change_avatar));
        }};
        for (Button button : buttons) button.setOnClickListener(this);
        avatar_image = (ImageView) findViewById(R.id.avatar);
        avatar_image.setImageBitmap(DataHolder.getInstance().avatarBitmap);
        TextView loginText = (TextView) findViewById(R.id.login);
        TextView emailText = (TextView) findViewById(R.id.email);
        newEmail = (EditText) findViewById(R.id.new_email);
        progressDialog = new ProgressDialog(this);
        if(DataHolder.getInstance().dark_theme)
        {
            cards.get(0).setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkRed));
            cards.get(1).setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark1));
            cards.get(2).setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark2));
            cards.get(3).setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkYellow));
        }
        if (DataHolder.firebaseUser != null)
        {
            String name = DataHolder.firebaseUser.getDisplayName();
            String email = DataHolder.firebaseUser.getEmail();
            // Check if user's email is verified
            loginText.setText(name);
            emailText.setText(getString(R.string.email_verified, email, DataHolder.firebaseUser.isEmailVerified()));
        }
    }
}
