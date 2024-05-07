package com.hracces.openhr.Controllers;

import com.hracces.openhr.Services.EmployeeService;
import com.hracces.openhr.Services.OpenHRService;
import com.hracces.openhr.Services.TestService;
import com.hracces.openhr.entities.Employee;
import com.hraccess.openhr.dossier.HRDossier;
import com.hraccess.openhr.dossier.HRDossierListIterator;
import com.hraccess.openhr.exception.HRException;
import com.hraccess.openhr.msg.HRMsgExtractData;
import com.hraccess.openhr.msg.HRResultExtractData;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee")

public class EmployeeController {

    private final EmployeeService employeeService;

    private final OpenHRService openHRService;

    private final TestService testService;

    public EmployeeController(EmployeeService employeeService, OpenHRService openHRService, TestService testService) {
        this.employeeService = employeeService;
        this.openHRService = openHRService;
        this.testService = testService;
    }




    @GetMapping("/test")
    public List<Employee> GET() throws ConfigurationException, HRException {

        return   testService.getLabel();

    }

    @GetMapping("/datastructure")
    public List<Employee> getData() throws ConfigurationException, HRException {

           return   employeeService.test();


    }







    @GetMapping("/employees")
    public List<Employee> getAllEmployees() throws ConfigurationException, HRException {

        return openHRService.getEmployees();

    }


    @GetMapping("/load")
    public void load() throws ConfigurationException, HRException {

        employeeService.loadAllEmployeeDossiers();

    }





}
