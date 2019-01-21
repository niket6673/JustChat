package com.example.niket.chatapplication.pojoClass;


public class SignupModelClass {
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String firebaseTokenID;

    public String getFirebaseTokenID() {
        return firebaseTokenID;
    }

    public void setFirebaseTokenID(String firebaseTokenID) {
        this.firebaseTokenID = firebaseTokenID;
    }

    private String userID;
    private String Email;
    private String password;
    private String userImage;
    private String username;
    private boolean isConnectedWithPartner;

    public boolean isConnectedWithPartner() {
        return isConnectedWithPartner;
    }

    public void setConnectedWithPartner(boolean connectedWithPartner) {
        isConnectedWithPartner = connectedWithPartner;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
