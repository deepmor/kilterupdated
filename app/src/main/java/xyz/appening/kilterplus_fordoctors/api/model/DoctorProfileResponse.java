package xyz.appening.kilterplus_fordoctors.api.model;

/**
 * Created by salildhawan on 28/01/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoctorProfileResponse {

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
    @SerializedName("availability")
    @Expose
    private String availability;
    @SerializedName("doc_reg_no")
    @Expose
    private String docRegNo;
    @SerializedName("experience")
    @Expose
    private String experience;
    @SerializedName("speciality")
    @Expose
    private String speciality;
    @SerializedName("gst_no")
    @Expose
    private String gstNo;
    @SerializedName("pan_no")
    @Expose
    private String panNo;
    @SerializedName("qualification")
    @Expose
    private String qualification;
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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDocRegNo() {
        return docRegNo;
    }

    public void setDocRegNo(String docRegNo) {
        this.docRegNo = docRegNo;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
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
