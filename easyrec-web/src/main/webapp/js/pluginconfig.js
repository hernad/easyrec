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

$(function() {
    // view input fields for changing time for scheduler and archiver only when enabled.
    if ($('#edit-scheduler').html() == "false") {
        $('#layer-executiontime').hide();
    } else {
        $('#layer-executiontime').show();
    }

    if ($('#edit-archiving').html() == "false") {
        $('#layer-archivingtime').hide();
    } else {
        $('#layer-archivingtime').show();
    }

    if ($('#edit-maxactions').html() == "false") {
        $('#layer-maxactions').hide();
    } else {
        $('#layer-maxactions').show();
    }
});

/*
 * Enables or Disables the auto rule mining
 */
function scheduler(operatorId, tenantId) {
    if ($('#edit-scheduler').html() == "false") {
        $('#edit-scheduler').html("true");
        $('#layer-executiontime').show();
    } else {
        $('#edit-scheduler').html("false");
        $('#layer-executiontime').hide();
    }

    $('#scheduler').html(waitingImage);

    $.ajax({
        url:webappPath + "dev/scheduler?operatorId=" + operatorId + "&tenantId=" + tenantId + "&enabled=" +
                $('#edit-scheduler').html() + "&executiontime=" + $('#edit-excecutiontime').val(),
        cache: false
    });

    $('#scheduler').html("change");
}

/*
 * Enables or Disables the backtracking
 */
function backtracking(operatorId, tenantId) {
    if ($('#edit-backtracking').html() == "false") {
        $('#edit-backtracking').html("true");
    } else {
        $('#edit-backtracking').html("false");
    }

    $('#backtracking').html(waitingImage);

    $.ajax({
        url:webappPath + "dev/storebacktracking?operatorId=" + operatorId + "&tenantId=" + tenantId + "&backtracking=" +
                $('#edit-backtracking').html(),
        cache: false
    });

    $('#backtracking').html("change");
}

/*
 * Enables or Disables the  Archiving Function
 */
function archiving(operatorId, tenantId) {
    if ($('#edit-archiving').html() == "false") {
        $('#edit-archiving').html("true");
        $('#layer-archivingtime').show();
    } else {
        $('#edit-archiving').html("false");
        $('#layer-archivingtime').hide();
    }

    $('#archiving').html(waitingImage);

    $.ajax({
        url:webappPath + "dev/storearchive?operatorId=" + operatorId + "&tenantId=" + tenantId + "&archiving=" +
                $('#edit-archiving').html(),
        cache: false
    });

    $('#archiving').html("change");
}

/**
 * Change the execution time for a tenant.
 */
function changeExecutionTime(operatorId, tenantId) {
    if ($('#excecutiontime').html() == "edit") {
        $('#excecutiontime').html("apply");
        $('#static-excecutiontime').hide();
        $('#edit-excecutiontime').show();
        $('#edit-excecutiontime').focus();
        $('#edit-excecutiontime').select();
    } else {
        $('#static-excecutiontime').html($('#edit-excecutiontime').val());
        $('#static-excecutiontime').show();
        $('#edit-excecutiontime').hide();
        $('#excecutiontime').html(waitingImage);

        $.ajax({
            url:webappPath + "dev/scheduler?operatorId=" + operatorId + "&tenantId=" + tenantId + "&enabled=" +
                    $('#edit-scheduler').html() + "&executiontime=" + $('#edit-excecutiontime').val(),
            cache: false
        });

        $('#excecutiontime').html("edit");
    }
}

/**
 * Change the execution time for a tenant.
 */
function changeBackTrackingURL(operatorId, tenantId) {
    if ($('#backtrackingURL').html() == "edit") {
        $('#backtrackingURL').html("apply");
        $('#static-backtrackingURL').hide();
        $('#edit-backtrackingURL').show();
        $('#edit-backtrackingURL').focus();
        $('#edit-backtrackingURL').select();
    } else {
        $('#static-backtrackingURL').html($('#edit-backtrackingURL').val());
        $('#static-backtrackingURL').show();
        $('#edit-backtrackingURL').hide();
        $('#backtrackingURL').html(waitingImage);

        $.ajax({
            url:webappPath + "dev/storebacktracking?operatorId=" + operatorId + "&tenantId=" + tenantId + "&backtrackingURL=" + $('#edit-backtrackingURL').val(),
            cache: false
        });

        $('#backtrackingURL').html("edit");
    }
}



/**
 * Change the number of days when action are archived.
 */
function changeArchivingTime(operatorId, tenantId) {
    if ($('#archivingtime').html() == "edit") {
        $('#archivingtime').html("apply");
        $('#static-archivingtime').hide();
        $('#edit-archivingtime').show();
        $('#edit-archivingtime').focus();
        $('#edit-archivingtime').select();
    } else {
        $('#static-archivingtime').html($('#edit-archivingtime').val());
        $('#static-archivingtime').show();
        $('#edit-archivingtime').hide();
        $('#archivingtime').html(waitingImage);

        $.ajax({
            url:webappPath + "dev/storearchive?operatorId=" + operatorId + "&tenantId=" + tenantId + "&archiving=" +
                    $('#edit-archiving').html() + "&archivingtime=" + $('#edit-archivingtime').val(),
            cache: false
        });

        $('#archivingtime').html("edit");
    }
}

/**
 * Change the number of max actions allowed per month
 */
function changeMaxActions(operatorId, tenantId) {
    if ($('#maxactions').html() == "edit") {
        $('#maxactions').html("apply");
        $('#static-maxactions').hide();
        $('#edit-maxactions').show();
        $('#edit-maxactions').focus();
        $('#edit-maxactions').select();
    } else {
        $('#static-maxactions').html($('#edit-maxactions').val());
        $('#static-maxactions').show();
        $('#edit-maxactions').hide();
        $('#maxactions').html(waitingImage);

        $.ajax({
            url:webappPath + "dev/storemaxactions?operatorId=" + operatorId + "&tenantId=" + tenantId + "&maxactions=" +
                    $('#edit-maxactions').val(),
            cache: false
        });

        $('#maxactions').html("edit");
    }
}

/*
 * Enables or Disables the plugin support
 */
function plugins(operatorId, tenantId) {
    if ($('#edit-pluginsActive').html() == "false") {
        $('#edit-pluginsActive').html("true");
    } else {
        $('#edit-pluginsActive').html("false");
    }

    $('#pluginsActive').html(waitingImage);

    $.ajax({
        url:webappPath + "dev/storepluginsactive?operatorId=" + operatorId + "&tenantId=" + tenantId +
                "&pluginsactive=" + $('#edit-pluginsActive').html(),
        cache: false,
        dataType: 'xml',
        success: function(data) {
            $('#div_plugins').slideToggle('slow');
            if ($('#edit-pluginsActive').html() == "true") {
                showPluginSettings(operatorId, tenantId);
            }
        }
    });

    $('#pluginsActive').html("change");
}

function toggleConfigDetails(assocTypeId) {
    $('#plugin-configuration-' + assocTypeId).slideToggle('slow');
}

/*
 * Edit an element of the rule miner config
 */
function editPlugin(operatorId, tenantId, assocTypeId, configurationName, key, element) {
    pluginId = $('#plugin-select-' + assocTypeId).val();

    if ($('#' + element).html() == "edit") {
        $('#' + element).html("apply");
        $('#static-' + element).hide();
        $('#edit-' + element).show();
        $('#edit-' + element).focus();
        $('#edit-' + element).select();
    } else {
        $('#' + element).html(waitingImage);

        $.ajax({
            url:webappPath + "dev/storepluginconfig?operatorId=" + operatorId + "&tenantId=" + tenantId + "&pluginId=" +
                    pluginId + "&assocTypeId=" + assocTypeId + "&configurationName=" +
                    encodeURIComponent(configurationName) + "&key=" + key + "&value=" + $('#edit-' + element).val(),
            cache: false,
            dataType: 'xml',
            success: function(data) {
                if ($(data).find('error').attr('code') == '602') {
                    $('#plugin-error-' + element).show();
                    $('#plugin-error-' + element).html($(data).find('error').attr('message'));
                    $('#edit-' + element)[0].className = "error";
                    $('#' + element).html("apply");
                } else {
                    $('#plugin-error-' + element).hide();
                    $('#plugin-error-' + element).html("");
                    $('#static-' + element).html($('#edit-' + element).val());
                    $('#static-' + element).show();
                    $('#edit-' + element).hide();
                    $('#edit-' + element)[0].className = "";
                    $('#' + element).html("edit");
                }
            }
        });

    }
}
