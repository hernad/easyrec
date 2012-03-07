/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.store.dao.core;

import org.easyrec.model.core.TenantVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/utils/aop/DAO.xml",
        "spring/core/dao/TenantDAO.xml"})
@DataSet(value = TenantDAOTest.DATA_FILENAME)
public class TenantDAOTest {
    // constants
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/tenant.xml";

    private final TenantVO TENANT_TEST = new TenantVO(0, "TEST", "aaa", 1, 10, 5.5d);
    private final TenantVO TENANT_MEX_TEST = new TenantVO(1, "MEX-TEST", "bbb", 1, 10, 5.5d);
    private final TenantVO TENANT_RASCALLI = new TenantVO(3, "RASCALLI", "ddd", 1, 10, 5.5d);
    private final TenantVO TENANT_EASYREC = new TenantVO("EASYREC", "Easyrec tenant", 1, 10, 5.5d);

    // members
    @SpringBeanByName
    private TenantDAO tenantDAO;

    @Test
    public void testGetTenantById() {
        TenantVO resultTenant = tenantDAO.getTenantById(0);
        assertEquals(TENANT_TEST, resultTenant);

        resultTenant = tenantDAO.getTenantById(1);
        assertEquals(TENANT_MEX_TEST, resultTenant);

        resultTenant = tenantDAO.getTenantById(3);
        assertEquals(TENANT_RASCALLI, resultTenant);

        resultTenant = tenantDAO.getTenantById(Integer.MAX_VALUE);
        assertNull(resultTenant);
    }

    @Test
    public void testGetTenantByIdEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        TenantVO resultAssoc = tenantDAO.getTenantById(0);
        assertNull(resultAssoc);
    }

    @Test
    public void testGetTenantByStringId() {
        TenantVO resultTenant = tenantDAO.getTenantByStringId("TEST");
        assertEquals(TENANT_TEST, resultTenant);

        resultTenant = tenantDAO.getTenantByStringId("MEX-TEST");
        assertEquals(TENANT_MEX_TEST, resultTenant);

        resultTenant = tenantDAO.getTenantByStringId("RASCALLI");
        assertEquals(TENANT_RASCALLI, resultTenant);

        resultTenant = tenantDAO.getTenantByStringId("NOT VALID");
        assertNull(resultTenant);
    }

    @Test
    public void testGetTenantByStringIdEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        TenantVO resultAssoc = tenantDAO.getTenantByStringId("TEST");
        assertNull(resultAssoc);
    }

    @Test
    public void testInsertTenant() {
        int i = tenantDAO.insertTenant(TENANT_EASYREC);
        assertEquals(i, 5);
    }

    @Test
    public void testSetTenantActive() {
        tenantDAO.setTenantActive(TENANT_TEST, false);
        TenantVO tmpTenant = tenantDAO.getTenantById(TENANT_TEST.getId());
        assertEquals(tmpTenant.getActive(), false);
    }

    @Test
    public void testGetAllTenants() {
        List<TenantVO> tenants = tenantDAO.getAllTenants();
        assertEquals(tenants.size(), 5);
    }
}
