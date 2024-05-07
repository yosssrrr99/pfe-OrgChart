package com.hracces.openhr.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hracces.openhr.entities.Employee;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import com.hraccess.openhr.exception.HRException;
import com.hraccess.openhr.exception.UserConnectionException;
import com.hraccess.openhr.msg.HRMsgExtractData;
import com.hraccess.openhr.msg.HRResultExtractData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
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
// Sending message via the user's conversation (synchronous task)
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        // Créer un ObjectMapper pour convertir les objets en JSON
        ObjectMapper objectMapper = new ObjectMapper();

// Créer une liste pour stocker les données à convertir en JSON
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

    public List<Employee> test() throws HRException, ConfigurationException {

        List<Employee> employees = new ArrayList<>();

        try {
            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setProcessName("MA001");
            parameters.setDataStructureName("ZY");
            parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));

            HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));
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
                        System.out.println("data section"+dataSection);
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
    }

}
