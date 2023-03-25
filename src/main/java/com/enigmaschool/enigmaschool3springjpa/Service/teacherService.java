package com.enigmaschool.enigmaschool3springjpa.Service;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import com.enigmaschool.enigmaschool3springjpa.Repository.TeacherRepository;
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
public class teacherService{
    @Autowired
    private TeacherRepository teacherRepository;

    private final int DB_MAX_DATA = 9;
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
            if (teacherRepository.findAll().size() >= DB_MAX_DATA){
                throw new MaxDataException("Teacher", DB_MAX_DATA);
            }
            List<Teacher> teachers = teacherRepository.findAll();
            if (teachers.stream().anyMatch(existingTeacher ->
                    existingTeacher.getEmail().equalsIgnoreCase(teacher.getEmail()))) {
                throw new DataIntegrityViolationException("Teacher email already exists " + teacher.getEmail());
            }
            return teacherRepository.save(teacher);
        }catch (MaxDataException | DataIntegrityViolationException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Teacher> createBulk(List<Teacher> teachers) {
        try {
            List<Teacher> teacherList = teacherRepository.findAll();
            if (teacherList.size() + teachers.size() >= DB_MAX_DATA){
                throw new MaxDataException("Teacher", DB_MAX_DATA);
            }
            for (Teacher teacher : teachers) {
                Set<String> emailSet = teachers.stream().map(Teacher::getEmail).collect(Collectors.toSet());
                if (emailSet.size() != teachers.size()) {
                    throw new DataIntegrityViolationException("Duplicate email found in bulk data " + teacher.getEmail());
                }
                if (teacherList.stream().anyMatch(existingTeacher -> existingTeacher.getEmail().equalsIgnoreCase(teacher.getEmail()))) {
                    throw new DataIntegrityViolationException("Teacher email already exists " + teacher.getEmail());
                }
            }
            return teacherRepository.saveAll(teachers);
        }catch (DataIntegrityViolationException | MaxDataException  e){
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
                throw new DataIntegrityViolationException("Teacher email already exists " + teacher.getEmail());
            }
          Teacher existing = teacherUpdate.get();
            existing.setFirstName(teacher.getFirstName());
            existing.setEmail(teacher.getEmail());
            existing.setLastName(teacher.getLastName());
            teacherRepository.save(existing);
        }catch (NotFoundException| DataIntegrityViolationException e){
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

    public Optional<List<Teacher>> findByName(String firstName, String lastName){
        try {
            Optional<List<Teacher>> teachers = teacherRepository.findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(firstName,lastName);
            if (teachers.isEmpty()){
                throw new NotFoundException("Cannot Find Teacher");
            }
            return teachers;
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
