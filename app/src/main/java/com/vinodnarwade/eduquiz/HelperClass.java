package com.vinodnarwade.eduquiz;

public class HelperClass {
    String name,userName,emailId,phoneNumber,password;

    public HelperClass() {
    }

    public HelperClass(String emailId, String name, String password, String phoneNumber, String userName) {
        this.emailId = emailId;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
