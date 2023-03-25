package com.enigmaschool.enigmaschool3springjpa.Controller;

import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.IdentityDto;
import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.InsertIntoSubject;
import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.SearchDto;
import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.SubjectDto;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.CommonResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.SuccessResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Subject;
import com.enigmaschool.enigmaschool3springjpa.Service.SubjectService;
import com.enigmaschool.enigmaschool3springjpa.Service.studentService;
import com.enigmaschool.enigmaschool3springjpa.Service.teacherService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subject")
@Validated
public class SubjectController {
    @Autowired
    SubjectService schoolService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{size}/{page}/{sort}")
    public ResponseEntity getAllSubject(@PathVariable("size") int size, @PathVariable("page") int page, @PathVariable("sort") String sort){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        if (sort.equalsIgnoreCase("desc")){
            pageable = PageRequest.of(size, page, Sort.by("name").descending());
        }
        Page<Subject> subjectList = schoolService.getAll(pageable);
        CommonResponse successResponse = new SuccessResponse<>("Success", subjectList);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping
    public ResponseEntity createEntity(@Valid @RequestBody SubjectDto subjectRequest){
        Subject subject = modelMapper.map(subjectRequest, Subject.class);
        Subject result = schoolService.create(subject);
        CommonResponse commonResponse = new SuccessResponse<>("success", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping("/addbulk")
    public ResponseEntity createBulk(@RequestBody List< @Valid SubjectDto> studentDtoList){
        List<Subject> subjects = studentDtoList.stream().
                map(studentReq -> modelMapper.map(studentReq, Subject.class))
                .collect(Collectors.toList());
        List<Subject> creatBulk = schoolService.createBulk(subjects);
        CommonResponse commonResponse = new SuccessResponse<>("Success", creatBulk);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/search/studentName/{size}/{page}")
    public ResponseEntity findStudentName(@RequestBody SearchDto searchDto,@PathVariable("size") int size, @PathVariable("page") int page){
        Pageable pageable = PageRequest.of(size,page, Sort.by("id").ascending());
        Optional<Page<Subject>> subjects = schoolService.findStudentInSubjectByName(searchDto.getSearchFirstName(), searchDto.getSearchLastName(),pageable);
        CommonResponse commonResponse = new SuccessResponse<>("Success",subjects);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/search/teacherName/{size}/{page}")
    public ResponseEntity findTeacherName(@RequestBody SearchDto searchDto, @PathVariable int size, @PathVariable int page){
        Pageable pageable = PageRequest.of(size,page, Sort.by("id").ascending());
        Optional<Page<Subject>> subjects = schoolService.findTeacherInSubjectByName(searchDto.getSearchFirstName(),searchDto.getSearchLastName(),pageable);
        CommonResponse commonResponse = new SuccessResponse<>("Success", subjects);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity getSubjectById(@PathVariable Integer id){
        Optional<Subject> subject = schoolService.findById(id);
        CommonResponse commonResponse = new SuccessResponse<>("success", subject);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateStudent(@Valid @RequestBody SubjectDto subjectDto,@PathVariable Integer id){
        Subject subject = modelMapper.map(subjectDto, Subject.class);
        Optional<Subject> subjectUpdate = schoolService.findById(id);
        schoolService.update(subject, id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", subjectUpdate.get());
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStudent(@PathVariable Integer id){
        Optional<Subject> subject = schoolService.findById(id);
        schoolService.delete(id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", subject);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/{subjectId}/add-students")
    public ResponseEntity addStudentsToSubject(@PathVariable Integer subjectId, @RequestBody List< Student> students) {
//        List<Student> collect = students.stream().
//                map(studentReq -> modelMapper.map(studentReq, Student.class))
//                .collect(Collectors.toList());
        Subject subject = schoolService.addStudentsToSubject(subjectId, students);
        return ResponseEntity.ok().body(new SuccessResponse<>("Success", subject));
    }



    @PutMapping("/{subjectId}/change-teacher")
    public ResponseEntity changeTeacherInSubject(@PathVariable Integer subjectId,  @RequestBody Teacher teacher) {
//        Teacher teacherDto = modelMapper.map(teacher, Teacher.class);
        Subject subject = schoolService.updateTeacherInSubject(subjectId,teacher);
        CommonResponse commonResponse = new SuccessResponse<>("Success", subject);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @DeleteMapping("/{subjectId}/deleteStudents")
    public ResponseEntity deleteStudent(@PathVariable Integer subjectId, @RequestBody List<Student> studentId){
        Subject subject = schoolService.deleteStudentsFromSubject(subjectId,studentId);
        return ResponseEntity.ok().body(new SuccessResponse<>("Success",subject));
    }
}
