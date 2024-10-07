package com.hracces.openhr.Services;


import com.hracces.openhr.Repositories.EmployeeRemRepositories;
import com.hracces.openhr.Repositories.EmployeeRepositories;
import com.hracces.openhr.Repositories.OrganisationRepositories;
import com.hracces.openhr.dto.EmployeeAndTotalSalaryResponse;
import com.hracces.openhr.entities.*;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
import com.hraccess.openhr.exception.HRException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {


    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private OrganisationRepositories organisationRepositories;


    @Autowired
    private EmployeeRemRepositories employeeRemRepositories;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public List<EmployeeData> loadEmpBydep (){
        return employeeRepositories.findEmp( );
    }
    public List<Organisation> loadOrg (){
        return organisationRepositories.findOrg();
    }

    private static final String topic = "statusRem";
    public EmployeeAndTotalSalaryResponse getEmployeesAndTotalSalaryByDepartment(String departmentId) throws HRException, ConfigurationException, ParseException {
        List<EmployeeData> allEmployees = loadEmpBydep();
        System.out.println("Filtering for departmentId: " + departmentId);
        BigDecimal totalSalary = BigDecimal.ZERO;
        List<EmployeeData> employeesAndTotalSalary = new ArrayList<>();

        for (EmployeeData employee : allEmployees) {
            System.out.println("Checking employee with idorg: " + employee.getIdorg());
            if (departmentId.equals(employee.getIdorg())) {

                employeesAndTotalSalary.add(employee);
                BigDecimal salary = new BigDecimal(employee.getMtsal());
                totalSalary = totalSalary.add(salary);


            }
        }

        System.out.println("Found " + employeesAndTotalSalary.size() + " employees in department " + departmentId);

        EmployeeAndTotalSalaryResponse response = new EmployeeAndTotalSalaryResponse();
        response.setEmployees(new ArrayList<>(employeesAndTotalSalary));
        response.setTotalSalary(totalSalary); // Stockage de la somme des salaires dans la réponse
        return response;
    }



    public void saveEmpRem(List<EmployeeRem> employeeRem,double budgetGlobal,double gab,String idorg){
        if (employeeRem == null || employeeRem.isEmpty()) {
            throw new IllegalArgumentException("Employee list cannot be null or empty");
        }

        for (EmployeeRem employeeDto : employeeRem) {

            EmployeeRem employeeRec = new EmployeeRem();
            employeeRec.setIdorg(idorg);
            employeeRec.setBudgetGloabl(budgetGlobal);
            employeeRec.setGab(gab);
            employeeRec.setDate(LocalDate.now());
            employeeRec.setNom(employeeDto.getNom());
            employeeRec.setMtsal(employeeDto.getMtsal());
            employeeRec.setPoste(employeeDto.getPoste());
            employeeRec.setTypeStatus(Status.Encours);
            employeeRec.setIdManger("123456");
            employeeRec.setBudgetAnnuel(employeeDto.getBudgetAnnuel());
            employeeRec.setPourcentage(employeeDto.getPourcentage());
            employeeRemRepositories.save(employeeRec);
        }
        List<EmployeeRem> emp=employeeRemRepositories.findAllByIdManger("123456");
        // Create the message payload (you can format it as needed)
        String message = String.format("Saved Employee ID: %s, with Budget de renumeration: %.2f", emp.get(0).getIdManger(), emp.get(0).getBudgetGloabl());

        // Send the message to Kafka
        kafkaTemplate.send(topic, message);
    }

  /* public List<Employee> getLabel() {
    List<Employee> employees = new ArrayList<>();

    try {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");
        parameters.addDataSection(new HRDataSourceParameters.DataSection("00")); // Utilisation de la data section "00"

        // Charger la configuration OpenHR
        PropertiesConfiguration configuration = new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties");

        // Initialiser la session OpenHR
        IHRSession session = HRSessionFactory.getFactory().createSession(configuration);
        IHRDictionary dictionary = session.getDictionary();

        // Accéder à la structure de données ZY
        IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");
        if (dataStructure != null) {
            // Récupérer les types de dossiers dans ZY
            List<IHRDossierType> dossierTypes = dataStructure.getDossierTypes();
            for (IHRDossierType dossierType : dossierTypes) {
                // Explorer les données dans chaque dossier
                List<IHRDataSection> dataSections = dossierType.getDataSections();
                for (IHRDataSection dataSection : dataSections) {
                    List<IHRItem> items = dataSection.getAllItems();

                    for (IHRItem item : items) {
                        // Extraire les données ici et les transformer en objets Employee
                        employees.add(new Employee(item.getName(), item.getLabel()));
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return employees;
}*/

}
