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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class NewQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private Toolbar toolbar;
    private ImageButton avatar, settings;
    private ImageView image1View;
    private Button addQuizButton, clearFormButton, chooseImage1;
    private EditText namequiz, categoryquiz, questionquiz1, okodp1, otherodp1, questionquiz2, okodp2, otherodp2,
            questionquiz3, okodp3, otherodp3, questionquiz4, okodp4, otherodp4, questionquiz5, okodp5, otherodp5;
    private Uri uriFilePath;
    private String image1URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_new_quiz);

        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);

        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_gear:
                intent = new Intent(NewQuizActivity.this, SettingsActivity.class);
                NewQuizActivity.this.startActivity(intent);
                break;

            case R.id.avatar:
                intent = new Intent(NewQuizActivity.this, AccountSettingsActivity.class);
                NewQuizActivity.this.startActivity(intent);
                break;
            case R.id.choose_image1:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    DataHolder.requestStoragePermission(this);
                } else {
                    chooseImage();
                }
                break;
            case R.id.clear_form:
                clearForm();
                Toast.makeText(getApplicationContext(),"Wyczyszczono formularz!", Toast.LENGTH_LONG).show();
                break;
            case R.id.add_quiz:
                uploadImage();
                addQuiz();
                Toast.makeText(getApplicationContext(),"Pomyslnie dodano Quiz!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), DataHolder.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DataHolder.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFilePath);
                image1View.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (uriFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference refStoragePath = DataHolder.getInstance().storageReference
                    .child("images").child(uriFilePath.getLastPathSegment());

            image1URL = refStoragePath.toString();
            UploadTask uploadTask = refStoragePath.putFile(uriFilePath);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(NewQuizActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewQuizActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void addQuiz() {
        DatabaseReference pathName = DataHolder.firebaseDatabase.child("quizy").child(categoryquiz.getText().toString()).child(namequiz.getText().toString());

        // image1URL zawiera sciezke URL obrazka (Storage > images > obrazek)
        pathName.child(questionquiz1.getText().toString()).child("image1").setValue(image1URL);
        pathName.child(questionquiz1.getText().toString()).child("okodp").setValue(okodp1.getText().toString());
        pathName.child(questionquiz1.getText().toString()).child("otherodp").setValue(otherodp1.getText().toString());

        pathName.child(questionquiz2.getText().toString()).child("okodp").setValue(okodp2.getText().toString());
        pathName.child(questionquiz2.getText().toString()).child("otherodp").setValue(otherodp2.getText().toString());

        pathName.child(questionquiz3.getText().toString()).child("okodp").setValue(okodp3.getText().toString());
        pathName.child(questionquiz3.getText().toString()).child("otherodp").setValue(otherodp3.getText().toString());

        pathName.child(questionquiz4.getText().toString()).child("okodp").setValue(okodp4.getText().toString());
        pathName.child(questionquiz4.getText().toString()).child("otherodp").setValue(otherodp4.getText().toString());

        pathName.child(questionquiz5.getText().toString()).child("okodp").setValue(okodp5.getText().toString());
        pathName.child(questionquiz5.getText().toString()).child("otherodp").setValue(otherodp5.getText().toString());
    }

    private void clearForm() {
        namequiz.getText().clear();
        categoryquiz.getText().clear();

        questionquiz1.getText().clear();
        okodp1.getText().clear();
        otherodp1.getText().clear();

        questionquiz2.getText().clear();
        okodp2.getText().clear();
        otherodp2.getText().clear();
        questionquiz3.getText().clear();
        okodp3.getText().clear();
        otherodp3.getText().clear();
        questionquiz4.getText().clear();
        okodp4.getText().clear();
        otherodp4.getText().clear();
        questionquiz5.getText().clear();
        okodp5.getText().clear();
        otherodp5.getText().clear();
    }

    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.user_bar);
        avatar = (ImageButton) findViewById(R.id.avatar);
        settings = (ImageButton) findViewById(R.id.settings_gear);
        addQuizButton = (Button) findViewById(R.id.add_quiz);
        clearFormButton = (Button) findViewById(R.id.clear_form);
        chooseImage1 = (Button) findViewById(R.id.choose_image1);
        image1View = (ImageView) findViewById(R.id.image1);
        avatar.setImageBitmap(DataHolder.getInstance().avatarBitmap);
        avatar.setOnClickListener(this);
        settings.setOnClickListener(this);
        addQuizButton.setOnClickListener(this);
        clearFormButton.setOnClickListener(this);
        chooseImage1.setOnClickListener(this);

        namequiz = (EditText) findViewById(R.id.nameQuiz);
        categoryquiz = (EditText) findViewById(R.id.categoryQuiz);
        questionquiz1 = (EditText) findViewById(R.id.questionQuiz1);
        okodp1 = (EditText) findViewById(R.id.okodp1);
        otherodp1 = (EditText) findViewById(R.id.otherodp1);
        questionquiz2 = (EditText) findViewById(R.id.questionQuiz2);
        okodp2 = (EditText) findViewById(R.id.okodp2);
        otherodp2 = (EditText) findViewById(R.id.otherodp2);
        questionquiz3 = (EditText) findViewById(R.id.questionQuiz3);
        okodp3 = (EditText) findViewById(R.id.okodp3);
        otherodp3 = (EditText) findViewById(R.id.otherodp3);
        questionquiz4 = (EditText) findViewById(R.id.questionQuiz4);
        okodp4 = (EditText) findViewById(R.id.okodp4);
        otherodp4 = (EditText) findViewById(R.id.otherodp4);
        questionquiz5 = (EditText) findViewById(R.id.questionQuiz5);
        okodp5 = (EditText) findViewById(R.id.okodp5);
        otherodp5 = (EditText) findViewById(R.id.otherodp5);
    }


}
