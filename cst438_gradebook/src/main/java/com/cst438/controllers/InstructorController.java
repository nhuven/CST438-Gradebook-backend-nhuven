package com.cst438.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import com.cst438.domain.Course;
import com.cst438.domain.CourseListDTO;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"})
public class InstructorController {

	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/instructor/{email}/courses")
	@Transactional
	public CourseListDTO getInstructorCourses(@PathVariable String email){
		
		email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		List<Course> c = courseRepository.findByInstructor(email);
		
		CourseListDTO courseList = new CourseListDTO();
		
		for (Course course : c) {
			courseList.courses.add(new CourseListDTO.CourseDTO(course.getTitle(), course.getCourse_id()));
		}
		
		return courseList;
	}
	
}