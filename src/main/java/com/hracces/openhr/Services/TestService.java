package com.hracces.openhr.Services;


import com.hracces.openhr.entities.Employee;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {


   public List<Employee> getLabel() {
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
}

}
