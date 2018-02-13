package xyz.appening.kilterplus_fordoctors.api.model;

/**
 * Created by salildhawan on 28/01/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HospitalProfileResponse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("cin_no")
    @Expose
    private String cinNo;
    @SerializedName("emergency_available")
    @Expose
    private String emergencyAvailable;
    @SerializedName("gst_no")
    @Expose
    private String gstNo;
    @SerializedName("no_of_doctors")
    @Expose
    private String noOfDoctors;
    @SerializedName("pan_no")
    @Expose
    private String panNo;
    @SerializedName("speciality")
    @Expose
    private String speciality;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type_of_consultation")
    @Expose
    private List<String> typeOfConsultation = null;
    @SerializedName("profile_created")
    @Expose
    private Boolean profileCreated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCinNo() {
        return cinNo;
    }

    public void setCinNo(String cinNo) {
        this.cinNo = cinNo;
    }

    public String getEmergencyAvailable() {
        return emergencyAvailable;
    }

    public void setEmergencyAvailable(String emergencyAvailable) {
        this.emergencyAvailable = emergencyAvailable;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getNoOfDoctors() {
        return noOfDoctors;
    }

    public void setNoOfDoctors(String noOfDoctors) {
        this.noOfDoctors = noOfDoctors;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTypeOfConsultation() {
        return typeOfConsultation;
    }

    public void setTypeOfConsultation(List<String> typeOfConsultation) {
        this.typeOfConsultation = typeOfConsultation;
    }

    public Boolean getProfileCreated() {
        return profileCreated;
    }

    public void setProfileCreated(Boolean profileCreated) {
        this.profileCreated = profileCreated;
    }

}
