package org.easyrec;

import org.easyrec.plugin.arm.ARMGenerator;
import org.easyrec.plugin.arm.model.ARMConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

/**
 * Unit test for simple App.
 */
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
        "spring/easyrec-plugin-test.xml"})
@DataSet("/dbunit/plugins/arm/testData.xml")
public class ARMGeneratorTest
{
    @SpringBeanByName
    protected ARMGenerator armGenerator;

    @Test
    @ExpectedDataSet("/dbunit/plugins/arm/ARMGeneratorTest_expected.xml")
    public void perform_shouldCalculateRules() {
        try {
            armGenerator.install(false);
            armGenerator.initialize();
            ARMConfiguration config = armGenerator.newConfiguration();
            config.setTenantId(1);
            armGenerator.setConfiguration(config);
            armGenerator.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
