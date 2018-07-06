package pl.com.fireflies.quizapp;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayQuiz extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ImageView imageQuestion1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_play_quiz);
        String names = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        TextView namequiz = (TextView) findViewById(R.id.nameQuiz);
        namequiz.setText(names);
        final AandQ[] tab = new AandQ[5];
        final AandQ[] aq = new AandQ[1];
        final EditText question1 = (EditText) findViewById(R.id.questionquiz1);
//        imageQuestion1 = (ImageView) findViewById(R.id.question1_image1);
        final RadioButton yodp1 = (RadioButton) findViewById(R.id.yodp1);
        final RadioButton nodp1 = (RadioButton) findViewById(R.id.nodp1);
        final EditText question2 = (EditText) findViewById(R.id.questionquiz2);
        final RadioButton yodp2 = (RadioButton) findViewById(R.id.yodp2);
        final RadioButton nodp2 = (RadioButton) findViewById(R.id.nodp2);
        final EditText question3 = (EditText) findViewById(R.id.questionquiz3);
        final RadioButton yodp3 = (RadioButton) findViewById(R.id.yodp3);
        final RadioButton nodp3 = (RadioButton) findViewById(R.id.nodp3);
        final EditText question4 = (EditText) findViewById(R.id.questionquiz4);
        final RadioButton yodp4 = (RadioButton) findViewById(R.id.yodp4);
        final RadioButton nodp4 = (RadioButton) findViewById(R.id.nodp4);
        final EditText question5 = (EditText) findViewById(R.id.questionquiz5);
        final RadioButton yodp5 = (RadioButton) findViewById(R.id.yodp5);
        final RadioButton nodp5 = (RadioButton) findViewById(R.id.nodp5);
        final Button check = (Button) findViewById(R.id.checkQuiz);

        mDatabase.child("quizy").child(category).child(names).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int il = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int i = 0;
                    String key = snapshot.getKey();
                    //Toast.makeText(PlayQuiz.this, "key: "+key, Toast.LENGTH_SHORT).show();
                    aq[0] = new AandQ();
                    aq[0].question = key;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue().toString();
                        //Toast.makeText(PlayQuiz.this, "value: "+value, Toast.LENGTH_SHORT).show();
                        aq[0].odp[i] = value;
                        i++;
                    }
                    tab[il] = aq[0];
                    il++;
                }
                question1.setText(tab[0].question.toString());
                yodp1.setText(tab[0].odp[0].toString());
                nodp1.setText(tab[0].odp[1].toString());
                question2.setText(tab[1].question.toString());
                yodp2.setText(tab[1].odp[0].toString());
                nodp2.setText(tab[1].odp[1].toString());
                question3.setText(tab[2].question.toString());
                yodp3.setText(tab[2].odp[0].toString());
                nodp3.setText(tab[2].odp[1].toString());
                question4.setText(tab[3].question.toString());
                yodp4.setText(tab[3].odp[0].toString());
                nodp4.setText(tab[3].odp[1].toString());
                question5.setText(tab[4].question.toString());
                yodp5.setText(tab[4].odp[0].toString());
                nodp5.setText(tab[4].odp[1].toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayQuiz.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer score = 0;
                if (yodp1.isChecked()) {
                    ++score;
                    nodp1.setTextColor(Color.rgb(200, 0, 0));
                    yodp1.setTextColor(Color.rgb(0, 200, 0));
                } else {
                    nodp1.setTextColor(Color.rgb(200, 0, 0));
                    yodp1.setTextColor(Color.rgb(0, 200, 0));
                }
                if (yodp2.isChecked()) {
                    ++score;
                    nodp2.setTextColor(Color.rgb(200, 0, 0));
                    yodp2.setTextColor(Color.rgb(0, 200, 0));
                } else {
                    nodp2.setTextColor(Color.rgb(200, 0, 0));
                    yodp2.setTextColor(Color.rgb(0, 200, 0));
                }
                if (yodp3.isChecked()) {
                    ++score;
                    nodp3.setTextColor(Color.rgb(200, 0, 0));
                    yodp3.setTextColor(Color.rgb(0, 200, 0));
                } else {
                    nodp3.setTextColor(Color.rgb(200, 0, 0));
                    yodp3.setTextColor(Color.rgb(0, 200, 0));
                }
                if (yodp4.isChecked()) {
                    ++score;
                    nodp4.setTextColor(Color.rgb(200, 0, 0));
                    yodp4.setTextColor(Color.rgb(0, 200, 0));
                } else {
                    nodp4.setTextColor(Color.rgb(200, 0, 0));
                    yodp4.setTextColor(Color.rgb(0, 200, 0));
                }
                if (yodp5.isChecked()) {
                    ++score;
                    nodp5.setTextColor(Color.rgb(200, 0, 0));
                    yodp5.setTextColor(Color.rgb(0, 200, 0));
                } else {
                    nodp5.setTextColor(Color.rgb(200, 0, 0));
                    yodp5.setTextColor(Color.rgb(0, 200, 0));
                }
                AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayQuiz.this);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_quiz_result,null);

                TextView punktacja = (TextView)view1.findViewById(R.id.punktacja);
                Button play_again = (Button)view1.findViewById(R.id.play_again);
                Button back = (Button)view1.findViewById(R.id.back);

                punktacja.setText(score);

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
            }
        });
    }

}
