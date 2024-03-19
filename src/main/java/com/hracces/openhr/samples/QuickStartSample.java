package com.hracces.openhr.samples;


import com.hracces.openhr.OpenHrApplication;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;

public class QuickStartSample {


    public static void main(String[] args) throws Exception {
        HRApplication.configureLogs("C:\\Users\\DELL\\Desktop\\pfe\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\lo4j.properties");
        IHRSession session = HRSessionFactory.getFactory().createSession(
                new PropertiesConfiguration("C:\\Users\\DELL\\Desktop\\pfe\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));


        IHRUser user=null;

        try{
            user = session.connectUser("HRAUSER", "SECRET");

            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();

            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setProcessName("FS001");
            parameters.setDataStructureName("ZY");


            parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
            parameters.addDataSection(new HRDataSourceParameters.DataSection("10"));


            HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("EMPLOYEE(123456)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));


            HRDossier employeeDossier = dossierCollection.loadDossier(new HRKey("HRA","123456"));

            HROccur birthOccurrence = employeeDossier.getDataSectionByName("10").getOccur();

            birthOccurrence.setDate("DATNAI", java.sql.Date.valueOf("1970-06-18"));

            employeeDossier.commit();


        } finally {
            if ((user != null) && user.isConnected()) {
                // Disconnecting user
                user.disconnect();
            }
            if ((session != null) && session.isConnected()) {
                // Disconnecting OpenHR session
                session.disconnect();
            }
        }
        HRApplication.setLoggingSystem(HRLoggingSystem.LOG4J);
        // Configuring logging system to use Commons Logging
        HRApplication.setLoggingSystem(HRLoggingSystem.COMMONS_LOGGING);
        // Configuring logging system to use standard output
        HRApplication.setLoggingSystem(HRLoggingSystem.STANDARD);
        // Configuring logging system to use Log4J
        HRApplication.setLoggingSystem(HRLoggingSystem.LOG4J);
        // Configuring Log4J from given configuration file
        HRApplication.configureLogs("C:\\Users\\DELL\\Desktop\\pfe\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");

        SpringApplication.run(OpenHrApplication.class, args);
    }

}
