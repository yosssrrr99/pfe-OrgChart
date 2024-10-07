package com.hracces.openhr.dto;

import com.hracces.openhr.entities.EmployeeRec;

import java.util.List;

public class UpdateRecRequest {
    private List<EmployeeRec> employees;
    private double budgetGlobal;
    private double gab;

    // Getters and setters
    public List<EmployeeRec> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeRec> employees) {
        this.employees = employees;
    }

    public double getBudgetGlobal() {
        return budgetGlobal;
    }

    public void setMinBudget(double budgetGlobal) {
        this.budgetGlobal = budgetGlobal;
    }

    public double getGab() {
        return gab;
    }

    public void setBudgetGlobal(double budgetGlobal) {
        this.budgetGlobal = budgetGlobal;
    }

    public void setGab(double gab) {
        this.gab = gab;
    }

}