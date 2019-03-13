package com.sh.snsecommerce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    TextView tv_memory_usage;

    private Timer mTimer;
    RecyclerAdapter adapter;
    Thread th;

    ArrayList<Comment> comments;
    private String auth_name;
    private String auth_email;
    private String user_name;
    private String user_email;
    double maxMemory;
    double allocate_memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        constraintLayout = (ConstraintLayout)navigationView.getHeaderView(0);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        tv_memory_usage = (TextView)findViewById(R.id.tv_memory_usage);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getUser(mAuth);                         //Firestore DB 정보 가져오기

        user_name = user.getDisplayName();
        user_email = user.getEmail();

        //NavigationView - TextView에 이름, 메일 출력
        if(user_name != null) {
            TextView tv_user_name = (TextView) constraintLayout.findViewById(R.id.tv_user_name);
            tv_user_name.setText(user_name);
        }
        if(user_email != null) {
            TextView tv_user_email = (TextView) constraintLayout.findViewById(R.id.tv_user_email);
            tv_user_email.setText(user_email);
        }

        comments = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            String name = "name" + String.valueOf(i);
            String contents = "contents" + String.valueOf(i);
            String date = "2019-01-" + String.valueOf(i);

            comments.add(new Comment(name, contents, date));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new RecyclerAdapter(comments, getApplicationContext());
        recyclerView.setAdapter(adapter);

    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_calendar) {                //캘린더 켜기
            goCalendarActivity();
        }
        else if(v.getId() == R.id.btn_setting) {                 //drawerLayout 켜기
            if(!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            }
        }
        else if(v.getId() == R.id.btn_logout) {             //로그 아웃
            mAuth.signOut();
            goLoginActivity();
        }
    }

    @Override
    public void onBackPressed() {                           //drawerLayout 끄기
       if(drawerLayout.isDrawerOpen(navigationView)) {
           drawerLayout.closeDrawer(navigationView);
       }
       else {
           super.onBackPressed();
       }
    }

    public void getUser(FirebaseAuth user) {                         //Firestore DB 정보 가져오기
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot document:task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.exists()) {
                                    auth_name = document.getData().get("name").toString();
                                    auth_email = document.getData().get("email").toString();

                                    if(auth_name != null) {
                                        TextView tv_auth_name = (TextView) constraintLayout.findViewById(R.id.tv_auth_name);
                                        tv_auth_name.setText(auth_name);
                                    }
                                    if(auth_email != null) {
                                        TextView tv_auth_email = (TextView) constraintLayout.findViewById(R.id.tv_auth_email);
                                        tv_auth_email.setText(auth_email);
                                    }
                                }
                                else {
                                    Log.d(TAG, "No such document");
                                }
                            }
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void checkMemory() {                 //사용 메모리 체크
        maxMemory = Runtime.getRuntime().maxMemory() / (1024.0f);
        allocate_memory = Debug.getNativeHeapAllocatedSize() / (1024.0f);
    }

    public void goLoginActivity() {                          //LoginActvity로 이동
        Intent goIntent = new Intent(this, LoginActivity.class);
        startActivity(goIntent);
        finish();
    }

    public void goCalendarActivity() {                        //CalendarActvity로 이동
        Intent goIntent = new Intent(this, CalendarActivity.class);
        startActivity(goIntent);
    }

    public class CustomRunnable implements Runnable {
        @Override
        public void run() {
            int count = 0;
            while(count++ < 1000) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                checkMemory();
                runOnUiThread(new Runnable() {          //UI 변경
                    @Override
                    public void run() {
                        tv_memory_usage.setText("최대 메모리 : " + (int)maxMemory + "KB\n사용 메모리 : " + (int)allocate_memory + "KB");
                        Log.e(TAG,"최대 메모리 : " + (int)maxMemory + "KB, 사용 메모리 : " + (int)allocate_memory + "KB");
                    }
                });
            }
        }
    }

}
