package com.example.version01;


public class UserResponse {
    private String status;
    private String fullname;
    private String height;
    private String weight;
    private String age;
    private String location;
    private String job;
    private String diseaseRecords;
    private String hobby;
    private String gender;
    private String message;

    // افزودن گتر و ستر برای هر فیلد
    public String getStatus() { return status; }
    public String getFullname() { return fullname; }
    public String getHeight() { return height; }
    public String getWeight() { return weight; }
    public String getAge() { return age; }
    public String getLocation() { return location; }
    public String getJob() { return job; }
    public String getDiseaseRecords() { return diseaseRecords; }
    public String getHobby() { return hobby; }
    public String getGender() { return gender; }
    public String getMessage() { return message; }
}
