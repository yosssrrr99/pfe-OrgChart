package com.hracces.openhr.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hracces.openhr.Repositories.EmployeeRecRepositories;
import com.hracces.openhr.Repositories.EmployeeRepositories;
import com.hracces.openhr.Repositories.OrganisationRepositories;
import com.hracces.openhr.dto.EmployeeAndTotalSalaryResponse;
import com.hracces.openhr.entities.*;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import com.hraccess.openhr.exception.HRException;
import com.hraccess.openhr.exception.UserConnectionException;
import com.hraccess.openhr.msg.HRMsgExtractData;
import com.hraccess.openhr.msg.HRResultExtractData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class EmployeeService {

    @Autowired
    private EmployeeRepositories employeeRepositories;
    @Autowired
    private EmployeeRecRepositories employeeRecRepositories;

    @Autowired
    private OrganisationRepositories organisationRepositories;

    private final HRSessionFactory hrSessionFactory;
    private final String username = "TALAN2PR";
    private final String password = "HRA2023!";
    private IHRSession session;
    private IHRUser user;

    @Autowired
    public EmployeeService(HRSessionFactory hrSessionFactory) {
        this.hrSessionFactory = hrSessionFactory;
    }

    @PostConstruct
    public void init() throws HRException, ConfigurationException {
        HRApplication.configureLogs("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");
        session = HRSessionFactory.getFactory().createSession(
                new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));
        user = session.connectUser(username, password);
    }

    @PreDestroy
    public void cleanup() throws UserConnectionException {
        if (user != null && user.isConnected()) {
            user.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public void loadAllEmployeeDossiers() throws HRException, ConfigurationException {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");
        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("10"));


        HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


        IHRDictionary dictionary = session.getDictionary();


        // HRDossierListIterator iterator = dossierCollection.loadDossiers("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");



        IHRConversation conversation = user.getMainConversation();
// Retrieving one of the user's roles to send the message
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(100);
        request.setSqlStatement("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");

        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);


        ObjectMapper objectMapper = new ObjectMapper();


        List<Map<String, Object>> jsonData = new ArrayList<>();

        if (result != null && result.getRows() != null && result.getRows().length > 0) {
            Object[][] rows = result.getRows();

            // Parcourir chaque ligne et ajouter les valeurs à la liste jsonData
            for (Object[] row : rows) {
                // Assurez-vous que la ligne contient des valeurs
                if (row != null && row.length > 0) {
                    // La première valeur correspond à MATCLE, la deuxième à DATSOR
                    String MATCLE = (String) row[0];
                    Date dateValue;

                    // Vérifier le type de la deuxième valeur
                    if (row[1] instanceof Timestamp) {
                        // Convertir Timestamp en Date
                        Timestamp timestamp = (Timestamp) row[1];
                        dateValue = new Date(timestamp.getTime());
                    } else if (row[1] instanceof Date) {
                        // Si c'est déjà une Date, pas besoin de conversion
                        dateValue = (Date) row[1];
                    } else {
                        // Gérer d'autres cas si nécessaire
                        dateValue = null;
                    }

                    // Formater la date au format "jj-MM-aaaa"
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = sdf.format(dateValue);

                    // Créer une carte pour stocker les données de cette ligne
                    Map<String, Object> rowData = new HashMap<>();
                    rowData.put("MATCLE", MATCLE);
                    rowData.put("DATSOR", formattedDate);

                    // Ajouter les données de cette ligne à la liste jsonData
                    jsonData.add(rowData);
                }
            }
        } else {
            // Aucun résultat à afficher
            System.out.println("Aucun résultat disponible.");
        }

// Convertir la liste jsonData en JSON et l'afficher
        try {
            String jsonResult = objectMapper.writeValueAsString(jsonData);
            System.out.println(jsonResult);
        } catch (Exception e) {
            // Gérer les erreurs de conversion en JSON
            e.printStackTrace();
        }

    }


    public List<EmployeeData> loadEmpParDepartement() throws HRException, ConfigurationException, ParseException {

        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");

        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("4I"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("3B"));


        //  HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


        IHRDictionary dictionary = session.getDictionary();
        HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

        // HRDossierListIterator iterator = dossierCollection.loadDossiers("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");
        //HRDossierListIterator iterator =dossierCollection.loadDossiers("select mte3ek");

        IHRConversation conversation = user.getMainConversation();
// Retrieving one of the user's roles to send the message
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(100);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("SELECT DISTINCT a.MATCLE, b.MTSAL,a.BLOB01, a.NOMUSE,a.PRENOM,e.IDOU00,d.LBOULG,m.LBPSLG ,e.DTEF00,e.DTEN00  FROM  ZA00 p,ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f,ZA01 m WHERE  i.IDOU00=e.IDOU00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS  ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<EmployeeData> employeeDataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("bb" + result);
        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            EmployeeData employeeData = new EmployeeData();
            employeeData.setMatcle(rowData[0].toString());
            employeeData.setMtsal(Double.parseDouble(rowData[1].toString()));
            employeeData.setBlobData(rowData[2].toString());
            employeeData.setNomuse(rowData[3].toString());
            employeeData.setPrenom(rowData[4].toString());
            employeeData.setIdorg(rowData[5].toString());
            employeeData.setNomorg(rowData[6].toString());
            employeeData.setPoste(rowData[7].toString());
            employeeData.setDateE(dateFormat.parse(rowData[8].toString()));
            employeeData.setDateS(dateFormat.parse(rowData[9].toString()));


            employeeDataList.add(employeeData);
         //  employeeRepositories.save(employeeData);

        }

        return employeeDataList;

    }
    public List<Organisation> loadOrganisation() throws HRException, ConfigurationException, ParseException {

        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");

        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("4I"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("3B"));


        //  HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


        IHRDictionary dictionary = session.getDictionary();
        HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

        // HRDossierListIterator iterator = dossierCollection.loadDossiers("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");
        //HRDossierListIterator iterator =dossierCollection.loadDossiers("select mte3ek");

        IHRConversation conversation = user.getMainConversation();
// Retrieving one of the user's roles to send the message
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(99999);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("SELECT DISTINCT i.IDOU00,d.LBOULG,d.LBOUSH  FROM  ZA00 p,ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f,ZA01 m WHERE  i.IDOU00=e.IDOU00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS  ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<Organisation> organisationList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("bb" + result);
        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            Organisation organisation= new Organisation();
            organisation.setIdorg(rowData[0].toString());
            organisation.setNomorgL(rowData[1].toString());
            organisation.setNomorgS(rowData[2].toString());



            organisationList.add(organisation);
           // organisationRepositories.save(organisation);

        }

        return organisationList;

    }


    public EmployeeAndTotalSalaryResponse getEmployeesAndTotalSalaryByDepartment(String departmentId) throws HRException, ConfigurationException, ParseException {
        List<EmployeeData> allEmployees = loadEmpParDepartement();
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

    public Map<String ,Integer> NbEmpBydep(String departmentId) throws HRException, ConfigurationException, ParseException {
        List<EmployeeData> allEmployees = loadEmpParDepartement();
    int NbEmp=0;
    int NbEmpByDep=0;

        Map<String,Integer> maplist=new HashMap<>();




        for (EmployeeData employee : allEmployees) {
          NbEmp+=1;
            if (departmentId.equals(employee.getIdorg())) {

                NbEmpByDep+=1;
            }
        }

        // Stocker le nombre total d'employés et le nombre d'employés par département dans la Map
        maplist.put("totalEmployees", NbEmp);
        maplist.put("employeesInDepartment", NbEmpByDep);



        return  maplist;
    }






    public List<Organisation> AffecterBudgetGlobal() throws HRException, ConfigurationException, ParseException {
        List<Organisation> allOrganisations = loadOrganisation();

        // Variable pour stocker le total du budget global
        BigDecimal budgetGlobal = BigDecimal.valueOf(800000000);

        // Parcours de chaque organisation
        for (Organisation org : allOrganisations) {
            Map<String, Integer> map = NbEmpBydep(org.getIdorg());
            int totalEmployees = map.get("totalEmployees");
            int employeesInDepartment = map.get("employeesInDepartment");

            // Calcul du pourcentage d'employés dans cette organisation par rapport au nombre total d'employés
            BigDecimal pourcentage = BigDecimal.valueOf(employeesInDepartment).divide(BigDecimal.valueOf(totalEmployees), 2, RoundingMode.HALF_UP);

            // Allocation du budget global pour cette organisation en fonction du pourcentage
            BigDecimal allocation = budgetGlobal.multiply(pourcentage);

            // Mise à jour du budget global de l'organisation
            org.setBudgetGloabl(allocation);
            organisationRepositories.save(org);
        }

        return allOrganisations;
    }


    public void poste() throws HRException, ConfigurationException {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");

        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("4I"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("EX"));


        //  HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


        IHRDictionary dictionary = session.getDictionary();


        // HRDossierListIterator iterator = dossierCollection.loadDossiers("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");


        IHRConversation conversation = user.getMainConversation();
// Retrieving one of the user's roles to send the message
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(100);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("select * from ZYSB   WHERE SOCDOS = 'MIT'  ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);


        System.out.println("bbb" + result);

    }


    public List<EmployeeData> loadEmpBYNomPreEmailImageCost() throws HRException, ConfigurationException {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");
        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));


        //   HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


        IHRDictionary dictionary = session.getDictionary();


        // HRDossierListIterator iterator = dossierCollection.loadDossiers("select a.MATCLE, b.DATSOR from ZY00 a,ZYES b where a.NUDOSS=b.NUDOSS");


        IHRConversation conversation = user.getMainConversation();

        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(100);
        request.setSqlStatement("select a.MATCLE,b.MTSAL,b.DATEFF,b.DATFIN from ZY00 a ,ZYAU b  where a.NUDOSS =b.NUDOSS  and a.SOCDOS='MIT'");

        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<EmployeeData> employeeDataList = new ArrayList<>();

        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            EmployeeData employeeData = new EmployeeData();
            employeeData.setMtsal(Double.parseDouble(rowData[1].toString()));
            employeeDataList.add(employeeData);
        }

        return employeeDataList;

    }

    public List<Employee> test() throws HRException, ConfigurationException {

        List<Employee> employees = new ArrayList<>();

        try {
            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setProcessName("MA001");
            parameters.setDataStructureName("ZY");
            parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));

            // Charger la configuration OpenHR
            PropertiesConfiguration configuration = new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties");

            // Initialiser la session OpenHR
            IHRSession session = HRSessionFactory.getFactory().createSession(configuration);
            IHRDictionary dictionary = session.getDictionary();

            // Accéder à la structure de données ZY
            IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");

            if (dataStructure != null) {
                // Récupérer les types de dossiers dans ZE
                List<IHRDossierType> dossierTypes = dataStructure.getDossierTypes();
                for (IHRDossierType dossierType : dossierTypes) {
                    // Explorer les données dans chaque dossier
                    List<IHRDataSection> dataSections = dossierType.getDataSections();
                    for (IHRDataSection dataSection : dataSections) {
                        List<IHRItem> items = dataSection.getAllItems();
                        for (IHRItem item : items) {
                            // Extraire les données ici et les transformer en objets Employee
                            employees.add(new Employee(item.getName(), item.getLabel()));
                            System.out.println("Data section: " + dataSection.getName() + ", Item Name: " + item.getName() + ", Item Label: " + item.getLabel());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }


    public List<Employee> loadZE() throws HRException, ConfigurationException {

        List<Employee> employees = new ArrayList<>();
        try {
            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setProcessName("MA001");
            parameters.setDataStructureName("ZE");


// Charger la configuration OpenHR
            PropertiesConfiguration configuration = new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties");

            // Initialiser la session OpenHR
            IHRSession session = HRSessionFactory.getFactory().createSession(configuration);
            IHRDictionary dictionary = session.getDictionary();

            // Accéder à la structure de données ZY
            IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZE");


            if (dataStructure != null) {
                // Récupérer les types de dossiers dans ZE
                List<IHRDossierType> dossierTypes = dataStructure.getDossierTypes();
                for (IHRDossierType dossierType : dossierTypes) {
                    System.out.print("b" + dossierType.getName());
                    System.out.print("b" + dossierType.getLabel());

                    // Explorer les données dans chaque dossier
                    List<IHRDataSection> dataSections = dossierType.getDataSections();
                    for (IHRDataSection dataSection : dataSections) {
                        List<IHRItem> items = dataSection.getAllItems();
                        for (IHRItem item : items) {
                            // Extraire les données ici et les transformer en objets Employee
                            employees.add(new Employee(item.getName(), item.getLabel()));
                            System.out.println("Data section: " + dataSection.getName() + ", Item Name: " + item.getName() + ", Item Label: " + item.getLabel());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }
// Dumping rows


    public List<EmployeeData> findEmp() {
        return employeeRepositories.findEmp();
    }

    public List<Organisation> getBudgetByDep() throws HRException, ConfigurationException {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");
        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        IHRDictionary dictionary = session.getDictionary();
        IHRConversation conversation = user.getMainConversation();

        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(200);
        request.setSqlStatement("SELECT DISTINCT  e.IDOU00,d.LBOULG  FROM  ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f,ZA01 m,ZE40 y WHERE  i.IDOU00=e.IDOU00 and y.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS and y.DTEF00 >= '2023-01-01' ");

        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);


        System.out.println("bbb" + result);
        List<Organisation> organisationDataList = new ArrayList<>();

        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            Organisation org = new Organisation();

            org.setIdorg(rowData[0].toString());
            org.setNomorgL(rowData[1].toString());

            organisationDataList.add(org);
        }

        return organisationDataList;

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

    public void saveEmployeesAndBudget(List<EmployeeRec> employeeDtos, double minBudget, double maxBudget, String idorg) {
        for (EmployeeRec employeeDto : employeeDtos) {
            EmployeeRec employeeRec = new EmployeeRec();
            employeeRec.setIdorg(idorg);
            employeeRec.setNumber(employeeDto.getNumber());
            employeeRec.setClassification(employeeDto.getClassification());
            employeeRec.setMinbudget(minBudget);
            employeeRec.setMaxbudget(maxBudget);
            employeeRec.setDate(LocalDate.now());
            employeeRecRepositories.save(employeeRec);
        }

    }


    public List<EmployeeRec> getHistory(String depId) {

        return employeeRecRepositories.findAllByIdorg(depId);

    }
    public List<EmployeeRec> getEmployeesByIdOrg(String idorg) {
        return employeeRecRepositories.findAllByIdorg(idorg);
    }
    public void updateEmployeeRecByIdorg(String idorg, List<EmployeeRec> updatedEmployeeRec, double minBudget, double maxBudget) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdorg(idorg);

        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);

            if (i < updatedEmployeeRec.size()) {
                EmployeeRec updatedRec = updatedEmployeeRec.get(i);
                emp.setNumber(updatedRec.getNumber());
                emp.setClassification(updatedRec.getClassification());
            }

            emp.setMinbudget(minBudget);
            emp.setMaxbudget(maxBudget);

            employeeRecRepositories.save(emp);
        }

        for (int i = employeeRecs.size(); i < updatedEmployeeRec.size(); i++) {
            EmployeeRec newRec = updatedEmployeeRec.get(i);
            newRec.setIdorg(idorg);
            newRec.setMinbudget(minBudget);
            newRec.setMaxbudget(maxBudget);
            employeeRecRepositories.save(newRec);
        }
    }
public void setStatus(String id){
    List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdorg(id);
    for (int i = 0; i < employeeRecs.size(); i++) {
        EmployeeRec emp = employeeRecs.get(i);
         emp.setStatus(true);
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
                System.out.println("bbbb"+daysBetween);
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
            EmployeeData emp= employeeRepositories.findById(employee.getId()).orElse(null);
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


    public double calculateBudgetPourcentageDepense(double budgetGlobal,double budgetDepense) {
       return  ((budgetGlobal - budgetDepense)/budgetGlobal) * 100;
    }

    public double calculateBudgetPourcentageRestant(double budgetGlobal,double budgetRestant) {
        return  (budgetRestant/budgetGlobal) * 100;
    }
}



