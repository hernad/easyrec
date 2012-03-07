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

import com.google.common.base.Strings;
import org.apache.commons.validator.UrlValidator;
import org.easyrec.utils.io.Text;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class offers for validating url, email and formatting urls.
 *
 * @author phlavac
 */
public class Web {

    @SuppressWarnings({"UnusedDeclaration"})
    public static final String XML = "XML";
    public static final String HTML = "HTML";

    private static String path;


    /**
     * This method trims a http-request with the given parameter. e.g. request:
     * /myServlet?id=1&desc=hallo --> trimRequest(request, "id") -->
     * /myServlet?desc=hallo
     *
     * @param request   HttpServletRequest
     * @param parameter String
     * @return String
     */
    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static String trimRequest(HttpServletRequest request, String parameter) {

        String query = "";
        if (!Strings.isNullOrEmpty(parameter) && request.getParameterMap() != null) {
            for (final Object o : request.getParameterMap().entrySet()) {
                Entry<String, String[]> m = (Entry<String, String[]>) o;
                if (!parameter.equals(m.getKey())) {
                    query += m.getKey() + "=" + m.getValue()[0] + "&";
                }
            }

            return request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() +
                    request.getContextPath() + request.getServletPath() + "?" + Text.removeLast(query);
        } else return null;
    }

    /**
     * This function returns a relative servlet url into a complete one. e.g.
     * /peppi?id=43 --> http://localhost:8080/sat-xxx/peppi?id=43
     *
     * @param request HttpServletRequest
     * @param servlet String
     * @return String
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static String createRequestFromServlet(HttpServletRequest request, String servlet) {
        return request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() +
                request.getContextPath() + servlet;
    }

    /**
     * This function adds a hyperlink to a given Text.
     *
     * @param text   String
     * @param target String
     * @return String
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static String createLink(String text, String target) {
        return "'<a target='_blank' href='" + target + "'>" + text + "</a>'";
    }

    /**
     * Validate the form of an email address.
     * <p/>
     * <p/>
     * Return <tt>true</tt> only if
     * <ul>
     * <li> <tt>aEmailAddress</tt> can successfully construct an
     * {@link javax.mail.internet.InternetAddress}
     * <li> when parsed with "@" as delimiter, <tt>aEmailAddress</tt> contains
     * two tokens which satisfy
     * {@code hirondelle.web4j.util.Util#textHasContent}.
     * </ul>
     * <p/>
     * <p/>
     * The second condition arises since local email addresses, simply of the
     * form "<tt>albert</tt>", for example, are valid for
     * {@link javax.mail.internet.InternetAddress}, but almost always
     * undesired.
     *
     * @param aEmailAddress String
     * @return boolean
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean isValidEmailAddress(String aEmailAddress) {
        if (aEmailAddress == null) return false;
        boolean result = true;
        try {
            new InternetAddress(aEmailAddress);

            if (!hasNameAndDomain(aEmailAddress)) {
                result = false;
            }
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private static boolean hasNameAndDomain(String aEmailAddress) {
        // Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        // Match the given string with the pattern
        Matcher m = p.matcher(aEmailAddress);

        // check whether match is found
        boolean matchFound = m.matches();

        return matchFound;
    }

    /**
     * This function tries to download the content from the
     * given Url and returns true in case of success.
     *
     * @param sUrl String
     * @return boolean
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean isDownloadAbleUrl(String sUrl) {
        if (Security.inWhiteListDomain(sUrl)) {
            return true;
        }
        if (Strings.isNullOrEmpty(sUrl)) {
            return false;
        } else {
            if (sUrl.length() < 4) return false;
            try {
                URL url = new URL(sUrl);
                url.getContent();
                return true;
            } catch (UnknownHostException e) {
                return false; // ("Unknown Host");
            } catch (MalformedURLException e) {
                return false; // ("Bad URL
            } catch (FileNotFoundException e) {
                return false; // ("404 error returned");
            } catch (IOException e) {
                return false; // ("Communication failure");
            } catch (Exception e) {
                return false; // ("Another Shit happend");
            }
        }
    }


    /**
     * This procedure extracts the values
     * of the <name>-Tags of a given Xml file into a list of Strings.
     * e.g.
     * <name>hanso</name>
     * <name>stritzi</name>
     * <p/>
     * --> {"hansi","stritzi"}
     *
     * @param apiURL  String
     * @param tagName String
     * @return a list of strings
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static List<String> getInnerHTMLfromTags(String apiURL, String tagName) {

        List<String> innerHTMLList = new ArrayList<String>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException e) throws SAXException {}

                public void error(SAXParseException e) throws SAXException {}

                public void fatalError(SAXParseException e) throws SAXException {}
            });
            Document doc = db.parse(apiURL.replaceAll(" ", "%20"));

            NodeList tagNodes = doc.getElementsByTagName(tagName);

            for (int i = 0; i < tagNodes.getLength(); i++) {
                innerHTMLList.add(tagNodes.item(i).getTextContent());
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return innerHTMLList;
    }


    /**
     * This function checks the syntax of a given url
     * and returns true in case of the right syntax.
     *
     * @param sUrl String
     * @return boolean
     */
    public static boolean isValidUrl(String sUrl) {
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(sUrl)) {
            return true;
        } else {
            return urlValidator.isValid(sUrl.replaceFirst("^https?://localhost", "http://www.example.com"));
        }
    }

     /**
     *
     * This functions help to avoid XSS
     *
     * @param sUrl String
     * @return boolean
     */
    public static String makeUrlSecure(String sUrl) {
        if (sUrl != null) {
            return sUrl.replaceAll("'","%27").replaceAll("\"","%22");
        } else
            return null;
    }

    /**
     * This function returns a processed HTML Page from a given XML
     * transformed with an XSL.
     *
     * @param xmlUrl String
     * @param xslUrl String
     * @return String
     * @throws Exception Exception
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static String transformXML(String xmlUrl, String xslUrl) throws Exception {

        String sHTML;

        try {

            TransformerFactory factory = TransformerFactory.newInstance();

            Templates sourceTemplate = factory.newTemplates(new StreamSource(xslUrl));

            Transformer sourceTransformer = sourceTemplate.newTransformer();

            URI uri = new URI("http", xmlUrl.replace("http:", ""), null);

            Source source = new StreamSource(uri.toURL().toString());

            StringWriter writer = new StringWriter();
            Result localResult = new StreamResult(writer);

            sourceTransformer.transform(source, localResult);
            sHTML = writer.toString();

        } catch (Exception e) {
            throw new Exception();
        }
        return sHTML;
    }

    /**
     * returns the complete path of the webapp e.g.
     * http://my.server.com/easyrec-web/
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static String getExtendedWebappPath(HttpServletRequest request) {

        if (path == null) {
            String localName = request.getLocalName();
            localName = localName.equals("0.0.0.0") ? "localhost" : localName;
            path = request.getScheme() + "://" +
                    //request.getLocalAddr()+ ":" +
                    localName + ":" + request.getLocalPort() + request.getContextPath();
        }
        return path;
    }

    /**
     * If mav contain already error messages return false
     *
     * @param mav ModelAndView
     * @return boolean
     */
    public static boolean validated(ModelAndView mav) {
        return !MessageBlock.DEFAULT_VIEW_NAME.equals(mav.getViewName());
    }
}
