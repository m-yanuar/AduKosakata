package com.payjoe.adukosakata;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.payjoe.adukosakata.Fragment.MyMatchesFragment;
import com.payjoe.adukosakata.Fragment.HomeFragment;
import com.payjoe.adukosakata.Fragment.RanksFragment;

public class MainActivity extends AppCompatActivity {
    //View
    private ActionBar toolbar;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Home");
        loadFragment(new HomeFragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch(item.getItemId()){
                case R.id.bottombaritem_home:
                    toolbar.setTitle("Home");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottombaritem_myMatches:
                    toolbar.setTitle("My Matches");
                    fragment = new MyMatchesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottombaritem_ranks:
                    toolbar.setTitle("Ranks");
                    fragment = new RanksFragment();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment){
        //load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
        getMenuInflater().inflate(R.menu.menu_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Menghandle menu pojok kanan atas
        switch (item.getItemId()){
            case R.id.about_button:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Again to exit...", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        },2000);
    }
    /**@Override
    public void onStop(){
        super.onStop();
        finish();
    }**/
}
