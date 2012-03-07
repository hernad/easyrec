package org.easyrec.utils;


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


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * !!! CAUTION This is a class to fill the DB with action & items
 * all existing DATA will be DELETED !!!
 * <p/>
 * This function creates equal distributed actions for the number of given
 * tenants in the given time range.
 * <p/>
 * Every created action has a random itemid, userid and actiontypeid.
 * <p/>
 * USE THIS utitlity AFTER successfully setting up the easyrec server
 * with your chossen operator id and password.
 *
 * @author Peter Hlavac
 */
public class Benchmark {

    private static final int NUMBER_OF_TENANTS = 3;
    private static final int NUMBER_OF_USERS = 500000;
    private static final int NUMBER_OF_ITEMS = 100000;
    private static final int NUMBER_OF_ACTIONS = 10 * 1000 * 1000 * NUMBER_OF_TENANTS; // x actions per tenant

    private static final int ACTION_TYPE_VIEW = 1;
    private static final int ACTION_TYPE_BUY = 3;
    private static final int ACTION_TYPE_RATE = 2;
    private static final int MAX_RATING_VALUE = 10;

    private static final int MAX_ACTIONS_PER_USER = 500;
    private static final int MAX_ACTIONS_ON_ITEM = 10000;

    private static final int ACTIONS_TIME_RANGE = 5; // this year - x years e.g. 2005-2010

    private static final String OPERATOR_ID = "p";
    private static final String OPERATOR_PASSWORD = "ppppp";

    private static final String SIGN_IN_REQUEST =
            "http://localhost:8084/easyrec-web/operator/signin?operatorId=" + OPERATOR_ID + "&password=" +
                    OPERATOR_PASSWORD;

    private static final String START_PLUGINS_REQUEST =
            "http://localhost:8084/easyrec-web/PluginStarter?operatorId=" + OPERATOR_ID + "&tenantId=EASYREC_DEMO";


    private static final int[] ACTIONTYPES = {ACTION_TYPE_VIEW, ACTION_TYPE_BUY, ACTION_TYPE_RATE};

    private static final String DB_HOST = "localhost";
    private static final String DB_NAME = "easyrec";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";


    private static final Random r = new Random();

    private static int[] userIdDistribution = new int[NUMBER_OF_USERS];
    private static int[] itemIdDistribution = new int[NUMBER_OF_ITEMS];

    private static Header header = null;
    private static Header[] headers = null;


    // SQL query to decrease the number of items in action table
    // (works only one way, start with a high number and decrease step by step for benchmarks)
    // UPDATE actions SET itemid = itemdid % NEW_NUMBER_OF_ITEMS


    public static void main(String[] args) {

        Connection con = null;
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + "/" + DB_NAME, DB_USER, DB_PASSWORD);

            System.out.println(
                    "begin:" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
            Statement st = con.createStatement();
            initItemAndUserIds();


            st.executeUpdate("DELETE FROM action;");
            st.executeUpdate("DELETE FROM item;");
            st.executeUpdate("DELETE FROM itemassoc;");
            st.executeUpdate("DELETE FROM idmapping;");
            System.out.println("db reset.");


            createItems(st);
            createIdMapping(st);
            createActions(st, NUMBER_OF_ACTIONS);
            startPlugins();

            System.out.println(
                    "end:" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Benchmark.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void startPlugins() {
        String signin = loadWebsiteHtmlCode(SIGN_IN_REQUEST);
        System.out.println(signin);
        if (signin.contains("113")) {
            // TODO: Set cookie jsession ID or disable security check in RM
            String pluginOutput = loadWebsiteHtmlCode(START_PLUGINS_REQUEST);
            if (pluginOutput.contains("901")) {
                System.out.println("plugins finished. See plugin logs for details.");
            } else {
                System.out.println("plugins have a problem:" + pluginOutput);
            }
        } else {
            System.out.println("Login failed");
        }
    }


    private static void createItems(Statement st) throws Exception {

        for (int j = 1; j <= NUMBER_OF_TENANTS; j++) {

            for (int i = 1; i <= NUMBER_OF_ITEMS; i++) {

                StringBuilder s = new StringBuilder().append("          INSERT INTO ").append("  item").append("(")
                        .append("  tenantId,").append("  itemid,").append("  itemtype,").append("  description,")
                        .append("  url,").append("  imageurl,").append("  active").append(") ").append("VALUE (")
                        .append(j).append("  ,").append(i).append("  ,'ITEM',")
                        .append(" 'The item description of item: " + i + "'")
                        .append("  ,'http://this.is.my.tenant.com/item/url/itemid/" + i + "',")
                        .append("  'http://easyrec.org/img/easyrec_logo.gif?id=" + i + "',").append("  1").append(")");
                st.executeUpdate(s.toString());
            }
        }
        System.out.println("items for " + NUMBER_OF_TENANTS + " tenant(s) created.");
    }

    private static void createIdMapping(Statement st) throws Exception {
        for (int i = 1; i <= Math.max(NUMBER_OF_ITEMS, NUMBER_OF_USERS); i++) {

            StringBuilder s = new StringBuilder().append("          INSERT INTO ").append("  idmapping").append("(")
                    .append("  intId,").append("  stringId").append(") ").append("VALUE (").append(i).append("  ,")
                    .append(i).append(")");
            st.executeUpdate(s.toString());
        }
        System.out.println("idmapping created.");
    }

    /**
     * Creates actions with a random timestamp starting at the beginning of the
     * year until current date.
     *
     * @param st
     * @param actionType
     * @param actions
     * @throws Exception
     */
    private static void createActions(Statement st, int actions) throws Exception {

        int i = 0;
        int actionTypeId = 0;

        Calendar actionTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();

        while (i < actions) {
            i++;
            //actionTime.set(
            //        actionTime.get(Calendar.YEAR),
            //        r.nextInt(Calendar.getInstance().get(Calendar.MONTH)+1),
            //        1
            //);
            actionTime.set(currentTime.get(Calendar.YEAR) - ACTIONS_TIME_RANGE + r.nextInt(ACTIONS_TIME_RANGE) + 1,
                    r.nextInt(12) + 1, 1);


            actionTypeId = getActionTypeId();

            StringBuilder s = new StringBuilder().append("INSERT INTO ").append("  action").append("(")
                    .append("  tenantId,").append("  userId,").append("  sessionId,").append("  ip,")
                    .append("  itemId,").append("  itemTypeId,").append("  actionTypeId,");
            if (ACTION_TYPE_RATE == actionTypeId) s.append("  ratingValue,");
            s.append("  description,").append("  actionTime").append(")").append("VALUE (")
                    .append(r.nextInt(NUMBER_OF_TENANTS) + 1).append("  ,").append(getUserId()).append(" ,'1',")
                    .append(" '1',").append(getItemId()).append("  ,1,").append(actionTypeId);
            if (ACTION_TYPE_RATE == actionTypeId) s.append(",").append(r.nextInt(MAX_RATING_VALUE));
            s.append("  ,'1','").append(actionTime.get(Calendar.YEAR)).append("-")
                    .append(actionTime.get(Calendar.MONTH) + 1).append("-")
                    .append(r.nextInt(actionTime.getActualMaximum(Calendar.DAY_OF_MONTH)) + 1).append("'").append(")");

            st.executeUpdate(s.toString());
            if (i % 100000 == 0) {
                System.out.println(i + " actions inserted.");
            }
        }
        System.out.println(i + " random actions created for " + NUMBER_OF_TENANTS +
                " tenant(s) with actiontypes (view: 99,4%, buy: 0,5%, rate: 0,1% ");
    }

    // on average 0.5 percent of the viewed items are bought
    // on average 0.1 percent of the viewed item are rated
    // (source: flimmit_beta, tallat)
    private static int getActionTypeId() {
        int i = r.nextInt(1000);          // Random int from 0 - 999
        if (i < 995) return ACTIONTYPES[0]; // view
        if (i < 999) return ACTIONTYPES[1]; // buy
        return ACTIONTYPES[2]; // rate
    }

    /**
     * Items are distributed as expontial occurences (longtail)
     * Most of the items are not interesting to the users. Only
     * some of them are viewed a lot.
     *
     * @return
     */
    private static int getItemId() {
        int n = r.nextInt(itemIdDistribution[NUMBER_OF_ITEMS - 1]);
        for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
            if (itemIdDistribution[i] > n) return i + 1;
        }
        return 0;
    }

    /**
     * Users are distributed as expontial occurences (longtail)
     * Most of the Users show up 1 time only very view do more actions
     *
     * @return
     */
    private static int getUserId() {
        int n = r.nextInt(userIdDistribution[NUMBER_OF_USERS - 1]);
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            if (userIdDistribution[i] > n) return i + 1;
        }
        return 0;
    }

    private static void initItemAndUserIds() {

        //f(y) = 1/x mod n

        userIdDistribution[0] = (NUMBER_OF_USERS - 1) % MAX_ACTIONS_PER_USER;
        for (int i = 1; i < NUMBER_OF_USERS; i++) {
            userIdDistribution[i] = userIdDistribution[i - 1] +
                    (int) (Math.round((1. / (i + 1)) * NUMBER_OF_USERS) % MAX_ACTIONS_PER_USER);
        }
        itemIdDistribution[0] = (NUMBER_OF_ITEMS - 1) % MAX_ACTIONS_ON_ITEM;
        for (int i = 1; i < NUMBER_OF_ITEMS; i++) {
            itemIdDistribution[i] = itemIdDistribution[i - 1] +
                    (int) (Math.round((1. / (i + 1)) * NUMBER_OF_ITEMS) % MAX_ACTIONS_ON_ITEM);
        }
        System.out.println(NUMBER_OF_USERS + " userIds and " + NUMBER_OF_ITEMS + " itemIds created.");

    }

    /**
     * This function loads a Webpage into a string (like view source in a browser).
     *
     * @param url
     * @return
     */
    private static String loadWebsiteHtmlCode(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);
        String htmlCode = "";

        if (header != null) {
            getMethod.setHeader(header);
        }

        try {

            HttpResponse resp = httpClient.execute(getMethod);
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Method failed!" + statusCode);
            }

            // Read the response body.
            htmlCode = EntityUtils.toString(resp.getEntity());
            header = resp.getHeaders("Set-Cookie")[0];

            //TODO: read the JSESSIONID and use it in the next request.
            /*
            headers = resp.getAllHeaders();
            HeaderElement[] headers = resp.getHeaders("Set-Cookie")[0].getElements();
            System.out.println(resp.getHeaders("Set-Cookie")[0].getValue());

            for (int i = 0; i < headers.length; i++) {
                HeaderElement headerElement = headers[i];
                System.out.println(headerElement.getValue());
            }*/


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Release the connection.
            //method.releaseConnection();
        }
        return htmlCode;
    }

}
