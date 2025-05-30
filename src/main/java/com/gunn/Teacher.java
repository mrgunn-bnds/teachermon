package com.gunn;

public class Teacher {
    private String name;
    private String img;

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Teacher() {
    }
}
