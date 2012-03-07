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
package org.easyrec.model.web;

import org.springframework.web.util.HtmlUtils;

import javax.xml.bind.annotation.*;


/**
 * This class is a basic Message
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-06-21 21:22:32 +0200 (Di, 21 Jun 2011) $<br/>
 * $Revision: 18451 $</p>
 *
 * @author phlavac
 * @author pmarschik
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
@XmlRootElement(name = "message")
@XmlSeeAlso({ErrorMessage.class, SuccessMessage.class})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Message {
    @XmlAttribute(required = true)
    private int code;
    @XmlAttribute(name = "message", required = true)
    private String description;
    @XmlElement(required = false)
    private Object content;

    protected Message() {}

    protected Message(final int code, final String description, final Object content) {
        this.code = code;
        this.description = HtmlUtils.htmlEscape(description);
        this.content = content;
    }

    public Message(final int code, final String description) {
        this(code, description, null);
    }

    protected abstract Message newInstance(final int code, final String description, final Object content);

    public int getCode() { return code; }

    public String getDescription() { return description; }

    public Object getContent() { return content; }

    public Message append(String toAppend) {
        return append(toAppend, null);
    }

    public Message append(String toAppend, Object content) {
        return newInstance(this.code, this.description + toAppend, content);
    }

    public Message replace(String description) {
        return replace(description, null);
    }

    public Message replace(String description, Object content) {
        return newInstance(this.code, description, content);
    }

    public Message content(Object content) {
        return newInstance(this.code, this.description, content);
    }
}