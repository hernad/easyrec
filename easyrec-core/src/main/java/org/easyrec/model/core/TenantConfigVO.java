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
package org.easyrec.model.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Stephan Zavrel
 */
public class TenantConfigVO {

    private List<String> itemTypes;
    private List<String> assocTypes;
    private List<String> aggregateTypes;
    private List<String> actionTypes;
    private List<String> sourceTypes;
    private List<String> viewTypes;
    private List<String> authenticationDomains;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public TenantConfigVO(List<String> itemTypes, List<String> assocTypes, List<String> aggregateTypes,
                          List<String> actionTypes, List<String> sourceTypes, List<String> viewTypes,
                          List<String> authenticationDomains) {
        this.itemTypes = itemTypes;
        this.assocTypes = assocTypes;
        this.aggregateTypes = aggregateTypes;
        this.actionTypes = actionTypes;
        this.sourceTypes = sourceTypes;
        this.viewTypes = viewTypes;
        this.authenticationDomains = authenticationDomains;
    }

    public List<String> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<String> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public List<String> getAssocTypes() {
        return assocTypes;
    }

    public void setAssocTypes(List<String> assocTypes) {
        this.assocTypes = assocTypes;
    }

    public List<String> getActionTypes() {
        return actionTypes;
    }

    public void setActionTypes(List<String> actionTypes) {
        this.actionTypes = actionTypes;
    }

    public List<String> getSourceTypes() {
        return sourceTypes;
    }

    public void setSourceTypes(List<String> sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

    public List<String> getViewTypes() {
        return viewTypes;
    }

    public void setViewTypes(List<String> viewTypes) {
        this.viewTypes = viewTypes;
    }

    public List<String> getAggregateTypes() {
        return aggregateTypes;
    }

    public void setAggregateTypes(List<String> aggregateTypes) {
        this.aggregateTypes = aggregateTypes;
    }

    public List<String> getAuthenticationDomains() {
        return authenticationDomains;
    }

    public void setAuthenticationDomains(List<String> authenticationDomains) {
        this.authenticationDomains = authenticationDomains;
    }


}
