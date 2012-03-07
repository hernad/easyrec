/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import static org.junit.Assert.*;

/**
 * Tests for the {@link org.easyrec.utils.spring.store.dao.DBRootDAO} class.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml", "spring/utils/DBRootDAO.xml"})
public class DBRootDAOTest {
    @SpringBeanByName
    private DBRootDAO rootDAO;

    private static final String DB_NAME = "12345xyz";

    // test methods
    public void testExistsDatabaseNonExisting() {
        assertFalse(rootDAO.existsDatabase(DB_NAME));
    }

    @Test
    public void testExistsDatabaseExisting() {
        try {
            rootDAO.createDatabase(DB_NAME);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testCreateDatabaseNullDBName() {
        try {
            rootDAO.createDatabase(null);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), "passed param 'databaseName' must not be null or empty");
        }
    }

    @Test
    public void testCreateDatabaseEmptyDBName() {
        try {
            rootDAO.createDatabase("");
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), "passed param 'databaseName' must not be null or empty");
        }
    }

    @Test
    public void testCreateDatabaseForbiddenDBName() {
        try {
            rootDAO.createDatabase("create");
            fail();
        } catch (BadSqlGrammarException bsge) {
        }
    }

    @Test
    public void testCreateDatabaseNonExistingNoOverwrite() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testCreateDatabaseNonExistingOverwrite() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME, true);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testCreateDatabaseExistingNoOverwrite() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME);
            fail();
        } catch (IllegalArgumentException iae) {
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testCreateDatabaseExistingOverwrite() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME, true);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME, true);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testCreateDatabaseExistingNoOverwriteNoCheck() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME, false, false);
            fail();
        } catch (UncategorizedSQLException use) {
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
        }
    }

    @Test
    public void testCreateDatabaseExistingOverwriteNoCheck() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME);
            assertTrue(rootDAO.existsDatabase(DB_NAME));
            rootDAO.createDatabase(DB_NAME, true, false);
            fail();
        } catch (UncategorizedSQLException use) {
        } finally {
            rootDAO.deleteDatabase(DB_NAME);
            assertFalse(rootDAO.existsDatabase(DB_NAME));
        }
    }

    @Test
    public void testDeleteDatabaseNonExistingCheck() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.deleteDatabase(DB_NAME);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), "the database '" + DB_NAME + "' does not exist, it can not be deleted");
        }
    }

    @Test
    public void testDeleteDatabaseNonExistingNoCheck() {
        try {
            assertFalse(rootDAO.existsDatabase(DB_NAME));
            rootDAO.deleteDatabase(DB_NAME, false);
            fail();
        } catch (DataAccessException dae) {
        }
    }

    @Test
    public void testDeleteDatabaseExisting() {
        assertFalse(rootDAO.existsDatabase(DB_NAME));
        rootDAO.createDatabase(DB_NAME);
        assertTrue(rootDAO.existsDatabase(DB_NAME));
        rootDAO.deleteDatabase(DB_NAME);
        assertFalse(rootDAO.existsDatabase(DB_NAME));
    }
}

