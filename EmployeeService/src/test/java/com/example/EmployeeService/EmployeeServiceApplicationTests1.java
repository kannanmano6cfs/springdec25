package com.example.EmployeeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

//API Testing using Random Port

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeServiceApplicationTests1 {

    @Autowired
    public TestRestTemplate restTemplate;

    @Test
    void counttest(){
        long ct=restTemplate.getForObject("/count/v1", Long.class);
        Assertions.assertEquals(ct,10);
    }
}
