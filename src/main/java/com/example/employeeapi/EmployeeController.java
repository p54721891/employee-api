package com.example.employeeapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final Map<Long, Employee> employees = new ConcurrentSkipListMap<>();

    // Load sample employees when the application starts.
    public EmployeeController() {
        employees.put(1L, new Employee(1, "Asha Patel", "HR", 45000));
        employees.put(2L, new Employee(2, "Rohan Shah", "IT", 55000));
        employees.put(3L, new Employee(3, "Neha Sharma", "Finance", 60000));
    }

    // (b) Add an employee from a JSON request body.
    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        long id = employee.getId();
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (employees.putIfAbsent(id, employee) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.created(URI.create("/employees/" + id)).body(employee);
    }

    // (c) Get all employees.
    @GetMapping
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    // (c) Get one employee by ID.
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("id") long id) {
        Employee employee = employees.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(employee);
    }

    // (d) Update an existing employee using the ID in the URL.
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable("id") long id, @RequestBody Employee employee) {
        employee.setId(id);

        if (employees.replace(id, employee) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(employee);
    }

    // (e) Delete an employee by ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") long id) {
        if (employees.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}