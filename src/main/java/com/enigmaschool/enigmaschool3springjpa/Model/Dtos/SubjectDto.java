package com.enigmaschool.enigmaschool3springjpa.Model.Dtos;

import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Set;

public class SubjectDto {
    @NotBlank(message = "Subject name Cannot be blank")
    private String name;

    private List<Student> students;

    private Teacher teacher;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
