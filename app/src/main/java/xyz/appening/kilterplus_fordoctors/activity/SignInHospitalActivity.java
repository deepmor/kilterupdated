package xyz.appening.kilterplus_fordoctors.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.R;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceGenerator;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceInterface;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;
import xyz.appening.kilterplus_fordoctors.utils.Common;

public class SignInHospitalActivity extends AppCompatActivity implements AppCompatCheckBox.OnCheckedChangeListener {

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText passwordEditText;
    @BindView(R.id.checkBoxSignInHospital)
    AppCompatCheckBox checkBox;
    @BindView(R.id.buttonSignInHospital)
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_hospital);
        ButterKnife.bind(this);
        checkBox.setOnCheckedChangeListener(this);
    }

    public void onHospitalSignInClick(View view) {
        if (!Common.isOnline(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        CharSequence email = editTextEmail.getText();
        CharSequence password = passwordEditText.getText();
        if(!Common.isValidEmail(email))
            editTextEmail.setError(getString(R.string.error_invalid_email));
        else if(password.length() < 5)
            passwordEditText.setError(getString(R.string.error_invalid_password));
        else
            getSigninResponse(email, password);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
            passwordEditText.setVisibility(View.GONE);
        else
            passwordEditText.setVisibility(View.VISIBLE);
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onHospitalSignUpClick(View view) {
        onBackPressed();
    }

    private void getSigninResponse(CharSequence email, CharSequence password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<SignInResponse> signInCall = sInterface.postSigninDetails(email, password);
        signInCall.enqueue(new retrofit2.Callback<SignInResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignInResponse> call, @NonNull Response<SignInResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String token = response.headers().get("x-auth-token");
                    if (token == null) {
                        failEarly();
                        return;
                    }
                    SignInResponse signInResponse = response.body();
                    if(signInResponse == null) {
                        failEarly();
                        return;
                    }
                    SignInResponse.User user = signInResponse.getUser();
                    if(user == null) {
                        failEarly();
                        return;
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Common.API_TOKEN, token);
                    editor.putString(Common.USER_TYPE, user.getUserType());
                    editor.apply();
                    if(user.isProfileCreated())
                        startActivity(new Intent(SignInHospitalActivity.this, ComingSoonActivity.class));
                    else {
                        Gson gson = new Gson();
                        String userString = gson.toJson(user, SignInResponse.User.class);
                        Intent intent = new Intent(SignInHospitalActivity.this, DoctorCreateProfileActivity.class);
                        intent.putExtra(DoctorCreateProfileActivity.USER, userString);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    String message;
                    if(response.code() == 401)
                        message = getString(R.string.error_unauthorized);
                    else
                        message = getString(R.string.something_went_wrong);
                    Toast.makeText(SignInHospitalActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignInHospitalActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void failEarly() {
        Toast.makeText(SignInHospitalActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
    }
    
}
