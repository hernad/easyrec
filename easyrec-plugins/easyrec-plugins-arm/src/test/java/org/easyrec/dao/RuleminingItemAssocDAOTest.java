package org.easyrec.dao;


import org.easyrec.plugin.arm.store.dao.RuleminingItemAssocDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/TenantConfig_DEFAULT.xml",
        "spring/core/dao/AuthenticationDAO.xml",
        "spring/core/dao/types/ActionTypeDAO.xml",
        "spring/core/dao/types/AggregateTypeDAO.xml",
        "spring/core/dao/types/AssocTypeDAO.xml",
        "spring/core/dao/types/ItemTypeDAO.xml",
        "spring/core/dao/types/SourceTypeDAO.xml",
        "spring/core/dao/types/ViewTypeDAO.xml",
        "spring/core/dao/TenantDAO.xml",
        "spring/core/dao/ActionDAO.xml",
        "spring/core/dao/ProfileDAO.xml",
        "spring/core/dao/ItemAssocDAO.xml",
        "spring/core/idMapping.xml",
        "spring/core/service/ItemAssocService.xml",
        "spring/core/service/ClusterStrategies.xml",
        "spring/core/service/TenantService.xml",
        "spring/core/service/ClusterService.xml",
        "spring/domain/service/TypeMappingService.xml",
        "/spring/plugins/arm/dao/RuleMiningActionDAO.xml"})

public class RuleminingItemAssocDAOTest {

    @SpringBean("ruleMiningItemAssocDAO")
    private RuleminingItemAssocDAO ruleMiningItemAssocDAO;

    @Test
    public void ruleMiningItemAssocDAOtests() {
        System.out.println(ruleMiningItemAssocDAO.existsTable())   ;
        ruleMiningItemAssocDAO.getDefaultTableName();
        ruleMiningItemAssocDAO.createTable();
        ruleMiningItemAssocDAO.existsTable();
        System.out.println(ruleMiningItemAssocDAO.existsTable())   ;
    }

}
