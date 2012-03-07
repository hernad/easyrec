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
// This class checks the input form


/*
 * This functions tries to update an operator.
 * In case of failure all errors are shown in form
 */
function updateUser(operatorId)
{
    url = webappPath + "operator/update?operatorId=" + operatorId + "&firstName=" +
            encodeURIComponent($('#firstName').val()) + "&lastName=" + encodeURIComponent($('#lastName').val()) +
            "&email=" + encodeURIComponent($('#email').val()) + "&phone=" + encodeURIComponent($('#phone').val()) +
            "&company=" + encodeURIComponent($('#company').val()) + "&address=" +
            encodeURIComponent($('#address').val());

    $.ajax({
        url:url,
        cache: false,
        dataTyoe: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '111') {   // Account Updated!
                //sucess
                $('#updateOperator').hide();
                $('#updateSuccess').show();
            } else {
                //fail
                if ($(data).find('error').attr('code') ==
                        '905') { // Special Characters like: &gt; , &lt; , &#34;,' or %" not allowed
                    $('#register-error').html($(data).find('error').attr('message'));
                }
            }
        }
    });
}

/*
 * This function registers an operator .
 * Note: works only during the installer process.
 */
function registerOperator()
{

    url = webappPath + "load?operatorId=" + encodeURIComponent($('#operatorId').val()) + "&password=" +
            encodeURIComponent($('#password').val()) + "&passwordConfirm=" +
            encodeURIComponent($('#passwordConfirm').val()) + "&firstName=" +
            encodeURIComponent($('#firstName').val()) + "&lastName=" + encodeURIComponent($('#lastName').val());

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '110') {   // Account Registered!
                //sucess
                $('#registerOperator').hide();
                $('#registerSuccess').show();
            } else {
                //fail

                // reset error messages
                $('#fieldStatus-operatorId').html('');
                $('#fieldStatus-password').html('');
                $('#register-error').html('');
                $('#fieldStatus-passwordConfirm').html('');

                if ($(data).find('error').attr('code') == '120') {
                    // A User Id is required.
                    $('#fieldStatus-operatorId').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '121') {
                    // Minimum Password length failed.
                    $('#fieldStatus-password').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '122') {
                    // User Id contains space character.
                    $('#fieldStatus-operatorId').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '905') {
                    // Special Characters like: &gt; , &lt; , &#34;, ' or %" not allowed.
                    $('#register-error').html($(data).find('error').attr('message'));
                }

                if ($('#operatorId').val().length > 0) {
                    if ($(data).find('error').attr('code') == '102') {
                        // User Name already exists.
                        $('#fieldStatus-operatorId').html($(data).find('error').attr('message'));
                    }
                }
                if ($('#password').val().length > 0) {
                    if ($(data).find('error').attr('code') == '130') {
                        // The Password must match with the confirmed Password.
                        $('#fieldStatus-passwordConfirm').html($(data).find('error').attr('message'));
                    }
                }
            }
        }
    });
}

/*
 * Registers tenant and redirects to the easyrec management section
 */
function redirect()
{
    document.getElementById('redirect').outerHTML = '<div style="background-color: #ffffff;margin-top: 5px">' +
            '<img src="' + webappPath + 'img/wait16.gif"/>redirecting...</div>';

    url = webappPath + "tenant/register?operatorId=" + $('#operatorId').val() + "&tenantId=EASYREC_DEMO" + "&url=" +
            window.location.protocol+"//localhost:"+(window.location.port==""?"80":window.location.port) + webappPath.substring(0,webappPath.length - 1) + "&description=This is a demo tenant."

    $.ajax({
        url:url,
        cache: false,
        success: function()
        {
            window.location = webappPath + "easyrec/overview?tenantId=EASYREC_DEMO";
        }
    });
}

function login()
{
    document.getElementById('redirect').outerHTML = '<div style="background-color: #ffffff;margin-top: 5px">' +
            '<img src="' + webappPath + 'img/wait16.gif"/>redirecting...</div>';

    $.ajax({
        url: webappPath + "loadcontext",
        cache: false,
        success: function()
        {
            window.location = webappPath + "home";
        }
    });
}

/*
 * Update the passwort of a registered operator
 */
function updatePassword(operatorId)
{
    $('#fieldStatus-oldPassword').html('');
    $('#fieldStatus-newPassword').html('');
    $('#fieldStatus-confirmPassword').html('');
    $('#PasswordMessage').hide();

    url = webappPath + "operator/updatePassword?operatorId=" + operatorId + "&oldPassword=" + $('#oldPassword').val() +
            "&newPassword=" + $('#newPassword').val() + "&confirmPassword=" + $('#confirmPassword').val();

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '152') {
                // Password successfully updated!
                //sucess
                $('#PasswordMessage').show();
            } else {
                //fail
                if ($(data).find('error').attr('code') == '132') {
                    // 132 The old password is wrong!
                    $('#fieldStatus-oldPassword').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '121') {
                    // 121 The Password must be at least x ...
                    $('#fieldStatus-newPassword').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '130') {
                    // The Password must match with the confirmed Password!";
                    $('#fieldStatus-confirmPassword').html($(data).find('error').attr('message'));
                }
            }
        }
    });
}