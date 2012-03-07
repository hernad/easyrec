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
package org.easyrec.utils.spring.store.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("spring/easyrecDataSource.xml")
public class DaoUtilsWithDBTest {
    @SpringBean("easyrecDataSource")
    private DataSource dataSource;

    @Test
    public void testGetDatabaseURLAndUserName() throws Exception {
        String urlAndUsername = DaoUtils.getDatabaseURLAndUserName(dataSource);

        assertEquals("jdbc:mysql://localhost/easyrec_test (userName='root@localhost')", urlAndUsername);
    }
}
