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
package org.easyrec.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * This class contains methods for String parsing, Date functions and a
 * website-->string procedure.
 *
 * @author phlavac
 */
public class MyUtils {
    public static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    public static final int CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;
    @SuppressWarnings({"UnusedDeclaration"})
    public static final int CURRENT_DAY_OF_MONTH = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    @SuppressWarnings({"UnusedDeclaration"})
    public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    @SuppressWarnings({"UnusedDeclaration"})
    public static final String CURRENT_MONTH_NAME = MONTH_NAMES[CURRENT_MONTH - 1];


    private final static Log logger = LogFactory.getLog(MyUtils.class);

    /**
     * This function always returns the size of a list; no
     * matter if it is null, 0 or >0.
     */
    public static Integer sizeOf(List<?> l) {
        return l != null ? l.size() : 0;
    }

    /**
     * This function always returns an int. In case of an
     * conversion error def is returned.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static Integer valueOf(String s, int def) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static String getCurrentMonthName() {
        return MONTH_NAMES[getCurrentMonth() - 1];
    }

    /**
     * @return the Current Month as Integer (January = 1; December = 12)
     */
    public static Integer getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * @return the Current Year
     */
    public static Integer getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    /**
     * This function sorts the Strings in the
     * given list by their first occurence in the given text.
     *
     * @param listToOrder e.g. FORMULA_1_DRIVERS = {"Lewis Hamilton","Heikki Kovalainen","Felipe Massa"}
     * @param textToParse e.g. "Felipe Masse is on fire. His is ahead of Lewis Hamilton"
     * @param fuzzy:      tokenize Strings and ordery by their occurence e.g. "Lewis Hamilton" --> {"Lewis","Hamilton"}
     * @return {"Felipe Massa","Lewis Hamilton"}
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static List<String> orderByFirstOccurenceInText(String listToOrder[], String textToParse, boolean fuzzy) {

        List<String> sortedList = new ArrayList<String>();
        HashMap<Integer, String> h = new HashMap<Integer, String>();


        if (listToOrder != null && !textToParse.equals("")) {
            for (String s : listToOrder) {
                if (textToParse.indexOf(s) > 0) {
                    h.put(textToParse.indexOf(s), s);
                }
            }

            List<Integer> keys = new ArrayList<Integer>(h.keySet());
            Collections.sort(keys);

            for (Integer k : keys) {
                sortedList.add(h.get(k));
            }
        }

        if (fuzzy) {
            sortedList.addAll(orderByFuzzyFirstOccurenceInText(listToOrder, textToParse));
        }
        return sortedList;
    }

    /**
     * This function sorts the Strings in the given list by their first
     * occurrence in the given text. The Strings in the list are tokenized
     * e.g. "red bull" is split in "red" and "bull" those tokens are
     * matched by their first occurrence in the text.
     */
    public static List<String> orderByFuzzyFirstOccurenceInText(String listToOrder[], String textToParse) {
        List<String> sortedList = new ArrayList<String>();
        HashMap<Integer, String> h = new HashMap<Integer, String>();

        if (listToOrder != null && !textToParse.equals("")) {
            for (String stringTokens : listToOrder) {

                String[] tokens = stringTokens.split(" ");
                for (String token : tokens) {

                    if (textToParse.indexOf(token) > 0 && token.length() > 3) {

                        h.put(textToParse.indexOf(token), token);
                    }
                }
            }

            List<Integer> keys = new ArrayList<Integer>(h.keySet());
            Collections.sort(keys);

            for (Integer k : keys) {
                sortedList.add(h.get(k));
            }
        }
        return sortedList;
    }

    public static String loadWebsiteHtmlCode(String url) {
        return loadWebsiteHtmlCode(url, null);
    }

    /**
     * This function loads a Webpage into a string (like view source in a browser).
     */
    public static String loadWebsiteHtmlCode(String url, String useragent) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);
        String htmlCode = "";

        if (useragent != null) {
            getMethod.setHeader("user-agent", useragent);
        }

        try {

            HttpResponse resp = httpClient.execute(getMethod);
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.debug("Method failed!" + statusCode);
            }
            // Read the response body.
            htmlCode = EntityUtils.toString(resp.getEntity());

        } catch (Exception e) {
            logger.debug("Fatal protocol violation: " + e.getMessage());
            logger.trace(e);
        }
        return htmlCode;
    }


    /**
     * This function trie to parse a date given as string and returns a date if
     * successfull.
     */
    public static Date dateFormatCheck(String dateString, SimpleDateFormat dateFormatter) {
        try {
            Date date = dateFormatter.parse(dateString, new ParsePosition(0));
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            // mysql specific: cannot handle Dates with Years > 9999
            if (c.get(Calendar.YEAR) < 10000) {
                return date;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
