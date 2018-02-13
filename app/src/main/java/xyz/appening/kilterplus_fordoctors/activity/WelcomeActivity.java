package xyz.appening.kilterplus_fordoctors.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import xyz.appening.kilterplus_fordoctors.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onHospitalClick(View view) {
        startActivity(new Intent(this, SignUpHospitalActivity.class));
    }

    public void onDoctorClick(View view) {
        startActivity(new Intent(this, SignUpDoctorActivity.class));
    }
}
