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
package org.easyrec.service.core.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.TenantVO;
import org.easyrec.service.core.TenantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/TenantService_AllInOne.xml"})
@DataSet("/dbunit/core/dao/tenant.xml")
public class TenantServiceTest {

    private static final Log logger = LogFactory.getLog(TenantServiceTest.class);

    private final TenantVO TENANT_EASYREC = new TenantVO("EASYREC", "Easyrec tenant", 1, 10, 5.5d);

    @SpringBeanByName
    private TenantService tenantService;

    @Test
    public void testInsertTenant() {
        tenantService.insertTenantWithTypes(TENANT_EASYREC, null);
        int i = 0;
    }

}
