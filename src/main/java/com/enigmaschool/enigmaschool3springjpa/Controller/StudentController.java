package com.enigmaschool.enigmaschool3springjpa.Controller;

import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.IdentityDto;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.CommonResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.SuccessResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Student;
import com.enigmaschool.enigmaschool3springjpa.Service.ISchoolService;
import com.enigmaschool.enigmaschool3springjpa.Service.studentService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@Validated
public class StudentController {

    @Autowired
    studentService schoolService;

    @Autowired
    ModelMapper modelMapper;
    @GetMapping("/{size}/{page}")
    public ResponseEntity getAllStudent(@PathVariable("size") int size, @PathVariable("page") int page){
        Pageable pageable = PageRequest.of(size, page);
        Page<Student> studentList = schoolService.getAll(pageable);
        CommonResponse successResponse = new SuccessResponse<>("Success", studentList);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping
    public ResponseEntity createEntity(@Valid @RequestBody IdentityDto student){
        Student studentSave = modelMapper.map(student, Student.class);
        Student result = schoolService.create(studentSave);
        CommonResponse commonResponse = new SuccessResponse<>("success", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping("/addbulk")
    public ResponseEntity createBulk(@RequestBody List< @Valid IdentityDto> studentRequest){
        List<Student> students = studentRequest.stream().
                map(studentReq -> modelMapper.map(studentReq, Student.class))
                .collect(Collectors.toList());
        List<Student> creatBulk = schoolService.createBulk(students);
        CommonResponse commonResponse = new SuccessResponse<>("Success", creatBulk);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity getStudentById(@PathVariable Integer id){
        Optional<Student> student = schoolService.findById(id);
        CommonResponse commonResponse = new SuccessResponse<>("success", student);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateStudent(@Valid @RequestBody IdentityDto studentRequest,@PathVariable Integer id){
        Optional<Student> studentUpdate = schoolService.findById(id);
        Student student = modelMapper.map(studentRequest, Student.class);
        schoolService.update(student, id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", studentUpdate.get());
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStudent(@PathVariable Integer id){
        Optional<Student> student = schoolService.findById(id);
        schoolService.delete(id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", student);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
