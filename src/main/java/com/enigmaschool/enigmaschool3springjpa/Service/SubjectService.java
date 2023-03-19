package com.enigmaschool.enigmaschool3springjpa.Service;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Subject;
import com.enigmaschool.enigmaschool3springjpa.Repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService{
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private studentService studentService;

    public Page<Subject> getAll(Pageable pageable){
        try {
            List<Subject> subjectList = subjectRepository.findAll();
            if (subjectList.isEmpty()){
                throw new NotFoundException("Database Empty");
            }
            Page<Subject> subjects = subjectRepository.findAll(pageable);
            if (subjects.isEmpty()){
                throw new NotFoundException("Wrong Page Size");
            }
            return subjects;
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    public List<Subject> getAll() {
//        try {
//            List<Subject> subjects = subjectRepository.findAll();
//            if (subjects.isEmpty()){
//                throw new NotFoundException("Databse Empty");
//            }
//            return subjects;
//        }catch (NotFoundException e){
//            throw e;
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }

    public Subject create(Subject subject) {
        try {
            Long count = subjectRepository.count();
            if (count>= 6){
                throw new MaxDataException("Student",6);
            }
//            if (subjectList.stream().anyMatch(name ->
//                    name.getName().equalsIgnoreCase(subject.getName()))){
//                throw new DuplicateDataException("subject Already exists");
//            }
            return subjectRepository.save(subject);
        }catch (MaxDataException | DuplicateDataException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public List<Subject> createBulk(List<Subject> subjects) {
        try {
            List<Subject> subjectList = subjectRepository.findAll();
            if (subjectList.size()+subjects.size() >= 25){
                throw new MaxDataException("Subject", 25);
            }
            if (subjects.stream().map(Subject::getName)
                    .distinct()
                    .count() < subjects.size()) {
                throw new DuplicateDataException("Duplicate subject found");
            }
            List<Subject> existingSubjects = subjectRepository.findAll();
            if (existingSubjects.stream().anyMatch(subject ->
                    subject.getName().equalsIgnoreCase(subject.getName()))) {
                throw new DuplicateDataException("Student email already exists");
            }
            return subjectRepository.saveAll(subjects);
        }catch (DuplicateDataException | MaxDataException  e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Subject> findById(Integer id) {
        try {
            Optional<Subject> subjects = subjectRepository.findById(id);
            if (subjects.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            return subjects;
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void update(Subject subject, Integer id) {
        try {
            Optional<Subject> subjectUpdate = subjectRepository.findById(id);
            if (subjectUpdate.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.stream().anyMatch(existingsubject ->
                    existingsubject.getName().equalsIgnoreCase(subject.getName()))) {
                throw new DuplicateDataException("Student email already exists " + subject.getName());
            }
            Subject existing = subjectUpdate.get();
            existing.setName(subject.getName());
            subjectRepository.save(existing);
        }catch (NotFoundException| DuplicateDataException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer id) {
        try {
            boolean subjectExists = subjectRepository.existsById(id);
            if (!subjectExists){
                throw new NotFoundException(id + " Not found");
            }
            subjectRepository.deleteById(id);
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    public Optional<Page<Subject>> findStudentInSubjectByName(String firstName, String lastName, Pageable pageable){
        try {
            Optional<Page<Subject>> subjects = subjectRepository.findByStudents_FirstNameContainsIgnoreCaseAndStudents_LastNameContainsIgnoreCase(firstName,lastName,pageable);
            if (subjects.isEmpty()){
                throw new NotFoundException("Cannot Find Student");
            }
            return subjects;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Page<Subject>> findTeacherInSubjectByName(String firstName, String lastName, Pageable pageable){
        try {
            Optional<Page<Subject>> subjects = subjectRepository.findByTeacher_FirstNameContainsIgnoreCaseOrTeacher_LastNameContainsIgnoreCase(firstName, lastName, pageable);
            if (subjects.isEmpty()){
                throw new NotFoundException("Cannot Find Teacher");
            }
            return subjects;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
