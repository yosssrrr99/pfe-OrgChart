package com.hracces.openhr.dto;


import com.hracces.openhr.entities.EmployeeData;

import java.util.List;

public class EmployeeDataResponse {
    private int employeeCount;
    private List<EmployeeData> employees;

    public EmployeeDataResponse(int employeeCount, List<EmployeeData> employees) {
        this.employeeCount = employeeCount;
        this.employees = employees;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public List<EmployeeData> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeData> employees) {
        this.employees = employees;
    }
}
