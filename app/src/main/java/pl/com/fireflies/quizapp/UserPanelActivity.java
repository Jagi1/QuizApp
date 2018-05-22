package pl.com.fireflies.quizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class UserPanelActivity extends AppCompatActivity implements View.OnClickListener
{
    private Intent intent;
    private CardView fast_game_card,invite_friend_card,categories_card,new_quiz_card,my_quizes_card,challenges_card;
    private Toolbar toolbar;
    private AlertDialog.Builder friend_list_builder;
    private AlertDialog friend_list_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                intent = new Intent(UserPanelActivity.this,settingsActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.fast_game_card:
                intent = new Intent(UserPanelActivity.this,QuizActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.invite_friend_card:
                String[]PLACEHOLDER_FRIEND_LIST={"Friend 1","Friend 2","Friend 3"};
                friend_list_builder = new AlertDialog.Builder(UserPanelActivity.this);
                friend_list_builder.setTitle("Friend list");

                /**
                 * Trzeba dodac liste znajomych z bazy do dialogu
                 * */
                friend_list_builder.setItems(PLACEHOLDER_FRIEND_LIST, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                friend_list_dialog = friend_list_builder.create();
                friend_list_dialog.show();
                break;

            case R.id.categories_card:
                intent = new Intent(UserPanelActivity.this,CategoriesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.new_quiz_card:
                intent = new Intent(UserPanelActivity.this,NewQuizActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.my_quizzes_card:
                intent = new Intent(UserPanelActivity.this,MyQuizzesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;

            case R.id.challenges_card:
                intent = new Intent(UserPanelActivity.this,ChallengesActivity.class);
                UserPanelActivity.this.startActivity(intent);
                break;
        }
    }

    protected void initViews()
    {
        toolbar = (Toolbar)findViewById(R.id.user_bar);
        fast_game_card = (CardView) findViewById(R.id.fast_game_card);
        invite_friend_card = (CardView) findViewById(R.id.invite_friend_card);
        categories_card = (CardView) findViewById(R.id.categories_card);
        new_quiz_card = (CardView) findViewById(R.id.new_quiz_card);
        my_quizes_card = (CardView) findViewById(R.id.my_quizzes_card);
        challenges_card = (CardView) findViewById(R.id.challenges_card);
        fast_game_card.setOnClickListener(this);
        invite_friend_card.setOnClickListener(this);
        categories_card.setOnClickListener(this);
        new_quiz_card.setOnClickListener(this);
        my_quizes_card.setOnClickListener(this);
        challenges_card.setOnClickListener(this);
    }
}
