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
package org.easyrec.controller;

import com.google.common.base.Strings;
import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.model.web.Message;
import org.easyrec.model.web.Operator;
import org.easyrec.store.dao.web.LoaderDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.MyUtils;
import org.easyrec.utils.Security;
import org.easyrec.utils.io.Text;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author szavrel
 */
public class LoaderController extends AbstractController {

    private Resource resource;
    private Properties props;
    private LoaderDAO loaderDAO;

    private String action;
    private EasyRecSettings easyrecSettings;


    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse arg1)
            throws Exception {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String test = System.getProperty("catalina.home");
        Properties env= System.getProperties();


        String db_username = ServletUtils.getSafeParameterDecoded(request, "db_username", "");
        String db_password = ServletUtils.getSafeParameterDecoded(request, "db_password", "");
        String db_host = ServletUtils.getSafeParameterDecoded(request, "db_host", "");
        String db_name = ServletUtils.getSafeParameterDecoded(request, "db_name", "");


        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String password = ServletUtils.getSafeParameter(request, "password", "");
        String passwordConfirm = ServletUtils.getSafeParameter(request, "passwordConfirm", "");
        String firstName = ServletUtils.getSafeParameter(request, "firstName", "");
        String lastName = ServletUtils.getSafeParameter(request, "lastName", "");

        String sourcePage = ServletUtils.getSafeParameter(request, "sourcePage", "");

        firstName = Text.capitalize(firstName);
        lastName = Text.capitalize(lastName);

        String localName = request.getLocalName();
        localName = localName.equals("0.0.0.0") ? "localhost" : localName;

        Float currentVersion   = Float.parseFloat(easyrecSettings.getVersion());
        // Float currentVersion = new Float(0.95);

        mav.addObject("currentVersion", currentVersion);
        mav.addObject("currentYear", MyUtils.getCurrentYear());
        mav.addObject("webappPath", request.getContextPath());
        mav.addObject("extendedWebAppPath", request.getScheme() + "://" +
                //request.getLocalAddr()+ ":" +
                localName + ":" + request.getLocalPort() + request.getContextPath());


        if ("home".equals(action)) {
            mav.setViewName("home");

            URL connString = new URL(props.getProperty("db.easyrec.url").replace("jdbc:mysql", "http"));
            //mav.addObject("db_username", props.get("db.easyrec.username"));
            //mav.addObject("db_password", props.get("db.easyrec.password"));
            mav.addObject("db_host",
                    connString.getHost() + (connString.getPort() == -1 ? "" : ":" + connString.getPort()));
            mav.addObject("db_name", connString.getPath().replace("/", ""));
            return mav;

        }

        if ("connect".equals(action)) {

            mav.setViewName("create");
            mav.addObject("db_username", db_username);
            mav.addObject("db_password", db_password);
            mav.addObject("db_host", db_host);
            mav.addObject("db_name", db_name);

            Float installedVersion = LoaderDAO.INITIAL_VERSION;

            if (db_username != null) {
                try {
                    loaderDAO.testConnection("jdbc:mysql://" + db_host + "/" + db_name + "?useUnicode=true&characterEncoding=UTF-8", db_username, db_password);

                    // check easyrec version
                    // if a previous version is installed offer user
                    // to use migrate script and keep existing data.
                    // if the same version is reinstalled offer to
                    // keep existing data.
                    installedVersion = loaderDAO.checkVersion();
                    mav.addObject("installedVersion", installedVersion);


                } catch (Exception e) {
                    mav.setViewName("home");
                    // if wrong host
                    if (e.getMessage().contains("Communications link failure")) {
                        mav.addObject("exceptionMessage",
                                "easyrec could not connect to the specified host '" + db_host + "'.<br/> " +
                                        "Please make sure the host is reachable and a MySQL server is running!");
                    } else
                        // if wrong db name
                        if ((e.getMessage().contains("Unknown database"))) {
                            mav.addObject("exceptionMessage",
                                    "A database with name '" + db_name + "' does not exist on host '" + db_host +
                                            "'!<br/>" +
                                            "Please change the name to a valid database or create a new <br/>" +
                                            "database with the name specified above!");
                        } else
                            // if wrong credentials
                            if ((e.getMessage().contains("Access denied"))) {
                                mav.addObject("exceptionMessage",
                                        "easyrec has been denied access to the specified database!<br/>" +
                                                "Please make sure you entered a valid username/password combination.");
                            } else {
                                mav.addObject("exceptionMessage", e.getMessage());
                            }
                    //mav.addObject("exceptionCause", e.getCause());
                    //mav.addObject("exceptionStackTrace", e.getStackTrace());
                }
                // if successful
                if ("create".equals(mav.getViewName())) {

                    //store settings to Prop-file
                    mav.addObject("db_name", db_name);
                    props.setProperty("db.easyrec.url", "jdbc:mysql://" + db_host + "/" + db_name + "?useUnicode=true&characterEncoding=UTF-8");
                    props.setProperty("db.easyrec.username", db_username);
                    props.setProperty("db.easyrec.password", db_password);

                    try {
                        File f = new File(resource.getFile(), "easyrec.database.properties");
                        f.createNewFile();
                        props.store(new FileOutputStream(f), "");
                    } catch (IOException ioe) {
                        mav.setViewName("home");
                        mav.addObject("exceptionMessage", "easyrec could not store your database settings!" +
                                "Please make sure easyrec has write privileges in its own web application folder!");
                        mav.addObject("exceptionCause", ioe.getMessage());
                    }
                    if (installedVersion == null)
                        installedVersion = 0f; //somethings wrong with the db; treat as new installation!
                    if ((installedVersion > 0f) && (installedVersion < currentVersion)) {
                        if (installedVersion < 0.95f) {
                            mav.addObject("showBox", true);
                        } else
                          mav.addObject("showBox", false);
                        mav.addObject("installedVersion", installedVersion);
                        mav.setViewName("migrate");
                    }
                }

            }
            return mav;
        }

        if ("migrate".equals(action)) {
            // if successful: load spring context and show login page
            mav.setViewName("config");
            try {
                mav.addObject("action", "login");
                loaderDAO.migrateDB();
            } catch (Exception e) {
                logger.warn("Error during migration.", e);
                mav.setViewName("migrate");
                mav.addObject("exceptionMessage", "An error occured migrating the database! " +
                        "Is the MySQL Server running and does the given user have sufficient <br/>" +
                        "privileges to create and alter the database? You may also check the easyrec log for details.");
                mav.addObject("exceptionCause", e.getMessage());
            }
            return mav;
        }

        // user decided to rebuild database from scratch
        if ("create".equals(action)) {
            mav.setViewName("config");
            try {
                loaderDAO.createDB();
                mav.addObject("action", "signup");
            } catch (Exception e) {
                if (sourcePage != null) {
                    mav.setViewName(sourcePage);
                } else {
                    mav.setViewName("create");
                }
                mav.addObject("exceptionMessage", "An error occured creating the database! <br/>" +
                        "Is the MySQL Server running and does the given user have sufficient <br/>" +
                        "privileges to create the database? You may also check the easyrec log for details.");
                mav.addObject("exceptionCause", e.getMessage());
            }
            return mav;
        }

        if ("existing".equals(action)) {
            mav.setViewName("config");
            mav.addObject("action", "login");
            return mav;
        }

        // user decided to keep existing data
        if ("login".equals(action)) {
            mav.setViewName("login");
            try {
                mav.setViewName("login");

                saveConfig(request);

                loaderDAO.reloadBackend();

            } catch (IOException ioe) {
                mav.setViewName("config");
                mav.addObject("action", "config");
                mav.addObject("exceptionMessage", "easyrec could not load context!");
                mav.addObject("exceptionCause", ioe.getMessage());
            }
            return mav;
        }

        if ("loadcontext".equals(action)) {

            loaderDAO.reloadFrontend();
            return MessageBlock.create(mav, messages, action, MSG.SUCCESS);
        }

        if ("signup".equals(action)) {
            try {
                mav.setViewName("signup");

                saveConfig(request);

                loaderDAO.reloadBackend();
                return mav;
            } catch (IOException ioe) {

                mav.setViewName("config");
                mav.addObject("exceptionMessage", "easyrec could not store your configuration settings!" +
                        "Please make sure easyrec has write privileges in its own web application folder!");
                mav.addObject("exceptionCause", ioe.getMessage());
                return mav;
            }
        }


        if ("load".equals(action)) {

            // Hashfunction of user name
            // produces API Key (e.g. 5ZAOMB3BUR8QUN4P = hash(operatorId))
            String apiKey = Text.generateHash(operatorId);

            if (Strings.isNullOrEmpty(operatorId)) {
                messages.add(MSG.OPERATOR_EMPTY);
            }

            if (operatorId.contains(" ")) {
                messages.add(MSG.OPERATOR_CONTAINS_SPACE);
            }

            if (Text.containsEvilSpecialChar(operatorId) || Text.containsEvilSpecialChar(password) ||
                    Text.containsEvilSpecialChar(passwordConfirm) || Text.containsEvilSpecialChar(firstName) ||
                    Text.containsEvilSpecialChar(lastName)) {
                messages.add(MSG.SPECIAL_CHARACTERS);
            }

            if (password.length() < Operator.MIN_PASSWORD_LENGTH) {
                messages.add(MSG.OPERATOR_PASSWORD_TO_SHORT);
            }

            if (!passwordConfirm.equals(password)) {
                messages.add(MSG.OPERATOR_PASSWORD_MATCH);
            }

            if (messages.size() > 0) {
                return MessageBlock.create(mav, "../xml/messageblock", messages, action, MSG.ERROR);
            } else {

                Security.signIn(request,
                        loaderDAO.addOperator(operatorId, password, firstName, lastName, null, // email,
                                null, // phone,
                                null, // company,
                                null, // address,
                                apiKey, null // ip
                        ));

                loaderDAO.reloadFrontend();

                messages.add(MSG.OPERATOR_REGISTERED.append(" (" + operatorId + ")"));

                return MessageBlock.create(mav, messages, action, MSG.SUCCESS);
            }
        }

        // user kept existing database thats why he can be signed in without
        // creating a new account
        //        if ("login".equals(action)) {
        //            mav.setViewName("login");
        //            // get operator id
        //            return mav;
        //        }

        mav.setViewName("home");
        return mav;
    }


    private void saveConfig(HttpServletRequest request) throws IOException {

        String rest = ServletUtils.getSafeParameterDecoded(request, "rest", "");
        String soap = ServletUtils.getSafeParameterDecoded(request, "soap", "");
        String dev = ServletUtils.getSafeParameterDecoded(request, "dev", "");

        props.setProperty("easyrec.rest", rest);
        props.setProperty("easyrec.soap", soap);
        props.setProperty("easyrec.dev", dev);
        props.setProperty("easyrec.firstrun", "true");

        File f = new File(resource.getFile(), "easyrec.database.properties");
        props.store(new FileOutputStream(f), "");
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setLoaderDAO(LoaderDAO loaderDAO) {
        this.loaderDAO = loaderDAO;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public void setEasyrecSettings(EasyRecSettings easyrecSettings) {
        this.easyrecSettings = easyrecSettings;
    }
}