package pl.com.fireflies.quizapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PlayQuiz extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ConstraintLayout main_constraintLayout;
    private LinearLayout inner_linearLayout;
    private ScrollView scrollView;
    private TextView quiz_name;
    private ArrayList<TextView> questions_array;
    private ArrayList<RadioGroup> anwsers_array;
    private ArrayList<RadioButton> right_anwsers_array;
    private ArrayList<RadioButton> wrong_anwsers_array;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_play_quiz);

        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);

        initViews();


        String names = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");

        quiz_name.setText(names);

        mDatabase.child("quizy").child(category).child(names).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int il = 0;
                Toast.makeText(PlayQuiz.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().toString().equals("metadata")) continue;
                    questions_array.add(new TextView(PlayQuiz.this));
                    questions_array.get(il).setTextSize(20);
                    Random r = new Random();
                    switch (r.nextInt(4)) {
                        case 0:
                            if (DataHolder.getInstance().dark_theme) questions_array.get(il).setTextColor(getColor(R.color.colorPrimaryDark));
                            else questions_array.get(il).setTextColor(getColor(R.color.DarkPink));
                            break;
                        case 1:
                            if (DataHolder.getInstance().dark_theme) questions_array.get(il).setTextColor(getColor(R.color.colorMaterialViolet));
                            else questions_array.get(il).setTextColor(getColor(R.color.DarkBlue));
                            break;
                        case 2:
                            if (DataHolder.getInstance().dark_theme) questions_array.get(il).setTextColor(getColor(R.color.colorMaterialDeepPurple));
                            else questions_array.get(il).setTextColor(getColor(R.color.DarkGreen));
                            break;
                        case 3: questions_array.get(il).setTextColor(getColor(R.color.fui_bgEmail)); break;
                    }
                    anwsers_array.add(new RadioGroup(PlayQuiz.this));
                    questions_array.get(il).setText(snapshot.getKey().toString());
                    right_anwsers_array.add(new RadioButton(PlayQuiz.this));
                    wrong_anwsers_array.add(new RadioButton(PlayQuiz.this));
                    if (DataHolder.getInstance().dark_theme) {
                        right_anwsers_array.get(il).setTextColor(getColor(R.color.colorMaterialLightWhite));
                        wrong_anwsers_array.get(il).setTextColor(getColor(R.color.colorMaterialLightWhite));
                    }
                    inner_linearLayout.addView(questions_array.get(il));
                    int i = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (i == 0) right_anwsers_array.get(il).setText(snapshot1.getValue().toString());
                        else wrong_anwsers_array.get(il).setText(snapshot1.getValue().toString());
                        ++i;
                    }
                    if (r.nextInt(2) == 0) {
                        anwsers_array.get(il).addView(right_anwsers_array.get(il));
                        anwsers_array.get(il).addView(wrong_anwsers_array.get(il));
                    } else {
                        anwsers_array.get(il).addView(wrong_anwsers_array.get(il));
                        anwsers_array.get(il).addView(right_anwsers_array.get(il));
                    }
                    inner_linearLayout.addView(anwsers_array.get(il));
                    ++il;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayQuiz.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer score = 0;
                for (RadioButton rB : right_anwsers_array) {
                    rB.setTextColor(getColor(R.color.colorMaterialGreen));
                    if (rB.isChecked())
                        ++score;
                }
                for (RadioButton rB : wrong_anwsers_array)
                    rB.setTextColor(getColor(R.color.colorMaterialRed));
                AlertDialog alertDialog;
                AlertDialog.Builder builder;
                if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(PlayQuiz.this, android.R.style.Theme_Material_Dialog_Alert);
                else builder = new AlertDialog.Builder(PlayQuiz.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_quiz_result,null);

                TextView punktacja = (TextView)view1.findViewById(R.id.punktacja);
                Button play_again = (Button)view1.findViewById(R.id.play_again);
                Button back = (Button)view1.findViewById(R.id.back);

                punktacja.setText(score.toString());

                play_again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataHolder.getInstance().play_again = true;
                        finish();
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataHolder.getInstance().play_again = false;
                        finish();
                    }
                });

                builder.setView(view1);
                alertDialog = builder.create();
                alertDialog.show();

                if (score == questions_array.size())
                    DataHolder.firebaseDatabase
                        .child("users")
                        .child(DataHolder.firebaseUser.getUid())
                        .child("level")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int value = Integer.parseInt(dataSnapshot.getValue().toString());
                                ++value;
                                dataSnapshot.getRef().setValue(Integer.toString(value));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    protected void initViews() {
        main_constraintLayout = (ConstraintLayout) findViewById(R.id.main_constraint_layout);
        inner_linearLayout = (LinearLayout) findViewById(R.id.inner_linear_layout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        quiz_name = (TextView) findViewById(R.id.nameQuiz);
        button = (Button) findViewById(R.id.checkQuiz);
        anwsers_array = new ArrayList<RadioGroup>();
        questions_array = new ArrayList<TextView>();
        right_anwsers_array = new ArrayList<RadioButton>();
        wrong_anwsers_array = new ArrayList<RadioButton>();
    }
}
