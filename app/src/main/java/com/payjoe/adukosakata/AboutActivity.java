package com.payjoe.adukosakata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        Log.v("AboutActivity", "getbackStackEntryCount : " + getFragmentManager().getBackStackEntryCount());
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStackImmediate();
        }
        finish();
    }
}
