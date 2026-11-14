package com;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public String getDepartmentName(Long id) {
        return employeeRepository.findById(id).orElseThrow().getDepartment().getName();
    }

    public Employee getEmployeeWithDepartment(Long id) {
        return employeeRepository.findByIdWithDepartment(id).orElseThrow();
    }
}
