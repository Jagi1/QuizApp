package pl.com.fireflies.quizapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView avatar_image;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private boolean friend_found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        AlertDialog.Builder builder;
        View view;
        switch (v.getId())
        {
            case R.id.change_email:
                EditText text = findViewById(R.id.email_edit);
                String stringNewEmail = String.valueOf(text.getText());
                DataHolder.firebaseUser
                        .updateEmail(stringNewEmail)
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

            case R.id.update_username:
                if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(AccountSettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                else builder = new AlertDialog.Builder(AccountSettingsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                view = getLayoutInflater().inflate(R.layout.dialog_update_username,null);
                final EditText name = view.findViewById(R.id.name);
                Button update = view.findViewById(R.id.update);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                        DataHolder.firebaseDatabase.child("users").child(DataHolder.firebaseUser.getUid()).child("name").setValue(name.getText().toString());
                        DataHolder.firebaseUser.updateProfile(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AccountSettingsActivity.this,"Displayname have been updated",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AccountSettingsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.add_friend:
                if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(AccountSettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                else builder = new AlertDialog.Builder(AccountSettingsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                view = getLayoutInflater().inflate(R.layout.dialog_friend_find,null);
                final TextInputEditText name2 = view.findViewById(R.id.name_t);
                final Button button = view.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataHolder.firebaseDatabase.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("name").getValue() != null)
                                    {
                                        if (snapshot.child("name").getValue().toString().equals(name2.getText().toString()))
                                        {
                                            DataHolder.firebaseDatabase.child("users").child(DataHolder.firebaseUser.getUid()).child("friendList")
                                                    .child(snapshot.getKey()).setValue(snapshot.child("name").getValue());
                                            Toast.makeText(AccountSettingsActivity.this,snapshot.child("name").getValue()+" have been added into your friendlist",Toast.LENGTH_SHORT).show();
                                            friend_found = true;
                                            dialog.dismiss();
                                            return;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(AccountSettingsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setView(view);
                dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (friend_found) Toast.makeText(AccountSettingsActivity.this,"Friend have been added...", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(AccountSettingsActivity.this, "Sorry, we couldn't find user with this name...", Toast.LENGTH_SHORT).show();
                        friend_found = false;
                    }
                });
                break;
        }
    }

    private void chooseImage()
    {
        startActivityForResult(Intent.createChooser(new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT), "Wybierz zdjęcie"), DataHolder.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == DataHolder.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(this, "Pozwolenie przyznane.", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Pozwolenie nie zostało przyznane.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DataHolder.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            try
            {
                // ustawianie awataru lokalnie
                avatar_image.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                // wysylanie awataru do storage
                uploadImage(data.getData());
//                DataHolder.setAvatarImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri uriFilePath)
    {
        if (uriFilePath != null)
        {
            if(DataHolder.getInstance().dark_theme) progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
            else progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // sciezka do folderu uzytkownika :
            DataHolder.storageReference
                    .child("user")
                    .child(DataHolder.firebaseUser.getUid())
                    .child("avatarImage.jpg")
                    .putFile(uriFilePath)
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
                            progressDialog.setMessage(getString(R.string.upload_info,(int)progress));
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
        avatar_image = findViewById(R.id.avatar);
        avatar_image.setImageBitmap(DataHolder.getInstance().avatarBitmap);
        TextView loginText = findViewById(R.id.username);
        progressDialog = new ProgressDialog(this);
        if(DataHolder.getInstance().dark_theme)
        {
            cards.get(0).setCardBackgroundColor(getColor(R.color.colorMaterialDarkRed));
            cards.get(1).setCardBackgroundColor(getColor(R.color.colorMaterialDark1));
            cards.get(2).setCardBackgroundColor(getColor(R.color.colorMaterialDark2));
            cards.get(3).setCardBackgroundColor(getColor(R.color.colorMaterialDarkYellow));
        }
        if (DataHolder.firebaseUser != null)
        {
            if (DataHolder.firebaseUser.getDisplayName() == null)
                loginText.setText(R.string.username_info);
            else
                loginText.setText(DataHolder.firebaseUser.getDisplayName());
        }
        Button update_username = findViewById(R.id.update_username);
        update_username.setOnClickListener(this);
        Button add_friend = findViewById(R.id.add_friend);
        add_friend.setOnClickListener(this);
    }
}
