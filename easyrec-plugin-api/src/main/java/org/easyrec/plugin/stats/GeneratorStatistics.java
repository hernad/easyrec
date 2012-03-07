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

package org.easyrec.plugin.stats;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.StringWriter;

/**
 * @author fkleedorfer
 */
public class GeneratorStatistics extends ExecutableStatistics {
    protected int numberOfRulesCreated;
    protected int numberOfActionsConsidered;

    public GeneratorStatistics() {
    }

    public int getNumberOfActionsConsidered() {
        return numberOfActionsConsidered;
    }

    public void setNumberOfActionsConsidered(int numberOfActionsConsidered) {
        this.numberOfActionsConsidered = numberOfActionsConsidered;
    }

    public void incNumberOfActionsConsidered() {
        this.numberOfActionsConsidered++;
    }

    public void incNumberOfActionsConsidered(int increment) {
        this.numberOfActionsConsidered += increment;
    }

    public int getNumberOfRulesCreated() {
        return numberOfRulesCreated;
    }

    public void setNumberOfRulesCreated(int numberOfRulesCreated) {
        this.numberOfRulesCreated = numberOfRulesCreated;
    }

    public void incNumberOfRulesCreated() {
        this.numberOfRulesCreated++;
    }

    public void incNumberOfRulesCreated(int increment) {
        this.numberOfRulesCreated += increment;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final GeneratorStatistics other = (GeneratorStatistics) obj;
        if (this.numberOfRulesCreated != other.numberOfRulesCreated) {
            return false;
        }
        if (this.numberOfActionsConsidered != other.numberOfActionsConsidered) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + this.numberOfRulesCreated;
        hash = 31 * hash + this.numberOfActionsConsidered;
        return hash;
    }

    public String marshal() {
        return marshal(this);
    }

    public String getXmlRepresentation() {
        return marshal();
    }

    @SuppressWarnings({"unchecked"})
    public static String marshal(GeneratorStatistics statistics) {
        StringWriter xmlRepresentation = new StringWriter();

        try {
            JAXBContext jaxbContext =
                    JAXBContext.newInstance(statistics.getClass(), StatisticsConstants.STATS_MARSHAL_FAILED.getClass(),
                            StatisticsConstants.STATS_FORCED_END.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (!statistics.getClass().isAnnotationPresent(XmlRootElement.class)) {
                //actual class is not XMLRootAnnotated, use generic marshaling
                JAXBElement<GeneratorStatistics> jaxbElement =
                        new JAXBElement<GeneratorStatistics>(new QName(statistics.getClass().getCanonicalName()),
                                (Class<GeneratorStatistics>) statistics.getClass(), statistics);
                marshaller.marshal(jaxbElement, xmlRepresentation);
            } else {
                // use the class defined xml marshaling
                marshaller.marshal(statistics, xmlRepresentation);
            }
        } catch (JAXBException e) {
            // TODO pass throwable?
            if (statistics != StatisticsConstants.STATS_MARSHAL_FAILED)
                return marshal(StatisticsConstants.STATS_MARSHAL_FAILED);

            xmlRepresentation.append("marshal exception :" + e.toString());
        }

        return xmlRepresentation.toString();
    }
}
