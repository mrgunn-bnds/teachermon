package com.gunn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunn.models.Teacher;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the repository of all teachers.  It is populated from the /data/teachers.json file.
 * It is following the Singleton design pattern.
 *
 * @author Daniel Gunn
 */
public class TeacherRepository {
    private static TeacherRepository instance = null;
    private Teacher[] teachers = null;

    /**
     * Create an new TeacherRepository instance.  It is private to manage instances in this class.
     * We are following the SingleTon design pattern to make sure we only have one instance of this class globally.
     *
     * @throws IOException
     */
    private TeacherRepository() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = TeacherMonServer.class.getResourceAsStream("/data/teachers.json");
        teachers = mapper.readValue(is, Teacher[].class);
        // for now, we will just use the teacher index in this array as their id
        for (int id = 0; id < teachers.length; id++) {
            teachers[id].setId(id);
        }
    }

    /**
     * This follows the SingleTon pattern, instead of a constructor we should call this method.
     * It will return the one and only instance of this TeacherRepository.
     * The reason im concerned about this is because, I dont want this called many times for each
     * user.
     *
     * @return the one and only instance of this class
     */
    public static synchronized TeacherRepository getInstance() throws IOException {
        if (instance == null) {
            instance = new TeacherRepository();
        }
        return instance;
    }

    private int getRandomTeacherID() {
        return (int) (Math.random()*teachers.length);
    }

    /**
     * Give me a random Teacher from the known set of teachers
     *
     * @return a Teacher object
     */
    public Teacher getRandomTeacher() {
        Teacher r = teachers[getRandomTeacherID()];
        return r;
    }

    /**
     * Get a random Teacher for this teacher to fight
     *
     * @param t the given teacher
     * @return a different teacher from t
     */
    public Teacher getRandomOpponent(Teacher t) {
        int rid = this.getRandomTeacherID();
        if (rid == t.getId()) {
            rid++;
            rid %= teachers.length;
        }
        return teachers[rid];
    }

    /**
     * Return the Teacher with the given id
     *
     * @param id the unique identifier of the Teacher
     * @return the given Teacher
     */
    public Teacher getById(int id) {
        if ((id <0 ) || (id > teachers.length))
            throw new ArrayIndexOutOfBoundsException(id);

        return teachers[id];
    }
}
