package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { AssignmentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class TestAssignmentController {

	static final String URL = "http://localhost:8081";
	public static final int TEST_COURSE_ID = 40442;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME = "test";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int TEST_YEAR = 2021;
	public static final String TEST_SEMESTER = "Fall";

	@MockBean
	AssignmentRepository assignmentRepository;

	@MockBean
	AssignmentGradeRepository assignmentGradeRepository;

	@MockBean
	CourseRepository courseRepository; // must have this to keep Spring test happy

	@MockBean
	RegistrationService registrationService; // must have this to keep Spring test happy

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addAssignmentSuccess() throws Exception {

		MockHttpServletResponse response;

		// mock database data

		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setEnrollments(new java.util.ArrayList<Enrollment>());
		course.setAssignments(new java.util.ArrayList<Assignment>());

		// given -- stubs for database repositories that return test data
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		
		Assignment assignment = new Assignment();
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		// set dueDate to 1 week before now.
		assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		assignment.setId(1);
		assignment.setName("Assignment 1");
		assignment.setNeedsGrading(1);

		// given -- stubs for database repositories that return test data
		given(assignmentRepository.save(any())).willReturn(assignment);

		// end of mock data

		// then do an http get request for assignment 1
		AssignmentListDTO.AssignmentDTO request = new AssignmentListDTO.AssignmentDTO();
		request.assignmentName = assignment.getName();
		request.dueDate = assignment.getDueDate().toString();
		request.courseId = course.getCourse_id();
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/assignment")
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify return data with entry for one student without no score
		assertEquals(200, response.getStatus());
		
		AssignmentListDTO.AssignmentDTO returnedDTO = fromJsonString(response.getContentAsString(), AssignmentListDTO.AssignmentDTO.class);
		
		assertEquals(1, returnedDTO.assignmentId);
		
		verify(assignmentRepository, times(1)).save(any());
	}

	@Test
	public void updateAssignmentNameSuccess() throws Exception {

		MockHttpServletResponse response;

		// mock database data

		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setEnrollments(new java.util.ArrayList<Enrollment>());
		course.setAssignments(new java.util.ArrayList<Assignment>());

		Assignment assignment = new Assignment();
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		// set dueDate to 1 week before now.
		assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		assignment.setId(1);
		assignment.setName("Assignment 1");
		assignment.setNeedsGrading(1);


		// given -- stubs for database repositories that return test data
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
		given(assignmentRepository.save(any())).willReturn(assignment);

		// end of mock data

		// then do an http get request for assignment 1
		AssignmentListDTO.AssignmentDTO request = new AssignmentListDTO.AssignmentDTO();
		request.assignmentName = "unitTest";
		response = mvc.perform(
				MockMvcRequestBuilders
				.patch("/assignment/1")
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify return data with entry for one student without no score
		assertEquals(200, response.getStatus());

		// verify that returned data has new name
		AssignmentListDTO.AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentListDTO.AssignmentDTO.class);
		// assignment name is unitTest
		assertEquals("unitTest", result.assignmentName);
	}
	
	@Test
	public void updateAssignmentNameFailure() throws Exception {

		MockHttpServletResponse response;

		// mock database data

		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setEnrollments(new java.util.ArrayList<Enrollment>());
		course.setAssignments(new java.util.ArrayList<Assignment>());

		Assignment assignment = new Assignment();
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		// set dueDate to 1 week before now.
		assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		assignment.setId(1);
		assignment.setName("Assignment 1");
		assignment.setNeedsGrading(1);


		// given -- stubs for database repositories that return test data
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
		given(assignmentRepository.save(any())).willReturn(assignment);

		// end of mock data

		// then do an http get request for assignment 1
		AssignmentListDTO.AssignmentDTO request = new AssignmentListDTO.AssignmentDTO();
		request.assignmentName = "unitTest";
		response = mvc.perform(
				MockMvcRequestBuilders
				.patch("/assignment/2")
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify return data with entry for one student without no score
		assertEquals(400, response.getStatus());
	}

	@Test
	public void deleteAssignmentSuccess() throws Exception {

		MockHttpServletResponse response;

		// mock database data

		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setEnrollments(new java.util.ArrayList<Enrollment>());
		course.setAssignments(new java.util.ArrayList<Assignment>());

		Assignment assignment = new Assignment();
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		// set dueDate to 1 week before now.
		assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		assignment.setId(1);
		assignment.setName("Assignment 1");
		assignment.setNeedsGrading(1);


		// given -- stubs for database repositories that return test data
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));

		// end of mock data

		// then do an http get request for assignment 1
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/assignment/1")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify return data with entry for one student without no score
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void deleteAssignmentFailure() throws Exception {

		MockHttpServletResponse response;

		// mock database data

		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setEnrollments(new java.util.ArrayList<Enrollment>());
		course.setAssignments(new java.util.ArrayList<Assignment>());

		Assignment assignment = new Assignment();
		assignment.setCourse(course);
		course.getAssignments().add(assignment);
		// set dueDate to 1 week before now.
		assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		assignment.setId(1);
		assignment.setName("Assignment 1");
		assignment.setNeedsGrading(1);


		// given -- stubs for database repositories that return test data
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));

		// end of mock data

		// then do an http get request for assignment 1
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/assignment/2")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify return data with entry for one student without no score
		assertEquals(400, response.getStatus());
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

