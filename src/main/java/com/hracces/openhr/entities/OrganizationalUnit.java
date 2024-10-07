package com.hracces.openhr.entities;

import java.util.ArrayList;
import java.util.List;
public class OrganizationalUnit {
    private String unitCode;
    private int hierarchyLevel;
    private OrganizationalUnit parentUnit;
    private List<OrganizationalUnit> subUnits;

    public OrganizationalUnit(String unitCode) {
        this.unitCode = unitCode;
        this.subUnits = new ArrayList<>();
    }

    public OrganizationalUnit(String unitCode, int hierarchyLevel) {
        this.unitCode = unitCode;
        this.hierarchyLevel = hierarchyLevel;
    }

    // Getter and Setter for unitCode
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    // Getter and Setter for hierarchyLevel
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    // Getter and Setter for parentUnit
    public OrganizationalUnit getParentUnit() {
        return parentUnit;
    }

    public void setParentUnit(OrganizationalUnit parentUnit) {
        this.parentUnit = parentUnit;
    }

    // Method to add subUnit
    public void addSubUnit(OrganizationalUnit subUnit) {
        this.subUnits.add(subUnit);
    }

    // Getter for subUnits
    public List<OrganizationalUnit> getSubUnits() {
        return subUnits;
    }

    @Override
    public String toString() {
        return "OrganizationalUnit{" +
                "unitCode='" + unitCode + '\'' +
                ", hierarchyLevel=" + hierarchyLevel +
                ", parentUnit=" + (parentUnit != null ? parentUnit.unitCode : "None") +
                ", subUnits=" + subUnits +
                '}';
    }
}
