package com.vinodnarwade.eduquiz;

public class HelperClass {

    String name, userName, emailId, phoneNumber, password, roleIs, userId, parentEmailId, parentPhoneNumber;
    String className;

    public HelperClass() {
    }

    public HelperClass(
            String userId,
            String emailId,
            String parentEmailId,
            String name,
            String password,
            String phoneNumber,
            String parentPhoneNumber,
            String userName,
            String roleIs,
            String className) {

        this.userId = userId;
        this.emailId = emailId;
        this.parentEmailId = parentEmailId;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.parentPhoneNumber = parentPhoneNumber;
        this.userName = userName;
        this.roleIs = roleIs;
        this.className = className;
    }

    public String getRoleIs() {
        return roleIs;
    }

    public void setRoleIs(String roleIs) {
        this.roleIs = roleIs;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getParentEmailId() {
        return emailId;
    }

    public void setParentEmailId(String emailId) {
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
    public String getParentPhoneNumber() {
        return phoneNumber;
    }

    public void setParentPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}