package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectCategories extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DataHolder.getInstance().dark_theme)
        {
            setTheme(R.style.DarkAppTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_select_categories);
        final String st = getIntent().getStringExtra("category");
        final View.OnClickListener btnclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SelectCategories.this,view.getId()+view.getTag().toString(),Toast.LENGTH_LONG).show();
                intent = new Intent(SelectCategories.this, PlayQuiz.class);
                intent.putExtra("name",view.getTag().toString());
                intent.putExtra("category",st);
                SelectCategories.this.startActivity(intent);
            }
        };
        mDatabase.child("quizy").child(st).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=1;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String key= snapshot.getKey().toString();
                    LinearLayout ll = (LinearLayout)findViewById(R.id.linear);
                    Button btn = new Button(SelectCategories.this);
                    btn.setText(key);
                    btn.setTag(key);
                    btn.setId(i);
                    btn.setOnClickListener(btnclick);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(btn);
                    i++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SelectCategories.this,databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
