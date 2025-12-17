package com.example.EmployeeService.Controller;

import com.example.EmployeeService.Exception.EmployeeNotFoundException;
import com.example.EmployeeService.Model.Employee;
import com.example.EmployeeService.Repository.employeeRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.Optional;

@RestController
public class employeeController {

    @Autowired
    private Environment env;

    @Autowired
    private employeeRepository repo;

    @Autowired
    private RestTemplate restTemplate;

    //Get Methods
    @GetMapping("/api/{i}")
    public String api(@PathVariable int i) {
        return "Hello World!! Server is running on port "+env.getProperty("server.port")+
                " Value obtained in url is "+i;
    }

    @GetMapping("/api1")
    public String api1(@RequestParam int i) {
        return "Hello World!! Server is running on port "+env.getProperty("server.port")+
                " Value obtained in url is "+i;
    }

    // Data JPA Rest Methods
    //To Display all the employee details
    @GetMapping("/employees/v1")
    public Iterable<Employee> employeesV1() {
        return repo.findAll();
    }

    //To display the count of the employee details
    @GetMapping("/count")
    public ResponseEntity<String> count(){
        long ct=repo.count();
        return new ResponseEntity<>("Number of Employees is "+ct, HttpStatus.OK);
    }

    //To display the employee by empID (PathVariable)
    //Exception Handling Demo

    @GetMapping("/employee/v1/{id}")
    public Employee getEmployee(@PathVariable int id) {
        Optional<Employee> emp=repo.findById(id);
        return emp.orElseThrow(()-> new EmployeeNotFoundException(id));
        //return emp.orElse(null);
    }

    //To display the employee by empname (RequestParam)
    @GetMapping("/employee/v2")
    public Employee getEmployeeV2(@RequestParam String name) {
        Optional<Employee> emp=repo.findByEmpname(name);
        return emp.orElse(null);
    }

    //To display the employee by empname with Custom Query (RequestParam)
    @GetMapping("/employee/v3")
    public Employee getEmployeeV3(@RequestParam String name) {
        Optional<Employee> emp=repo.findByEmpnameLike(name);
        return emp.orElse(null);
    }

    //Post Methods
    @PostMapping("/new/v1")
    @CacheEvict(value="allemps", allEntries=true)
    public ResponseEntity<String> api2(@RequestBody Employee emp){
        repo.save(emp);
        return new ResponseEntity<String>("New Employee details added successfully" , HttpStatus.OK);
    }

    @PostMapping("/new/v2")
    public ResponseEntity<String> newemp(){
        Employee emp=new Employee();
        emp.setEmpname("Kannan");
        emp.setEmpage(35);
        repo.save(emp);
        return new ResponseEntity<>("New Employee details added successfully" , HttpStatus.OK);
    }
    //Put Methods
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editEmployee(@PathVariable int id, @RequestBody Employee emp){
        Employee emp1 = repo.findById(id).orElseThrow();
        emp1.setEmpname(emp.getEmpname());
        emp1.setEmpage(emp.getEmpage());
        repo.save(emp1);
        return new ResponseEntity<>("Employee details updated successfully" , HttpStatus.OK);
    }

    //Delete Methods
    //To delete all the employee details
    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAll(){
        repo.deleteAll();
        return new ResponseEntity<>("All Employee details deleted successfully" , HttpStatus.OK);
    }

    //To delete the employee by ID
    @DeleteMapping("/delete/v1/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        repo.deleteById(id);
        return new ResponseEntity<>("Employee details deleted successfully" , HttpStatus.OK);
    }

    //To delete the employee by name
    //    @DeleteMapping("/delete/v2/{name}")
    //    public ResponseEntity<String> deleteEmployeeV2(@PathVariable String name) {
    //        repo.deleteByEmpname(name);
    //        return new ResponseEntity<>("Employee details deleted successfully" , HttpStatus.OK);
    //    }

    //Inter-Service Communication  and Request Retry using Resilience4J
    private static final String deptsvc_API="http://localhost:8082/newemp";
    int attempt=1;

    @GetMapping("/selectemp/{id}")
    @Transactional
    @Retry(name="retry1", fallbackMethod = "fallback")
    public ResponseEntity<String> selectEmployee(@PathVariable int id) {

        System.out.println("Employee selection request sent for HR "+attempt++);
        Optional<Employee> emp=repo.findById(id);

        ResponseEntity<String> response=restTemplate.postForEntity(deptsvc_API,emp,String.class);
        System.out.println("Employee assigned as HR successfully");
        return response;
    }

    //Fallback Method for Retry
    public ResponseEntity<String> fallback(Throwable ex){
        System.out.println("Unable to place the request");
        return new ResponseEntity<>("Sorry!! Department Service unavailable!!" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Caching
    @GetMapping("/employees/v2")
    @Cacheable(value="allemps")
    public Iterable<Employee> employeesV2() {
        return repo.findAll();
    }

    //Pagination
    @GetMapping("/employees/v3")
    public Page<Employee> employeesV3(@PageableDefault(size=3) Pageable pageable) {
        return repo.findAll(pageable);
    }

    //Filtering
    @GetMapping("/search")
    public ResponseEntity<Page<Employee>> employeesV4(@RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(repo.findByEmpnameContainingIgnoreCase(name,pageable));
    }
    //Request Validation
    @PostMapping("/new/v3")
    public ResponseEntity<String> api3(@Valid @RequestBody Employee emp){
        repo.save(emp);
        return new ResponseEntity<String>("New Employee details added successfully" , HttpStatus.OK);
    }

    //Testing
    @GetMapping("/count/v1")
    public long empcount(){
        return repo.count();
    }

}
