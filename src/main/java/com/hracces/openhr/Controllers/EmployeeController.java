package com.hracces.openhr.Controllers;

import com.hracces.openhr.Services.*;
import com.hracces.openhr.dto.EmployeeAndTotalSalaryResponse;
import com.hracces.openhr.dto.EmployeeDTO;
import com.hracces.openhr.dto.UpdateRecRequest;
import com.hracces.openhr.dto.Zy3bDT0;
import com.hracces.openhr.entities.*;
import com.hraccess.openhr.IHRSessionUser;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.dossier.HRDossierCollectionCommitException;
import com.hraccess.openhr.dossier.HRDossierCollectionException;
import com.hraccess.openhr.exception.*;
import com.hraccess.openhr.msg.HRResultExtractData;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final OpenHRService openHRService;

    private final TestService testService;

    private final LoginService loginService;





    public EmployeeController(EmployeeService employeeService, OpenHRService openHRService, TestService testService, LoginService loginService) {
        this.employeeService = employeeService;
        this.openHRService = openHRService;
        this.testService = testService;
        this.loginService = loginService;

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

    @GetMapping("/emplo")
    public List<EmployeeData> emplo() throws ConfigurationException, HRException {

       return employeeService.loadEmpBYNomPreEmailImageCost();

    }


    @GetMapping("/dep")
    public List<EmployeeData> emplodep() throws ConfigurationException, HRException, ParseException {

      return   employeeService.loadEmpParDepartement();

    }

    @GetMapping("/ZEATT")
    public  List<Employee> loadze() throws ConfigurationException, HRException {

        return employeeService.loadZE();

    }

    @GetMapping("/poste")
    public void poste() throws ConfigurationException, HRException {

         employeeService.poste();

    }

    @GetMapping("/col")
    public List<EmployeeData> col() throws ConfigurationException, HRException {

     return   employeeService.findEmp();

    }

    @GetMapping("/Empdepart/{id}")
    public EmployeeAndTotalSalaryResponse getEmpByDep(@PathVariable("id") String idorg) throws ConfigurationException, HRException, ParseException {

        return   employeeService.getEmployeesAndTotalSalaryByDepartment(idorg);

    }


    @GetMapping("/org")
    public List<Organisation> getBudgetByDep() throws ConfigurationException, HRException {

        return   employeeService.getBudgetByDep();

    }


    @PostMapping("/save/{id}")
    public ResponseEntity<Void> saveEmployeesAndBudget(@RequestBody List<EmployeeRec> employees,@PathVariable("id")String idorg) {
        double minBudget=100000;
        double maxBudget=200000;
        // Calculer le budget min et max ici (vous pouvez utiliser la méthode de service appropriée pour cela)
        minBudget -= employeeService.calculateMinBudget(employees);
        maxBudget -= employeeService.calculateMaxBudget(employees);


        // Enregistrer les employés et les valeurs de budget dans la base de données
        employeeService.saveEmployeesAndBudget(employees, minBudget, maxBudget,idorg);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/affecterBudgetGlobal")
    public List<Organisation> AffecterBudgetGlobal() throws HRException, ConfigurationException, ParseException {
    return employeeService.AffecterBudgetGlobal();
    }



    @PutMapping("/put/{id}")
    public ResponseEntity<Void> putEmployeesAndBudget(@PathVariable("id") String idorg, @RequestBody UpdateRecRequest request) {
        double minBudget = 100000;
        double maxBudget = 200000;

        List<EmployeeRec> employees = request.getEmployees();

        // Calculer le budget min et max ici
        minBudget -= employeeService.calculateMinBudget(employees);
        maxBudget -= employeeService.calculateMaxBudget(employees);

        // Enregistrer les employés et les valeurs de budget dans la base de données
        employeeService.updateEmployeeRecByIdorg(idorg, employees, minBudget, maxBudget);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/department/{id}")
    public ResponseEntity<List<EmployeeRec>> getEmployeesByDepartment(@PathVariable("id") String idorg) {
        List<EmployeeRec> employees = employeeService.getEmployeesByIdOrg(idorg,Status.Encours);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/demande")
    public ResponseEntity<List<String>> getIdMangerByStatus() {
        List<String> idMangers = employeeService.getIdMangerByStatus(Status.Encours);
        return ResponseEntity.ok(idMangers);
    }


    @GetMapping("/count")
    public ResponseEntity<Integer> countgetIdMangerByStatus() {
      int count= employeeService.countgetIdMangerByStatus(Status.Encours);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/delete/{idorg}")
    public ResponseEntity<Map<String, String>> deleteEmployeeRecByIdorgAndCheckDate(@PathVariable("idorg") String idorg) {
        Map<String, String> response = new HashMap<>();
        try {
            employeeService.deleteEmployeeRecByIdorgAndCheckDate(idorg);
            response.put("message", "Enveloppe supprimée avec succès.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }




    @PutMapping("/update/{id}/{date}")
    public List<EmployeeData> updateEmployees(@RequestBody List<EmployeeData> employees,@PathVariable("id") String idOrg, @PathVariable("date") String date) {
        LocalDate currentDate = LocalDate.parse(date);
        return employeeService.updateEmployees(employees,idOrg,currentDate);
    }
@GetMapping("/budgetAnnuel/{id}")
public Double cal(@PathVariable("id")String id ){
    return    employeeService.calculateBudgetAnnuel(id);
}


    @GetMapping("/calculateRemainingSalary/{id}/{date}")
    public double getSalaire(@PathVariable("id") Double mtsal, @PathVariable("date") String date) {
        LocalDate currentDate = LocalDate.parse(date);
        return employeeService.calculateRemainingSalary(mtsal,currentDate);
    }
    @GetMapping("/calculatePourcentageBudgetDep/{global}/{depense}")
    public double calculateBudgetPourcentageDepense(@PathVariable("global") Double budgetGloabl, @PathVariable("depense") double budgetDepense) {

        return employeeService.calculateBudgetPourcentageDepense(budgetGloabl,budgetDepense);
    }

    @GetMapping("/calculatePourcentageBudgetRes/{global}/{restant}")
    public double calculateBudgetPourcentageRestant(@PathVariable("global") Double budgetGloabl, @PathVariable("restant") double budgetRestant) {

        return employeeService.calculateBudgetPourcentageRestant(budgetGloabl,budgetRestant);
    }
@PutMapping("/status/{id}")
public void setStatus(@PathVariable("id")String id){
        employeeService.setStatus(id);
}
    @PutMapping("/statusR/{id}")
    public void setStatusR(@PathVariable("id")String id){
        employeeService.setStatusRefuser(id);
    }

@GetMapping("/login")
public HRResultExtractData login() throws ConfigurationException, HRException {
        return loginService.login();
    }

@PutMapping("/update")
public void Employee() throws Exception {
         employeeService.updateEmployee();
}

    @PutMapping("/add")
    public String addEmployee() {
        return employeeService.addEmployee();
    }
    @GetMapping("/search")
    public List<Integer> findEmployeeDossierNumbersByName() {
        return employeeService.findEmployeeDossierNumbersByName();
    }


    @PutMapping("/test")
    public String testzy() {
        return employeeService.updateAndInsertZy();

    }


    @GetMapping("/values")
    Map<String, Integer> retrieveAttributeIndices() throws HRException{
        return employeeService.retrieveAttributeIndices();
    }

    @GetMapping("/value")
   List<Integer> retrieve() throws HRException{
        return employeeService.retrieve();
    }

    @DeleteMapping("/delete")
    public String deletEmps(){
        return employeeService.deleteEmptyRows();
    }



    @PostMapping("/occur/{idorg}/{numdoss}")
    public String loadAndDisplayLastOccurrence(
            @RequestBody Zy3bDT0 employeeDTO,
            @PathVariable("idorg") String idorg,
            @PathVariable("numdoss") int numdoss) throws HRDossierCollectionException, HRDossierCollectionCommitException {

        return employeeService.loadAndDisplayLastOccurrence(employeeDTO, idorg, numdoss);
    }
    @PostMapping("/login/{username}/{password}")
    public ResponseEntity<?> login(@PathVariable("username") String username, @PathVariable("password") String password) {
        try {
            // Perform login logic and return success response
            String token  = employeeService.login(username, password);


            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            // Handle exceptions and return appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    @GetMapping("/logged")
    public boolean isLoggedIn(){
        return employeeService.isLoggedIn();
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() throws UserConnectionException, AuthenticationException, ConfigurationException, SessionBuildException, SessionConnectionException {
        try {
            // Perform login logic and return success response
            String token = employeeService.logout();
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            // Handle exceptions and return appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getUserRole() {
        try {
            String userRole = employeeService.getRole(); // Appel à un service pour récupérer le rôle de l'utilisateur
            Map<String, String> response = new HashMap<>();
            response.put("role", userRole);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la récupération du rôle de l'utilisateur."));
        }
    }


    @GetMapping("/emploi")
    public List<Poste> emploi() throws HRException, ConfigurationException, ParseException{
        return employeeService.emploi();
    }

    @GetMapping("/motif")
    public List<String> motif() throws HRException, ConfigurationException, ParseException{
        return employeeService.motif();
    }
    @GetMapping("/idManager")
    public List<String> loginManager() throws HRException, ConfigurationException, ParseException{
        return employeeService.loginManager();
    }

}
