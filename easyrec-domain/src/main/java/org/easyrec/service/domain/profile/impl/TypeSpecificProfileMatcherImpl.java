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
import org.easyrec.service.domain.profile.ProfileMatcherService;
import org.easyrec.service.domain.profile.ProfileService;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * @author szavrel
 */
public class TypeSpecificProfileMatcherImpl implements ProfileMatcherService {

    private Integer tenantId;
    private String itemType;
    private String XSLTString;
    private Transformer XSLTMatcher;
    private ProfileService profileService;
    private static final String PROFILES_START = "<profiles>";
    private static final String PROFILES_STOP = "</profiles>";

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public TypeSpecificProfileMatcherImpl(ProfileService profileService, Integer tenantId, String itemType,
                                          String XSLTString) {

        this.tenantId = tenantId;
        this.itemType = itemType;
        this.XSLTString = XSLTString;
        this.profileService = profileService;
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            XSLTMatcher = tFactory.newTransformer(new StreamSource(new StringReader(XSLTString)));
            //            cfg = (Configuration) tFactory.getAttribute(FeatureKeys.CONFIGURATION);

        } catch (Exception e) {

        }
    }

    public float match(ItemVO<Integer, String> item1, ItemVO<Integer, String> item2) {

        //DocumentInfo param=null;
        //XdmNode param=null;
        //TODO: lookup-code for profiles from Manager
        float ret = -1;
        StringBuffer sb = new StringBuffer(PROFILES_START);

        String profile1 = profileService.getProfile(item1.getTenant(), item1.getItem(), item1.getType());
        String profile2 = profileService.getProfile(item2.getTenant(), item2.getItem(), item2.getType());
        //        StreamSource refP = new StreamSource(new StringReader(profile1));
        //        try {
        //            DocumentBuilder db = proc.newDocumentBuilder();
        //            param = db.build(refP);
        //
        //            //param = cfg.buildDocument(refP);
        //        } catch (Exception e) {
        //            logger.debug("Error building refParameter");
        //        }
        //        logger.debug(param.getStringValue());
        sb.append(profile1).append(profile2).append(PROFILES_STOP);
        logger.debug(sb.toString());
        //XSLTMatcher.setParameter("refProfile", param.axisIterator(Axis.CHILD));
        try {
            DOMResult result = new DOMResult();
            //StreamResult result = new StreamResult(System.out);
            XSLTMatcher.transform(new StreamSource(new StringReader(sb.toString())), result);

            //            logger.debug(result.getNode().getFirstChild().getTextContent());
            //logger.debug(result.getNode().getFirstChild().getNodeType());
            ret = Float.valueOf(result.getNode().getFirstChild().getTextContent());
        } catch (Exception e) {
            logger.debug("Error matching profiles!" + e);
        }
        return ret;
    }


}
