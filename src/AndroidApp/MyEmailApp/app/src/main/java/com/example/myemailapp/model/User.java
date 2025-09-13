package com.example.myemailapp.model;
import org.bson.types.ObjectId;

public class User {
    //private ObjectId id;
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String phoneNumber;
    private String birthDate;
    private String gender;

    // Getters
    //public ObjectId getId() { return id; }
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProfilePic() { return profilePic; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
}
