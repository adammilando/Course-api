package com.enigmaschool.enigmaschool3springjpa.Repository;

import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Page<Student>> findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);

//    Page<Student> findStudentsByEmail(String email);
}