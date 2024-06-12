package com.hracces.openhr.Services;


import com.hracces.openhr.Repositories.EmployeeRecRepositories;
import com.hracces.openhr.Repositories.EmployeeRepositories;
import com.hraccess.openhr.*;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
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

@Service
public class LoginService {

    @Autowired
    private EmployeeRepositories employeeRepositories;
    @Autowired
    private EmployeeRecRepositories employeeRecRepositories;

    private final HRSessionFactory hrSessionFactory;
    private final String username = "TALAN2PR";
    private final String password = "HRA2023!";
    private IHRSession session;
    private IHRUser user;

    @Autowired
    public LoginService(HRSessionFactory hrSessionFactory) {
        this.hrSessionFactory = hrSessionFactory;
    }



    public HRResultExtractData login() throws HRException, ConfigurationException {

        HRApplication.configureLogs("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\log4j.properties");
        session = HRSessionFactory.getFactory().createSession(
                new PropertiesConfiguration("G:\\pfe\\hos\\OpenHR\\src\\main\\java\\com\\hracces\\openhr\\conf\\openhr.properties"));
        user = session.connectUser(username, password);
        HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
        parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
        parameters.setProcessName("MA001");
        parameters.setDataStructureName("ZY");


        parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("4I"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("AU"));
        parameters.addDataSection(new HRDataSourceParameters.DataSection("3B"));


        IHRDictionary dictionary = session.getDictionary();


        IHRConversation conversation = user.getMainConversation();

        IHRRole role = user.getRole("ALLHRLO(MA)");

        HRMsgExtractData request = new HRMsgExtractData();
        request.setFirstRow(0);
        request.setMaxRows(200);
        //    request.setSqlStatement("SELECT a.MATCLE, b.MTSAL,a.BLOB01, b.DATEFF, b.DATFIN, a.NOMUSE, g.AMANN ,a.PRENOM,e.IDOU00, d.LBOULG FROM ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f, ZE40 g WHERE g.IDOU00=i.IDOU00 and i.IDOU00=e.IDOU00 and a.NUDOSS = b.NUDOSS AND a.NUDOSS = e.NUDOSS AND a.SOCDOS = 'MIT' AND a.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F' AND d.NUDOSS = i.NUDOSS");
        request.setSqlStatement("SELECT DISTINCT m.*,a.MATCLE, b.MTSAL,a.BLOB01, a.NOMUSE,a.PRENOM,e.IDOU00,d.LBOULG FROM  ZY00 a, ZYAU b,ZE01 d,ZE00 i, ZY3B e, ZY1S f,ZY4I m WHERE  i.IDOU00=e.IDOU00  and a.NUDOSS = b.NUDOSS and a.NUDOSS = m.NUDOSS AND a.NUDOSS = e.NUDOSS and a.SOCDOS = 'MIT' AND b.NUDOSS = f.NUDOSS AND f.STEMPL = 'A' AND e.DTEN00 >= GETDATE() AND b.DATFIN >= GETDATE() AND d.CDLANG = 'F'  and  d.NUDOSS = i.NUDOSS ");
        HRResultExtractData result = (HRResultExtractData) conversation.send(request, role);

        return result;

    }

}
