package com.hracces.openhr.dto;

import com.hracces.openhr.entities.EmployeeRec;

import java.util.List;

public class UpdateRecRequest {
    private List<EmployeeRec> employees;
    private double minBudget;
    private double maxBudget;

    // Getters and setters
    public List<EmployeeRec> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeRec> employees) {
        this.employees = employees;
    }

    public double getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(double minBudget) {
        this.minBudget = minBudget;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }
}
