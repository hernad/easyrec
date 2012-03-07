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
package org.easyrec.service.web.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.web.DisplayService;
import org.springframework.core.io.Resource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author szavrel
 */
public class DisplayServiceImpl implements DisplayService {

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    private Transformer displayTransformer;

    public DisplayServiceImpl(Resource XSLTString) {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            displayTransformer = tFactory.newTransformer(new StreamSource(XSLTString.getInputStream()));
            //            cfg = (Configuration) tFactory.getAttribute(FeatureKeys.CONFIGURATION);
        } catch (Exception e) {
            logger.error("Could not start DisplayService! ", e);
        }
    }

    public String displayXml(String xml) {

        logger.debug(xml);
        String table = null;
        if (xml != null) {
            try {
                StringWriter sw = new StringWriter();
                StreamResult result = new StreamResult(sw);
                displayTransformer.transform(new StreamSource(new StringReader(xml.trim())), result);
                sw.close();
                table = sw.toString();
            } catch (Exception e) {
                logger.debug("Error transforming xml for display!" + e);
            }
        }
        return table;
    }


    public String display(GeneratorStatistics stats) {
        return "stats:" + stats;
    }


}
