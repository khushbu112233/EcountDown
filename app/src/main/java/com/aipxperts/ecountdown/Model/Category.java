package com.aipxperts.ecountdown.Model;

import io.realm.RealmObject;

/**
 * Created by aipxperts-ubuntu-01 on 4/9/17.
 */

public class Category extends RealmObject {

    String CategoryName;
    String CategoryId;
    String CategoryColor;
    String isNew;
    String selected;

    public boolean isclick() {
        return isclick;
    }

    public void setIsclick(boolean isclick) {
        this.isclick = isclick;
    }

    boolean isclick=false;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryColor() {
        return CategoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        CategoryColor = categoryColor;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }



}
