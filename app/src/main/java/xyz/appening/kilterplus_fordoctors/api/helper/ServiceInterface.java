package xyz.appening.kilterplus_fordoctors.api.helper;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import xyz.appening.kilterplus_fordoctors.api.model.DoctorProfileResponse;
import xyz.appening.kilterplus_fordoctors.api.model.HospitalProfileResponse;
import xyz.appening.kilterplus_fordoctors.api.model.SignInResponse;

/**
 * Created by salildhawan on 26/01/18.
 */

public interface ServiceInterface {

    @FormUrlEncoded
    @POST("v1/auth/signin")
    Call<SignInResponse> postSigninDetails(@Field("email") CharSequence email, @Field("password") CharSequence password);

    @FormUrlEncoded
    @POST("v1/auth/signup")
    Call<SignInResponse> postSignUpDetails(@Field("name") CharSequence name, @Field("email") CharSequence email, @Field("phone") String phone,
                                           @Field("password") CharSequence password, @Field("user_type") CharSequence userType);

    @GET("v1/static/qualifications")
    Call<String[]> getQualifications();

    @GET("v1/static/speciality/doctor")
    Call<String[]> getDoctorSpecialities();

    @GET("v1/static/speciality/hospital")
    Call<String[]> getHospitalSpecialities();

    @FormUrlEncoded
    @PUT("v1/profile/update")
    Call<JSONObject> putDoctorProfile(@Header("x-auth-token") String token, @Field("phone") String phone, @Field("address") String address, @Field("aadhaar") String aadhaar, @Field("doc_reg_no") String doc_reg_no,
                                      @Field("pan_no") String pan, @Field("gst_no") String gst, @Field("qualification") String qualification,
                                      @Field("speciality") String speciality, @Field("experience") String experience,
                                      @Field("availability") String availability, @Field("type_of_consultation") List<String> type_of_consultation);

    @FormUrlEncoded
    @PUT("v1/profile/update")
    Call<JSONObject> putHospitalProfile(@Header("x-auth-token") String token, @Field("address") String address, @Field("cin_no") String cin_no,
                                    @Field("pan_no") String pan, @Field("gst_no") String gst, @Field("type") String hospitalType,
                                    @Field("no_of_doctors") String no_of_doctors, @Field("speciality") String speciality,
                                    @Field("emergency_available") String emergency_available);

    @GET("v1/profile")
    Call<DoctorProfileResponse> getDoctorProfile(@Header("x-auth-token") String token);

    @GET("v1/profile")
    Call<HospitalProfileResponse> getHospitalProfile(@Header("x-auth-token") String token);

    @FormUrlEncoded
    @POST("v1/auth/facebook")
    Call<SignInResponse> postFbLogin(@Field("access_token") String token, @Field("user_type") String userType);

    @FormUrlEncoded
    @POST("v1/auth/linkedin")
    Call<SignInResponse> postLinkedInLogin(@Field("access_token") String token, @Field("user_type") String userType);

}
