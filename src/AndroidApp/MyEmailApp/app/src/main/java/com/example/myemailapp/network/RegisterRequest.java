package com.example.myemailapp.network;

// com/example/myemailapp/model/RegisterRequest.java
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    private String birthDate;
    private String gender;
    private String profilePic;  // URL or null

    public RegisterRequest(String firstName, String lastName,
                           String username, String password,
                           String phoneNumber, String birthDate,
                           String gender, String profilePic) {
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.username    = username;
        this.password    = password;
        this.phoneNumber = phoneNumber;
        this.birthDate   = birthDate;
        this.gender      = gender;
        this.profilePic  = profilePic;
    }

    // --- getters & setters (or use Lombok/DataBinding) ---
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBirthDate()   { return birthDate; }
    public String getGender()      { return gender; }
    public String getProfilePic()  { return profilePic; }

    public void setFirstName(String f)   { this.firstName = f; }
    public void setLastName(String l)    { this.lastName  = l; }
    public void setUsername(String u)    { this.username  = u; }
    public void setPassword(String p)    { this.password  = p; }
    public void setPhoneNumber(String p) { this.phoneNumber = p; }
    public void setBirthDate(String d)   { this.birthDate  = d; }
    public void setGender(String g)      { this.gender     = g; }
    public void setProfilePic(String u)  { this.profilePic = u; }
}
