package com.cst438.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cst438.domain.Course;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;



@Service
public class ProcessEnrollment {
	
	@Autowired
	private EnrollmentRepository enrollmentRepository;
	
	public EnrollmentDTO processEnrollment(EnrollmentDTO enrollmentDTO, Course course) {

		Enrollment enrollment = new Enrollment();
		Enrollment newEnrollment = new Enrollment();
		
		
		// check to see if the student is already enrolled in the course.
		// if already enrolled will return the existing id instead of enrolling again
		List<Enrollment> alreadyEnrolled = enrollmentRepository.findByStudentEmailAndCourse(enrollmentDTO.studentEmail, course);
		
		if(alreadyEnrolled.size() > 0) {
			enrollment = alreadyEnrolled.get(0);
		}
		else {
			newEnrollment.setStudentEmail(enrollmentDTO.studentEmail);
			newEnrollment.setStudentName(enrollmentDTO.studentName);
			newEnrollment.setCourse(course);
			enrollment = enrollmentRepository.save(newEnrollment);
		}
		
		enrollmentDTO.id = enrollment.getId();
		
		return enrollmentDTO;
	}

}
