package com.enigmaschool.enigmaschool3springjpa.Repository;

import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Page<Subject>> findByStudents_FirstNameContainsIgnoreCaseAndStudents_LastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);
    Optional<Page<Subject>> findByTeacher_FirstNameContainsIgnoreCaseOrTeacher_LastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);
}