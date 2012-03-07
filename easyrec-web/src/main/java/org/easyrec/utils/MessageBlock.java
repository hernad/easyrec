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
package org.easyrec.utils;

import org.easyrec.model.web.Message;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class adds a messages to the ModelAndView object. A message is an xml
 * format and gives information about the result of an ajax call. The xml is
 * parsed for a message code or description which is used for further
 * processing.
 * e.g. the ajax call
 * /easyrec-web/operator/signin?operatorId=notexistantoperator&password=wrong
 * <p/>
 * returns an xml message
 * <p/>
 * <easyrec>
 * <action>signin</action>
 * <error
 * code="106"
 * message="Login failed! Please check your login combination."/>
 * </easyrec>
 * <p/>
 * The code '106' is parsed by the client to display that the login was not
 * successfull.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-22 14:35:41 +0100 (Di, 22 Feb 2011) $<br/>
 * $Revision: 17734 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class MessageBlock {

    // TODO: move to vocabulary?
    public final static String DEFAULT_VIEW_NAME = "xml/messageblock";

    /**
     * This static functions creates a MVC Object with a messageblock.
     * A messageblock may be displayed to a user after filling in a form
     * and list the error he has made.
     *
     * @param mav
     * @param code
     * @param description
     * @return
     */
    public static ModelAndView create(ModelAndView mav, List<Message> messages, String action, String title) {

        mav.setViewName(DEFAULT_VIEW_NAME);
        mav.addObject("messages", messages);
        mav.addObject("action", action);
        mav.addObject("title", title);
        return mav;
    }

    /**
     * This static functions creates a MVC Object with a messageblock.
     * A messageblock may be displayed to a user after filling in a form
     * and list the error he has made.
     *
     * @param mav
     * @param code
     * @param description
     * @return
     */
    public static ModelAndView create(ModelAndView mav, String viewName, List<Message> messages, String action,
                                      String title) {

        mav.setViewName(viewName);
        mav.addObject("messages", messages);
        mav.addObject("action", action);
        mav.addObject("title", title);
        return mav;
    }

    /**
     * This static functions creates a MVC Object with a messageblock
     * that contains a single message.
     * A messageblock may be displayed to a user after filling in a form
     * and list the error he has made.
     *
     * @param mav
     * @param code
     * @param description
     * @return
     */
    public static ModelAndView createSingle(ModelAndView mav, Message message, String action, String title) {

        mav.setViewName(DEFAULT_VIEW_NAME);
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        mav.addObject("messages", messages);
        mav.addObject("action", action);
        mav.addObject("title", title);
        return mav;
    }

    public static ModelAndView createSingle(ModelAndView mav, Message message, String action, String title,
                                            String token) {

        mav.setViewName(DEFAULT_VIEW_NAME);
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        mav.addObject("messages", messages);
        mav.addObject("action", action);
        mav.addObject("title", title);
        mav.addObject("token", token);
        return mav;
    }


}
