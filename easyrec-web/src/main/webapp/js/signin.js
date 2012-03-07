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

/*
 * Init Keylistener on Login Box when Enter is pressed in Passwort Textbox
 * Init Lister on Popup Info Box and Center infobox if browser window is resized.
 */
$(function()
{
    $('#signInPassword').keypress(function(event)
    {
        if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {
            signinUser();
        }
    });

    $('a.btn-ok, #dialog-overlay, #dialog-box').click(function ()
    {
        $('#dialog-overlay, #dialog-box').hide();
        return false;
    });
    $(window).resize(function ()
    {
        if (!$('#dialog-box').is(':hidden')) popup();
    });
});

/**
 * Sign in User or Administrator
 */
function signinUser()
{

    $('#signInMessage').html('');
    $('#signinProgress').html(waitingImage);

    url = webappPath + "operator/signin?operatorId=" + encodeURIComponent($('#signInOperatorId').val()) + "&password=" +
            encodeURIComponent($('#signInPassword').val());

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('error').attr('code') == '106' || // wrong login combination
                    $(data).find('error').attr('code') == '112') { // Account is not activated
                $('#signInMessage').html($(data).find('error').attr('message'));
                $('#signinProgress').html('');
            }

            if ($(data).find('success').attr('code') == '105' || // User
                    $(data).find('success').attr('code') == '113') { // Admin
                $('#register').html('Signed in as ' + $('#signInOperatorId').val());
                $('#register').attr('href', webappPath + 'update');

                $('#signin').html('Signout');
                $('#signin').onclick = (signoutUser);

                $('#signInPassword').val('');
                $('#signInMessage').html('');

                window.location = webappPath + "easyrec/overview";
            }

        }
    });
}

/*
 * Sign out User
 */
function signoutUser()
{
    $.ajax({
        url:webappPath + 'operator/signout',
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '107') { // signed out
                window.location = webappPath + 'home';
            }
        }
    });
}

/*
 * Use this function for popup alerts
 */
function popup(message)
{
    var maskHeight = $(document).height();
    var maskWidth = $(window).width();
    var dialogTop = (maskHeight / 3) - ($('#dialog-box').height());
    var dialogLeft = (maskWidth / 2) - ($('#dialog-box').width() / 2);

    $('#dialog-overlay').css({height:maskHeight, width:maskWidth}).show();
    $('#dialog-box').css({top:dialogTop, left:dialogLeft}).show();
    $('#dialog-message').html(message);
}