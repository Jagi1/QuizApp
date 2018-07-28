package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class PlayQuiz extends AppCompatActivity implements View.OnClickListener
{
    private TextView quiz_name, question_number;
    private Button prev, next;
    private ArrayList<Pair<String, ArrayList<String>>> questions;
    private String question_name;
    private ArrayList<Button> buttons;
    private TextView textQuestion;
    private int iter = 0, score = 0;

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

        DataHolder.firebaseDatabase
                .child("quizy")
                .child(category)
                .child(names)
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            if (snapshot.getKey() != null && snapshot.getKey().equals("metadata"))
                                continue;
                            questions.add(new Pair<String, ArrayList<String>>(snapshot.getKey(), new ArrayList<String>())); // Add new set question / answers
                            textQuestion.setText(snapshot.getKey()); // Write questions
                            for (DataSnapshot mySnapshot : snapshot.getChildren()) // Write answers
                                if (mySnapshot.getValue() != null)
                                    questions.get(iter).second.add(mySnapshot.getValue().toString());
                            ++iter;
                        }
                        textQuestion.setText(questions.get(0).first); // Set first pair of question and answers
                        question_number.setText("1/"+(iter+1));
                        int i = 0;
                        for (String s : questions.get(0).second)
                        {
                            if (i == 0)
                                buttons.get(i).setContentDescription("true");
                            else
                                buttons.get(i).setContentDescription("false");
                            buttons.get(i).setText(s);
                            buttons.get(i).setVisibility(View.VISIBLE);
                            buttons.get(i).setClickable(true);
                            ++i;
                        }
                        iter = 0;
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(PlayQuiz.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void initViews()
    {
        quiz_name = (TextView) findViewById(R.id.nameQuiz);
        questions = new ArrayList<>();
        prev = (Button) findViewById(R.id.button_prev);
        next = (Button) findViewById(R.id.button_next);
        question_number = (TextView) findViewById(R.id.question_id);
        textQuestion = (TextView) findViewById(R.id.question);
        buttons = new ArrayList<Button>() {{
           add((Button) findViewById(R.id.ans1));
           add((Button) findViewById(R.id.ans2));
           add((Button) findViewById(R.id.ans3));
           add((Button) findViewById(R.id.ans4));
        }};
        for (Button button : buttons)
        {
            button.setOnClickListener(this);
            button.setClickable(false);
            button.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getContentDescription() == "true" || v.getContentDescription() == "false")
        {
            if (v.getContentDescription() == "true")
            {
                ++score;
                Toast.makeText(PlayQuiz.this,"Correct answer!",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(PlayQuiz.this,"Wrong answer!",Toast.LENGTH_SHORT).show();
            ++iter;
            if (iter == questions.size())
            {
                AlertDialog alertDialog;
                AlertDialog.Builder builder;
                if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(PlayQuiz.this, android.R.style.Theme_Material_Dialog_Alert);
                else builder = new AlertDialog.Builder(PlayQuiz.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_quiz_result,null);
                TextView punktacja = (TextView)view1.findViewById(R.id.punktacja);
                Button play_again = (Button)view1.findViewById(R.id.play_again);
                Button back = (Button)view1.findViewById(R.id.back);
                punktacja.setText(String.valueOf(score));
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
                iter = 0;
                if (score == questions.size())
                    DataHolder.firebaseDatabase
                            .child("users")
                            .child(DataHolder.firebaseUser.getUid())
                            .child("level")
                            .addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int value = Integer.parseInt((String)dataSnapshot.getValue());
                                    ++value;
                                    dataSnapshot.getRef().setValue(Integer.toString(value));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
            }
            else
            {
                textQuestion.setText(questions.get(iter).first);
                question_number.setText((iter+1)+"/"+questions.size());
                Random random = new Random();
                int[]arr = new int[questions.get(iter).second.size()];
                int j = 0;
                for (int i=0;i<questions.get(iter).second.size();++i)
                {
                    if (j==0)
                        arr[i] = getRandomWithExclusion(random,0,questions.get(iter).second.size()-1);
                    else if (j==1)
                        arr[i] = getRandomWithExclusion(random,0,questions.get(iter).second.size()-1, arr[0]);
                    else if (j==2)
                        arr[i] = getRandomWithExclusion(random,0,questions.get(iter).second.size()-1, arr[0], arr[1]);
                    else
                        arr[i] = getRandomWithExclusion(random,0,questions.get(iter).second.size()-1, arr[0], arr[1], arr[2]);
                    ++j;
                }
                int i = 0;
                for (Button b : buttons)
                {
                    if (i == arr.length)
                        break;
                    if (arr[i] == 0)
                        b.setContentDescription("true");
                    else
                        b.setContentDescription("false");
                    b.setText(questions.get(iter).second.get(arr[i]));
                    b.setVisibility(View.VISIBLE);
                    b.setClickable(true);
                    ++i;
                }
            }
        }
    }

    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            ++random;
        }
        return random;
    }
}
