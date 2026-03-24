package com.example.demo.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	@Query(value = """
			    SELECT * FROM employee e
			    WHERE (:field IS NULL OR LOWER(e.field::text) = LOWER(:field))
			    AND (:position IS NULL OR LOWER(e.position::text) = LOWER(:position))
			      AND (
			            :search IS NULL OR
			            LOWER(e.name::text) LIKE LOWER(CONCAT('%', :search, '%')) OR
			            LOWER(e.company_email::text) LIKE LOWER(CONCAT('%', :search, '%')) OR
			            LOWER(e.personal_email::text) LIKE LOWER(CONCAT('%', :search, '%')) OR
			            e.phone_number::text LIKE CONCAT('%', :search, '%')
			      )
			""", nativeQuery = true)
	Page<Employee> searchEmployees(@Param("field") String field, @Param("position") String position,
			@Param("search") String search, Pageable pageable);

	boolean existsByName(String name);

	boolean existsByCompanyEmail(String companyEmail);

}
