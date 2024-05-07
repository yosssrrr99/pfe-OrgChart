package com.hracces.openhr.samples;

import com.hracces.openhr.OpenHrApplication;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;

import java.sql.Date;

public class QuickStartSample {

    public static void main(String[] args) throws Exception {
        // Configurer les journaux
        HRApplication.configureLogs("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");

        // Initialiser la session et l'utilisateur
        IHRSession session = null;
        IHRUser user = null;
        try {
            session = HRSessionFactory.getFactory().createSession(
                    new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));
            user = session.connectUser("TALAN2PR", "HRA2023!");

            // Créer les paramètres de la collection de dossiers
            HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
            parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
            parameters.setProcessName("MA001");
            parameters.setDataStructureName("ZY");
            parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
            parameters.addDataSection(new HRDataSourceParameters.DataSection("10"));

            // Charger la collection de dossiers
            HRDossierCollection dossierCollection = new HRDossierCollection(parameters, user.getMainConversation(), user.getRole("ALLHRLO(MA)"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

            // Charger un dossier spécifique et obtenir la date de naissance
            HRDossier dossier = dossierCollection.loadDossier(1000);
            Date dateNaissance = dossier.getDataSectionByName("00").getOccur().getDate("ZY00 DATNAI");

            // Utiliser la date de naissance pour effectuer d'autres opérations si nécessaire
            System.out.println("Date de naissance de l'employé : " + dateNaissance);
        } finally {
            // Déconnecter l'utilisateur et fermer la session
            if (user != null && user.isConnected()) {
                user.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }

        // Configurer le système de journalisation
        HRApplication.setLoggingSystem(HRLoggingSystem.LOG4J);
        SpringApplication.run(OpenHrApplication.class, args);
    }
}
