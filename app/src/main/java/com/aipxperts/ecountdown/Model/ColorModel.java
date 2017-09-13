package com.aipxperts.ecountdown.Model;

/**
 * Created by aipxperts on 14/2/17.
 */

public class ColorModel {

    public String name;
    public String background;
    public String color;
    boolean isClick=false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
