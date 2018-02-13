package xyz.appening.kilterplus_fordoctors.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.R;
import xyz.appening.kilterplus_fordoctors.api.helper.ErrorUtils;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceGenerator;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceInterface;
import xyz.appening.kilterplus_fordoctors.api.model.ErrorResponse;
import xyz.appening.kilterplus_fordoctors.api.model.HospitalProfileResponse;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;
import xyz.appening.kilterplus_fordoctors.custom.CustomSpinner;
import xyz.appening.kilterplus_fordoctors.utils.Common;

public class HospitalCreateProfileActivity extends AppCompatActivity {

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.editTextAddress)
    EditText editTextAddress;
    @BindView(R.id.editTextCIN)
    EditText editTextCIN;
    @BindView(R.id.editTextPan)
    EditText editTextPan;
    @BindView(R.id.editTextGST)
    EditText editTextGST;
    @BindView(R.id.spinnerHospitalType)
    CustomSpinner spinnerHospitalType;
    @BindView(R.id.spinnerNoOfDoctors)
    CustomSpinner spinnerNoOfDoctors;
    @BindView(R.id.spinnerSpeciality)
    CustomSpinner spinnerSpeciality;
    @BindArray(R.array.array_hospital_types)
    String[] arrayHospitalTypes;
    @BindArray(R.array.array_doctors_range)
    String[] arrayDoctorRange;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    private String hospitalType;
    private String noOfDoctors;
    private String speciality;
    public static final String USER = "user";
    private static final String TAG = "HospitalCreateProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_create_profile);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String userString = intent.getStringExtra(USER);
        Gson gson = new Gson();
        SignInResponse.User user = gson.fromJson(userString, SignInResponse.User.class);
        if (user == null) {
            if (!Common.isOnline(this)) {
                Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences(Common.SHARED_PREF, MODE_PRIVATE);
            String token = sharedPreferences.getString(Common.API_TOKEN, null);
            if (token == null) {
                Toast.makeText(this, R.string.error_unauthenticated, Toast.LENGTH_LONG).show();
                return;
            }
            getProfile(token);
        } else {
            editTextName.setText(user.getName());
            editTextEmail.setText(user.getEmail());
            String phone = user.getPhone();
            if(phone != null) {
                editTextPhone.setText(user.getPhone());
                editTextPhone.setEnabled(false);
            }
        }
        getSpecialities();
        setupSpinnerHospitalType();
        setupSpinnerDoctorRange();
    }

    private void setupSpinnerHospitalType() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arrayHospitalTypes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospitalType.setAdapter(spinnerArrayAdapter);
        spinnerHospitalType.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hospitalType = position == -1 ? null : arrayHospitalTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSpinnerDoctorRange() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arrayDoctorRange);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNoOfDoctors.setAdapter(spinnerArrayAdapter);
        spinnerNoOfDoctors.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noOfDoctors = position == -1 ? null : arrayDoctorRange[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSpinnerSpeciality(final String[] array) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpeciality.setAdapter(spinnerArrayAdapter);
        spinnerSpeciality.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speciality = position == -1 ? null : array[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onContinueClick(View view) {
        if (!Common.isOnline(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Common.SHARED_PREF, MODE_PRIVATE);
        String token = sharedPreferences.getString(Common.API_TOKEN, null);
        if (token == null) {
            Toast.makeText(this, R.string.error_unauthenticated, Toast.LENGTH_LONG).show();
            return;
        }
        String address = editTextAddress.getText().toString();
        String cin = editTextCIN.getText().toString();
        String pan = editTextPan.getText().toString();
        String gst = editTextGST.getText().toString();
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String emergencyAvailable = radioButton.getText().toString().toLowerCase();
        if (address.trim().length() < 10) {
            editTextAddress.setError(getString(R.string.error_invalid).concat(editTextAddress.getHint().toString()));
        } else if (cin.trim().length() > 0 && cin.trim().length() < 20) {
            editTextCIN.setError(getString(R.string.error_invalid).concat(editTextCIN.getHint().toString()));
        } else if (pan.trim().length() > 0 && pan.trim().length() < 10) {
            editTextPan.setError(getString(R.string.error_invalid).concat(editTextPan.getHint().toString()));
        } else if (gst.trim().length() > 0 && gst.trim().length() < 15) {
            editTextGST.setError(getString(R.string.error_invalid).concat(editTextGST.getHint().toString()));
        } else if (hospitalType == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.activity_hospital_create_profile_type)), Toast.LENGTH_LONG).show();
        else if (noOfDoctors == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.activity_hospital_create_profile_no_of_doctors)), Toast.LENGTH_LONG).show();
        else if (speciality == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.profile_speciality)), Toast.LENGTH_LONG).show();
        else
            putProfile(token, address, cin, pan, gst, hospitalType, noOfDoctors, speciality, emergencyAvailable);

    }

    private void putProfile(String token, String address, String cin, String pan, String gst, String type,
                            String noOfDoctors, String speciality, String emergencyAvailable) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<JSONObject> signInCall = sInterface.putHospitalProfile(token, address, cin, pan, gst, type, noOfDoctors,
                speciality, emergencyAvailable);
        signInCall.enqueue(new retrofit2.Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    startActivity(new Intent(HospitalCreateProfileActivity.this, ComingSoonActivity.class));
                    finish();
                } else {
                    String message;
                    ErrorResponse error = ErrorUtils.parseError(response);
                    message = (error == null) || (error.getMsg() == null) ? getString(R.string.something_went_wrong) : error.getMsg();
                    Toast.makeText(HospitalCreateProfileActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(HospitalCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProfile(String token) {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<HospitalProfileResponse> signInCall = sInterface.getHospitalProfile(token);
        signInCall.enqueue(new retrofit2.Callback<HospitalProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<HospitalProfileResponse> call, @NonNull Response<HospitalProfileResponse> response) {
                if (response.isSuccessful()) {
                    HospitalProfileResponse user = response.body();
                    if(user != null) {
                        editTextName.setText(user.getName());
                        editTextEmail.setText(user.getEmail());
                        String phone = user.getPhone();
                        if(phone != null) {
                            editTextPhone.setText(user.getPhone());
                            editTextPhone.setEnabled(false);
                        }
                    }
                } else
                    Toast.makeText(HospitalCreateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<HospitalProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(HospitalCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSpecialities() {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<String[]> signInCall = sInterface.getHospitalSpecialities();
        signInCall.enqueue(new retrofit2.Callback<String[]>() {
            @Override
            public void onResponse(@NonNull Call<String[]> call, @NonNull Response<String[]> response) {
                if (response.isSuccessful()) {
                    String[] specialities = response.body();
                    setupSpinnerSpeciality(specialities);
                } else
                    Toast.makeText(HospitalCreateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<String[]> call, @NonNull Throwable t) {
                Toast.makeText(HospitalCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

}
