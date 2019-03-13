package com.sh.snsecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();

                Intent popupIntent = new Intent(CalendarActivity.this, CalendarPopup.class);
                popupIntent.putExtra("DATE", date);
                startActivity(popupIntent);
            }
        });
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

}
