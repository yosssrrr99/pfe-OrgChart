package com.hracces.openhr.Controllers;

import com.hracces.openhr.Services.*;
import com.hracces.openhr.dto.EmployeeAndTotalSalaryResponse;
import com.hracces.openhr.dto.UpdateRecRequest;
import com.hracces.openhr.dto.UpdateRemRequest;
import com.hracces.openhr.dto.Zy3bDT0;
import com.hracces.openhr.entities.*;
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

    private final NotificationService notificationService;

    private final LoginService loginService;





    public EmployeeController(EmployeeService employeeService, OpenHRService openHRService, TestService testService, NotificationService notificationService, LoginService loginService) {
        this.employeeService = employeeService;
        this.openHRService = openHRService;
        this.testService = testService;
        this.notificationService = notificationService;
        this.loginService = loginService;

    }







  /*  @GetMapping("/datastructure")
    public List<Employee> getData() throws ConfigurationException, HRException {

           return   employeeService.test();


    }*/









   /* @GetMapping("/load")
    public void load() throws ConfigurationException, HRException {

        employeeService.loadAllEmployeeDossiers();

    }*/

   /* @GetMapping("/emplo")
    public List<EmployeeData> emplo() throws ConfigurationException, HRException {

       return employeeService.loadEmpBYNomPreEmailImageCost();

    }*/


  /*  @GetMapping("/dep")
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

    }*/




    /*@GetMapping("/Empdepart/{id}")
    public EmployeeAndTotalSalaryResponse getEmpByDep(@PathVariable("id") String idorg) throws ConfigurationException, HRException, ParseException {

        return   employeeService.getEmployeesAndTotalSalaryByDepartment(idorg);

    }*/

    @GetMapping("/Empdepart/{id}")
    public EmployeeAndTotalSalaryResponse getEmpByDep(@PathVariable("id") String idorg) throws ConfigurationException, HRException, ParseException {

        return   testService.getEmployeesAndTotalSalaryByDepartment(idorg);

    }


   /* @GetMapping("/org")
    public List<Organisation> getBudgetByDep() throws ConfigurationException, HRException {

        return   employeeService.getBudgetByDep();

    }*/


    @GetMapping("/org")
    public List<Organisation> getBudgetByDep() throws ConfigurationException, HRException {

        return   testService.loadOrg();

    }




    /*@PostMapping("/save/{budget}/{gab}/{id}")
    public ResponseEntity<Void> saveEmployeesAndBudget(@RequestBody List<EmployeeRec> employees,@PathVariable("budget")double budgetGlobal,@PathVariable("gab")double gab,@PathVariable("id")String idorg) {

        // Enregistrer les employés et les valeurs de budget dans la base de données
        employeeService.saveEmployeesAndBudget(employees, budgetGlobal, gab,idorg);

        return ResponseEntity.ok().build();
    }*/
@PutMapping("/updateuo/{idorg}/{nudoss}")
    public EmployeeData updateU0(@RequestBody Zy3bDT0 zy3bDT0,@PathVariable("idorg") String idorg,@PathVariable("nudoss") int nudoss){
       return employeeService.updateU0(zy3bDT0,idorg,nudoss);


    }

    @PostMapping("/save/{budget}/{gab}/{id}")
    public ResponseEntity<String> saveEmployeesAndBudget2(@RequestBody List<EmployeeRec> employees,@PathVariable("budget")double budgetGlobal,@PathVariable("gab")double gab,@PathVariable("id")String idorg) {

        employeeService.saveEmployeesAndBudget(employees, budgetGlobal, gab, idorg);
        return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");
    }

    @PostMapping("/saves/{budget}/{gab}/{id}")
    public ResponseEntity<String> saveEmpRem(@RequestBody List<EmployeeRem> employeeRem,@PathVariable("budget")double budgetGlobal,@PathVariable("gab")double gab,@PathVariable("id")String idorg) {



        // Enregistrer les employés et les valeurs de budget dans la base de données
        testService.saveEmpRem(employeeRem, budgetGlobal, gab,idorg);

        return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");

    }

   /* @GetMapping("/affecterBudgetGlobal")
    public List<Organisation> AffecterBudgetGlobal() throws HRException, ConfigurationException, ParseException {
    return employeeService.AffecterBudgetGlobal();
    }
*/


    @PutMapping("/put/{id}")
    public ResponseEntity<Void> putEmployeesAndBudget(@PathVariable("id") String idorg, @RequestBody UpdateRecRequest updateRecRequest) {

        List<EmployeeRec> employees = updateRecRequest.getEmployees();
        double budgetGlobal = updateRecRequest.getBudgetGlobal();
        double gab = updateRecRequest.getGab();

        // Enregistrer les employés et les valeurs de budget dans la base de données
        employeeService.updateEmployeeRecByIdorg(idorg, employees, budgetGlobal, gab);


        return ResponseEntity.ok().build();
    }

    @PutMapping("/putRem/{id}")
    public ResponseEntity<Void> putRemEmployeesAndBudget(
            @PathVariable("id") String idorg,
            @RequestBody List<UpdateRemRequest> updateRequests) {

        // Process each request
        for (UpdateRemRequest updateRecRequest : updateRequests) {
            List<EmployeeRem> employees = updateRecRequest.getEmployees();
            double budgetGlobal = updateRecRequest.getBudgetGlobal();
            double gab = updateRecRequest.getGab();
            double budgetAnnuel=updateRecRequest.getBudgetAnnuel();

            // Enregistrer les employés et les valeurs de budget dans la base de données
            employeeService.updateEmployeeRemByIdorg(idorg, employees, budgetGlobal, gab,budgetAnnuel);
        }

        return ResponseEntity.ok().build();
    }
    @GetMapping("/demandeAff/{id}")
    public List<EmployeeRec> findDemandeRec(@PathVariable("id") String id){
        return employeeService.findDemandeRec(id);
    }
    @GetMapping("/demandeAffRem/{id}")
    public List<EmployeeRem> findDemandeRem(@PathVariable("id") String id){
        return employeeService.findDemandeRem(id);
    }

    @DeleteMapping("/supp/{id}")
    public void deleteById(@PathVariable("id") int id){
         employeeService.supprimerEmpRec(id);
    }

    @DeleteMapping("/suppRem/{id}")
    public void deleteRemById(@PathVariable("id") Long id){
        employeeService.supprimerEmpRem(id);
    }
    @GetMapping("/department/{id}")
    public ResponseEntity<List<EmployeeRec>> getEmployeesByDepartment(@PathVariable("id") String idorg) {
        List<EmployeeRec> employees = employeeService.getEmployeesByIdOrg(idorg);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/departmentRem/{id}")
    public ResponseEntity<List<EmployeeRem>> getEmployeesByDepartmentRem(@PathVariable("id") String idorg) {
        List<EmployeeRem> employees = employeeService.getHistoryRem(idorg);
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
    @GetMapping("/countRem")
    public ResponseEntity<Integer> countgetIdMangerByStatusRem() {
        int count= employeeService.countgetIdMangerByStatusRem(Status.Encours);
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
public ResponseEntity<String> setStatus(@PathVariable("id")String id){
        notificationService.updateStatusApp(id);
    return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");
}



    @PutMapping("/statusR/{id}")
    public ResponseEntity<String> setStatusR(@PathVariable("id")String id){
        notificationService.updateStatusRef(id);
        return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");
    }


    @PutMapping("/statusRem/{id}")
    public ResponseEntity<String> setStatusRem(@PathVariable("id")String id){
        notificationService.updateStatusAppRem(id);
        return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");
    }
    @PutMapping("/statusRRem/{id}")
    public ResponseEntity<String> setStatusRRem(@PathVariable("id")String id){
        notificationService.updateStatusRefRem(id);
        return ResponseEntity.ok("Employees and budget saved successfully and message sent to Kafka");
    }


    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable("id") Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/manager/{managerId}")
    public List<Notification> getNotificationsByManagerId(@PathVariable("managerId") String managerId) {
        return notificationService.getNotificationsByManagerId(managerId);
    }
    @GetMapping("/nbNotif/{id}")
    public int nbNotif(@PathVariable("id") String idManager){
        return notificationService.nbNotif(idManager);
    }





    @GetMapping("/zy")
    public Map<Integer, List<String>> executeQuery() throws UserConnectionException, ConfigurationException, AuthenticationException, SessionBuildException, SessionConnectionException {
        return employeeService.getOrganigramme();

    }
    @GetMapping("/tree")
    public TreeNode getOrganigrammeAsTree() {
        Map<Integer, List<String>> data=employeeService.getOrganigramme();
        return employeeService.buildHierarchy(data);
    }
    @GetMapping("/tree2")
    public TreeNode getOrganigrammeAsTree2() {
        Map<Integer, List<String>> data=employeeService.getOrganigramme();
        return employeeService.buildHierarchy2(data);
    }
    @GetMapping("/emp")
    public Map<String, List<String>> getEmployee() {
        return employeeService.getEmployee();
    }


    @GetMapping("/emp2")
    public Map<String, List<String>> getEmployee2() {
        return employeeService.getEmployee2();
    }






   /* @PostMapping("/login/{username}/{password}")
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
    }*/




  /*  @GetMapping("/emploii")
    public List<Poste> emploi() throws HRException, ConfigurationException, ParseException{
        return employeeService.emploi();
    }*/
    @GetMapping("/emploi")
    public List<String> emploii() throws HRException, ConfigurationException, ParseException{
        return employeeService.emploii();
    }

    @PutMapping("/emploi/{emploi}/{motif}/{idorg}")
    public void puta(@RequestBody EmployeeData emp,@PathVariable("emploi") String emploi,@PathVariable("motif") String motif,@PathVariable("idorg") String idorg) throws HRException, ConfigurationException, ParseException{
         employeeService.updateAffect(emp,emploi,motif,idorg);
    }

    /*@GetMapping("/motifa")
    public List<String> motif() throws HRException, ConfigurationException, ParseException{
        return employeeService.motif();
    }*/
    @GetMapping("/motif")
    public List<String> motifa() throws HRException, ConfigurationException, ParseException{
        return employeeService.motifa();
    }
    /*@GetMapping("/idManager")
    public List<String> loginManager() throws HRException, ConfigurationException, ParseException{
        return employeeService.loginManager();
    }*/
    @GetMapping("/children/{parenid}")
    public List<EmployeeOrg> getChildren(@PathVariable("parenid") String parentid){
        return employeeService.getChildren(parentid);
    }
    @GetMapping("/children/{parentId}/{nbOrdre}")
    public ResponseEntity<List<EmployeeOrg>> getChildrenByOrder(
            @PathVariable String parentId,
            @PathVariable int nbOrdre) {
        List<EmployeeOrg> nodes = employeeService.getChildrenByOrder(parentId, nbOrdre);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/all-levels")
    public ResponseEntity<List<EmployeeOrg>> getAllLevels(@RequestParam List<Integer> levels) {
        List<EmployeeOrg> units = employeeService.getAllLevels(levels);
        return ResponseEntity.ok(units);
    }
}
