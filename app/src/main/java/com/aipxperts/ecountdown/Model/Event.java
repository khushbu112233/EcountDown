package com.aipxperts.ecountdown.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;

import static android.R.attr.name;

/**
 * Created by aipxperts-ubuntu-01 on 13/6/17.
 */

public class Event extends RealmObject implements Parcelable,Serializable{

    String _token;
    String event_uuid;
    String event_name;
    String event_description;
    String event_image="";
    String is_cover;
    String date;
    String end_date;
    String isComplete="0";
    String created_date;
    String modified_date;
    String operation="1";
    String Category;
    String CategoryColor;
    String isFrom = "";

    private static final long serialVersionUID = -29238982928391L;
    public String getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(String isFrom) {
        this.isFrom = isFrom;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCategoryColor() {
        return CategoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        CategoryColor = categoryColor;
    }
    public String get_token() {
        return _token;
    }

    public void set_token(String _token) {
        this._token = _token;
    }

    public String getEvent_uuid() {
        return event_uuid;
    }

    public void setEvent_uuid(String event_uuid) {
        this.event_uuid = event_uuid;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public String getIs_cover() {
        return is_cover;
    }

    public void setIs_cover(String is_cover) {
        this.is_cover = is_cover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public  Event(String event_uuid,String event_name,String date,String category,String categoryColor){
        this.event_uuid = event_uuid;
        this.event_name = event_name;
        this.date = date;
        this.Category = category;
        this.CategoryColor = categoryColor;
    }
    public Event(){

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
    @Override
    public String toString() {
        return new StringBuffer(" Id : ").append(this.event_uuid)
                .append(" Name : ").append(this.event_name)
                .append(" Description : ").append(this.event_description)
                .append(" Image : ").append(this.event_image)
                .append(" Date : ").append(this.date)
                .append(" Category : ").append(this.Category)
                .append(" CategoryColor : ").append(this.CategoryColor)
                .toString();
    }

  /*  @Override
    public String toString() {
        return "Id:" + event_uuid + "\nName: " + event_name + "\nDate: " + date+ "\nCategory: " + Category+ "\nCategoryColor: " + CategoryColor;
    }*/
}
