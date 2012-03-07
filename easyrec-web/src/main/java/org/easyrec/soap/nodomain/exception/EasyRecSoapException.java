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
package org.easyrec.soap.nodomain.exception;

import org.easyrec.model.web.Message;

import java.util.List;

/**
 * Exception thrown at the ShopRecommender WebService with no specific domain.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-06-28 18:32:35 +0200 (Di, 28 Jun 2011) $<br/>
 * $Revision: 18473 $</p>
 *
 * @author Stephan Zavrel
 */
public class EasyRecSoapException extends Exception {

    private static final long serialVersionUID = 8167767882920920061L;

    private static final String EASYREC_EXCEPTION_ACTION_BEGIN = "<easyrec><action>";
    private static final String EASYREC_EXCEPTION_ACTION_END = "</action>";
    private static final String EASYREC_EXCEPTION_ERROR_BEGIN = "  <error code=\"";
    private static final String EASYREC_EXCEPTION_ERROR_MIDDLE = "\" message=\"";
    private static final String EASYREC_EXCEPTION_ERROR_END = "\"/>";
    private static final String EASYREC_EXCEPTION_END = "</easyrec>";

    private Message messageObject;

    public EasyRecSoapException(Message messageObject) {
        super(messageObject.getDescription());
        this.messageObject = messageObject;
    }

    public EasyRecSoapException(String message) {
        super(message);
    }

    public EasyRecSoapException(List<Message> messages, String action) {

        super(toXMLString(messages, action));
    }

    private static String toXMLString(List<Message> messages, String action) {

        StringBuilder mb = new StringBuilder().append(EASYREC_EXCEPTION_ACTION_BEGIN).append(action)
                .append(EASYREC_EXCEPTION_ACTION_END);

        for (Message m : messages) {
            mb.append(EASYREC_EXCEPTION_ERROR_BEGIN).append(m.getCode()).append(EASYREC_EXCEPTION_ERROR_MIDDLE)
                    .append(m.getDescription()).append(EASYREC_EXCEPTION_ERROR_END);
        }
        mb.append(EASYREC_EXCEPTION_END);

        return mb.toString();
    }

    public Message getMessageObject() {
        return messageObject;
    }
}
