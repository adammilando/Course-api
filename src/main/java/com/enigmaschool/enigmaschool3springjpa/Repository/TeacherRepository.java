package com.enigmaschool.enigmaschool3springjpa.Repository;

import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

}