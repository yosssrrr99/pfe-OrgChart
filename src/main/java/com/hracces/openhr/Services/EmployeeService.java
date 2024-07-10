package com.hracces.openhr.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hracces.openhr.Repositories.EmployeeRecRepositories;
import com.hracces.openhr.Repositories.EmployeeRepositories;
import com.hracces.openhr.Repositories.OrganisationRepositories;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@Log4j
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

    public String login(String username,String password) throws UserConnectionException, AuthenticationException, ConfigurationException, SessionBuildException, SessionConnectionException {
        HRApplication.configureLogs("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");
        session = HRSessionFactory.getFactory().createSession(
                new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));
        user = session.connectUser(username, password);
      //  System.out.println("+++++++++"+user.getRoles().get(0).getDelegation().getName());
      //  System.out.println("+++++++++"+user.getRoles().get(0).getTemplateLabel());
     //  System.out.println("+++++++++"+user.getDescription().getUserDossierNudoss());
    //    System.out.println("+++++++++"+user.getDescription());
    //    System.out.println("+++++++++"+user.getVirtualSessionId());
     //   user.getSQLkey();
       return "avec succes";
    }
    public boolean isLoggedIn() {


    return user.isConnected();
 }
    public String getRole() {
        List<IHRRole> roles = user.getRoles();

        // Déboguer: afficher tous les rôles et leurs labels
        System.out.println("Roles for user " + user.getUserId() + ":");
        for (IHRRole role : roles) {
            System.out.println("Role Label: " + role.getTemplate());
        }

        // Vérifier si la liste des rôles n'est pas vide et retourner le premier rôle
        if (!roles.isEmpty()) {
            return roles.get(0).getTemplate().toString();
        }

        // Retourner une valeur par défaut ou lever une exception si aucun rôle n'est trouvé
        return ""; // ou lever une exception
    }
    public String logout() throws UserConnectionException {
        if (user != null && user.isConnected()) {

            user.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }

        return "logout avec succes";
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
        request.setMaxRows(200);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("SELECT DISTINCT a.MATCLE, b.MTSAL,a.BLOB01, a.NOMUSE,a.PRENOM,e.IDOU00,d.LBOULG,m.LBPSLG ,e.DTEF00,e.DTEN00 ,e.IDPS00,e.IDJB00,a.NUDOSS,e.NBASHR,e.RSCHGE FROM  ZA00 p,ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f,ZA01 m WHERE  i.IDOU00=e.IDOU00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS  ");
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
            employeeData.setIdposte(rowData[10].toString());
            employeeData.setIdemploi(rowData[11].toString());
            employeeData.setNudoss(Integer.parseInt(rowData[12].toString()));
            employeeData.setNbHeure(Double.parseDouble(rowData[13].toString()));
            employeeData.setMotif(rowData[14].toString());

            employeeDataList.add(employeeData);
            //   employeeRepositories.save(employeeData);

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
            Organisation organisation = new Organisation();
            organisation.setIdorg(rowData[0].toString());
            organisation.setNomorgL(rowData[1].toString());
            organisation.setNomorgS(rowData[2].toString());


            organisationList.add(organisation);
            // organisationRepositories.save(organisation);

        }

        return organisationList;

    }

    public List<Poste> emploi() throws HRException, ConfigurationException, ParseException {

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
        request.setSqlStatement("SELECT DISTINCT i.IDJB00,d.LBJBSH FROM  ZA00 p,ZY00 a, ZYAU b,ZC01 d,ZC00 i, ZY3B e, ZY1S f,ZA01 m WHERE  i.IDJB00=e.IDJB00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS  ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<Poste> emploiList = new ArrayList<>();
       ;
        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            Poste poste = new Poste();
            poste.setIdjbo00(rowData[0].toString());
            poste.setEmploi(rowData[1].toString());



            emploiList.add(poste);
            // organisationRepositories.save(organisation);

        }

        return emploiList;

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

    public Map<String, Integer> NbEmpBydep(String departmentId) throws HRException, ConfigurationException, ParseException {
        List<EmployeeData> allEmployees = loadEmpParDepartement();
        int NbEmp = 0;
        int NbEmpByDep = 0;

        Map<String, Integer> maplist = new HashMap<>();


        for (EmployeeData employee : allEmployees) {
            NbEmp += 1;
            if (departmentId.equals(employee.getIdorg())) {

                NbEmpByDep += 1;
            }
        }

        // Stocker le nombre total d'employés et le nombre d'employés par département dans la Map
        maplist.put("totalEmployees", NbEmp);
        maplist.put("employeesInDepartment", NbEmpByDep);


        return maplist;
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
        request.setMaxRows(5);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("select * from zy3b  where socdos='MIT' and nudoss='9931'");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        System.out.println(result);


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
            employeeRec.setTypeStatus(Status.Encours);
            employeeRec.setIdManger(user.getUserId());
            employeeRecRepositories.save(employeeRec);
        }

    }


    public List<EmployeeRec> getHistory(String depId) {

        return employeeRecRepositories.findAllByIdorg(depId);

    }

    public List<EmployeeRec> getEmployeesByIdOrg(String idorg,Status status) {
        return employeeRecRepositories.findByIdManagerAndStatus(idorg,status);
    }

    public List<String> getIdMangerByStatus(Status status) {
        return employeeRecRepositories.findMangerTypeStatus(status);
    }

    public int countgetIdMangerByStatus(Status status) {
        return employeeRecRepositories.countfindMangerTypeStatus(status);
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


    public void updateEmployee() throws Exception {
        PrimaryKey technicalKey = new IntegerPrimaryKey(100);
// Creating a functional key to identify a given HR Access dossier
        PrimaryKey functionalKey = new
                CompositePrimaryKey().addValue("HRA").addValue("123456");

        IHRConversation conversation = user.getMainConversation();
// Retrieving one of the user's roles to send the message
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgUpdateDossier request = new HRMsgUpdateDossier();
        request.setProcessName("MA001");
        request.setDataStructureName("ZY");
        request.setIgnoreSeriousWarnings(true);
        request.setPrimaryKeyType(PrimaryKey.INTEGER);
        request.setUpdateMode(UpdateMode.NORMAL);
        HRMsgUpdateDossier.Dossier dossierToUpdate = new
                HRMsgUpdateDossier.Dossier(technicalKey);
// Creating a data section to represent an employee's nationality (ZY12)
        HRMsgUpdateDossier.Section dataSectionToUpdate = new
                HRMsgUpdateDossier.Section("12");
        dossierToUpdate.addSection(dataSectionToUpdate);

        Object[] values = new Object[7];
        values[0] = "FRA";
        values[4] = "1"; // Item “Is it the main nationality ?” (optional)
        values[5] = java.sql.Date.valueOf("2008-01-01");
        values[6] = java.sql.Date.valueOf("2010-12-31");
        System.out.println("Values being set:");
        for (Object value : values) {
            System.out.println(value);
        }
        HRMsgUpdateDossier.Row occurrenceToCreate = new
                HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_INSERT);
        occurrenceToCreate.setValues(values);
        request.addDossier(dossierToUpdate);

        HRResultUpdateDossier result = (HRResultUpdateDossier)
                conversation.send(request, role);

        System.out.println(result.getDossierUpdateCount() + "“ dossier(s) were updated”");
        HRResultUpdateDossier.DossierUpdate[] dossierUpdates =
                result.getDossierUpdates();
        for (int i = 0; i < dossierUpdates.length; i++) {
            HRResultUpdateDossier.DossierUpdate dossierUpdate = dossierUpdates[i];
            System.out.println("“Dossier with number ”" + dossierUpdate.getNudoss() + "“ has been updated”");
            System.out.println("“Dossier is mapped to rule system ”" +
                    dossierUpdate.getRuleSystem());
            System.out.println("“Dossier's last update timestamp is ”" +
                    dossierUpdate.getLastUpdateTimestamp());
            System.out.println(dossierUpdate.getDataSectionUpdateCount() + "“ data section(s) have been updated”");


        }


    }

    public String createEmployee(int dossierNumber, Object[] values) throws Exception {
        // Récupérer la conversation principale de l'utilisateur
        IHRConversation conversation = user.getMainConversation();
        // Récupérer l'un des rôles de l'utilisateur
        IHRRole role = user.getRole("ALLHRLO(MA)");

        // Créer une nouvelle requête pour créer un dossier d'employé
        HRMsgUpdateDossier request = new HRMsgUpdateDossier();
        request.setProcessName("MA001"); // Nom du processus HR Access
        request.setDataStructureName("ZY"); // Structure de données du dossier
        request.setIgnoreSeriousWarnings(true);
        request.setPrimaryKeyType(PrimaryKey.INTEGER);
        request.setUpdateMode(UpdateMode.NORMAL);

        // Créer une clé technique pour identifier le nouveau dossier
        PrimaryKey technicalKey = new IntegerPrimaryKey(dossierNumber); // Numéro de dossier unique

        // Créer une section de données pour l'employé
        HRMsgUpdateDossier.Dossier dossierToUpdate = new HRMsgUpdateDossier.Dossier(technicalKey);
        HRMsgUpdateDossier.Section dataSectionToUpdate = new HRMsgUpdateDossier.Section("00"); // Section de données

        // Ajouter les valeurs à la section de données
        HRMsgUpdateDossier.Row newRow = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_INSERT);
        newRow.setValues(values);
        dataSectionToUpdate.addRow(newRow);

        // Ajouter la section de données au dossier
        dossierToUpdate.addSection(dataSectionToUpdate);

        // Ajouter le dossier à la requête de mise à jour
        request.addDossier(dossierToUpdate);

        // Envoyer le message via la conversation de l'utilisateur en utilisant le rôle
        HRResultUpdateDossier result = (HRResultUpdateDossier) conversation.send(request, role);

        // Traiter le résultat
        return result.getDossierUpdateCount() + " dossier(s) ont été mis à jour";
    }


    public String addEmployee() {
        try {
            // Assuming user is retrieved and authenticated
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            PrimaryKey technicalKey = new IntegerPrimaryKey(1000);
            HRMsgUpdateDossier request = new HRMsgUpdateDossier();
            request.setProcessName("MA001");
            request.setDataStructureName("ZY");
            request.setIgnoreSeriousWarnings(true);
            request.setPrimaryKeyType(PrimaryKey.INTEGER);
            request.setUpdateMode(UpdateMode.NORMAL);

            HRMsgUpdateDossier.Dossier dossierToUpdate = new HRMsgUpdateDossier.Dossier(technicalKey);
            HRMsgUpdateDossier.Section dataSectionToUpdate = new HRMsgUpdateDossier.Section("12");

            Object[] values = new Object[8];


            values[6] = java.sql.Date.valueOf("2008-01-01");
            values[7] = java.sql.Date.valueOf("2010-12-31");
            // Log the values before sending
            System.out.println("Values to be sent: " + Arrays.toString(values));

            HRMsgUpdateDossier.Row occurrenceToCreate = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_INSERT);
            occurrenceToCreate.setValues(values);
            dataSectionToUpdate.addRow(occurrenceToCreate);
            dossierToUpdate.addSection(dataSectionToUpdate);
            request.addDossier(dossierToUpdate);
            log.debug("Values to update: " + Arrays.toString(values));
            HRResultUpdateDossier result = (HRResultUpdateDossier) conversation.send(request, role);

            if (result.getDossierUpdates() == null) {
                return "No updates were made to the dossiers.";
            }

            StringBuilder response = new StringBuilder("Employee added successfully. Dossier count: ");
            response.append(result.getDossierUpdateCount()).append("\n");

            HRResultUpdateDossier.DossierUpdate[] dossierUpdates = result.getDossierUpdates();
            for (HRResultUpdateDossier.DossierUpdate dossierUpdate : dossierUpdates) {
                response.append("Dossier with number ").append(dossierUpdate.getNudoss()).append(" has been updated\n");
                response.append("Dossier is mapped to rule system ").append(dossierUpdate.getRuleSystem()).append("\n");
                response.append("Dossier's last update timestamp is ").append(dossierUpdate.getLastUpdateTimestamp()).append("\n");
                response.append(dossierUpdate.getDataSectionUpdateCount()).append(" data section(s) have been updated\n");

                for (HRResultUpdateDossier.DataSectionUpdate dataSectionUpdate : dossierUpdate.getDataSectionUpdates()) {
                    response.append("Data section ").append(dataSectionUpdate.getDataSectionName()).append(" has been updated\n");
                    response.append("Data section has ").append(dataSectionUpdate.getRowUpdateCount()).append(" row update(s)\n");

                    for (HRResultUpdateDossier.RowUpdate rowUpdate : dataSectionUpdate.getRowUpdates()) {
                        response.append("The row's new values are ").append(Arrays.toString(rowUpdate.getValues())).append("\n");
                        response.append("The row's line number is ").append(rowUpdate.getNulign()).append("\n");
                    }
                }
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'ajout de l'employé : " + e.getMessage();
        }
    }


    private String checkField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is null or invalid");
        }
        return fieldValue;
    }




    public List<Integer> findEmployeeDossierNumbersByName() {
        try {
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            HRMsgSelectPopulation request = new HRMsgSelectPopulation();
            request.setSqlStatement("SELECT * FROM ZY12 A WHERE A.NUDOSS =1000 ");
            request.setDataStructure("ZY");

            HRResultSelectPopulation result = (HRResultSelectPopulation) conversation.send(request, role);

            List<Integer> dossierNumbers = new ArrayList<>();
            for (Integer dossierNumber : result.getDossiers()) {
                dossierNumbers.add(dossierNumber);
            }
            return dossierNumbers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche de l'employé : " + e.getMessage());
        }
    }


    public Map<String, Integer> retrieveAttributeIndices() throws HRException {
        IHRConversation conversation = user.getMainConversation();
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(1);
        request.setSqlStatement("select * from zy3b where socdos='MIT'");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        Map<String, Integer> attributeIndexMap = new HashMap<>();
        for (int i = 0; i < result.getColumnCount(); i++) {
            attributeIndexMap.put(result.getColumn(i).getName(), i);
        }

        return attributeIndexMap;
    }

   /* public String ekhdem(Zy3bDT0 employeeDTO, int numdoss) {
        try {
            // Assuming user is retrieved and authenticated
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            // Create the request for updating dossier
            HRMsgUpdateDossier request = new HRMsgUpdateDossier();
            request.setProcessName("MA001");
            request.setDataStructureName("ZY");
            request.setIgnoreSeriousWarnings(true);
            request.setPrimaryKeyType(PrimaryKey.INTEGER);
            request.setUpdateMode(UpdateMode.NORMAL);

            // Assuming the dossier number is provided in the DTO
            PrimaryKey technicalKey = new IntegerPrimaryKey(numdoss);
            HRMsgUpdateDossier.Dossier dossierToUpdate = new HRMsgUpdateDossier.Dossier(technicalKey);
            HRMsgUpdateDossier.Section dataSectionToUpdate = new HRMsgUpdateDossier.Section("3B");

            Object[] values = new Object[53];

            // Check and set each field, throwing an error if a required field is missing


            if (employeeDTO.getIdou00() == null) {
                throw new IllegalArgumentException("IDOU00 is null or invalid");
            }
            values[11] = convertToSqlDate(employeeDTO.getDten00());


            HRMsgUpdateDossier.Row occurrenceToUpdate = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_MODIFY);
            occurrenceToUpdate.setValues(values);
            dataSectionToUpdate.addRow(occurrenceToUpdate);
            dossierToUpdate.addSection(dataSectionToUpdate);
            request.addDossier(dossierToUpdate);
            log.debug("Values to update: " + Arrays.toString(values));
            HRResultUpdateDossier result = (HRResultUpdateDossier) conversation.send(request, role);

            if (result.getDossierUpdates() == null) {
                return "No updates were made to the dossiers.";
            }

            StringBuilder response = new StringBuilder("Employee updated successfully. Dossier count: ");
            response.append(result.getDossierUpdateCount()).append("\n");

            HRResultUpdateDossier.DossierUpdate[] dossierUpdates = result.getDossierUpdates();
            for (HRResultUpdateDossier.DossierUpdate dossierUpdate : dossierUpdates) {
                response.append("Dossier with number ").append(dossierUpdate.getNudoss()).append(" has been updated\n");
                response.append("Dossier is mapped to rule system ").append(dossierUpdate.getRuleSystem()).append("\n");
                response.append("Dossier's last update timestamp is ").append(dossierUpdate.getLastUpdateTimestamp()).append("\n");
                response.append(dossierUpdate.getDataSectionUpdateCount()).append(" data section(s) have been updated\n");

                for (HRResultUpdateDossier.DataSectionUpdate dataSectionUpdate : dossierUpdate.getDataSectionUpdates()) {
                    response.append("Data section ").append(dataSectionUpdate.getDataSectionName()).append(" has been updated\n");
                    response.append("Data section has ").append(dataSectionUpdate.getRowUpdateCount()).append(" row update(s)\n");

                    for (HRResultUpdateDossier.RowUpdate rowUpdate : dataSectionUpdate.getRowUpdates()) {
                        response.append("The row's new values are ").append(Arrays.toString(rowUpdate.getValues())).append("\n");
                        response.append("The row's line number is ").append(rowUpdate.getNulign()).append("\n");
                    }
                }
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la mise à jour de l'employé : " + e.getMessage();
        }
    }*/




    public List<Integer> retrieve() throws HRException {
        IHRConversation conversation = user.getMainConversation();
        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(1);
        request.setSqlStatement("select * from zy3b where socdos='MIT'");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<Integer> attributeIndexMap = new ArrayList<>();
        for (int i = 0; i < result.getColumnCount(); i++) {
            attributeIndexMap.add(i);
            System.out.println("i" + i + "column" + result.getColumn(i).getName());
        }

        return attributeIndexMap;
    }

    private String truncateString(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }


    public String deleteEmptyRows() {
        try {
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            HRMsgExtractData extractRequest = new HRMsgExtractData();
            extractRequest.setSqlStatement("SELECT * FROM ZY3B WHERE SOCDOS='MIT'");
            HRResultExtractData extractResult = (HRResultExtractData) conversation.send(extractRequest, role);

            HRMsgUpdateDossier updateRequest = new HRMsgUpdateDossier();
            updateRequest.setProcessName("MA001");
            updateRequest.setDataStructureName("ZY");
            updateRequest.setIgnoreSeriousWarnings(true);
            updateRequest.setPrimaryKeyType(PrimaryKey.INTEGER);
            updateRequest.setUpdateMode(UpdateMode.NORMAL);

            for (int i = 0; i < extractResult.getRowCount(); i++) {
                boolean allNull = true;
                for (int j = 0; j < extractResult.getColumnCount(); j++) {
                    Object value = extractResult.getValue(i, j);
                    if (value != null) {
                        allNull = false;
                        break;
                    }
                }

                if (allNull) {
                    Integer numdoss = extractResult.getValue(i, 0) != null ?
                            Integer.parseInt(extractResult.getValue(i, 0).toString()) : null; // Assuming column 0 is the NUDOSS
                    if (numdoss != null) {
                        PrimaryKey technicalKey = new IntegerPrimaryKey(numdoss);
                        HRMsgUpdateDossier.Dossier dossierToUpdate = new HRMsgUpdateDossier.Dossier(technicalKey);
                        HRMsgUpdateDossier.Section dataSectionToUpdate = new HRMsgUpdateDossier.Section("3B");

                        HRMsgUpdateDossier.Row rowToDelete = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_DELETE);
                        dataSectionToUpdate.addRow(rowToDelete);

                        dossierToUpdate.addSection(dataSectionToUpdate);
                        updateRequest.addDossier(dossierToUpdate);
                    }
                }
            }

            log.debug("Deleting rows in dossiers with all null values.");
            HRResultUpdateDossier result = (HRResultUpdateDossier) conversation.send(updateRequest, role);

            if (result.getDossierUpdates() == null) {
                return "No updates were made to the dossiers.";
            }

            StringBuilder response = new StringBuilder("Rows deleted successfully. Dossier count: ");
            response.append(result.getDossierUpdateCount()).append("\n");

            HRResultUpdateDossier.DossierUpdate[] dossierUpdates = result.getDossierUpdates();
            for (HRResultUpdateDossier.DossierUpdate dossierUpdate : dossierUpdates) {
                response.append("Dossier with number ").append(dossierUpdate.getNudoss()).append(" has been updated\n");
                response.append("Dossier is mapped to rule system ").append(dossierUpdate.getRuleSystem()).append("\n");
                response.append("Dossier's last update timestamp is ").append(dossierUpdate.getLastUpdateTimestamp()).append("\n");
                response.append(dossierUpdate.getDataSectionUpdateCount()).append(" data section(s) have been updated\n");

                for (HRResultUpdateDossier.DataSectionUpdate dataSectionUpdate : dossierUpdate.getDataSectionUpdates()) {
                    response.append("Data section ").append(dataSectionUpdate.getDataSectionName()).append(" has been updated\n");
                    response.append("Data section has ").append(dataSectionUpdate.getRowUpdateCount()).append(" row update(s)\n");

                    for (HRResultUpdateDossier.RowUpdate rowUpdate : dataSectionUpdate.getRowUpdates()) {
                        response.append("The row's new values are ").append(Arrays.toString(rowUpdate.getValues())).append("\n");
                        response.append("The row's line number is ").append(rowUpdate.getNulign()).append("\n");
                    }
                }
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la suppression des lignes : " + e.getMessage();
        }


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







    public String updateAndInsertEmployee2(Zy3bDT0 employeeDTO, int numdoss) {
        try {
            // Récupération et authentification de l'utilisateur
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            // Récupérer la dernière occurrence de ZY3B pour le dossier spécifié
            HRMsgExtractData extractRequest = new HRMsgExtractData();
            extractRequest.setSqlStatement("SELECT DISTINCT b.DTEF00, b.DTEN00, a.NUDOSS FROM ZY3B b, ZY00 a WHERE a.SOCDOS='MIT' AND a.MATCLE='042788' AND a.NUDOSS=b.NUDOSS ORDER BY b.DTEN00 DESC");
            HRResultExtractData extractResult = (HRResultExtractData) conversation.send(extractRequest, role);

            if (extractResult.getRowCount() == 0) {
                return "Aucune occurrence trouvée pour la mise à jour.";
            }

            // Récupérer la dernière occurrence
            Date datef00 = (Date) extractResult.getValue(0, 0); // Assuming the first column is DTEF00
            Date dten00 = subtractOneDay(datef00); // Assuming the second column is DTEN00

            // Création de la requête pour mise à jour et ajout de dossier
            HRMsgUpdateDossier request = new HRMsgUpdateDossier();
            request.setProcessName("MA001");
            request.setDataStructureName("ZY");
            request.setIgnoreSeriousWarnings(true);
            request.setPrimaryKeyType(PrimaryKey.INTEGER);
            request.setUpdateMode(UpdateMode.NORMAL);

            // Clé primaire basée sur le numéro de dossier fourni
            PrimaryKey technicalKey = new IntegerPrimaryKey(numdoss);
            HRMsgUpdateDossier.Dossier dossierToUpdate = new HRMsgUpdateDossier.Dossier(technicalKey);
            HRMsgUpdateDossier.Section dataSectionToUpdate = new HRMsgUpdateDossier.Section("3B");

            // Valeurs pour la modification
            Object[] values = new Object[53];
            values[4] = dten00; // dten00

            // Valeurs pour l'insertion
            Object[] newValues = new Object[53];
            newValues[0] = employeeDTO.getIdps00(); // poste
            newValues[1] = employeeDTO.getIdjbo00(); // emploi
            newValues[2] = employeeDTO.getIdou00(); // organisation
            newValues[7] = employeeDTO.getChecked(); // motif
            newValues[12] = employeeDTO.getTypemotif(); // type

            // Création de la ligne pour la modification
            HRMsgUpdateDossier.Row rowToModify = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_MODIFY);
            rowToModify.setValues(values);
            dataSectionToUpdate.addRow(rowToModify);

            // Création de la ligne pour l'insertion
            HRMsgUpdateDossier.Row rowToAdd = new HRMsgUpdateDossier.Row(HRMsgUpdateDossier.ROW_INSERT);
            rowToAdd.setValues(newValues);
            dataSectionToUpdate.addRow(rowToAdd);

            // Ajout de la section et du dossier à la requête
            dossierToUpdate.addSection(dataSectionToUpdate);
            request.addDossier(dossierToUpdate);

            // Envoi de la requête et gestion des résultats
            HRResultUpdateDossier result = (HRResultUpdateDossier) conversation.send(request, role);

            // Construction de la réponse
            if (result.getDossierUpdates() == null) {
                return "Aucune mise à jour n'a été effectuée sur les dossiers.";
            }

            StringBuilder response = new StringBuilder("Employé mis à jour et nouvelle occurrence ajoutée avec succès. Nombre de dossiers : ");
            response.append(result.getDossierUpdateCount()).append("\n");

            HRResultUpdateDossier.DossierUpdate[] dossierUpdates = result.getDossierUpdates();
            for (HRResultUpdateDossier.DossierUpdate dossierUpdate : dossierUpdates) {
                response.append("Dossier numéro ").append(dossierUpdate.getNudoss()).append(" a été mis à jour\n");
                response.append("Le dossier est lié au système de règles ").append(dossierUpdate.getRuleSystem()).append("\n");
                response.append("Dernier horodatage de mise à jour du dossier : ").append(dossierUpdate.getLastUpdateTimestamp()).append("\n");
                response.append(dossierUpdate.getDataSectionUpdateCount()).append(" section(s) de données ont été mises à jour\n");

                for (HRResultUpdateDossier.DataSectionUpdate dataSectionUpdate : dossierUpdate.getDataSectionUpdates()) {
                    response.append("Section de données ").append(dataSectionUpdate.getDataSectionName()).append(" a été mise à jour\n");
                    response.append("La section de données contient ").append(dataSectionUpdate.getRowUpdateCount()).append(" mise(s) à jour de ligne(s)\n");

                    for (HRResultUpdateDossier.RowUpdate rowUpdate : dataSectionUpdate.getRowUpdates()) {
                        response.append("Les nouvelles valeurs de la ligne sont ").append(Arrays.toString(rowUpdate.getValues())).append("\n");
                        response.append("Numéro de ligne de la ligne : ").append(rowUpdate.getNulign()).append("\n");
                    }
                }
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la mise à jour et de l'ajout de l'employé : " + e.getMessage();
        }
    }


    public void InserAndUpdate(Zy3bDT0 zy3bDT0,int numdoss) throws HRDossierCollectionException, HRDossierCollectionCommitException {
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");

        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("4I"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("EX"));


        HRDossierCollection dossierCollection = new
                HRDossierCollection(parameters,
                user.getMainConversation(),
                user.getRole("ALLHRLO(MA)"),
                new
                        HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

        HRDossier employeeDossier = dossierCollection.loadDossier(numdoss);

        HROccur dateEnd = employeeDossier.getDataSectionByName("3B").getOccur();

       // dateEnd.setDate("DTEN00", convertToSqlDate(zy3bDT0.getDten00()));

        employeeDossier.commit();
    }



  public String updateAndInsertZy() {
        try {
            // Récupération et authentification de l'utilisateur
            IHRConversation conversation = user.getMainConversation();
            IHRRole role = user.getRole("ALLHRLO(MA)");

            // Chargement du dossier de l'employé
            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
            parameters.setProcessName("MA001");
            parameters.addDataSection(new HRDataSourceParameters.DataSection("12"));

            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setDataStructureName("ZY");
            HRDossierCollection dossierCollection = new HRDossierCollection(parameters, conversation, role, new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));
            HRDossier employeeDossier = dossierCollection.loadDossier(1000);
            if (employeeDossier == null) {
                throw new Exception("employe dossier.");
            }
            // Récupération de l'occurrence unique de la section de données ZY3B (on suppose que ZY3B est la section correcte)
            HROccur occurrence = employeeDossier.getDataSectionByName("12").getOccur();
            if (occurrence == null) {
                throw new Exception("La section de données ZY3B n'a pas été trouvée dans le dossier de l'employé.");
            }
            // Mise à jour de DTEN00 de la dernière occurrence

            occurrence.setString("NATION", "TUN");

            // Committing the update changes to the HR Access server
            employeeDossier.commit();

         //  ekhdem(employeeDTO,numdoss);


        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la mise à jour et de l'ajout de l'employé : " + e.getMessage();
        }
        return "updated";
    }





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
   public String loadAndDisplayLastOccurrence(Zy3bDT0 empZy3bDT0,String idorg, int numdoss) throws  HRDossierCollectionException {
       try {

           IHRConversation conversation = user.getMainConversation();
           IHRRole role = user.getRole("ALLHRLO(MA)");
           PrimaryKey technicalKey = new IntegerPrimaryKey(100);
           // Chargement du dossier de l'employé
           HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
           parameters.setProcessName("MA001");
           parameters.setDataStructureName("ZY");
           parameters.addDataSection(new HRDataSourceParameters.DataSection("3B"));

           parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
           HRDossierCollection dossierCollection = new HRDossierCollection(parameters, conversation, role, new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));
           HRDossier employeeDossier = dossierCollection.loadDossier(numdoss);

           HRDataSect dataSection = employeeDossier.getDataSectionByName("3B");
           int lastOccur=dataSection.getOccurCount();
           HROccur lastOccurrence = dataSection.getOccurByNulign(lastOccur);
           System.out.println(lastOccur);
           String dateEffetStr = empZy3bDT0.getDtef00(); // Supposons que c'est une chaîne au format "yyyy-MM-dd"
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           java.util.Date dateEffet = sdf.parse(dateEffetStr);
           Timestamp dateEffetTimestamp = new Timestamp(dateEffet.getTime());


           //calculer la date
           Calendar calendar = Calendar.getInstance();
           calendar.setTimeInMillis(dateEffetTimestamp.getTime());
           calendar.add(Calendar.DAY_OF_MONTH, -1);
           Timestamp dateEnTimestamp = new Timestamp(calendar.getTimeInMillis());

           HROccur occurrence = dataSection.createOccur();

          // calendar.setTime(newOccurrenceDate);
        //   calendar.add(Calendar.DAY_OF_MONTH, -1);
           Timestamp lastOccurrenceDate = new Timestamp(calendar.getTimeInMillis());

           lastOccurrence.setTimestamp("DTEN00", dateEnTimestamp);
           // Mise en place des valeurs pour la nouvelle occurrence
           occurrence.setTimestamp("DTEF00",dateEffetTimestamp);
           occurrence.setDouble("NBASHR", lastOccurrence.getDouble("NBASHR"));

           occurrence.setString("IDOU00", idorg);
           // occurrence.setString("IDPS00","COC");
           occurrence.setString("IDJB00", empZy3bDT0.getIdjbo00());
           occurrence.setString("TYASSI", "1");
           occurrence.setString("RSCHGE", empZy3bDT0.getTypemotif());

           occurrence.setInteger("RTASSI", 100);
           //  occurrence.setInteger("PGPDOS", 0);




           System.out.println("------------------------------------------------------------");



           System.out.println("------------------------------------------------------------");

           //  dossierCollection.commitAllDossiers();
           ICommitResult commitResult = employeeDossier.commit();
           if (commitResult.getDossierCommitResult().getErrors().length > 0) {
               // There is at least one functional error
               System.err.println("The dossier update failed for a functional reason: " + Arrays.toString(commitResult.getDossierCommitResult().getErrors()));
               return "Dossier update failed due to functional errors.";
           } else {
               System.out.println("The dossier update succeeded");
               return "Dossier update succeeded.";
           }
       } catch (HRDossierCollectionCommitException e) {
           // A technical error occurred when updating the dossier
           System.err.println("Technical error during dossier commit: " + e.getMessage());
           throw new HRDossierCollectionException("Technical error during dossier commit", e);
       } catch (Exception e) {
           // General error handling
           System.err.println("An error occurred: " + e.getMessage());
           throw new HRDossierCollectionException("An error occurred while processing the dossier", e);
       }
   }
    public List<String> motif() throws HRException, ConfigurationException, ParseException {

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
        request.setSqlStatement("SELECT DISTINCT e.RSCHGE FROM  ZA00 p,ZY00 a, ZYAU b,ZC01 d,ZC00 i, ZY3B e, ZY1S f,ZA01 m WHERE  i.IDJB00=e.IDJB00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS  ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<String> emploiList = new ArrayList<>();
        ;
        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;

            emploiList.add(rowData[0].toString());
            // organisationRepositories.save(organisation);

        }

        return emploiList;

    }



    public List<String> loginManager() throws HRException, ConfigurationException, ParseException {

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
        request.setMaxRows(5);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("select * from zysb where socdos='MIT' ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        List<String> emploiList = new ArrayList<>();
        ;
        for (Object row : result.getRows()) {
            Object[] rowData = (Object[]) row;
            Poste poste = new Poste();
            poste.setIdjbo00(rowData[0].toString());
            poste.setEmploi(rowData[1].toString());



           // emploiList.add(poste);
            // organisationRepositories.save(organisation);

        }

        return emploiList;

    }



    //Notification





}




