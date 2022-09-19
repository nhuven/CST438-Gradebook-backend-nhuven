package com.cst438.domain;

import java.util.ArrayList;
import java.util.List;

import com.cst438.domain.AssignmentListDTO.AssignmentDTO;

public class CourseListDTO {

	public static class CourseDTO {
		public String name;
		public int id;	
		
		public CourseDTO(String name, int id) {
			this.name = name;
			this.id = id;
		}
	}
	
	public ArrayList<CourseDTO> courses = new ArrayList<>();

}