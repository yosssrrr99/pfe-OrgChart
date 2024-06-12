package com.hracces.openhr.entities;

public class SalaryRange {
    private int minSalary;
    private int maxSalary;

    // Getters and Setters

    public SalaryRange(int minSalary, int maxSalary) {
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public int getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(int minSalary) {
        this.minSalary = minSalary;
    }

    public int getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(int maxSalary) {
        this.maxSalary = maxSalary;
    }
}