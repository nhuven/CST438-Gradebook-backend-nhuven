package com.cst438.controllers;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.services.ProcessEnrollment;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	ProcessEnrollment pe;

	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		
		
		Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
		
		// Check that course exists
		if (c == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Course not found. " + enrollmentDTO.course_id );
		}
		
		if (enrollmentDTO.studentEmail == null || enrollmentDTO.studentEmail.isBlank()) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email not provided." );
		}
		
		if (enrollmentDTO.studentName == null || enrollmentDTO.studentName.isBlank()) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student name not provided." );
		}

		pe.processEnrollment(enrollmentDTO, c);
		
		return enrollmentDTO;

	}

}
