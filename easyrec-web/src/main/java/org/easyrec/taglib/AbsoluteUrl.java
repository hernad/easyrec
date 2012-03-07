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
package org.easyrec.taglib;

import org.easyrec.model.web.RemoteTenant;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.io.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import static org.owasp.esapi.tags.ELEncodeFunctions.encodeForHTMLAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: szavrel
 * Date: 29.07.11
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class AbsoluteUrl implements Tag {

    private PageContext pageContext;
    private Tag parent;
    private String tenantId;
    private String operatorId;
    private String itemUrl;
    private RemoteTenantDAO remoteTenantDAO;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public void setParent(Tag tag) {
        this.parent = parent;
    }

    public Tag getParent() {
        return parent;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {

        if (this.remoteTenantDAO == null) {
            ServletContext servletContext = this.pageContext.getServletContext();
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            this.remoteTenantDAO= (RemoteTenantDAO) context.getBean("remoteTenantDAO");
        }

        try {
            RemoteTenant rt = this.remoteTenantDAO.get(operatorId, tenantId);
            String stringRep = Text.matchMax(rt.getUrl(), itemUrl);
            String result = encodeForHTMLAttribute(stringRep);
            this.pageContext.getOut().write(result);
        } catch (java.io.IOException e) {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

    public void release() {

    }
}
