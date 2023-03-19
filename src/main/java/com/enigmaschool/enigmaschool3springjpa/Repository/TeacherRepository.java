package com.enigmaschool.enigmaschool3springjpa.Repository;

import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<List<Teacher>> findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String firstName, String lastName);
    Teacher findByEmailIgnoreCase(String email);

}