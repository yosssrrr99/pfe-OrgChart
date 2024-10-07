package com.hracces.openhr.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hracces.openhr.Repositories.*;
import com.hracces.openhr.dto.EmployeeAndTotalSalaryResponse;
import com.hracces.openhr.dto.Zy3bDT0;
import com.hracces.openhr.entities.*;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import com.hraccess.openhr.exception.*;
import com.hraccess.openhr.msg.*;
import com.hraccess.openhr.security.UserDescription;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.id.IntegralDataTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j
public class EmployeeService {
    @Autowired
    private EmployeeRemRepositories employeeRemRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;
    @Autowired
    private EmployeeRecRepositories employeeRecRepositories;

    @Autowired
    private OrganisationRepositories organisationRepositories;

    @Autowired
    private EmployeeOrgRepositories employeeOrgRepositories;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;






  /*  @PostConstruct
    public void init() throws HRException, ConfigurationException {
        HRApplication.configureLogs("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");
        session = HRSessionFactory.getFactory().createSession(
                new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));
        user = session.connectUser(username, password);
    }*/

    /*@PreDestroy
    public void cleanup() throws UserConnectionException {
        if (user != null && user.isConnected()) {
            user.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }*/










    public List<String> emploii()  {

   return employeeRepositories.findPoste();
    }

   public void updateAffect(EmployeeData emp,String poste , String motif,String idorg){
        emp.setMotif(motif);
        emp.setPoste(poste);
        emp.setNomorg(idorg);
        employeeRepositories.save(emp);

    }








    private final Map<String, SalaryRange> salaryRanges = Map.of(
            "junior", new SalaryRange(2000, 5000),
            "senior", new SalaryRange(5000, 10000),
            "confirme", new SalaryRange(4000, 8000),
            "anapec", new SalaryRange(1500, 3000)
    );

    public int calculateMinBudget(List<EmployeeRec> employees) {
        int totalMinCost = 0;
        for (EmployeeRec employee : employees) {
            SalaryRange range = salaryRanges.get(employee.getClassification());
            if (range != null) {
                totalMinCost += employee.getNumber() * range.getMinSalary();
            }
        }
        return totalMinCost;
    }

    public int calculateMaxBudget(List<EmployeeRec> employees) {
        int totalMaxCost = 0;
        for (EmployeeRec employee : employees) {
            SalaryRange range = salaryRanges.get(employee.getClassification());
            if (range != null) {
                totalMaxCost += employee.getNumber() * range.getMaxSalary();
            }
        }
        return totalMaxCost;
    }

    public Double calculateBudgetAnnuel(String idOrg) {

        return employeeRepositories.BudgetAnnuelByDep(idOrg);
    }

    public Map<String, Integer> calculateBudget(@RequestBody List<EmployeeRec> employees) {
        int minBudget = calculateMinBudget(employees);
        int maxBudget = calculateMaxBudget(employees);
        Map<String, Integer> result = new HashMap<>();
        result.put("minBudget", minBudget);
        result.put("maxBudget", maxBudget);
        return result;
    }
    private static final String topic = "notifications";

    public void saveEmployeesAndBudget(List<EmployeeRec> employeeDtos, double budgetGlobal, double gab, String idorg) {
        for (EmployeeRec employeeDto : employeeDtos) {
            EmployeeRec employeeRec = new EmployeeRec();
            employeeRec.setIdorg(idorg);
            employeeRec.setNumber(employeeDto.getNumber());
            employeeRec.setClassification(employeeDto.getClassification());
            employeeRec.setBudgetGlobal(budgetGlobal);
            employeeRec.setGab(gab);
            employeeRec.setDate(LocalDate.now());
            employeeRec.setTypeStatus(Status.Encours);
            employeeRec.setIdManger("123456");
            employeeRecRepositories.save(employeeRec);


        }
        List<EmployeeRec> emp=employeeRecRepositories.findAllByIdManger("123456");
        // Create the message payload (you can format it as needed)
        String message = String.format("Saved Employee ID: %s, with Budget: %.2f", emp.get(0).getIdManger(), emp.get(0).getBudgetGlobal());

        // Send the message to Kafka
        kafkaTemplate.send(topic, message);
    }


    public List<EmployeeRec> getHistory(String depId) {

        return employeeRecRepositories.findAllByIdorg(depId);

    }


    public List<EmployeeRem> getHistoryRem(String depId) {

        return employeeRemRepositories.findAllByIdManger(depId);

    }

    public List<EmployeeRec> getEmployeesByIdOrg(String idorg) {
        return employeeRecRepositories.findAllByIdManger(idorg);
    }

    public List<String> getIdMangerByStatus(Status status) {
        return employeeRecRepositories.findMangerTypeStatus(status);
    }

    public int countgetIdMangerByStatus(Status status) {
        return employeeRecRepositories.countfindMangerTypeStatus(status);
    }

    public int countgetIdMangerByStatusRem(Status status) {
        return employeeRemRepositories.countfindMangerTypeStatus(status);
    }

    public void updateEmployeeRecByIdorg(String idorg, List<EmployeeRec> updatedEmployeeRec, double budgetGlobal, double gab) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdManger(idorg);
        String idManger = "";
        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);

            if (i < updatedEmployeeRec.size()) {
                EmployeeRec updatedRec = updatedEmployeeRec.get(i);
                emp.setNumber(updatedRec.getNumber());
                emp.setClassification(updatedRec.getClassification());
                emp.setDate(LocalDate.now());
                idManger = updatedRec.getIdorg();
            }

            emp.setBudgetGlobal(budgetGlobal);
            emp.setGab(gab);

            employeeRecRepositories.save(emp);
        }

        for (int i = employeeRecs.size(); i < updatedEmployeeRec.size(); i++) {
            EmployeeRec newRec = updatedEmployeeRec.get(i);
            newRec.setIdManger(idorg); // Assuming emp has idManager attribut
            newRec.setIdorg(idManger); // Assuming idorg is passed as a parameter
            newRec.setBudgetGlobal(budgetGlobal);
            newRec.setDate(LocalDate.now());
            newRec.setTypeStatus(Status.Encours);
            employeeRecRepositories.save(newRec);
        }
    }


    public void updateEmployeeRemByIdorg(String idorg, List<EmployeeRem> updatedEmployeeRec, double budgetGlobal, double gab, double budgetAnnuel) {
        List<EmployeeRem> employeeRecs = employeeRemRepositories.findAllByIdManger(idorg);
        String idManger = "";
        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRem emp = employeeRecs.get(i);

            if (i < updatedEmployeeRec.size()) {
                EmployeeRem updatedRec = updatedEmployeeRec.get(i);
                emp.setNom(updatedRec.getNom());
                emp.setMtsal(updatedRec.getMtsal());
                emp.setPourcentage(updatedRec.getPourcentage());
                emp.setBudgetAnnuel(budgetAnnuel);
                emp.setDate(LocalDate.now());
                idManger = updatedRec.getIdorg();
            }

            emp.setBudgetGloabl(budgetGlobal);
            emp.setGab(gab);

            employeeRemRepositories.save(emp);
        }

        for (int i = employeeRecs.size(); i < updatedEmployeeRec.size(); i++) {
            EmployeeRem newRec = updatedEmployeeRec.get(i);
            newRec.setIdManger(idorg); // Assuming emp has idManager attribut
            newRec.setIdorg(idManger); // Assuming idorg is passed as a parameter
            newRec.setBudgetGloabl(budgetGlobal);
            newRec.setDate(LocalDate.now());
            newRec.setTypeStatus(Status.Encours);
            employeeRemRepositories.save(newRec);
        }
    }

    public void supprimerEmpRem(Long id) {
        employeeRemRepositories.deleteById(id);
    }

    public List<EmployeeRec> findDemandeRec(String id) {
        return employeeRecRepositories.findByIdManagerAndStatusWithLatestDate(id);
    }


    public List<EmployeeRem> findDemandeRem(String id) {
        return employeeRemRepositories.findByIdManagerRemAndStatusWithLatestDate(id);
    }
    public void supprimerEmpRec(int id) {
        employeeRecRepositories.deleteById(id);
    }


    public void setStatus(String id) {

        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdManger(id);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Approuver);
            emp.setStatus(true);
            employeeRecRepositories.save(emp);
        }




    }

    public void setStatusRefuser(String id) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdManger(id);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Refuser);
            //  emp.setStatus(true);
            employeeRecRepositories.save(emp);
        }


    }


    public void deleteEmployeeRecByIdorgAndCheckDate(String idorg) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdorg(idorg);
        if (!employeeRecs.isEmpty()) {
            for (EmployeeRec employeeRec : employeeRecs) {
                LocalDate dateAdded = employeeRec.getDate();
                LocalDate currentDate = LocalDate.now();
                long daysBetween = ChronoUnit.DAYS.between(dateAdded, currentDate);
                System.out.println("bbbb" + daysBetween);
                if (daysBetween <= 1) {
                    employeeRecRepositories.delete(employeeRec);
                } else {
                    throw new RuntimeException("Vous ne pouvez supprimer l'enveloppe que dans la fenêtre de 1 jour après l'ajout.");
                }
            }
        }
    }


    public double calculateRemainingSalary(double mtsal, LocalDate date) {
        DecimalFormat df = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));
        LocalDate endOfYear = LocalDate.of(date.getYear(), 12, 31);
        LocalDate startOfYear = LocalDate.of(date.getYear(), 1, 1);
        long daysRemaining = ChronoUnit.DAYS.between(date, endOfYear) + 1;
        long nbDaysInYear = ChronoUnit.DAYS.between(startOfYear, endOfYear) + 1;
        System.out.println("daysRemaining: " + daysRemaining);
        System.out.println("endOfYear: " + endOfYear);
        double dailySalary = mtsal / nbDaysInYear;
        System.out.println("dailySalary: " + dailySalary);
        double remainingSalary = dailySalary * daysRemaining;

        // Formater le résultat avec DecimalFormat
        String formattedSalary = df.format(remainingSalary);
        System.out.println("Formatted remainingSalary: " + formattedSalary);


        remainingSalary = Double.parseDouble(formattedSalary.replace(',', '.'));

        System.out.println("remainingSalary: " + remainingSalary);
        return Math.round(remainingSalary);
    }


    public List<EmployeeData> updateEmployees(List<EmployeeData> employees, String idOrg, LocalDate currentDate) {
        List<EmployeeData> updatedEmployees = new ArrayList<>();

        List<String> orgName = employeeRepositories.findNameById(idOrg);

        for (EmployeeData employee : employees) {
            EmployeeData emp = employeeRepositories.findById(employee.getId()).orElse(null);
            LocalDate newDateS = currentDate.plusDays(1);
            Date newDateSAsDate = Date.from(newDateS.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Mise à jour de la date de sortie de l'employé existant
            emp.setDateS(newDateSAsDate);

            // Sauvegarde de l'employé mis à jour
            // Sauvegarde de l'employé mis à jour
            employeeRepositories.save(emp);

            // Création d'un nouvel employé avec les modifications apportées à l'organisation
            EmployeeData newEmployee = new EmployeeData();
            newEmployee.setMatcle(employee.getMatcle());
            Date dateEff = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            newEmployee.setDateE(dateEff); // Même date d'entrée que l'employé existant
            newEmployee.setDateS(employee.getDateS()); // Même date de sortie que l'employé existant
            newEmployee.setIdorg(idOrg);
            newEmployee.setNomorg(orgName.get(0)); // Nouveau nom d'organisation
            newEmployee.setBlobData(employee.getBlobData());
            newEmployee.setNomuse(employee.getNomuse());
            newEmployee.setPrenom(employee.getPrenom());
            newEmployee.setPoste(employee.getPoste());
            newEmployee.setMtsal(employee.getMtsal());

            updatedEmployees.add(newEmployee); // Ajout du nouvel employé à la liste de retour
        }

        // Enregistrement de tous les employés mis à jour une fois la boucle terminée
        return employeeRepositories.saveAll(updatedEmployees);
    }


    public double calculateBudgetPourcentageDepense(double budgetGlobal, double budgetDepense) {
        return (budgetDepense / budgetGlobal) * 100;
    }

    public double calculateBudgetPourcentageRestant(double budgetGlobal, double budgetRestant) {
        return (budgetRestant / budgetGlobal) * 100;
    }















    private String truncateString(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }





// Method to subtract one day from the given date


    private Timestamp addOneDay(String dateStr) throws ParseException {
        // Using LocalDate for adding one day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);
        localDate = localDate.minusDays(1);
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    private java.sql.Date subtractOneDay(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);

        // Convertir java.util.Date en java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
        return sqlDate;
    }

    // Method to get default values for missing fields











    public static Timestamp subtractOneDay(String dateStr) throws ParseException {
        // Convertir la chaîne de date en objet Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateStr);

        // Créer un objet Calendar et définir la date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Soustraire un jour
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        // Obtenir la nouvelle date en tant qu'objet Date
        Date modifiedDate = calendar.getTime();

        // Convertir la date modifiée en Timestamp
        return new Timestamp(modifiedDate.getTime());
    }


    //loaddossir







    public List<String> motifa() {

      return employeeRepositories.findMotif();

    }






    //Notification




    public Map<Integer, List<String>> getOrganigramme() {
        List<EmployeeOrg> employees = employeeOrgRepositories.findAllByOrderByNbOrdreAsc();
        return employees.stream()
                .collect(Collectors.groupingBy(EmployeeOrg::getNudoss,
                        Collectors.mapping(EmployeeOrg::getParentId, Collectors.toList())));
    }

    public Map<String, List<String>> getEmployee() {
        List<EmployeeData> employees = employeeRepositories.findAll();
        return employees.stream()
                .collect(Collectors.groupingBy(EmployeeData::getIdorg,
                        Collectors.mapping(EmployeeData::getNomuse, Collectors.toList())));
    }
    public Map<String, List<String>> getEmployee2() {
        List<EmployeeData> employees = employeeRepositories.findAll();

        return employees.stream()
                .collect(Collectors.groupingBy(
                        EmployeeData::getIdorg,
                        Collectors.mapping(
                                emp -> emp.getNomuse() + "," + emp.getPrenom() + "," + emp.getPoste(),
                                Collectors.toList()
                        )
                ));
    }

    public TreeNode buildHierarchy(Map<Integer, List<String>> data) {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        TreeNode root = null;

        // Build the tree structure
        for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
            List<String> path = entry.getValue();
            TreeNode currentNode = null;

            for (int i = path.size() - 1; i >= 0; i--) {
                String nodeId = path.get(i);
                TreeNode node = nodeMap.computeIfAbsent(nodeId, k -> new TreeNode(nodeId));

                if (i == path.size() - 1) {
                    root = node;
                } else {
                    if (currentNode != null && !currentNode.findChildById(nodeId).isPresent()) {
                        currentNode.addChild(node);
                    }
                }
                currentNode = node;
            }
        }

        // Retrieve all EmployeeOrg objects
        List<EmployeeOrg> employees = employeeOrgRepositories.findAllByOrderByNbOrdreAsc();
        if (employees.isEmpty()) {
            System.out.println("No EmployeeOrg data retrieved.");
        } else {
            for (EmployeeOrg employee : employees) {
                System.out.println("Retrieved employee: ID=" + employee.getIdorg() + ", nameEmployee=" + employee.getNameEmployee() + ", nameManager=" + employee.getNameManager());
            }
        }

        // Update tree nodes with employee data
        for (EmployeeOrg employee : employees) {
            TreeNode node = nodeMap.get(employee.getIdorg());
            if (node != null) {
                System.out.println("Assigning to node ID: " + employee.getIdorg());

                // Initialize the set for unique employee names
                if (node.getNameEmployees() == null) {
                    node.setNameEmployees(new HashSet<>());
                }

                // Add the employee to the set
                if (employee.getNameEmployee() != null) {
                    node.getNameEmployees().add(employee.getNameEmployee());
                }

                // Assign the nameManager
                node.setNameManager(employee.getNameManager());

                System.out.println("Assigned nameEmployee: " + employee.getNameEmployee() + " and nameManager: " + employee.getNameManager());
            } else {
                System.out.println("No node found for ID: " + employee.getIdorg());
            }
        }

        // Retrieve additional employee data
        Map<String, List<String>> employeeDataMap = getEmployee();
        if (employeeDataMap.isEmpty()) {
            System.out.println("No EmployeeData data retrieved.");
        } else {
            for (Map.Entry<String, List<String>> entry : employeeDataMap.entrySet()) {
                System.out.println("Retrieved employeeData for ID=" + entry.getKey() + ": " + entry.getValue());
            }
        }

        // Update tree nodes with the list of employees from EmployeeData
        for (Map.Entry<String, List<String>> entry : employeeDataMap.entrySet()) {
            TreeNode node = nodeMap.get(entry.getKey());
            if (node != null) {
                List<String> employeeList = entry.getValue();
                if (node.getNameEmployees() == null) {
                    node.setNameEmployees(new HashSet<>());
                }

                // Add the employees from the list to the set
                node.getNameEmployees().addAll(employeeList);
                System.out.println("Added nameEmployees from EmployeeData: " + employeeList);

                // Assign the nameManager
                if (node.getNameManager() == null && employeeList.size() >= 1) {
                    node.setNameManager(employeeList.get(0)); // Utiliser le deuxième élément comme nom du gestionnaire
                    System.out.println("Assigned nameManager from EmployeeData: " + employeeList.get(0));
                }
            } else {
                System.out.println("No node found for ID: " + entry.getKey());
            }
        }

        return root;
    }

    public TreeNode buildHierarchy2(Map<Integer, List<String>> data) {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        TreeNode root = null;

        // Build the tree structure
        for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
            List<String> path = entry.getValue();
            TreeNode currentNode = null;

            for (int i = path.size() - 1; i >= 0; i--) {
                String nodeId = path.get(i);
                TreeNode node = nodeMap.computeIfAbsent(nodeId, k -> new TreeNode(nodeId));

                if (i == path.size() - 1) {
                    root = node;
                } else {
                    if (currentNode != null && !currentNode.findChildById(nodeId).isPresent()) {
                        currentNode.addChild(node);
                    }
                }
                currentNode = node;
            }
        }

        // Retrieve all EmployeeOrg objects
        List<EmployeeOrg> employees = employeeOrgRepositories.findAllByOrderByNbOrdreAsc();
        if (employees.isEmpty()) {
            System.out.println("No EmployeeOrg data retrieved.");
        } else {
            for (EmployeeOrg employee : employees) {
                System.out.println("Retrieved employee: ID=" + employee.getIdorg() + ", nameEmployee=" + employee.getNameEmployee() + ", nameManager=" + employee.getNameManager());
            }
        }

        // Update tree nodes with employee data
        for (EmployeeOrg employee : employees) {
            TreeNode node = nodeMap.get(employee.getIdorg());
            if (node != null) {
                System.out.println("Assigning to node ID: " + employee.getIdorg());

                // Initialize the set for unique employee names
                if (node.getNameEmployees() == null) {
                    node.setNameEmployees(new HashSet<>());
                }

                // Add the employee to the set
                if (employee.getNameEmployee() != null) {
                    node.getNameEmployees().add(employee.getNameEmployee());
                }

                // Assign the nameManager
                node.setNameManager(employee.getNameManager());

                System.out.println("Assigned nameEmployee: " + employee.getNameEmployee() + " and nameManager: " + employee.getNameManager());
            } else {
                System.out.println("No node found for ID: " + employee.getIdorg());
            }
        }

        // Retrieve additional employee data
        Map<String, List<String>> employeeDataMap = getEmployee2();
        if (employeeDataMap.isEmpty()) {
            System.out.println("No EmployeeData data retrieved.");
        } else {
            for (Map.Entry<String, List<String>> entry : employeeDataMap.entrySet()) {
                System.out.println("Retrieved employeeData for ID=" + entry.getKey() + ": " + entry.getValue());
            }
        }

        // Update tree nodes with the list of employees from EmployeeData
        for (Map.Entry<String, List<String>> entry : employeeDataMap.entrySet()) {
            TreeNode node = nodeMap.get(entry.getKey());
            if (node != null) {
                List<String> employeeList = entry.getValue();
                if (node.getNameEmployees() == null) {
                    node.setNameEmployees(new HashSet<>());
                }

                // Add the employees from the list to the set
                node.getNameEmployees().addAll(employeeList);
                System.out.println("Added nameEmployees from EmployeeData: " + employeeList);

                // Assign the nameManager
                if (node.getNameManager() == null && employeeList.size() >= 1) {
                    node.setNameManager(employeeList.get(0)); // Utiliser le deuxième élément comme nom du gestionnaire
                    System.out.println("Assigned nameManager from EmployeeData: " + employeeList.get(0));
                }
            } else {
                System.out.println("No node found for ID: " + entry.getKey());
            }
        }

        return root;
    }
    // Generate a random name from a predefined list
    private String generateRandomName() {
        String[] names = {"John Doe", "Jane Smith", "Alex Johnson", "Emily Davis"};
        Random random = new Random();
        return names[random.nextInt(names.length)];
    }


    // Generate a random name from a predefined list


    public List<EmployeeOrg> getChildren(String parentid){
        return employeeOrgRepositories.findByParentIdOrderByNbOrdreAsc(parentid);
    }
    public List<EmployeeOrg> getChildrenByOrder(String parentId, int nbOrdre) {
        // Récupérer les nœuds enfants du parent avec nbOrdre spécifique
        return employeeOrgRepositories.findByParentIdAndNbOrdre(parentId, nbOrdre);
    }

    public List<EmployeeOrg> getAllLevels(List<Integer> levels) {
        return employeeOrgRepositories.findAllByLevels(levels);
    }

    public EmployeeData updateU0(Zy3bDT0 zy3bDT0,String idorg,int nudoss){
        EmployeeData emp=employeeRepositories.findByNudoss(nudoss);
        emp.setMotif(zy3bDT0.getTypemotif());
        emp.setIdorg(idorg);
        return employeeRepositories.save(emp);


    }
}