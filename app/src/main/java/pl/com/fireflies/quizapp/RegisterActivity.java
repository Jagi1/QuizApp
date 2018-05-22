package pl.com.fireflies.quizapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity
{
    private static final String TAG = "RegisterActivity";
    private Button date_button,register_button;
    private DatePickerDialog.OnDateSetListener date_listener;
    private String date;
    private Integer year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_NoActionBar_Overscan,
                        date_listener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d(TAG,"onDateSet: mm/dd/yyyy: "+month+"/"+dayOfMonth+"/"+year);
                date = month+"/"+dayOfMonth+"/"+year;
                Toast.makeText(RegisterActivity.this,date,Toast.LENGTH_LONG).show();
            }
        };

    }

    protected void initViews()
    {
        date_button = (Button)findViewById(R.id.date_button);
        register_button = (Button)findViewById(R.id.register_button);
    }

}

