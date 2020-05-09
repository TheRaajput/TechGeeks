package com.yasharth.techgeeks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private Button addPostBtn;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private BottomNavigationView mainbottomView;
    private FragmentHome homeFragment;
    private FragmentNotification notificationFragment;
    private FragmentAccount fragmentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("TECHGEEKS");
        addPostBtn = findViewById(R.id.mainPostbtn);

        if (mAuth.getCurrentUser() != null) {

            mainbottomView = findViewById(R.id.mainBottomNav);

            //Fragments
            homeFragment = new FragmentHome();
            notificationFragment = new FragmentNotification();
            fragmentAccount = new FragmentAccount();

            mainbottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true;


                        case R.id.bottom_action_account:
                            replaceFragment(fragmentAccount);
                            return true;


                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;
                        default:
                            return false;
                    }
                }
            });


            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent addPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(addPostIntent);

                }
            });

        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            sendToLogin();

        }
        replaceFragment(homeFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_logoutBtn:

                logOut();

                return true;
            case R.id.Account_settingBtn:
                ToAccountSettings();
                return true;

            default:
                return false;
        }

    }

    private void ToAccountSettings() {
        Intent SetupIntent = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(SetupIntent);

    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

    }

}
