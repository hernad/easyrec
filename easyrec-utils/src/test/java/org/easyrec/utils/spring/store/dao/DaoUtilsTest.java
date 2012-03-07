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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link at.researchstudio.sat.util.store.dao.DaoUtils} class.
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p>
 * <b>last modified:</b><br/> $Author: sat-rsa $<br/> $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/> $Revision: 119 $
 * </p>
 *
 * @author Florian Kleedorfer
 */
public class DaoUtilsTest {

    /*
    * Test method for
    * 'at.researchstudio.sat.util.store.DaoUtils.createSqlList(Iterable,
    * boolean)'
    */
    @Test
    public void testCreateSqlList() {
        // test for a list of strings
        List<String> values = new ArrayList<String>();
        values.add("a");
        values.add("b");
        values.add("c");
        assertEquals("('a','b','c')", DaoUtils.createSqlList(values, true));
        assertEquals("(a,b,c)", DaoUtils.createSqlList(values, false));

        // test for a list of objects
        List<Object> values2 = new ArrayList<Object>();
        values2.add("a");
        values2.add(1L);
        values2.add("b");
        assertEquals("('a','1','b')", DaoUtils.createSqlList(values2, true));
        assertEquals("(a,1,b)", DaoUtils.createSqlList(values2, false));

        // test for empty list
        values2.clear();
        assertEquals("()", DaoUtils.createSqlList(values2, true));
        assertEquals("()", DaoUtils.createSqlList(values2, false));

        // test for only one element
        values2.add("foo");
        assertEquals("('foo')", DaoUtils.createSqlList(values2, true));
        assertEquals("(foo)", DaoUtils.createSqlList(values2, false));
    }
}
