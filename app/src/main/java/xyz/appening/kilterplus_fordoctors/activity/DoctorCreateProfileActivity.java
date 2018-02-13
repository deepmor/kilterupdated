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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.R;
import xyz.appening.kilterplus_fordoctors.api.helper.ErrorUtils;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceGenerator;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceInterface;
import xyz.appening.kilterplus_fordoctors.api.model.DoctorProfileResponse;
import xyz.appening.kilterplus_fordoctors.api.model.ErrorResponse;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;
import xyz.appening.kilterplus_fordoctors.custom.CustomSpinner;
import xyz.appening.kilterplus_fordoctors.utils.Common;

public class DoctorCreateProfileActivity extends AppCompatActivity {

    @BindView(R.id.spinnerExperience)
    CustomSpinner spinnerExperience;
    @BindView(R.id.spinnerAvailability)
    CustomSpinner spinnerAvailability;
    @BindView(R.id.spinnerQualification)
    CustomSpinner spinnerQualification;
    @BindView(R.id.spinnerSpeciality)
    CustomSpinner spinnerSpeciality;
    @BindArray(R.array.array_availability)
    String[] arrayAvailability;
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.editTextAddress)
    EditText editTextAddress;
    @BindView(R.id.editTextAadhaar)
    EditText editTextAadhaar;
    @BindView(R.id.editTextDocRegNo)
    EditText editTextDocRegNo;
    @BindView(R.id.editTextPan)
    EditText editTextPan;
    @BindView(R.id.editTextGST)
    EditText editTextGST;
    @BindView(R.id.checkBoxVideo)
    CheckBox checkBoxVideo;
    @BindView(R.id.checkBoxVoice)
    CheckBox checkBoxVoice;
    @BindView(R.id.checkBoxChat)
    CheckBox checkBoxChat;
    private String[] arrayExperience = new String[22];
    public static final String USER = "user";
    private String experience;
    private String qualification;
    private String speciality;
    private String availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_create_profile);
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
        getQualifications();
        getSpecialities();
        setupSpinnerExperience();
        setupSpinnerAvailability();
    }

    private void setupSpinnerExperience() {
        arrayExperience[0] = "0 year";
        arrayExperience[1] = "1 year";
        int i = 2;
        while (i < 21) {
            arrayExperience[i] = String.valueOf(i).concat(" years");
            i++;
        }
        arrayExperience[21] = "20+".concat(" years");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arrayExperience);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExperience.setAdapter(spinnerArrayAdapter);
        spinnerExperience.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                experience = position == -1 ? null : arrayExperience[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSpinnerAvailability() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arrayAvailability);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(spinnerArrayAdapter);
        spinnerAvailability.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                availability = position == -1 ? null : arrayAvailability[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSpinnerQualifications(final String[] array) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQualification.setAdapter(spinnerArrayAdapter);
        spinnerQualification.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                qualification = position == -1 ? null : array[position];
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        String phone = null;
        if (editTextPhone.isEnabled()) {
            phone = editTextPhone.getText().toString();
            if (!Common.isValidPhone(phone)) {
                editTextPhone.setError(getString(R.string.error_invalid_phone));
                return;
            }
        }
        String address = editTextAddress.getText().toString();
        String aadhaar = editTextAadhaar.getText().toString();
        String docRegNo = editTextDocRegNo.getText().toString();
        String pan = editTextPan.getText().toString();
        String gst = editTextGST.getText().toString();
        List<String> consultationTypes = new ArrayList<>();
        if (checkBoxVideo.isChecked())
            consultationTypes.add(checkBoxVideo.getText().toString().toLowerCase());
        if (checkBoxVoice.isChecked())
            consultationTypes.add(checkBoxVoice.getText().toString().toLowerCase());
        if (checkBoxChat.isChecked())
            consultationTypes.add(checkBoxChat.getText().toString().toLowerCase());
        if (address.trim().length() < 10) {
            editTextAddress.setError(getString(R.string.error_invalid).concat(editTextAddress.getHint().toString()));
        } else if (aadhaar.trim().length() > 0 && aadhaar.trim().length() < 12) {
            editTextAadhaar.setError(getString(R.string.error_invalid).concat(editTextAadhaar.getHint().toString()));
        } else if (docRegNo.trim().length() > 0 && docRegNo.trim().length() < 5) {
            editTextDocRegNo.setError(getString(R.string.error_invalid).concat(editTextDocRegNo.getHint().toString()));
        } else if (pan.trim().length() > 0 && pan.trim().length() < 10) {
            editTextPan.setError(getString(R.string.error_invalid).concat(editTextPan.getHint().toString()));
        } else if (gst.trim().length() > 0 && gst.trim().length() < 15) {
            editTextGST.setError(getString(R.string.error_invalid).concat(editTextGST.getHint().toString()));
        } else if (qualification == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.activity_doctor_create_profile_qualification)), Toast.LENGTH_LONG).show();
        else if (speciality == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.profile_speciality)), Toast.LENGTH_LONG).show();
        else if (experience == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.activity_doctor_create_profile_experience)), Toast.LENGTH_LONG).show();
        else if (availability == null)
            Toast.makeText(this, getString(R.string.error_select).
                    concat(getString(R.string.activity_doctor_create_profile_availability)), Toast.LENGTH_LONG).show();
        else if (consultationTypes.size() == 0) {
            Toast.makeText(this, R.string.error_no_consultation_type_selected, Toast.LENGTH_LONG).show();
        } else
            putProfile(token, phone, address, aadhaar, docRegNo, pan, gst, qualification, speciality, experience, availability, consultationTypes);
    }

    private void putProfile(String token, String phone,String address, String aadhaar, String docRegNo, String pan, String gst, String qualification,
                            String speciality, String experience, String availability, List<String> consultationTypes) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<JSONObject> signInCall = sInterface.putDoctorProfile(token, phone, address, aadhaar, docRegNo, pan, gst, qualification, speciality,
                experience, availability, consultationTypes);
        signInCall.enqueue(new retrofit2.Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    startActivity(new Intent(DoctorCreateProfileActivity.this, ComingSoonActivity.class));
                    finish();
                } else {
                    String message;
                    ErrorResponse error = ErrorUtils.parseError(response);
                    message = (error == null) || (error.getMsg() == null) ? getString(R.string.something_went_wrong) : error.getMsg();
                    Toast.makeText(DoctorCreateProfileActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DoctorCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getQualifications() {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<String[]> signInCall = sInterface.getQualifications();
        signInCall.enqueue(new retrofit2.Callback<String[]>() {
            @Override
            public void onResponse(@NonNull Call<String[]> call, @NonNull Response<String[]> response) {
                if (response.isSuccessful()) {
                    String[] qualifications = response.body();
                    setupSpinnerQualifications(qualifications);
                } else
                    Toast.makeText(DoctorCreateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<String[]> call, @NonNull Throwable t) {
                Toast.makeText(DoctorCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSpecialities() {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<String[]> signInCall = sInterface.getDoctorSpecialities();
        signInCall.enqueue(new retrofit2.Callback<String[]>() {
            @Override
            public void onResponse(@NonNull Call<String[]> call, @NonNull Response<String[]> response) {
                if (response.isSuccessful()) {
                    String[] specialities = response.body();
                    setupSpinnerSpeciality(specialities);
                } else
                    Toast.makeText(DoctorCreateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<String[]> call, @NonNull Throwable t) {
                Toast.makeText(DoctorCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProfile(String token) {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<DoctorProfileResponse> signInCall = sInterface.getDoctorProfile(token);
        signInCall.enqueue(new retrofit2.Callback<DoctorProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<DoctorProfileResponse> call, @NonNull Response<DoctorProfileResponse> response) {
                if (response.isSuccessful()) {
                    DoctorProfileResponse user = response.body();
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
                    Toast.makeText(DoctorCreateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<DoctorProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(DoctorCreateProfileActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

}
