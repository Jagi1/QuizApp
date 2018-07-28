package pl.com.fireflies.quizapp;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserPanelActivity extends AppCompatActivity implements View.OnClickListener
{
    private Toolbar toolbar;
    private TextView userBar_level, userBar_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_user_panel);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        updateUserProperties();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
        if (DataHolder.firebaseAuth == null) finish();
    }

    /**
     * This method download user properties like his level, amount of in-game currency etc.
     * */
    protected void updateUserProperties() {
        DataHolder.firebaseDatabase
                .child("users")
                .child(DataHolder.firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            if (snapshot.getKey().equals("currency")) userBar_currency.setText(getString(R.string.currency, snapshot.getValue().toString()));
                            else if (snapshot.getKey().equals("level")) userBar_level.setText(getString(R.string.level, snapshot.getValue().toString()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(UserPanelActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fast_game_card:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, QuizActivity.class));
                break;

            case R.id.invite_friend_card:
                String[] PLACEHOLDER_FRIEND_LIST = {"Friend 1", "Friend 2", "Friend 3"};
                AlertDialog dialog;
                AlertDialog.Builder builder;
                if (DataHolder.getInstance().dark_theme) builder = new AlertDialog.Builder(UserPanelActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                else builder = new AlertDialog.Builder(UserPanelActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Friend list");
                // TODO: Add friend list.
                builder.setItems(PLACEHOLDER_FRIEND_LIST, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.categories_card:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, CategoriesActivity.class));
                break;

            case R.id.new_quiz_card:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, NewQuizActivity.class));
                break;

            case R.id.my_quizzes_card:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, MyQuizzesActivity.class));
                break;

            case R.id.challenges_card:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, ChallengesActivity.class));
                break;

            case R.id.avatar:
//                final View view = getLayoutInflater().inflate(R.layout.user_bar,null,false);
//                Pair<View, String> pair = new Pair<>(view.findViewById(R.id.avatar),"avatar");
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserPanelActivity.this,pair);
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, AccountSettingsActivity.class)
                //        ,options.toBundle()
                );
                break;

            case R.id.settings_gear:
                UserPanelActivity.this.startActivity(new Intent(UserPanelActivity.this, SettingsActivity.class));
                break;
        }
    }

    protected void initViews()
    {
        toolbar = (Toolbar) findViewById(R.id.user_bar);
        ArrayList<CardView> cards = new ArrayList<CardView>()
        {{
            add((CardView) findViewById(R.id.fast_game_card));
            add((CardView) findViewById(R.id.invite_friend_card));
            add((CardView) findViewById(R.id.categories_card));
            add((CardView) findViewById(R.id.new_quiz_card));
            add((CardView) findViewById(R.id.my_quizzes_card));
            add((CardView) findViewById(R.id.challenges_card));
        }};
        ImageButton userBar_settings = (ImageButton) findViewById(R.id.settings_gear);
        userBar_level = (TextView) findViewById(R.id.level);
        userBar_currency = (TextView) findViewById(R.id.currency);
        ImageButton userBar_avatar = (ImageButton) findViewById(R.id.avatar);
        userBar_avatar.setImageBitmap(DataHolder.getInstance().avatarBitmap);
        for (CardView card : cards) card.setOnClickListener(this);
        userBar_avatar.setOnClickListener(this);
        userBar_settings.setOnClickListener(this);
        if (DataHolder.getInstance().dark_theme)
        {
            cards.get(0).setCardBackgroundColor(getColor(R.color.colorMaterialDarkRed));
            cards.get(1).setCardBackgroundColor(getColor(R.color.colorMaterialDark1));
            cards.get(2).setCardBackgroundColor(getColor(R.color.colorMaterialDarkYellow));
            cards.get(3).setCardBackgroundColor(getColor(R.color.colorMaterialDark2));
            cards.get(4).setCardBackgroundColor(getColor(R.color.colorMaterialDarkPink));
            cards.get(5).setCardBackgroundColor(getColor(R.color.colorMaterialDarkViolet));
        }
    }
}
