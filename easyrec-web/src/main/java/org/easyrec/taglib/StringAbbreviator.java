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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * JSP tag that is used for shortening overly long texts.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-28 16:45:32 +0100 (Mo, 28 Feb 2011) $<br/>
 * $Revision: 17804 $</p>
 *
 * @author David Mann
 */
public class StringAbbreviator implements Tag {
    private PageContext pageContext;
    private Tag parent;
    private String myString = "";
    private int maxLength = 0;
    private boolean reversed = false;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public StringAbbreviator() {
        super();
    }

    public int doStartTag() throws javax.servlet.jsp.JspTagException {
        return SKIP_BODY;
    }

    public int doEndTag() throws javax.servlet.jsp.JspTagException {
        try {
            if (myString.length() > maxLength) {
                CharSequence substr;

                if (reversed)
                    substr = "&hellip;" + myString.subSequence(Math.max(myString.length() - maxLength, 0),
                            myString.length());
                else
                    substr = myString.subSequence(0, maxLength) + "&hellip;";

                pageContext.getOut().write("<span title='" + myString + "'>" + substr + "</span>");
            } else {
                pageContext.getOut().write(myString);
            }
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

