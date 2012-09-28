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

import org.easyrec.utils.spring.profile.Stopwatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Stephan Zavrel
 */

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({"/spring/easyrecDataSource.xml", "/spring/utils/IDMappingDAO.xml"})
@DataSet("/dbunit/IDMappingDAO/dbunit.idmapping.xml")
public class IDMappingDAOTest {
    @SpringApplicationContext
    private ApplicationContext context;

    @SpringBean("easyrecDataSource")
    private DataSource dataSource;

    @SpringBean("idMappingDAO")
    private IDMappingDAO mappingDAO;

    @Test
    public void lookup_string() {
        // test successful lookup
        int returnedId = mappingDAO.lookup("test2");

        assertThat(returnedId, is(2));
        // test create new mapping
        returnedId = mappingDAO.lookup("test4");

        assertThat(returnedId, is(not(1)));
        assertThat(returnedId, is(not(2)));
        assertThat(returnedId, is(not(3)));
        // test for equal datasets
    }

    @Test
    @DataSet("/dbunit/IDMappingDAO/dbunit.idmapping.one_less.xml")
    @ExpectedDataSet("/dbunit/IDMappingDAO/dbunit.idmapping_no_id.xml")
    public void lookup_nonExistantStringIsInserted() {
        mappingDAO.lookup("test3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void lookup_emptyString() {
        mappingDAO.lookup("");
    }

    @Test
    public void lookup_nullInteger() {
        String returnedName = mappingDAO.lookup((Integer) null);

        assertThat(returnedName, is(nullValue()));
    }

    @Test
    public void lookup_stringInCache() {
        Stopwatch st = new Stopwatch();
        st.start();
        // test successful lookup

        int returnedId = mappingDAO.lookup("test2");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(2));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test1");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(1));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test2");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(2));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test1");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(1));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test2");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(2));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test1");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(1));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test2");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(2));
        // test successful lookup

        st.restart();
        int newId = returnedId = mappingDAO.lookup("test4");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(not(1)));
        assertThat(returnedId, is(not(2)));
        assertThat(returnedId, is(not(3)));
        // test successful lookup

        st.restart();
        returnedId = mappingDAO.lookup("test4");
        System.out.println("Elapsed time: " + st.timePassed());
        assertThat(returnedId, is(newId));
        // test successful lookup
    }

    @Test
    public void lookup_Integer() {
        // test successful lookup
        String returnedName = mappingDAO.lookup(1);
        assertThat(returnedName, is("test1"));
        // test unsuccessful lookup
        returnedName = mappingDAO.lookup(7);
        assertThat(returnedName, is(nullValue()));
    }
}
