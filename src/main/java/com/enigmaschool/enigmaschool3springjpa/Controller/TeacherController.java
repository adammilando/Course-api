package com.enigmaschool.enigmaschool3springjpa.Controller;

import com.enigmaschool.enigmaschool3springjpa.Model.Dtos.IdentityDto;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.CommonResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.SuccessResponse;
import com.enigmaschool.enigmaschool3springjpa.Model.Entities.Teacher;
import com.enigmaschool.enigmaschool3springjpa.Service.ISchoolService;
import com.enigmaschool.enigmaschool3springjpa.Service.teacherService;
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
@RequestMapping("/teacher")
@Validated
public class TeacherController {

    @Autowired
    teacherService  schoolService;

    @Autowired
    ModelMapper modelMapper;


    @GetMapping("/{size}/{page}")
    public ResponseEntity getAllStudent(@PathVariable int size, @PathVariable int page){
        Pageable pageable = PageRequest.of(size, page);
        Page<Teacher> teacherList = schoolService.getAll(pageable);
        CommonResponse successResponse = new SuccessResponse<>("Success", teacherList);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping
    public ResponseEntity createEntity(@Valid @RequestBody IdentityDto teacherDto){
       Teacher teacher = modelMapper.map(teacherDto, Teacher.class);
        Teacher result = schoolService.create(teacher);
        CommonResponse commonResponse = new SuccessResponse<>("success", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping("/addbulk")
    public ResponseEntity createBulk(@RequestBody List< @Valid IdentityDto> teacherDtos){
        List<Teacher> teachers = teacherDtos.stream().
                map(studentReq -> modelMapper.map(studentReq, Teacher.class))
                .collect(Collectors.toList());
        List<Teacher> creatBulk = schoolService.createBulk(teachers);
        CommonResponse commonResponse = new SuccessResponse<>("Success", creatBulk);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity getStudentById(@PathVariable Integer id){
        Optional<Teacher> teacher = schoolService.findById(id);
        CommonResponse commonResponse = new SuccessResponse<>("success", teacher);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateStudent(@Valid @RequestBody IdentityDto teacherDto,@PathVariable Integer id){
        Optional<Teacher> teacherUpdate = schoolService.findById(id);
        Teacher teacher= modelMapper.map(teacherDto, Teacher.class);
        schoolService.update(teacher, id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", teacherUpdate.get());
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStudent(@PathVariable Integer id){
        Optional<Teacher> teacher = schoolService.findById(id);
        schoolService.delete(id);
        CommonResponse commonResponse = new SuccessResponse<>("Success", teacher);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
