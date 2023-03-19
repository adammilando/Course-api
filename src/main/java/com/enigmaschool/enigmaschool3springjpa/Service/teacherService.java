package com.enigmaschool.enigmaschool3springjpa.Service;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import com.enigmaschool.enigmaschool3springjpa.Repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class teacherService{
    @Autowired
    private TeacherRepository teacherRepository;

    public Page<Teacher> getAll(Pageable pageable) {
        try {
            List<Teacher> teachers = teacherRepository.findAll();
            if (teachers.isEmpty()){
                throw new NotFoundException("Database Empty");
            }
            Page<Teacher> teacherPage = teacherRepository.findAll(pageable);
            if (teacherPage.isEmpty()){
                throw new NotFoundException("Wrong Page Size");
            }
            return teacherPage;
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Teacher create(Teacher teacher) {
        try {
            List<Teacher> teacherList= teacherRepository.findAll();
            if (teacherList.size()> 9){
                throw new MaxDataException("Student",9);
            }
            if (teacherList.stream().anyMatch(email ->
                    email.getEmail().equalsIgnoreCase(teacher.getEmail()))){
                throw new DuplicateDataException("email taken");
            }
            return teacherRepository.save(teacher);
        }catch (MaxDataException | DuplicateDataException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Teacher> createBulk(List<Teacher> teachers) {
        try {
            if (teachers.size() >= 25){
                throw new MaxDataException("Student", 25);
            }
            if (teachers.stream().map(Teacher::getEmail)
                    .distinct()
                    .count() < teachers.size()) {
                throw new DuplicateDataException("Duplicate email found");
            }
            List<Teacher> existingStudents = teacherRepository.findAll();
            if (existingStudents.stream().anyMatch(student ->
                    student.getEmail().equalsIgnoreCase(student.getEmail()))) {
                throw new DuplicateDataException("Student email already exists");
            }
            return teacherRepository.saveAll(teachers);
        }catch (DuplicateDataException | MaxDataException  e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Teacher> findById(Integer id) {
        try {
            Optional<Teacher> teachers = teacherRepository.findById(id);
            if (teachers.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            return teachers;
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void update(Teacher teacher, Integer id) {
        try {
            Optional<Teacher> teacherUpdate = teacherRepository.findById(id);
            if (teacherUpdate.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            List<Teacher> teachers = teacherRepository.findAll();
            if (teachers.stream().anyMatch(existingTeacher ->
                    existingTeacher.getEmail().equalsIgnoreCase(teacher.getEmail()))) {
                throw new DuplicateDataException("Student email already exists " + teacher.getEmail());
            }
          Teacher existing = teacherUpdate.get();
            existing.setFirstName(teacher.getFirstName());
            existing.setEmail(teacher.getEmail());
            existing.setLastName(teacher.getLastName());
            teacherRepository.save(existing);
        }catch (NotFoundException| DuplicateDataException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer id) {
        try {
            boolean teacherExists = teacherRepository.existsById(id);
            if (!teacherExists){
                throw new NotFoundException(id + " Not found");
            }
            teacherRepository.deleteById(id);
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
