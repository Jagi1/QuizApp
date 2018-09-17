package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SelectCategories extends AppCompatActivity {
    private String st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_select_categories);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        st = getIntent().getStringExtra("category");
        final View.OnClickListener btnclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SelectCategories.this,view.getId()+view.getTag().toString(),Toast.LENGTH_LONG).show();
                startActivity(new Intent(SelectCategories.this, PlayQuiz.class)
                        .putExtra("name", view.getTag().toString())
                        .putExtra("category", st));
            }
        };
        DataHolder.firebaseDatabase.child("quizy").child(st).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int i = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LinearLayout linearLayout = findViewById(R.id.linear);
                    Button btn = new Button(SelectCategories.this);
                    btn.setText(snapshot.getKey());
                    btn.setTag(snapshot.getKey());
                    btn.setId(i);
                    btn.setOnClickListener(btnclick);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(btn);
                    ++i;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(SelectCategories.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
