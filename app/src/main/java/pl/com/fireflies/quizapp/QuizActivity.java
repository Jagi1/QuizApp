package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private int ilcat = 0, ilquiz = 0;
    private String[] cat;
    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_quiz);
        final Random r = new Random();
        final int[] in = new int[2];
        cat = new String[10];
        names = new String[10];
        mDatabase.child("quizy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey().toString();
                    cat[i] = key;
                    i = i + 1;
                }
                ilcat = i;
                in[0] = r.nextInt(ilcat);
                mDatabase.child("quizy").child(cat[in[0]]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        int j = 0;
                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            String key = snapshot1.getKey().toString();
                            names[j] = key;
                            j = j + 1;
                        }
                        ilquiz = j;
                        in[1] = r.nextInt(ilquiz);
                        Intent intent = new Intent(QuizActivity.this, PlayQuiz.class);
                        intent.putExtra("name", names[in[1]]);
                        intent.putExtra("category", cat[in[0]]);
                        QuizActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(QuizActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) {
            recreate();
        }
    }
}
