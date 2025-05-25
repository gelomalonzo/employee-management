package com.capstone.employeemanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.service.DepartmentServiceImpl;
import com.capstone.employeemanagement.service.EmployeeServiceImpl;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeServiceImpl employeeService;

    @MockitoBean
    private DepartmentServiceImpl departmentService;

    @Test
    void testGetAllEmployees() throws Exception {
        Employee employee = new Employee("John Doe", LocalDate.of(2000, 1, 1), new BigDecimal("50000"), new Department());
        Mockito.when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/employees/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.employees[0].name").value("John Doe"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee("Jane", LocalDate.of(1990, 5, 15), new BigDecimal("40000"), new Department());
        Mockito.when(employeeService.getEmployeeById(1)).thenReturn(employee);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[0].name").value("Jane"));
    }

    @Test
    void testFilterEmployeesByName() throws Exception {
        Employee employee = new Employee("Jake", LocalDate.of(1995, 8, 20), new BigDecimal("42000"), new Department());
        Mockito.when(employeeService.getEmployeesByName("Jake")).thenReturn(List.of(employee));

        mockMvc.perform(get("/employees/filter?name=Jake&departmentId=&minAge=&maxAge="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[0].name").value("Jake"));
    }

    @Test
    void testAddEmployee() throws Exception {
        Department department = new Department();
        department.setId(1);
        Employee employee = new Employee("Anna", LocalDate.of(1992, 3, 3), new BigDecimal("60000"), department);

        Mockito.when(departmentService.getDepartmentById(1)).thenReturn(department);
        Mockito.doNothing().when(employeeService).saveEmployee(any(Employee.class));

        mockMvc.perform(post("/employees/add")
                        .param("name", "Anna")
                        .param("birthDate", "1992-03-03")
                        .param("salary", "60000")
                        .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testEditEmployee() throws Exception {
        Department department = new Department();
        department.setId(1);
        Employee employee = new Employee("Mark", LocalDate.of(1990, 1, 1), new BigDecimal("70000"), department);

        Mockito.when(departmentService.getDepartmentById(1)).thenReturn(department);
        Mockito.when(employeeService.getEmployeeById(1)).thenReturn(employee);
        Mockito.doNothing().when(employeeService).saveEmployee(any(Employee.class));

        mockMvc.perform(post("/employees/1/edit")
                        .param("name", "Mark Updated")
                        .param("birthDate", "1990-01-01")
                        .param("salary", "70000")
                        .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee(1);

        mockMvc.perform(delete("/employees/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully deleted employee from the database."));
    }

}
