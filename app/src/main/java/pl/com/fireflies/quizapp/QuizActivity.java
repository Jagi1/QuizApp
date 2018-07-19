package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class QuizActivity extends AppCompatActivity
{
    private String[] cat;
    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_quiz);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        final Random r = new Random();
        final int[] in = new int[2];
        cat = new String[10];
        names = new String[10];
        DataHolder.firebaseDatabase
                .child("quizy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            cat[i] = snapshot.getKey();
                            ++i;
                        }
                        int ilcat = i;
                        in[0] = r.nextInt(ilcat);
                        DataHolder.firebaseDatabase
                                .child("quizy")
                                .child(cat[in[0]])
                                .addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1)
                                    {
                                        int j = 0;
                                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren())
                                        {
                                            names[j] = snapshot1.getKey();
                                            ++j;
                                        }
                                        int ilquiz = j;
                                        in[1] = r.nextInt(ilquiz);
                                        Intent intent = new Intent(QuizActivity.this, PlayQuiz.class);
                                        intent.putExtra("name", names[in[1]]);
                                        intent.putExtra("category", cat[in[0]]);
                                        QuizActivity.this.startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        Toast.makeText(QuizActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                    }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(QuizActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
        });
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if(!DataHolder.getInstance().play_again) finish();
        else recreate();
    }
}
