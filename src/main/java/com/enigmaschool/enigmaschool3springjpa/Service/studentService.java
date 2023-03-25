package com.enigmaschool.enigmaschool3springjpa.Service;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Repository.StudentRepository;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class studentService {
    @Autowired
    private StudentRepository studentRepository;

    private final int DB_MAX_DATA = 25;

    public Page<Student> getAll(Pageable pageable) {
        try {
            List<Student> students = studentRepository.findAll();
            if (students.isEmpty()){
                throw new NotFoundException("Database Empty");
            }
            Page<Student> studentPage = studentRepository.findAll(pageable);
            if (studentPage.isEmpty()){
                throw new NotFoundException("Wrong Page Size");
            }
            return studentPage;
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public Student create(Student student) {
        try {
//            Long count = studentRepository.count();
            if (studentRepository.findAll().size() >= DB_MAX_DATA){
                throw new MaxDataException("Student", DB_MAX_DATA);
            }
            List<Student> students = studentRepository.findAll();
            if (students.stream().anyMatch(existingTeacher ->
                    existingTeacher.getEmail().equalsIgnoreCase(student.getEmail()))) {
                throw new DataIntegrityViolationException("Teacher email already exists " + student.getEmail());
            }
            return studentRepository.save(student);
        }catch (MaxDataException | DataIntegrityViolationException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Student> createBulk(List<Student> students) {
        try {
            List<Student> studentList = studentRepository.findAll();
            if (studentList.size()+students.size() >= DB_MAX_DATA){
                throw new MaxDataException("Student", DB_MAX_DATA);
            }
            for (Student student : students) {
                Set<String> emailSet = students.stream().map(Student::getEmail).collect(Collectors.toSet());
                if (emailSet.size() != students.size()) {
                    throw new DataIntegrityViolationException("Duplicate email found in bulk data " + student.getEmail());
                }
                if (studentList.stream().anyMatch(existingStudent -> existingStudent.getEmail().equalsIgnoreCase(student.getEmail()))) {
                    throw new DataIntegrityViolationException("Student email already exists " + student.getEmail());
                }
            }
            return studentRepository.saveAll(students);
        }catch (DataIntegrityViolationException | MaxDataException  e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Student> findById(Integer id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()){
                throw new NotFoundException("Student With " + id + " Not Exists");
            }
            return student;
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void update(Student student, Integer id) {
        try {
            Optional<Student> studentUpdate = studentRepository.findById(id);
            if (studentUpdate.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            List<Student> students = studentRepository.findAll();
            if (students.stream().anyMatch(existingTeacher ->
                    existingTeacher.getEmail().equalsIgnoreCase(student.getEmail()))) {
                throw new DataIntegrityViolationException("Teacher email already exists " + student.getEmail());
            }
            Student existingStudent = studentUpdate.get();
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setEmail(student.getEmail());
            existingStudent.setLastName(student.getLastName());
            studentRepository.save(existingStudent);
        }catch (NotFoundException | DataIntegrityViolationException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer id) {
        try {
            boolean studentDelete = studentRepository.existsById(id);
            if (!studentDelete){
                throw new NotFoundException(id + " Not found");
            }
            studentRepository.deleteById(id);
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Page<Student>> fingByName(String firstName, String lastName, Pageable pageable){
       try {
           Optional<Page<Student>>student = studentRepository.findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(firstName,lastName, pageable);
           if (student.isEmpty()){
               throw new NotFoundException("Cannot Find Student");
           }
           return student;
       }catch (NotFoundException e){
           throw e;
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }
}
