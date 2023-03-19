package com.enigmaschool.enigmaschool3springjpa.Service;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Repository.StudentRepository;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class studentService {
    @Autowired
    private StudentRepository studentRepository;

    public Page<Student> getAll(Pageable pageable) {
        try{
            List<Student> students = studentRepository.findAll();
            if (students.isEmpty()){
                throw new NotFoundException("Database Empty");
            }
            Page<Student> studentsPage = studentRepository.findAll(pageable);
            if (studentsPage.isEmpty()){
                throw new NotFoundException("Wrong page Size");
            }
            return studentsPage;
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }


    public Student create(Student student) {
        try {
            Long count = studentRepository.count();
            if (count >= 25){
                throw new MaxDataException("Student",25);
            }
//            if (studentList.stream().anyMatch(email ->
//                    email.getEmail().equalsIgnoreCase(student.getEmail()))){
//                throw new DuplicateDataException("email taken");
//            }
            return studentRepository.save(student);
        }catch (MaxDataException | DuplicateDataException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Student> createBulk(List<Student> students) {
        try {
            List<Student> studentList = studentRepository.findAll();
            if (studentList.size()+students.size() >= 25){
                throw new MaxDataException("Student", 25);
            }
//            if (students.stream().map(Student::getEmail)
//                    .distinct()
//                    .count() < students.size()) {
//                throw new DuplicateDataException("Duplicate email found");
//            }
//            List<Student> existingStudents = studentRepository.findAll();
//            if (existingStudents.stream().anyMatch(s ->
//                    s.getEmail().equalsIgnoreCase(s.getEmail()))) {
//                throw new DuplicateDataException("Student email already exists");
//            }
            return studentRepository.saveAll(students);
        }catch (DuplicateDataException | MaxDataException  e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Student> findById(Integer id) {
        try {
            Optional<Student> students = studentRepository.findById(id);
            if (students.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            return students;
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
//            List<Student> students = studentRepository.findAll();
//            if (students.stream().anyMatch(existingStudent ->
//                    existingStudent.getEmail().equalsIgnoreCase(student.getEmail()))) {
//                throw new DuplicateDataException("Student email already exists " + student.getEmail());
//            }
            Student existingStudent = studentUpdate.get();
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setEmail(student.getEmail());
            existingStudent.setLastName(student.getLastName());
            studentRepository.save(existingStudent);
        }catch (NotFoundException| DuplicateDataException e){
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

    public Optional<List<Student>> fingByName(String firstName, String lastName){
       try {
           Optional<List<Student>>student = studentRepository.findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(firstName,lastName);
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
