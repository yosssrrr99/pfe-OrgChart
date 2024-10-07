package com.hracces.openhr.Services;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.hracces.openhr.entities.Employee;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;

import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenHRService {





   /* public List<Employee> getLabel() {
        List<Employee> employees = new ArrayList<>();

        try {
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



   /* public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();

        try {
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
                        String nom = null;
                        String prenom = null;

                        List<IHRItem> items = dataSection.getAllItems();
                        for (IHRItem item : items) {
                            // Identifier les rubriques pertinentes
                            switch (item.getName()) {
                                case "NOMPAT": // Nom de naissance
                                    nom = item.getLabel();
                                    break;
                                case "PRENOM": // Prénom
                                    prenom = item.getLabel();
                                    break;
                                // Ajoutez d'autres cases pour les rubriques que vous souhaitez extraire
                            }
                        }

                        // Ajoutez l'employé à la liste si des données valides ont été trouvées
                        if (nom != null || prenom != null) {
                            employees.add(new Employee(nom, prenom));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }
*/


}

