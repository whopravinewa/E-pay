package com.example.pravinewa.markii;

public class User {
    public String fullname, email, mobnumber, address;

    public User(){

    }
    public User(String fullname, String email, String mobnumber, String address) {
        this.fullname = fullname;
        this.email = email;
        this.mobnumber = mobnumber;
        this.address = address;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getMobnumber() {
        return mobnumber;
    }

    public String getAddress() {
        return address;
    }
}
