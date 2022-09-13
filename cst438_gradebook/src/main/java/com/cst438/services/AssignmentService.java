package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;


@Service
public class AssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepository;
	
	public Assignment save(Assignment a) {
		// create attempt entity and save to database
		a = assignmentRepository.save(a);

		return a;
	}
	
}
