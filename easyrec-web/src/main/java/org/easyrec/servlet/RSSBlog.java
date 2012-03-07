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
package org.easyrec.servlet;

import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.utils.MyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class for Servlet: RSSBlog
 */
public class RSSBlog extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    static final long serialVersionUID = 1L;

    private String blogPath = null;

    /* (non-Java-doc)
    * @see javax.servlet.http.HttpServlet#HttpServlet()
    */
    public RSSBlog() {
        super();
    }

    public void initBlog() {

        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        EasyRecSettings easyrecSettings = (EasyRecSettings) context.getBean("easyrecSettings");
        this.blogPath = easyrecSettings.getBlogURL();

    }

    /* (non-Java-doc)
      * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
      */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (blogPath == null) {
            initBlog();
        }
        response.setContentType("text/xml; charset=utf-8");
        response.getOutputStream().print(MyUtils.loadWebsiteHtmlCode(blogPath));
    }
}