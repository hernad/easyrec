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
//
// This class contains wizard methods for downloading plugins for easyRec
//

function downloadWikiPlugin(operatorId)
{
    $('#fieldStatus-process').html('Processing request. This may takes a few seconds <img src="' + webappPath +
            'img/wait16.gif"/>');

    registerWikiTenant(operatorId);
}

function registerWikiTenant(operatorId)
{
    // Create a tenantId from Url
    // e.g.
    // http://satsrv01.researchstudio.at/wiki/index.php/Hauptseite
    // --> WIKI_SATSRV01_RESEARCHSTUDIO_AT_WIKI_INDEX
    re = /^https?:\/\/(www\.)?(([^/]+)(\/[^/?&.]+)*)/i;
    $('#url').val().match(re);
    tenantId = "WIKI_" + RegExp.$2.replace(/\./g, "_").toUpperCase().replace(/\//g, "_");

    if (tenantId.indexOf(':') > 0) {
        tenantId = tenantId.substring(0, tenantId.indexOf(':'));
    }

    // Cut off Parameter (if exist) from an url
    // e.g.
    // http://www.literature.at/elib/index.php5?title=Hauptseite
    // --> http://www.literature.at/elib/index.php5
    tenantUrl = $('#url').val();
    if (tenantUrl.indexOf('?') > 0) {
        tenantUrl = tenantUrl.substring(0, tenantUrl.indexOf('?'));
    }

    url_register = webappPath + "tenant/register?operatorId=" + operatorId + "&tenantId=" + tenantId +
            "&tenantType=wiki" + "&url=" + tenantUrl + "&description=this easyrec was created for a wiki";

    $.ajax({
        url:url_register,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '200' || // Tenant successfully registered!";
                    $(data).find('error').attr('code') == '204') { // Tenant exists but send plugin anyway
                //sucess
                $('#fieldStatus-process').html(''); // 'Wiki Tenant created successfully!'
                $('#code-block').html($('#code-block').html().replace(/REMOTE_TENANT/, tenantId));
                $('#field-code').show();
                $('#wiki_url').attr('href', $('#url').val());

                // send wiki plugin
                //window.open(webappPath + "download/easyRec_WikiPlugin_v1.0.zip");
                document.location.href = (webappPath + "download/easyrec_WikiPlugin_v1.0.zip");
            } else {
                //fail
                if ($(data).find('error').attr('code') == '203' || // invalid URL
                        $(data).find('error').attr('code') == '205') { // cant recreate tenant
                    $('#fieldStatus-process').html('<p class="red">' + $(data).find('error').attr('message') + '</p>');
                }
            }
        }
    });
}	 
