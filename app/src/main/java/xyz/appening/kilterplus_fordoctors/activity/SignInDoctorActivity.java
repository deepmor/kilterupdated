package xyz.appening.kilterplus_fordoctors.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.R;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceGenerator;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceInterface;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;
import xyz.appening.kilterplus_fordoctors.utils.Common;

public class SignInDoctorActivity extends AppCompatActivity implements AppCompatCheckBox.OnCheckedChangeListener {

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText passwordEditText;
    @BindView(R.id.checkBoxSignInDoctor)
    AppCompatCheckBox checkBox;
    @BindView(R.id.buttonSignInDoctor)
    Button signInButton;
    CallbackManager callbackManager;
    private static final String USER_TYPE = "doctor";
    private static final String TAG = "SignInDoctorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_doctor);
        ButterKnife.bind(this);
        checkBox.setOnCheckedChangeListener(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        getFbLoginResponse(AccessToken.getCurrentAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignInDoctorActivity.this, R.string.fb_cancelled, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );
    }

    private void getSignInResponse(CharSequence email, CharSequence password) {
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
                    saveToken(token, user);
                    if(user.isProfileCreated())
                        startActivity(new Intent(SignInDoctorActivity.this, ComingSoonActivity.class));
                    else
                        startCreateProfileActivity(user);
                    finish();
                } else {
                    String message;
                    if(response.code() == 401)
                        message = getString(R.string.error_unauthorized);
                    else
                        message = getString(R.string.something_went_wrong);
                    Toast.makeText(SignInDoctorActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignInDoctorActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLinkedInResponse(String token) {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<SignInResponse> signInCall = sInterface.postLinkedInLogin(token, USER_TYPE);
        signInCall.enqueue(new retrofit2.Callback<SignInResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignInResponse> call, @NonNull Response<SignInResponse> response) {
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
                    saveToken(token, user);
                    if(user.isProfileCreated())
                        startActivity(new Intent(SignInDoctorActivity.this, ComingSoonActivity.class));
                    else
                        startCreateProfileActivity(user);
                    finish();
                } else
                    Toast.makeText(SignInDoctorActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                Toast.makeText(SignInDoctorActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getFbLoginResponse(String token) {
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<SignInResponse> signInCall = sInterface.postFbLogin(token, USER_TYPE);
        signInCall.enqueue(new retrofit2.Callback<SignInResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignInResponse> call, @NonNull Response<SignInResponse> response) {
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
                    saveToken(token, user);
                    if(user.isProfileCreated())
                        startActivity(new Intent(SignInDoctorActivity.this, ComingSoonActivity.class));
                    else
                        startCreateProfileActivity(user);
                    finish();
                } else
                    Toast.makeText(SignInDoctorActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                Toast.makeText(SignInDoctorActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onDoctorSignUpClick(View view) {
        onBackPressed();
    }

    public void onDoctorSignInClick(View view) {
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
            getSignInResponse(email, password);
    }

    public void onLinkedInLoginClick(View view) {
        LISessionManager.getInstance(this).clearSession();
        LISessionManager.getInstance(getApplicationContext())
                .init(this, Common.linkedInBuildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        getLinkedInResponse(LISessionManager
                                .getInstance(getApplicationContext())
                                .getSession().getAccessToken().getValue());
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Log.d(TAG, error.toString());
                        String errorMessage;
                        try {
                            JSONObject jsonObject = new JSONObject(error.toString());
                            errorMessage = jsonObject.getString("errorMessage");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMessage = "Failed";
                        }
                        Toast.makeText(getApplicationContext(), errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                }, true);
    }

    public void onFacebookLoginClick(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("email"));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            passwordEditText.setVisibility(View.GONE);
        else
            passwordEditText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext())
                .onActivityResult(this,
                        requestCode, resultCode, data);
    }

    private void saveToken(String token, SignInResponse.User user){
        SharedPreferences sharedPreferences = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Common.API_TOKEN, token);
        editor.putString(Common.USER_TYPE, user.getUserType());
        editor.apply();
    }

    private void startCreateProfileActivity(SignInResponse.User user) {
        Gson gson = new Gson();
        String userString = gson.toJson(user, SignInResponse.User.class);
        Intent intent = new Intent(SignInDoctorActivity.this, DoctorCreateProfileActivity.class);
        intent.putExtra(DoctorCreateProfileActivity.USER, userString);
        startActivity(intent);
    }

    private void failEarly() {
        Toast.makeText(SignInDoctorActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
    }

}
