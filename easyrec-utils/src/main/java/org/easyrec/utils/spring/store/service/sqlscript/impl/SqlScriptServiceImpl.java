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
package org.easyrec.utils.spring.store.service.sqlscript.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.utils.io.SqlFileParser;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * Ipmlementation of the SqlScriptService interface. It has to be configured with
 * a valid <code>DataSource</code> and makes use of {@link SqlFileParser}.
 * </p>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2006</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class SqlScriptServiceImpl extends JdbcDaoSupport implements SqlScriptService, ResourceLoaderAware {

    private final Log logger = LogFactory.getLog(getClass());
    private ResourceLoader resourceLoader;

    public SqlScriptServiceImpl(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }

    public List<String> parseSqlScript(String filename) {
        try {
            return parseSqlScript(resourceLoader.getResource(filename).getInputStream());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found '" + filename + "'", e);
        } catch (IOException ioe) {
            throw new RuntimeException("file not found '" + filename + "'", ioe);
        }
    }

    public List<String> parseSqlScript(InputStream stream) {
        SqlFileParser parser = new SqlFileParser(stream);
        return parser.parse();
    }

    public void executeSqlScript(String filename) throws RuntimeException {
        //open the file

        if (logger.isDebugEnabled()) {
            logger.debug("running sql script '" + filename + "'");
        }
        try {
            executeSqlScript(resourceLoader.getResource(filename).getInputStream());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found '" + filename + "'", e);
        } catch (IOException ioe) {
            throw new RuntimeException("file not found '" + filename + "'", ioe);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("running sql script '" + filename + "' done");
        }
    }

    public void executeSqlScript(InputStream stream) {
        //create a reader for the file
        SqlFileParser parser = new SqlFileParser(stream);
        List<String> statements = parser.parse();
        if (logger.isDebugEnabled()) {
            logger.debug("running these commands: " + statements);
        }
        try {
            getJdbcTemplate().batchUpdate(statements.toArray(new String[statements.size()]));
        } catch (RuntimeException e) {
            logger.warn("caught exception during sql script execution", e);
            throw e;
        }
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
