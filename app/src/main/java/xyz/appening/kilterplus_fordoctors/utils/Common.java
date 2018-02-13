package xyz.appening.kilterplus_fordoctors.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.linkedin.platform.utils.Scope;

/**
 * Created by salildhawan on 26/01/18.
 */

public class Common {

    public static final String SHARED_PREF = "KilterPlusForDoctorsPreferences";
    public static final String API_TOKEN = "apiToken";
    public static final String USER_TYPE = "userType";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(String phone) {
        String PHONE_PATTERN = "^[6789]\\d{9}$";
        return phone.matches(PHONE_PATTERN);
    }

    public static Scope linkedInBuildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

}
