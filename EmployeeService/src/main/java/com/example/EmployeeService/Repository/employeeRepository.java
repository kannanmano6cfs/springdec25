package com.example.EmployeeService.Repository;

import com.example.EmployeeService.Model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface employeeRepository extends JpaRepository<Employee, Integer> {

    //Custom Query Method for Employee Name
    Optional<Employee> findByEmpname(String name);

    //Custom Query Method for Native Query
    @Query(value="select empid,empname,empage from employee where empname Like %:word%", nativeQuery = true)
    Optional<Employee> findByEmpnameLike(@Param("word") String word);

    //Custom Query Method for Employee details deletion by name
    void deleteByEmpname(String name);

    //Method for Filtering and Pagination demo
    Page<Employee> findByEmpnameContainingIgnoreCase(String word, Pageable pageable);

}
