package pl.com.fireflies.quizapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class NewQuizActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView image1View;
    private Button addQuizButton, clearFormButton, chooseImage1;
    private ArrayList<String> textsQuestions;
    private ArrayList<ArrayList<String>> textsAnswers;
    private Uri uriFilePath;
    private String image1URL;
    private ConstraintLayout layout;
    private Spinner dialogSpinner_01, dialogSpinner_02;
    private EditText dialogEdit_01;
    private Button dialogButton_01;
    private ArrayList<String> dialogArray_01, dialogArray_02;
    private String selectedCategory;
    private int amountOfQuestions;
    private AlertDialog alertDialog;
    private String quizName;
    private AlertDialog.Builder builder;
    private Boolean isSelected_01 = false, isSelected_02 = false;
    private boolean canFinish = false;
    private boolean clicked;
    private int it;
    private Button next, prev;
    private TextView qID;
    private TextInputEditText question_input;
    private ArrayList<TextInputEditText> ans;
    private int iter = 0;
    private ArrayList<Boolean>image_changed;
    private ArrayList<Uri>images;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_new_quiz);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
        if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(NewQuizActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else builder = new AlertDialog.Builder(NewQuizActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        final View view = getLayoutInflater().inflate(R.layout.dialog_new_quiz,null,false);
        dialogSpinner_01 = (Spinner)view.findViewById(R.id.spinner_01);
        dialogSpinner_02 = (Spinner)view.findViewById(R.id.spinner_02);
        dialogEdit_01 = (EditText)view.findViewById(R.id.edit_01);
        dialogButton_01 = (Button)view.findViewById(R.id.button_01);
        dialogArray_01 = new ArrayList<String>();
        dialogArray_02 = new ArrayList<String>() {{ add("5"); add("6"); add("7"); add("8"); add("9"); add("10");}};
        DataHolder.firebaseDatabase.child("quizy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dialogArray_01.add(snapshot.getKey());
                }
                if (canFinish) {
                    finish();
                    return;
                }
                helpFunction(view);
                if (canFinish) NewQuizActivity.this.finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });
    }

    protected void helpFunction(View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewQuizActivity.this, android.R.layout.simple_spinner_item, dialogArray_01);
        ArrayAdapter<String> adapter_02 = new ArrayAdapter<String>(NewQuizActivity.this, android.R.layout.simple_spinner_item, dialogArray_02);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_02.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dialogSpinner_01.setAdapter(adapter);
        dialogSpinner_02.setAdapter(adapter_02);

        dialogSpinner_01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = dialogArray_01.get(position);
                isSelected_01 = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int xd = 1;
            }
        });

        dialogSpinner_02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountOfQuestions = Integer.parseInt(dialogArray_02.get(position));
                isSelected_02 = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int xd = 1;
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        dialogButton_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected_01 && isSelected_02 && !dialogEdit_01.getText().toString().equals(""))
                {
                    quizName = dialogEdit_01.getText().toString();
                    alertDialog.dismiss();
                    qID.setText("Pytanie "+(iter+1)+"/"+amountOfQuestions);
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    qID.setVisibility(View.VISIBLE);
                    question_input.setVisibility(View.VISIBLE);
                    textsAnswers = new ArrayList<ArrayList<String>>(){{
                        for (int i=0;i<amountOfQuestions;++i)
                            add(new ArrayList<String>(){{
                                add("");
                                add("");
                                add("");
                                add("");
                            }});
                    }};
                    textsQuestions = new ArrayList<String>(){{
                        for (int i=0;i<amountOfQuestions;++i)
                            add("");
                    }};
                    images = new ArrayList<Uri>(){{
                        for (int i=0;i<amountOfQuestions;++i)
                            add(null);
                    }};
                    image_changed = new ArrayList<Boolean>(){{
                        for (int i=0;i<amountOfQuestions;++i)
                            add(false);
                    }};
                }
                else Toast.makeText(NewQuizActivity.this,"Nieprawidłowe dane quizu.",Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alertDialog.dismiss();
                NewQuizActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.image:
                if (ContextCompat.checkSelfPermission(NewQuizActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) DataHolder.requestStoragePermission(this);
                else chooseImage();
                break;
//            case R.id.clear_form:
//                clearForm();
//                Toast.makeText(getApplicationContext(),"Wyczyszczono formularz!", Toast.LENGTH_LONG).show();
//                break;
            case R.id.add_quiz:
//                uploadImage();
                break;

            case R.id.button_next:
                if(iter<amountOfQuestions)
                {
                    textsQuestions.set(iter,question_input.getText().toString());
                    for (int i=0;i<4;++i)
                        textsAnswers.get(iter).set(i,ans.get(i).getText().toString());
                    ++iter;
                    if (iter == amountOfQuestions) // Last question --> Add quiz
                    {
                        addQuiz();
                        canFinish = true;
                        NewQuizActivity.this.finish();
                    }
                    else
                    {
                        if (images.get(iter) != null) image1View.setImageURI(images.get(iter));
                        else image1View.setImageURI(images.get(iter));
                        question_input.setText(textsQuestions.get(iter));
                        qID.setText("Pytanie "+(iter+1)+"/"+amountOfQuestions);
                        int i = 0;
                        for (TextInputEditText answer : ans)
                        {
                            answer.setText(textsAnswers.get(iter).get(i));
                            ++i;
                        }
                    }
                    if (iter == amountOfQuestions-1) next.setText("Add quiz");
                    else next.setText("Next");
                    question_input.clearFocus();
                    for (TextInputEditText answer : ans)
                        answer.clearFocus();
                }
                break;

            case R.id.button_prev:
                if(iter>0)
                {
                    textsQuestions.set(iter,question_input.getText().toString());
                    for (int i=0;i<4;++i)
                        textsAnswers.get(iter).set(i,ans.get(i).getText().toString());
                    question_input.setText(textsQuestions.get(iter-1));
                    int i = 0;
                    --iter;
                    if (images.get(iter) != null) image1View.setImageURI(images.get(iter));
                    else image1View.setImageResource(0);
                    qID.setText("Pytanie "+(iter+1)+"/"+amountOfQuestions);
                    for (TextInputEditText answer : ans)
                    {
                        answer.setText(textsAnswers.get(iter).get(i));
                        ++i;
                    }
                    next.setText("Next");
                    question_input.clearFocus();
                    for (TextInputEditText answer : ans)
                        answer.clearFocus();
                }
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(new Intent()
                                                            .setType("image/*")
                                                            .setAction(Intent.ACTION_GET_CONTENT), "Select Picture"), DataHolder.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DataHolder.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFilePath = data.getData();
            try
            {
                image1View.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uriFilePath));
                images.set(iter, uriFilePath);
                image_changed.set(iter, true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (uriFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            image1URL = DataHolder.storageReference
                    .child("images")
                    .child(uriFilePath.getLastPathSegment())
                    .toString();
            DataHolder.storageReference
                    .child("images")
                    .child(uriFilePath.getLastPathSegment())
                    .putFile(uriFilePath)
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

    private void addQuiz()
    {
        DatabaseReference pathName = DataHolder.firebaseDatabase.child("quizy").child(selectedCategory).child(quizName);
        // image1URL zawiera sciezke URL obrazka (Storage > images > obrazek)
        // Dodanie do quizu id użytkownika który stworzył ten quiz
        DataHolder.firebaseDatabase
                .child("quizy")
                .child(selectedCategory)
                .child(quizName)
                .child("metadata")
                .child("author")
                .setValue(DataHolder.firebaseUser.getUid());

        // Dodanie quizu do bazy danych
        for (int i=0;i<amountOfQuestions;++i)
        {
            for (int j=0;j<4;++j)
            {
                if (j == 0)
                {
                    pathName.child(textsQuestions.get(i)).child("okodp").setValue(textsAnswers.get(i).get(j));
                }
                else if (!textsAnswers.get(i).get(j).equals(""))
                {
                    pathName.child(textsQuestions.get(i)).child("otherodp"+String.valueOf(i)).setValue(textsAnswers.get(i).get(j));
                }
            }
            if (image_changed.get(i))
            {
                DataHolder.storageReference.child("quizzesImages").child(quizName).child("image"+i+".jpg").putFile(images.get(i));
            }
        }
        Toast.makeText(NewQuizActivity.this,"Quiz have been added.", Toast.LENGTH_SHORT).show();
//        pathName.child(questionquiz1.getText().toString()).child("image1").setValue(image1URL);
    }

    private void clearForm() {

    }

    protected void initViews()
    {
        image1View = (ImageView) findViewById(R.id.image);
        question_input = (TextInputEditText) findViewById(R.id.question_t);
        ans = new ArrayList<TextInputEditText>() {{
           add((TextInputEditText) findViewById(R.id.ans1_t));
           add((TextInputEditText) findViewById(R.id.ans2_t));
           add((TextInputEditText) findViewById(R.id.ans3_t));
           add((TextInputEditText) findViewById(R.id.ans4_t));
        }};
        prev = (Button) findViewById(R.id.button_prev);
        next = (Button) findViewById(R.id.button_next);
        image1View.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        qID = (TextView) findViewById(R.id.question_id);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        prev.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        qID.setVisibility(View.INVISIBLE);
        question_input.setVisibility(View.INVISIBLE);
    }
}
