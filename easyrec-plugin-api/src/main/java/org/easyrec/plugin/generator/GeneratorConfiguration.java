package org.easyrec.plugin.generator;

import org.easyrec.plugin.configuration.Configuration;
import org.easyrec.plugin.configuration.PluginParameter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.StringWriter;

//@SuitableAssocTypes({AssocType.VIEWED_TOGETHER, AssocType.BOUGHT_TOGETHER})
public class GeneratorConfiguration extends Configuration {
    /**
     * The <code>Generator</code>'s tenant id. A generator run is always tenant
     * specific. It is, however, not a configuration parameter that should be
     * accessible through the UI, so we don't use the PluginParameter annotation
     * here.
     */
    private Integer tenantId;

    /**
     * The <code>Generator</code>'s association type. A generator run is always
     * association-type specific. It is, however, not a configuration parameter
     * that should be accessible through the UI, so we don't use the
     * PluginParameter annotation here.
     */
    @PluginParameter(displayName = "Assoc type",
            description = "The generator uses the associationType to describe the association of 2 items",
            shortDescription = "")
    private String associationType = "IS_RELATED";

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    /**
     * @see #marshal(boolean) 
     */
    public String marshal() {
        return marshal(this, true);
    }

    /**
     * Marshal this generator instance as XML.
     * If any JAXB tags in subclasses were added they will be used.
     *
     * @param formatted If {@code true} the marshaled XML is indented and line-breaks are added.
     * @return String containing an XML representation of this instance.
     */
    public String marshal(boolean formatted) {
        return marshal(this, formatted);
    }

    public String getXmlRepresentation() {
        return marshal();
    }

    @SuppressWarnings({"unchecked"})
    private static String marshal(GeneratorConfiguration configuration, boolean formatted) {
        StringWriter xmlRepresentation = new StringWriter();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(configuration.getClass(),
                    GeneratorConfigurationConstants.CONF_MARSHAL_FAILED.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();

            if(formatted)
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (!configuration.getClass().isAnnotationPresent(XmlRootElement.class)) {
                //actual class is not XMLRootAnnotated, use generic marshaling
                JAXBElement<GeneratorConfiguration> jaxbElement =
                        new JAXBElement<GeneratorConfiguration>(new QName(configuration.getClass().getCanonicalName()),
                                (Class<GeneratorConfiguration>) configuration.getClass(), configuration);
                marshaller.marshal(jaxbElement, xmlRepresentation);
            } else {
                // use the class defined xml marshaling
                marshaller.marshal(configuration, xmlRepresentation);
            }
        } catch (JAXBException e) {
            // TODO pass throwable?
            if (configuration != GeneratorConfigurationConstants.CONF_MARSHAL_FAILED)
                return marshal(GeneratorConfigurationConstants.CONF_MARSHAL_FAILED, formatted);

            xmlRepresentation.append("marshal exception :" + e.toString());
        }

        return xmlRepresentation.toString();
    }
}
