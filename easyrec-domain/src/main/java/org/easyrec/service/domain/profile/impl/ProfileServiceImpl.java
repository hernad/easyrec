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
package org.easyrec.service.domain.profile.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.service.domain.profile.ProfileService;
import org.easyrec.store.dao.core.types.ProfiledItemTypeDAO;
import org.easyrec.store.dao.domain.TypedProfileDAO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;

/**
 * @author szavrel
 */
public class ProfileServiceImpl implements ProfileService {

    private TypedProfileDAO typedProfileDAO;
    private ProfiledItemTypeDAO profiledItemTypeDAO;
    //private Map<Integer, Map<String,SAXParser>> validationParsers;
    private Map<Integer, Map<String, DocumentBuilder>> validationParsers;
    private SchemaFactory sf;
    //        private SAXParserFactory spf;
    private DocumentBuilderFactory dbf;
    //private DocumentBuilder db;
    private Transformer trans;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public ProfileServiceImpl(TypedProfileDAO profileDAO, ProfiledItemTypeDAO profiledItemTypeDAO) {
        this(profileDAO, profiledItemTypeDAO, null);
    }


    public ProfileServiceImpl(TypedProfileDAO profileDAO, ProfiledItemTypeDAO profiledItemTypeDAO,
                              String docBuilderFactory) {

        this.typedProfileDAO = profileDAO;
        this.profiledItemTypeDAO = profiledItemTypeDAO;
        if (docBuilderFactory != null)
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", docBuilderFactory);
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if (logger.isDebugEnabled()) {
            logger.debug("DocumentBuilderFactory: " + dbf.getClass().getName());
            ClassLoader cl = Thread.currentThread().getContextClassLoader().getSystemClassLoader();
            URL url = cl.getResource("org/apache/xerces/jaxp/DocumentBuilderFactoryImpl.class");
            logger.debug("Parser loaded from: " + url);
        }
        validationParsers = new HashMap<Integer, Map<String, DocumentBuilder>>();

        loadSchemas();
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (Exception e) {

        }
    }

    public int storeProfile(Integer tenantId, Integer itemId, String itemTypeId, String profileXML, boolean validate) {

        // Hint: setValidating(true) only refers to DTD!!! XMLSchema validation is switched on automatically
        // when calling the setSchema() method. If you only want XMLSchema validation, don't use setValidating(true)
        // because otherwise you will get DTD validation errors that you don't want!
        //spf.setValidating(true);
        if (validate == true) {
            try {
                DocumentBuilder db = validationParsers.get(tenantId).get(itemTypeId);
                //TODO: Implement proper DefaultErrorHandler!!
                db.parse(new InputSource(new StringReader(profileXML)));
            } catch (Exception e) {
                logger.debug("Error validating Profile: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return typedProfileDAO.storeProfile(tenantId, itemId, itemTypeId, profileXML);
    }

    public String getProfile(Integer tenantId, Integer itemId, String itemTypeId) {
        return typedProfileDAO.getProfile(tenantId, itemId, itemTypeId);
    }

    public void activateProfile(Integer tenantId, Integer itemId, String itemTypeId) {
        typedProfileDAO.activateProfile(tenantId, itemId, itemTypeId);
    }

    public void deactivateProfile(Integer tenantId, Integer itemId, String itemTypeId) {
        typedProfileDAO.deactivateProfile(tenantId, itemId, itemTypeId);
    }

    public String getProfileSchema(Integer tenantId, Integer id) {
        return profiledItemTypeDAO.getProfileSchema(tenantId, id);
    }

    public String getProfileSchema(Integer tenantId, String itemType) {
        return profiledItemTypeDAO.getProfileSchema(tenantId, itemType);
    }

    public Set<String> getMultiDimensionValue(Integer tenantId, Integer itemId, String itemTypeId,
                                              String dimensionXPath) {
        return typedProfileDAO.getMultiDimensionValue(tenantId, itemId, itemTypeId, dimensionXPath);
    }

    public String getSimpleDimensionValue(Integer tenantId, Integer itemId, String itemTypeId, String dimensionXPath) {
        return typedProfileDAO.getSimpleDimensionValue(tenantId, itemId, itemTypeId, dimensionXPath);
    }

    public void insertOrUpdateMultiDimension(Integer tenantId, Integer itemId, String itemTypeId, String dimensionXPath,
                                             List<String> values) {

        XPathFactory xpf = XPathFactory.newInstance();

        try {
            // load and parse the profile
            DocumentBuilder db = validationParsers.get(tenantId).get(itemTypeId);
            Document doc = db.parse(new InputSource(new StringReader(getProfile(tenantId, itemId, itemTypeId))));
            // check if the element exists
            Node node = null;
            Node parent = null;
            XPath xp = xpf.newXPath();
            for (Iterator<String> it = values.iterator(); it.hasNext();) {
                String value = it.next();
                // look if value already exists
                node = (Node) xp.evaluate(dimensionXPath + "[text()='" + value + "']", doc, XPathConstants.NODE);
                // if value exists, value can be discarded
                if (node != null) {
                    // optimization: if a node was found, store the parent; later no new XPath evaluation is necessary
                    parent = node.getParentNode();
                    it.remove();
                }
            }
            if (values.isEmpty()) return; // nothing left to do
            String parentPath = dimensionXPath.substring(0, dimensionXPath.lastIndexOf("/"));
            // find path to parent
            if (parent == null) {
                String tmpPath = parentPath;
                while (parent == null) {
                    tmpPath = parentPath.substring(0, tmpPath.lastIndexOf("/"));
                    parent = (Node) xp.evaluate(tmpPath, doc, XPathConstants.NODE);
                }
                parent = insertElement(doc, parent, parentPath.substring(tmpPath.length()), null);
            }
            String tag = dimensionXPath.substring(parentPath.length() + 1);
            for (String value : values) {
                Element el = doc.createElement(tag);
                el.setTextContent(value);
                parent.appendChild(el);
            }

            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            trans.transform(new DOMSource(doc), result);
            writer.close();
            String xml = writer.toString();
            logger.debug(xml);
            storeProfile(tenantId, itemId, itemTypeId, xml, true);

        } catch (Exception e) {
            logger.error("Error inserting Multi Dimension: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertOrUpdateSimpleDimension(Integer tenantId, Integer itemId, String itemTypeId,
                                              String dimensionXPath, String value) {

        XPathFactory xpf = XPathFactory.newInstance();
        try {
            // load and parse the profile
            DocumentBuilder db = validationParsers.get(tenantId).get(itemTypeId);
            Document doc = db.parse(new InputSource(new StringReader(getProfile(tenantId, itemId, itemTypeId))));
            // check if the element exists
            XPath xp = xpf.newXPath();
            Node node = (Node) xp.evaluate(dimensionXPath, doc, XPathConstants.NODE);
            // if the element exists, just update the value
            if (node != null) {
                // if value doesn't change, there is no need to alter the profile and write it to database
                if (value.equals(node.getTextContent())) return;
                node.setTextContent(value);
            } else { // if the element cannot be found, insert it at the position given in the dimensionXPath
                // follow the XPath from bottom to top until you find the first existing path element
                String tmpPath = dimensionXPath;
                while (node == null) {
                    tmpPath = dimensionXPath.substring(0, tmpPath.lastIndexOf("/"));
                    node = (Node) xp.evaluate(tmpPath, doc, XPathConstants.NODE);
                }
                // found the correct node to insert or ended at Document root, hence insert
                insertElement(doc, node, dimensionXPath.substring(tmpPath.length()/*, dimensionXPath.length()*/),
                        value);
            }

            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            trans.transform(new DOMSource(doc), result);
            writer.close();
            String xml = writer.toString();
            logger.debug(xml);
            storeProfile(tenantId, itemId, itemTypeId, xml, true);

        } catch (Exception e) {
            logger.error("Error inserting Simple Dimension: " + e.getMessage());
            e.printStackTrace();
        }

    }


    /**
     * Loads the XMLSchema definitions for all itemTypes from the database and initializes a DOM Parser
     * (DocumentBuilder) for each schema found. The parsers are then stored in a HashMap using tenantId
     * as first and itemType as second key and the parser as value.
     */
    public void loadSchemas() {

        List<Integer> tenantIds = profiledItemTypeDAO.getTenantIds();
        for (Integer tenantId : tenantIds) {
            Set<String> typesForTenant = profiledItemTypeDAO.getTypes(tenantId);
            Map<String, DocumentBuilder> tenantParsers = new HashMap<String, DocumentBuilder>();
            for (String itemType : typesForTenant) {
                try {
                    String schemaString = profiledItemTypeDAO.getProfileSchema(tenantId, itemType);
                    if (schemaString != null) {
                        Schema schema = sf.newSchema(new StreamSource(new StringReader(schemaString)));
                        dbf.setSchema(schema);
                    } else {
                        dbf.setSchema(null);
                    }
                    tenantParsers.put(itemType, dbf.newDocumentBuilder());
                } catch (Exception e) {
                    logger.error("Error loading Schemas: " + e.getMessage());
                }
            }
            validationParsers.put(tenantId, tenantParsers);
        }
    }

    public List<ItemVO<Integer, Integer>> getItemsByDimensionValue(Integer tenantId, String itemType,
                                                                            String dimensionXPath, String value) {
        return typedProfileDAO.getItemsByDimensionValue(tenantId, itemType, dimensionXPath, value);
    }

    public List<ItemVO<Integer, Integer>> getItemsByItemType(Integer tenantId, String itemType, int count) {
        return typedProfileDAO.getItemsByItemType(tenantId, itemType, count);
    }

    /**
     * Inserts a new element and value into an XML Document at the position given in xPathExpression
     * relativ to the Node given in startNode.
     *
     * @param doc             the Document in which the Element is inserted
     * @param startNode       the Node in the Document used as start point for the XPath Expression
     * @param xPathExpression the XPath from the startNode to the new Element
     * @param value           the value of the new Element
     */
    private Node insertElement(Document doc, Node startNode, String xPathExpression, String value) {

        if (!"".equals(xPathExpression)) {
            String[] xPathTokens = xPathExpression.split("/");
            for (String tag : xPathTokens) {
                if (!"".equals(tag)) {
                    Element el = doc.createElement(tag);
                    startNode.appendChild(el);
                    startNode = startNode.getLastChild();
                }
            }
            if (value != null) startNode.setTextContent(value);
        }
        return startNode;
    }

}
