package pl.com.fireflies.quizapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
    private ArrayList<Pair<String, Pair<String, String>>> texts;
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
    private TextInputEditText question_input, right_ans, wrong_ans;
    private int iter = 0;

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
                    right_ans.setVisibility(View.VISIBLE);
                    wrong_ans.setVisibility(View.VISIBLE);
                    question_input.setHint("Question");
                    right_ans.setHint("Right answer");
                    wrong_ans.setHint("Wrong answer");
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
//            case R.id.choose_image1:
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    DataHolder.requestStoragePermission(this);
//                } else {
//                    chooseImage();
//                }
//                break;
//            case R.id.clear_form:
//                clearForm();
//                Toast.makeText(getApplicationContext(),"Wyczyszczono formularz!", Toast.LENGTH_LONG).show();
//                break;
            case R.id.add_quiz:
//                uploadImage();
                break;

            case R.id.button_next:
                if(iter>=0 && iter<amountOfQuestions)
                {
                    if (iter == texts.size() || iter == texts.size()-1)
                    {
                        texts.add(new Pair<String, Pair<String, String>>(question_input.getText().toString(), new Pair<String, String>(right_ans.getText().toString(), wrong_ans.getText().toString())));
                        question_input.setText("");
                        right_ans.setText("");
                        wrong_ans.setText("");
                    }
                    else
                    {
                        question_input.setText(texts.get(iter+1).first);
                        right_ans.setText(texts.get(iter+1).second.first);
                        wrong_ans.setText(texts.get(iter+1).second.second);
                    }
                    ++iter;
                    if (iter == amountOfQuestions)
                    {
                        addQuiz();
                        canFinish = true;
                        NewQuizActivity.this.finish();
                    }
                    else qID.setText("Pytanie "+(iter+1)+"/"+amountOfQuestions);
                    if (iter == amountOfQuestions-1) next.setText("Add quiz");
                    else next.setText("Next");
                    question_input.clearFocus();
                    right_ans.clearFocus();
                    wrong_ans.clearFocus();
                }
                break;

            case R.id.button_prev:
                if(iter>0 && iter<=amountOfQuestions)
                {
                    if (iter == texts.size())
                    {
                        texts.add(new Pair<String, Pair<String, String>>(question_input.getText().toString(), new Pair<String, String>(right_ans.getText().toString(), wrong_ans.getText().toString())));
                        question_input.setText("");
                        right_ans.setText("");
                        wrong_ans.setText("");
                    }
                    else
                    {
                        question_input.setText(texts.get(iter-1).first);
                        right_ans.setText(texts.get(iter-1).second.first);
                        wrong_ans.setText(texts.get(iter-1).second.second);
                    }
                    --iter;
                    next.setText("Next");
                    qID.setText("Pytanie "+(iter+1)+"/"+amountOfQuestions);
                    question_input.clearFocus();
                    right_ans.clearFocus();
                    wrong_ans.clearFocus();
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
        for (Pair<String, Pair<String, String>> pair : texts)
        {
            pathName.child(pair.first)
                    .child("okodp")
                    .setValue(pair.second.first);
            pathName.child(pair.first)
                    .child("otherodp")
                    .setValue(pair.second.second);
        }
        Toast.makeText(NewQuizActivity.this,"Quiz have been added.", Toast.LENGTH_SHORT).show();
//        pathName.child(questionquiz1.getText().toString()).child("image1").setValue(image1URL);
    }

    private void clearForm() {

    }

    protected void initViews()
    {
        question_input = (TextInputEditText) findViewById(R.id.question_t);
        right_ans = (TextInputEditText) findViewById(R.id.ans1_t);
        wrong_ans = (TextInputEditText) findViewById(R.id.ans2_t);
        prev = (Button) findViewById(R.id.button_prev);
        next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        qID = (TextView) findViewById(R.id.question_id);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        texts = new ArrayList<Pair<String, Pair<String, String>>>();
        prev.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        qID.setVisibility(View.INVISIBLE);
        question_input.setVisibility(View.INVISIBLE);
        right_ans.setVisibility(View.INVISIBLE);
        wrong_ans.setVisibility(View.INVISIBLE);

    }
}
