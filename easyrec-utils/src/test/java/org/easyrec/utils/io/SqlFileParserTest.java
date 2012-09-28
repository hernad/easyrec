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
package org.easyrec.utils.io;

import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for the {@link org.easyrec.utils.io.SqlFileParser} class.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2005</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */
public class SqlFileParserTest {

    private final static String TEST_FILE_PREFIX = "/sqlFileParserTest/";

    /*
    * Test method for 'org.easyrec.utils.io.SqlFileParser.parse()'
    */
    @Test
    public void testSimpleSql() {
        InputStream in = getClass().getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestSimple.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        List<String> statements = parser.parse();
        assertEquals(statements.size(), 5);
        int cnt = 0;
        for (String stmt : statements) {
            cnt++;
            assertEquals(" while processing statement " + cnt, "select * from dual", stmt);
        }
    }

    @Test
    public void testMultiLineSql() {
        InputStream in = getClass().getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestMultiline.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        List<String> statements = parser.parse();
        assertEquals(statements.size(), 5);
        int cnt = 0;
        for (String stmt : statements) {
            cnt++;
            assertEquals(" while processing statement " + cnt, "select * from dual", stmt);
        }
    }

    @Test
    public void testMultipleStatementsPerLine() {
        InputStream in = getClass()
                .getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestMultipleStatementsPerLine.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        List<String> statements = parser.parse();
        assertEquals(statements.size(), 10);
        int cnt = 0;
        for (String stmt : statements) {
            cnt++;
            assertEquals(" while processing statement " + cnt, "select * from dual", stmt);
        }
    }

    @Test
    public void testUnclosedStatement() {
        InputStream in = getClass().getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestUnclosedStatement.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        try {
            parser.parse();
            fail("expected an UnclosedStatementException");
        } catch (UnclosedStatementException e) {
        }
    }

    @Test
    public void testTrailingWhitespace() {
        InputStream in = getClass().getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestTrailingWhitespace.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        try {
            parser.parse();
        } catch (UnclosedStatementException e) {
            fail("parser must not throw an UnclosedStatementException here");
        }
    }

    @Test
    public void testMultipleCommentCharacters() {
        InputStream in = getClass().getResourceAsStream(TEST_FILE_PREFIX + "sqlFileParserTestMultipleCommentChars.sql");
        Reader reader = new InputStreamReader(in);
        SqlFileParser parser = new SqlFileParser(reader);
        try {
            List<String> statements = parser.parse();
            assertEquals(5, statements.size());
            int cnt = 0;
            for (String stmt : statements) {
                cnt++;
                assertEquals(" while processing statement " + cnt, "select * from dual", stmt);
            }
        } catch (UnclosedStatementException e) {
            fail("parser must not throw an UnclosedStatementException here");
        }
    }

}
