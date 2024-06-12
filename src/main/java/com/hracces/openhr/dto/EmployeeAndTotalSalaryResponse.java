package com.hracces.openhr.dto;

import com.hracces.openhr.entities.EmployeeData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EmployeeAndTotalSalaryResponse {
    private List<EmployeeData> employees;
    private BigDecimal totalSalary;
}
