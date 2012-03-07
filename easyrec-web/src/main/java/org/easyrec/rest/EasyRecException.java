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
package org.easyrec.rest;

import com.sun.jersey.api.json.JSONWithPadding;
import org.easyrec.model.web.Message;
import org.easyrec.vocabulary.WS;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author szavrel
 */
public class EasyRecException extends WebApplicationException {

    private static final long serialVersionUID = 7308072153620426466L;

    private static final String EASYREC_EXCEPTION_ACTION_BEGIN = "<easyrec><action>";
    private static final String EASYREC_EXCEPTION_ACTION_END = "</action>";
    private static final String EASYREC_EXCEPTION_ERROR_BEGIN = "  <error code=\"";
    private static final String EASYREC_EXCEPTION_ERROR_MIDDLE = "\" message=\"";
    private static final String EASYREC_EXCEPTION_ERROR_END = "\"/>";
    private static final String EASYREC_EXCEPTION_END = "</easyrec>";

    public EasyRecException() {}

    public EasyRecException(String message, String type) {
        super(Response.ok(message, WS.RESPONSE_TYPE_XML).build());
    }

    public EasyRecException(List<Message> messages, String action) {
        super(Response.ok(toXMLString(messages, action), WS.RESPONSE_TYPE_XML).build());
    }

    public EasyRecException(List<Message> messages, String action, String type, String callback) {
        //GenericEntity<List<Message>> entity = new GenericEntity<List<Message>>(messages) {};
        super(callback != null ?
              Response.ok(new JSONWithPadding(messagesToArray(messages), callback),
                      WS.RESPONSE_TYPE_JSCRIPT).build() :
              Response.ok(messagesToArray(messages), type).build());
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

    private static Message[] messagesToArray(List<Message> messages) {
        return messages.toArray(new Message[messages.size()]);
    }

    private static class GenericEntityMessageList extends GenericEntity<List<Message>> {
        public GenericEntityMessageList(List<Message> messages) {
            super(messages);
        }
    }
}
