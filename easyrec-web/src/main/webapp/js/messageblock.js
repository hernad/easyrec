// Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
//
// This file is part of easyrec.
//
// easyrec is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// easyrec is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
//
// The Class MessageBlock contains a set of Messages that are returned by a REST Service.

// example for a messageBlock for checking if an operator already exists.
// http://localhost:8080/operator/exists?operatorId=easyrec
// returns an XML DOM:
//
// <checkOperator>
//	 <success code="23" message="Operator already exists!"/>
// <checkOperator>
//

// This function returns true if the messageBlock
// contains a message with the given id
function containsMessage(xml, messageCode)
{
    if (isIE()) {
        return containsMessageIE(xml, messageCode);
    } else {
        return containsMessageNormal(xml, messageCode);
    }
}

function containsMessageNormal(xml, messageCode)
{
    for (i = 0; i < xml.firstChild.childNodes.length; i++) {
        try {
            if (xml.childNodes[0].childNodes[i].getAttribute('code') == messageCode) {
                return true;
            }
        } catch(err) {
            //alert(err);
        }
    }
    return false;
}

function containsMessageIE(xml, messageCode)
{
    try {
        for (i = 0; i <= xml.childNodes[1].childNodes.length; i++) {
            if (xml.childNodes[1].childNodes[i].getAttribute('code') == messageCode) {
                return true;
            }
        }
    } catch(err) {
    }

    return false;
}

// This function returns the message string
// for a given message id (if exists).
function getMessage(xml, messageCode)
{
    if (isIE()) {
        return getMessageIE(xml, messageCode);
    } else {
        return getMessageNormal(xml, messageCode);
    }
}

function getMessageNormal(xml, messageCode)
{
    for (i = 0; i < xml.firstChild.childNodes.length; i++) {
        try {
            if (xml.childNodes[0].childNodes[i].getAttribute('code') == messageCode) {
                return xml.childNodes[0].childNodes[i].getAttribute('message');
            }
        } catch(err) {
        }
    }

    return ''; // message not found;
}

function getMessageIE(xml, messageCode)
{
    for (i = 0; i <= xml.childNodes[1].childNodes.length; i++) {
        try {
            if (xml.childNodes[1].childNodes[i].getAttribute('code') == messageCode) {
                return xml.childNodes[1].childNodes[i].getAttribute('message');
            }
        } catch(err) {
        }
    }
    return ''; // message not found;
}

function isIE() {return (navigator.appName == "Microsoft Internet Explorer");}