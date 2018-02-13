package xyz.appening.kilterplus_fordoctors.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.R;
import xyz.appening.kilterplus_fordoctors.api.helper.ErrorUtils;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceGenerator;
import xyz.appening.kilterplus_fordoctors.api.helper.ServiceInterface;
import xyz.appening.kilterplus_fordoctors.api.model.ErrorResponse;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;
import xyz.appening.kilterplus_fordoctors.utils.Common;

public class SignUpHospitalActivity extends AppCompatActivity {

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    private static final String USER_TYPE = "hospital";
    private static final String TAG = "SignUpHospital";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_hospital);
        ButterKnife.bind(this);
        SpannableString ss = new SpannableString(getString(R.string.terms_conditions));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String url = "http://www.google.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(android.R.color.black));
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 9, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkBox.setText(ss);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onHospitalSignUpClick(View view) {
        CharSequence name = editTextName.getText();
        CharSequence email = editTextEmail.getText();
        String phone = editTextPhone.getText().toString();
        CharSequence password = editTextPassword.getText();
        if (name.length() < 2)
            editTextName.setError(getString(R.string.error_invalid_name));
        else if (!Common.isValidEmail(email))
            editTextEmail.setError(getString(R.string.error_invalid_email));
        else if (!Common.isValidPhone(phone))
            editTextPhone.setError(getString(R.string.error_invalid_phone));
        else if (password.length() < 5)
            editTextPassword.setError(getString(R.string.error_invalid_password));
        else if (!checkBox.isChecked()) {
            Toast.makeText(this, R.string.terms_conditions, Toast.LENGTH_LONG).show();
        } else if (!Common.isOnline(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        } else
            getSignUpResponse(name, email, phone, password);
    }

    public void onHospitalSignUpBackClick(View view) {
        onBackPressed();
    }

    public void onHospitalSignInClick(View view) {
        startActivity(new Intent(this, SignInHospitalActivity.class));
    }

    private void getSignUpResponse(CharSequence name, CharSequence email, String phone, CharSequence password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ServiceInterface sInterface = ServiceGenerator.getService();
        Call<SignInResponse> signInCall = sInterface.postSignUpDetails(name, email, phone, password, USER_TYPE);
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
                    //TODO: Check for null when api starts sending user object
//                    if(user == null) {
//                        failEarly();
//                        return;
//                    }
                    SharedPreferences sharedPreferences = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Common.API_TOKEN, token);
//                    editor.putString(Common.USER_TYPE, user.getUserType());
                    editor.apply();
                    Gson gson = new Gson();
                    String userString = gson.toJson(user, SignInResponse.User.class);
                    Intent intent = new Intent(SignUpHospitalActivity.this, HospitalCreateProfileActivity.class);
                    intent.putExtra(HospitalCreateProfileActivity.USER, userString);
                    startActivity(intent);
                    finish();
                } else {
                    String message;
                    ErrorResponse error = ErrorUtils.parseError(response);
                    message = (error == null) || (error.getMsg() == null) ? getString(R.string.something_went_wrong) : error.getMsg();
                    Toast.makeText(SignUpHospitalActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignUpHospitalActivity.this, R.string.onFailureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void failEarly() {
        Toast.makeText(SignUpHospitalActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
    }

}
