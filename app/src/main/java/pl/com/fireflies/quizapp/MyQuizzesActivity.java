package pl.com.fireflies.quizapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyQuizzesActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {
    private Intent intent;
    private ListView listView;
    private CustomAdapter adapter;
    private int number_of_quizzes = 0;
    private ArrayList<String> array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_my_quizzes);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);

        DataHolder.firebaseDatabase.child("quizy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot inner_snapshot : snapshot.getChildren()) {
                        if (inner_snapshot.child("metadata").child("author").getValue() == null) continue;
                        String author = inner_snapshot.child("metadata").child("author").getValue().toString();
                        if (author.equals(DataHolder.firebaseUser.getUid())) {
                            array.add(inner_snapshot.getKey().toString());
                            ++number_of_quizzes;
                        }
                    }
                }
                if (number_of_quizzes == 0) {
                    /**
                     * TODO: Swap AlertDialog layout to custom one.
                     * */
                    AlertDialog alertDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyQuizzesActivity.this);
                    builder.setMessage("It looks like you didn't created any quizzes. Wanna create one?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MyQuizzesActivity.this, NewQuizActivity.class);
                                    MyQuizzesActivity.this.startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyQuizzesActivity.this.finish();
                                }
                            });
                    alertDialog = builder.create();
                    alertDialog.show();
                }
                else initViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
        MyQuizzesActivity.this.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * TODO: Add AlertDialog to list items with options: remove quiz, play.
         * */
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return number_of_quizzes;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.listview_myquizzes, parent, false);
                holder = new ViewHolder(row, position);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder) row.getTag();
            }
            holder.text.setText(array.get(position));
            return row;
        }
    }

    static class ViewHolder {
        TextView text;
        int position;
        ViewHolder(View v, int position) {
            text = (TextView) v.findViewById(R.id.item);
            this.position = position;
        }
    }

    protected void initViews() {
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
}
