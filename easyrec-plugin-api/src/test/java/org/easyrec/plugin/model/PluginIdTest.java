/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.model;

import org.junit.Test;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author pmarschik
 */
public class PluginIdTest {

    @Test
    public void shouldBeXmlSerializable() throws JAXBException {
        URI id = URI.create("http://easyrec.org/plugin/arm");
        Version version = new Version(1, 0, 1);
        PluginId pluginId = new PluginId(id, version);

        JAXBContext jaxbContext = JAXBContext.newInstance(PluginId.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(pluginId, stringWriter);

        assertThat(stringWriter.toString(),
                is("<pluginId uri=\"http://easyrec.org/plugin/arm\" version=\"1.0.1\"/>"));

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader stringReader = new StringReader(stringWriter.toString());
        StreamSource source = new StreamSource(stringReader);
        JAXBElement<PluginId> jaxbElement = unmarshaller.unmarshal(source, PluginId.class);

        assertThat(jaxbElement.getValue(), equalTo(pluginId));
    }

    @Test
    public void parsePluginId_shouldBeIdenticalToToString() {
        assertThat(PluginId.parsePluginId("http://easyrec.org/plugin/arm/1.0.1").toString(),
                equalTo("http://easyrec.org/plugin/arm/1.0.1"));
    }

}
