package com.enigmaschool.enigmaschool3springjpa.Service;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Subject;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import com.enigmaschool.enigmaschool3springjpa.Repository.SubjectRepository;
import com.enigmaschool.enigmaschool3springjpa.Repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectService{
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private studentService studentService;

    private final int DB_MAX_DATA = 6;
    @Autowired
    private TeacherRepository teacherRepository;

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

    public Subject create(Subject subject) {
        try {
            Long count = subjectRepository.count();
            if (count>= DB_MAX_DATA){
                throw new MaxDataException("Student",DB_MAX_DATA);
            }
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.stream().anyMatch(existingSubject ->
                    existingSubject.getName().equalsIgnoreCase(subject.getName()))) {
                throw new DataIntegrityViolationException("Subject already exists " + subject.getName());
            }
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
            if (subjectList.size()+subjects.size() >= DB_MAX_DATA){
                throw new MaxDataException("Subject", DB_MAX_DATA);
            }
            for (Subject subject : subjects) {
                Set<String> emailSet = subjects.stream().map(Subject::getName).collect(Collectors.toSet());
                if (emailSet.size() != subjects.size()) {
                    throw new DataIntegrityViolationException("Duplicate name found in bulk data " + subject.getName());
                }
                if (subjectList.stream().anyMatch(existingSubject -> existingSubject.getName().equalsIgnoreCase(subject.getName()))) {
                    throw new DataIntegrityViolationException("Subject already exists " + subject.getName());
                }
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


    public void update(Subject newSubject, Integer id) {
        try {
            Optional<Subject> subjectUpdate = subjectRepository.findById(id);
            if (subjectUpdate.isEmpty()){
                throw new NotFoundException(id + " Not found");
            }
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.stream().anyMatch(existingsubject ->
                    existingsubject.getName().equalsIgnoreCase(newSubject.getName()))) {
                throw new DuplicateDataException("Subject name already exists " + newSubject.getName());
            }
            Subject existingSubject = subjectUpdate.get();
            existingSubject.setName(newSubject.getName());

            // untuk mendapatkan data student yang sudah ada pada subject
            List<Student> existingStudents = existingSubject.getStudents();
            // list student baru yang mau ditambah jika blom ada pada subject
            List<Student> newStudents = newSubject.getStudents();
            for (Student student : newStudents) {
                if (!existingStudents.contains(student)) {
                    existingStudents.add(student);
                }
            }
            //hapus student yang sudah tidak ada pada subject
            existingStudents.removeIf(student -> !newStudents.contains(student));
            existingSubject.setStudents(existingStudents);

            existingSubject.setTeacher(newSubject.getTeacher());

            subjectRepository.save(existingSubject);
        } catch (NotFoundException| DuplicateDataException e){
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
            Optional<Page<Subject>> subjects = subjectRepository.findByStudents_FirstNameContainsIgnoreCaseOrStudents_LastNameIgnoreCase(firstName,lastName,pageable);
            if (subjects.isEmpty()){
                throw new NotFoundException("Cannot Find Student");
            }
            Page<Subject> subjectPage = subjectRepository.findAll(pageable);
            if (subjectPage.isEmpty()){
                throw new NotFoundException("Wrong Page Size");
            }
            return subjects;
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
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
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Subject addStudentsToSubject(Integer subjectId, List<Student> students) {
        try {
            Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
            if (subjectOptional.isEmpty()) {
                throw new NotFoundException("Subject not found with id: " + subjectId);
            }
            //get student dari subject yang dicari
            Subject subject = subjectOptional.get();
            List<Student> existingStudents = subject.getStudents();

            // untuk mengeck id baru yang ditambahkan
            List<Integer> newStudentIds = students.stream().map(Student::getId).toList();

            //cek id student yang baru ditambah apakah sudah ada atau belum pada subject
            for (Student existingStudent : existingStudents) {
                if (newStudentIds.contains(existingStudent.getId())) {
                    throw new DuplicateDataException("Student with id " + existingStudent.getId() + " already exists in the subject");
                }
            }
            //periksa duplikat di data bulk
            boolean hasDuplicateIds = newStudentIds.stream().distinct().count() != newStudentIds.size();
            if (hasDuplicateIds) {
                throw new DuplicateDataException("Duplicate student ids are not allowed");
            }
            existingStudents.addAll(students);

            return subjectRepository.save(subject);
        }catch (NotFoundException | DuplicateDataException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    public Subject updateTeacherInSubject(Integer subjectId, Teacher teacher) {
        try {
            Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
            if (subjectOptional.isEmpty()) {
                throw new NotFoundException("Subject not found with id: " + subjectId);
            }

            Subject subject = subjectOptional.get();

            // Cek apakah teacher sudah ada di database
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacher.getId());
            if (teacherOptional.isEmpty()) {
                throw new NotFoundException("Teacher not found with id: " + teacher.getId());
            }
            Teacher validTeacher = teacherOptional.get();

            // Cek apakah teacher yang di-pass sama dengan teacher yang sudah ada di Subject
            if (subject.getTeacher() != null && subject.getTeacher().equals(validTeacher)) {
                throw new DuplicateDataException("Teacher already Teaching the subject");
            }

            subject.setTeacher(validTeacher);
            return subjectRepository.save(subject);
        }catch (NotFoundException | DuplicateDataException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public Subject deleteStudentsFromSubject(Integer subjectId, List<Student> students) {
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            if (optionalSubject.isEmpty()) {
                throw new NotFoundException("Subject with id " + subjectId + " not found");
            }
            Subject subject = optionalSubject.get();
            List<Student> subjectStudents = subject.getStudents();
            subjectStudents.removeIf(student -> students.stream()
                    .anyMatch(deleteStudent -> deleteStudent.getId().equals(student.getId())));
            return subjectRepository.save(subject);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
