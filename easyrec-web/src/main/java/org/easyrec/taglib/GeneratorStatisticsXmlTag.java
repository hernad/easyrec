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
package org.easyrec.taglib;

import org.easyrec.service.web.DisplayService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * JSP tag that is used for displaying a GeneratorStatistics object.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: szavrel $<br/>
 * $Date: 2010-02-17 16:27:25 +0100 (Mi, 17 Feb 2010) $<br/>
 * $Revision: 15605 $</p>
 *
 * @author Florian Kleedorfer
 */
public class GeneratorStatisticsXmlTag implements Tag {
    private PageContext pageContext;
    private Tag parent;
    private String xml;
    private DisplayService displayService;

    public void setXml(String xml) {
        this.xml = xml;
    }

    public GeneratorStatisticsXmlTag() {
        super();
    }

    public int doStartTag() throws javax.servlet.jsp.JspTagException {
        return SKIP_BODY;
    }

    public int doEndTag() throws javax.servlet.jsp.JspTagException {
        ServletContext servletContext = this.pageContext.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.displayService = (DisplayService) context.getBean("displayService");
        try {
            String stringRep = this.displayService.displayXml(this.xml);
            this.pageContext.getOut().write(stringRep);
        } catch (java.io.IOException e) {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }


    public void release() {}


    public void setPageContext(final javax.servlet.jsp.PageContext pageContext) {
        this.pageContext = pageContext;
    }


    public void setParent(final javax.servlet.jsp.tagext.Tag parent) {
        this.parent = parent;
    }

    public javax.servlet.jsp.tagext.Tag getParent() {
        return parent;
    }


}

