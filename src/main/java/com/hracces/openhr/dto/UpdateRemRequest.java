package com.hracces.openhr.dto;

import com.hracces.openhr.entities.EmployeeRec;
import com.hracces.openhr.entities.EmployeeRem;

import java.util.List;

public class UpdateRemRequest {

    private List<EmployeeRem> employees;

    public double getBudgetAnnuel() {
        return budgetAnnuel;
    }

    public void setBudgetAnnuel(double budgetAnnuel) {
        this.budgetAnnuel = budgetAnnuel;
    }

    private double budgetGlobal;
    private double gab;

    private double budgetAnnuel;

    public List<EmployeeRem> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeRem> employees) {
        this.employees = employees;
    }

    public double getBudgetGlobal() {
        return budgetGlobal;
    }

    public void setBudgetGlobal(double budgetGlobal) {
        this.budgetGlobal = budgetGlobal;
    }

    public double getGab() {
        return gab;
    }

    public void setGab(double gab) {
        this.gab = gab;
    }
}
