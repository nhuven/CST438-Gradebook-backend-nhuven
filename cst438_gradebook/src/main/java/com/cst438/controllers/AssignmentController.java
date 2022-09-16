package com.cst438.controllers;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
public class AssignmentController {

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@PostMapping("/assignment")
	@Transactional
	public AssignmentListDTO.AssignmentDTO addAssignment(@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO){
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		Course c = courseRepository.findById(assignmentDTO.courseId).orElse(null);
		
		// Check that course exists
		if (c == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Course not found. " + assignmentDTO.courseId );
		}
		
		// Check the user is authorized to add assignments to the course
		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		
		if (assignmentDTO.assignmentName.isBlank()) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment name not provided." );
		}
		
		if (assignmentDTO.dueDate == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Due date not provided." );
		}

		Assignment assignment = new Assignment();
		
		assignment.setName(assignmentDTO.assignmentName);
		
		assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate));
		
		assignment.setCourse(c);
		
		Assignment newAssignment = assignmentRepository.save(assignment);
		
		assignmentDTO.assignmentId = newAssignment.getId();
		assignmentDTO.courseTitle = c.getTitle();
		
		return assignmentDTO;
	}
	
	@PatchMapping("/assignment/{assignment_id}")
	@Transactional
	public AssignmentListDTO.AssignmentDTO updateAssignment(@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO, @PathVariable int assignment_id) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
		
		if (assignment == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment not found. " + assignment_id );
		}
		
		Course c = courseRepository.findById(assignment.getCourse().getCourse_id()).orElse(null);
		// Check the user is authorized to add assignments to the course
		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		
		assignment.setName(assignmentDTO.assignmentName);
		
		Assignment update = assignmentRepository.save(assignment);
		
		assignmentDTO.assignmentId = assignment_id;
		assignmentDTO.dueDate = update.getDueDate().toString();
		assignmentDTO.courseId = c.getCourse_id();
		assignmentDTO.courseTitle = c.getTitle();
		
		return assignmentDTO;
	}
	
	@DeleteMapping("/assignment/{assignment_id}")
	@Transactional
	public HttpStatus deleteAssignment(@PathVariable int assignment_id) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		Assignment delete = assignmentRepository.findById(assignment_id).orElse(null);
		
		if (delete == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment not found. " + assignment_id );
		}
		
		Course c = courseRepository.findById(delete.getCourse().getCourse_id()).orElse(null);
		// Check the user is authorized to add assignments to the course
		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		
		if (delete.hasGraded()) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Assignment has already been graded. " );
		}
		
		assignmentRepository.delete(delete);
		
		return HttpStatus.OK;
	}
}
