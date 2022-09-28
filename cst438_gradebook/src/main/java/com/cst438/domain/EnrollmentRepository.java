package com.cst438.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EnrollmentRepository extends CrudRepository <Enrollment, Integer> {

		List<Enrollment> findByStudentEmailAndCourse(String studentEmail, Course course);
}
