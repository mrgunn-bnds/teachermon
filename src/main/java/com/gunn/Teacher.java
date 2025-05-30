package com.gunn;

/**
 * A Teacher is used to store Teacher information such as name and image filepath (img)
 *
 * @see /data/teachers.json
 */
public class Teacher {
    private String name;
    private String img;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public Teacher() {
    }
}
