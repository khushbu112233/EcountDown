package com.aipxperts.ecountdown.Model;

import io.realm.RealmObject;

/**
 * Created by aipxperts-ubuntu-01 on 21/6/17.
 */

public class UProfile extends RealmObject {

    String U_id;
    String U_Name;
    String U_Email;
    String U_MobileNo;
    String U_image;
    String U_Password;
    String U_Gender;
    String Age;

    public String getU_Password() {
        return U_Password;
    }

    public void setU_Password(String u_Password) {
        U_Password = u_Password;
    }

    public String getU_id() {
        return U_id;
    }

    public void setU_id(String u_id) {
        U_id = u_id;
    }

    public String getU_Name() {
        return U_Name;
    }

    public void setU_Name(String u_Name) {
        U_Name = u_Name;
    }

    public String getU_Email() {
        return U_Email;
    }

    public void setU_Email(String u_Email) {
        U_Email = u_Email;
    }

    public String getU_MobileNo() {
        return U_MobileNo;
    }

    public void setU_MobileNo(String u_MobileNo) {
        U_MobileNo = u_MobileNo;
    }

    public String getU_image() {
        return U_image;
    }

    public void setU_image(String u_image) {
        U_image = u_image;
    }


    public String getU_Gender() {
        return U_Gender;
    }

    public void setU_Gender(String u_Gender) {
        U_Gender = u_Gender;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }
}
