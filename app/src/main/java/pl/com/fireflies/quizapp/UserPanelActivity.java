package pl.com.fireflies.quizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserPanelActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private CardView fast_game_card, invite_friend_card, categories_card, new_quiz_card, my_quizes_card, challenges_card;
    private Toolbar toolbar;
    private AlertDialog.Builder friend_list_builder;
    private AlertDialog friend_list_dialog;
    private ImageButton userBar_avatar, userBar_settings;
    private TextView userBar_level, userBar_rank, userBar_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    protected void onRestart() {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
        if (DataHolder.getInstance().firebaseAuth == null) {
            finish();
        }
    }

    protected void updateUserProperties() {
        DataHolder.firebaseDatabase
                .child("users")
                .child(DataHolder.firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (i == 0) userBar_currency.setText("Currency: "+snapshot.getValue().toString());
                            else userBar_level.setText("Level: "+snapshot.getValue().toString());
                            ++i;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fast_game_card:
                intent = new Intent(UserPanelActivity.this, QuizActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.invite_friend_card:
                String[] PLACEHOLDER_FRIEND_LIST = {"Friend 1", "Friend 2", "Friend 3"};
                if (DataHolder.getInstance().dark_theme) friend_list_builder = new AlertDialog.Builder(UserPanelActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                else friend_list_builder = new AlertDialog.Builder(UserPanelActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                friend_list_builder.setTitle("Friend list");
                // TODO: Add friend list.
                friend_list_builder.setItems(PLACEHOLDER_FRIEND_LIST, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                friend_list_dialog = friend_list_builder.create();
                friend_list_dialog.show();
                break;

            case R.id.categories_card:
                intent = new Intent(UserPanelActivity.this, CategoriesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.new_quiz_card:
                intent = new Intent(UserPanelActivity.this, NewQuizActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.my_quizzes_card:
                intent = new Intent(UserPanelActivity.this, MyQuizzesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.challenges_card:
                intent = new Intent(UserPanelActivity.this, ChallengesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.avatar:
                intent = new Intent(UserPanelActivity.this, AccountSettingsActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.settings_gear:
                intent = new Intent(UserPanelActivity.this, SettingsActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;
        }
    }

    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.user_bar);
        fast_game_card = (CardView) findViewById(R.id.fast_game_card);
        invite_friend_card = (CardView) findViewById(R.id.invite_friend_card);
        categories_card = (CardView) findViewById(R.id.categories_card);
        new_quiz_card = (CardView) findViewById(R.id.new_quiz_card);
        my_quizes_card = (CardView) findViewById(R.id.my_quizzes_card);
        challenges_card = (CardView) findViewById(R.id.challenges_card);
        userBar_settings = (ImageButton) findViewById(R.id.settings_gear);
        userBar_level = (TextView) findViewById(R.id.level);
        userBar_rank = (TextView) findViewById(R.id.rank);
        userBar_currency = (TextView) findViewById(R.id.currency);
        userBar_avatar = (ImageButton) findViewById(R.id.avatar);
        userBar_avatar.setImageBitmap(DataHolder.getInstance().avatarBitmap);

        fast_game_card.setOnClickListener(this);
        invite_friend_card.setOnClickListener(this);
        categories_card.setOnClickListener(this);
        new_quiz_card.setOnClickListener(this);
        my_quizes_card.setOnClickListener(this);
        challenges_card.setOnClickListener(this);
        userBar_avatar.setOnClickListener(this);
        userBar_settings.setOnClickListener(this);

        if (DataHolder.getInstance().dark_theme) {
            fast_game_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkRed));
            invite_friend_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark1));
            categories_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkYellow));
            new_quiz_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark2));
            my_quizes_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkPink));
            challenges_card.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkViolet));
        }
        else {

        }
    }
}
