package com.sh.snsecommerce;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarPopup extends Activity {
    private static final String TAG = "CalendarPopup";

    private FirebaseFirestore db;
    private ArrayList<Calendar> calendars;
    PopupRecyclerAdapter popupAdapter;

    EditText ed_popup_editText;
    RecyclerView popup_recyclerView;

    String pick_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 X
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar_popup);

        ed_popup_editText = (EditText)findViewById(R.id.ed_popup_editText);
        popup_recyclerView = (RecyclerView)findViewById(R.id.popup_recyclerView);
        calendars = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        ed_popup_editText.clearFocus();

        //날짜 정보 받기
        Intent getDateIntent = getIntent();
        pick_date = getDateIntent.getStringExtra("DATE");

        getCalendar(pick_date);             //오늘 일정 가져오기

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        popup_recyclerView.setLayoutManager(layoutManager);
        popup_recyclerView.setItemAnimator(new DefaultItemAnimator());

        popupAdapter = new PopupRecyclerAdapter(calendars, getApplicationContext());
        popup_recyclerView.setAdapter(popupAdapter);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_popup_back) {
            finish();
        }
        else if(v.getId() == R.id.btn_popup_ok) {
            saveCalender();
        }
    }

    public void saveCalender() {       //Firestore에 일정 정보 저장
        Map<String, String> calendar = new HashMap<>();
        calendar.put("content", ed_popup_editText.getText().toString());
        calendar.put("date", pick_date);

        db.collection("calendars").document()
                .set(calendar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        ed_popup_editText.setText("");
                        Toast.makeText(getApplicationContext(), "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                        calendars.clear();                  //UI Update
                        getCalendar(pick_date);

                        popupAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "일정 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getCalendar(final String today) {                 //Firestore에서 일정 정보 가져오기
        db.collection("calendars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot document:task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.exists()) {
                                    String id = document.getId();
                                    String content = document.getData().get("content").toString();
                                    String date = document.getData().get("date").toString();

                                    if(date.equals(today)) {            //오늘 일정 가져오기
                                        calendars.add(new Calendar(id, content, date));
                                    }
                                }
                                else {
                                    Log.d(TAG, "No such document");
                                }
                            }

                            popupAdapter.notifyDataSetChanged();
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
