package com.sh.snsecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout constraintLayout;

    private static final String TAG = "MainActivity";

    private String auth_name;
    private String auth_email;
    private String user_name;
    private String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        constraintLayout = (ConstraintLayout)navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getUser(mAuth);

        user_name = user.getDisplayName();
        user_email = user.getEmail();

        if(user_name != null) {
            TextView tv_user_name = (TextView) constraintLayout.findViewById(R.id.tv_user_name);
            tv_user_name.setText(user_name);
        }
        if(user_email != null) {
            TextView tv_user_email = (TextView) constraintLayout.findViewById(R.id.tv_user_email);
            tv_user_email.setText(user_email);
        }
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_setting) {                 //drawerLayout 겨키
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

    public void getUser(FirebaseAuth user) {
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

    public void goLoginActivity() {                          //LoginActvity로 이동
        Intent goIntent = new Intent(this, LoginActivity.class);
        startActivity(goIntent);
        finish();
    }
}
