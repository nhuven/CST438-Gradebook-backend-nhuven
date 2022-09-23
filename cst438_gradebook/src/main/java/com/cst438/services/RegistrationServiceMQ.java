package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	ProcessEnrollment pe;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		//TODO  complete this method in homework 4
		boolean validRequest = true;
		Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
		
		// Check that course exists
		if (c == null) {
			System.out.println("RegistrationServiceREST - receive: Enrollment request for invalid course.");
			validRequest = false;
		}
		
		else if (enrollmentDTO.studentEmail == null || enrollmentDTO.studentEmail.isBlank()) {
			System.out.println("RegistrationServiceREST - receive: Student email not provided." );
			validRequest = false;
		}
		
		if (enrollmentDTO.studentName == null || enrollmentDTO.studentName.isBlank()) {
			System.out.println("RegistrationServiceREST - receive: Student name not provided." );
			validRequest = false;
		}
		
		if(validRequest)
			pe.processEnrollment(enrollmentDTO, c);
		else {
			//some code to send a message back advising of the issue.
		}
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		 
		//TODO  complete this method in homework 4
		boolean validRequest = true;
		Course c = courseRepository.findById(course_id).orElse(null);
		
		// Check that course exists
		if (c == null) {
			System.out.println("RegistrationServiceREST - sendFinalGrades: Enrollment request for invalid course.");
			validRequest = false;
		}
		
		if (courseDTO.grades == null || courseDTO.grades.size() == 0) {
			System.out.println("RegistrationServiceREST - sendFinalGrades: No grades provided.");
			validRequest = false;
		}
		
		if (validRequest) {
			System.out.println("Sending rabbitmq message: "+ courseDTO);
			rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
			System.out.println("Message sent.");
		}
		
	}

}
