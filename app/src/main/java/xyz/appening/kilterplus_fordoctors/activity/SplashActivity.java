package xyz.appening.kilterplus_fordoctors.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import xyz.appening.kilterplus_fordoctors.utils.Common;

/**
 * Created by salildhawan on 27/01/18.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE).contains(Common.API_TOKEN))
            startActivity(new Intent(this, WelcomeActivity.class));
        else
            startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }
}
