/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

if (document.images) {
    picWait = new Image(16, 16);
    picWait.src = webappPath + "img/wait16.gif";
}

/*
 * view tenants
 */
function viewTenants(operatorId) {
    $.get(webappPath + "tenant/view?operatorId=" + operatorId, function(data) {
        $('#myRecommenders').html(data);
    });
}

function removeTenant(operatorId, tenantId) {
    confirm("You are about to delete the tenant '" + tenantId + "'. Do you want to continue?", function () {
        url = webappPath + "dev/removetenant?operatorId=" + operatorId + "&tenantId=" + tenantId;

        $('#tenantremove-' + operatorId + '-' + tenantId).html('<img src= "' + webappPath + 'img/wait16.gif"/>');
        $.ajax({
            url:url,
            cache: false,
            dataType: 'xml',
            success: function(data) {
                if ($(data).find('success').attr('code') == '201') {   // tenant removed
                    //success
                    window.location.reload();
                } else {
                    //fail
                    $('#tenantremove-' + operatorId + '-' + tenantId).html($(data).find('error').attr('message'));
                }
            }
        });
    });
}

function resetTenant(operatorId, tenantId) {
    confirm("Reseting the tenant '" + tenantId +
        "' will erase all its items, actions & rules. Do you want to continue?", function () {
        $('#status').html('<br^/>');
        url = 'resettenant?operatorId=' + operatorId + '&tenantId=' + tenantId;

        $('#tenantreset-' + operatorId + "-" + tenantId).html('<img src="' + webappPath + 'img/wait16.gif"/>');
        $.ajax({
            url:url,
            cache: false,
            dataType: 'xml',
            success: function(data) {
                if ($(data).find('success').attr('code') == '212') {   // Tenant successfully reset
                    //sucess
                    $('#status').html(tenantId + " successfully reset!");
                } else {
                    $('#status').html($(data).find('error').attr('message')); // Reseting Tenant failed
                }
                $('#tenantreset-' + operatorId + "-" + tenantId).html("<a href=\"javascript:resetTenant('" +
                    operatorId + "','" + tenantId + "');\"><img title='reset tenant' alt='reset tenant' src='" +
                    webappPath + "img/button_new.png'/></a>");
                $('#actions' + operatorId + "-" + tenantId).html("0");
                $('#rules' + operatorId + "-" + tenantId).html("0");
            }

        });
    });
}

function removeOperator(operatorId) {
    confirm("Removing the operator '" + operatorId +
        "' will also delete all the associated tenants and their data. Do you want to continue?", function () {
        url = webappPath + "operator/remove?operatorId=" + operatorId;

        $('#operatorremove-' + operatorId).html('<img src= "' + webappPath + 'img/wait16.gif"/>');
        $.ajax({
            url:url,
            cache: false,
            dataType: 'xml',
            success: function(data) {
                if ($(data).find('success').attr('code') == '150') {   // Operator successfully removed
                    //success
                    window.location.reload();
                } else {
                    //fail
                    $('#operatorremove-' + operatorId).html($(data).find('error').attr('message')); //Removing Operator failed!
                }
            }
        });
    });
}


/*
 * Start a plugin for a given operator and tenant.
 * e.g. http://easyrec.org/PluginStarter?operatorId=easyrec&tenantId=EASYREC_DEMO
 */
function startPlugin(operatorId, tenantId) {
    confirm("Depending on the number of actions, the computation of the rules may take a while." +
        "Nevertheless this task will run separately and will not affect your workflow. " +
        "Do you want to continue?", function () {
        $('#status').html('<br/>');

        divName = '#plugin-' + operatorId + "-" + tenantId;

        oldHtml = $(divName).html();

        $(divName).html('<img src="' + webappPath + 'img/wait16.gif"/>');

        $.ajax({
            url:webappPath + "PluginStarter?operatorId=" + operatorId + "&tenantId=" + tenantId,
            cache: false,
            dataType: 'xml',
            success: function(data) {
                resultHtml = '';

                $(data).find('generator').each(function() {
                    if ($(this).find('success').attr('code') == '901') {
                        // plugin finished successfully
                        var nrRules = 0;
                        var duration = 0;

                        var pluginName = $(this).attr('id');

                        nrRules = 0;
                        var rulesFound = false;
                        $(this).find('numberOfRulesCreated').each(function() {
                            nrRules += parseInt($(this).text());
                            rulesFound = true;
                        });

                        if (!rulesFound) nrRules = '?';
                        else if (nrRules == 0) nrRules = 'no';

                        var startDate;
                        var endDate;

                        $(this).find('startDate').each(function() {
                            var text = $(this).text();
                            startDate = Date.parse(text);
                            if (isNaN(startDate))
                                startDate = Date.parse(text.substring(0, 19).replace(/-/g, '/'));
                        });

                        $(this).find('endDate').each(function() {
                            var text = $(this).text();
                            endDate = Date.parse(text);
                            if (isNaN(endDate))
                                endDate = Date.parse(text.substring(0, 19).replace(/-/g, '/'));
                        });

                        duration = endDate - startDate;

                        resultHtml += '<p><img src="' + webappPath + 'img/button_check.png" />&nbsp;Plugin ' +
                            pluginName + ' computed ' + nrRules + ' new rules for ' + tenantId + ' in ' +
                            Math.round(duration / 1000) + ' seconds.</p>';

                        setActionCounter(operatorId, tenantId);
                    } else if ($(this).find('error').attr('code') == '911') {
                        // Plugin could not be started because another plugin is already running.
                        resultHtml += '<p><img src="' + webappPath + 'img/icon_warning.png" />&nbsp;' +
                            $(this).find('error').attr('message') + '</p>';
                    } else {
                        resultHtml += '<p>Execution of plugins failed!</p>';
                    }
                });

                $('#status').html(resultHtml);
                $(divName).html(oldHtml);
            },
            on500: function() {
                $('#status').html("<p>Execution of plugins failed!</p>");
                $(divName).html(oldHtml);
            }
        });
    });
}

function setActionCounter(operatorId, tenantId) {
    $.ajax({
        url:webappPath + "tenant/statistics?tenantId=" + tenantId + "&operatorId=" + operatorId + "&responseType=XML",
        cache: false,
        dataType: 'xml',
        success: function(data) {
            $('#actions' + operatorId + "-" + tenantId).html($(data).find('numberOfTotalActions').text());
            $('#rules' + operatorId + "-" + tenantId).html($(data).find('numberOfTotalRules').text());
        }
    });
}

/*
 * Empties the rule miner log table.
 */
function emptyPluginLogs(operatorId, tenantId) {
    confirm("You are about to empty the whole plugin log table. Do you want to continue?", function () {
        $('#emptyPluginLogs').html('<img src="' + webappPath + 'img/wait16.gif"/>');
        $.ajax({url:webappPath + 'dev/emptypluginlogs',cache: false});
        window.location = webappPath + "dev/viewpluginlogs?tenantId=" + tenantId + "&operatorId=" + operatorId;
    });
}

/*
 * Pull the used memory of the VM.
 */
function pullusedmem(heap, max) {
    $.ajax({
        url:webappPath + "dev/pullusedmem",
        cache: false,
        dataType: 'xml',
        success: function(data) {
            if ($(data).find('success').attr('code') == '904') {   // success
                used = $(data).find('action').text();
                percentage = Math.round((used / heap) * 100);
                if (used > max) {
                    max = used;
                }
                maxpercentage = Math.round((max / heap) * 100);
                $('#usedmem').html(used + " MB/" + heap + " MB (" + percentage + "% used) Max used: " + max + " MB (" +
                    maxpercentage + "%)");
                setTimeout('pullusedmem(' + heap + ', ' + max + ');', 1000);
            }
        }
    });
}


/**
 * Filter the table for rows that match the select value from the selectmenu
 */
function filterLogTable() {
    if ($("#logtype").val() == "ALL") {
        $("#logTable").find("tr").each(function(i) {
            if (i > 0) $(this).show();
        });
    } else {
        $("#logTable").find("tr").each(function(i) {
            if (i > 0) $(this).hide();
        });
        $("#logTable").find('#' + $("#logtype").val()).each(function() {
            $(this).show();
        });
    }
}

function installPlugin(pluginId, version, operatorId, tenantId) {
    $.ajax({
        url:webappPath + "dev/plugininstall?pluginId=" + pluginId + "&version=" + version,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            if ($(data).find('success').attr('code') == '600') {
                //success
                location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
            } else {
                //fail
                $('#plugininstall-' + pluginId).html($(data).find('error').attr('message'));
            }
        }
    });
}


function stopPlugin(pluginId, version, operatorId, tenantId) {
    $.ajax({
        url:webappPath + "dev/pluginstop?pluginId=" + pluginId + "&version=" + version,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
        }
    });
}

function deletePlugin(pluginId, version, operatorId, tenantId) {

    confirmYesNo("WARNING! If you delete your plugin you will loose all your rules!", function (clickedYes) {
            if (!clickedYes) {
                location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
                return;
            }

            $.ajax({
                url:webappPath + "dev/plugindelete?pluginId=" + pluginId + "&version=" + version,
                cache: false,
                dataType: 'xml',
                success: function(data) {
                    if ($(data).find('success').attr('code') == '904') {
                        //success
                        location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
                    } else {
                        //fail
                        $('#plugininstall-' + pluginId).html($(data).find('error').attr('message'));
                    }
                }
            });
        }
    );
}

function pluginChangeState(checkboxId, pluginId, version, operatorId, tenantId) {

    if (!$('#enablePluginCheckbox-' + checkboxId)[0].checked) {
        confirmYesNo("WARNING! If you deactivate your plugin you will loose all your rules!", function (clickedYes) {
                if (!clickedYes) {
                    location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
                    return;
                }
                pluginChangeStateRequest(checkboxId, pluginId, version);
            }
        );
    } else {
        pluginChangeStateRequest(checkboxId, pluginId, version);
    }
}

function pluginChangeStateRequest(checkboxId, pluginId, version) {
    $.ajax({
        url:webappPath + "dev/pluginchangestate?checked=" + $('#enablePluginCheckbox-' + checkboxId)[0].checked +
            "&pluginId=" + pluginId + "&version=" + version,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            if ($(data).find('success').attr('code') == '904') {
                //success
                location.href = webappPath + 'dev/plugins?operatorId=' + operatorId + '&tenantId=' + tenantId;
            } else {
                //fail
                $('#plugininstall-' + pluginId).html($(data).find('error').attr('message'));
            }
        }
    });
}


function showPluginSettings(operatorId, tenantId, assocTypeId) {
    pluginId = $('#plugin-select-' + assocTypeId).val();
    showPluginSettingsEx(operatorId, tenantId, assocTypeId, pluginId);
}

function showPluginSettingsEx(operatorId, tenantId, assocTypeId, pluginId) {
    pluginId = $('#plugin-select-' + assocTypeId).val();
    $.ajax({
        url: webappPath + 'dev/viewpluginconfigdetails?operatorId=' + operatorId + '&tenantId=' + tenantId +
            '&pluginId=' + encodeURIComponent(pluginId) + "&assocTypeId=" + assocTypeId,
        cache: false,
        dataType: 'html',
        success: function(data) {
            $('#plugin-details-' + assocTypeId).html(data);
        }
    });
}

function showPluginDescription(operatorId, tenantId, pluginId, version, divName) {
    $.ajax({
        url: webappPath + 'dev/viewplugindescription?operatorId=' + operatorId + '&tenantId=' + tenantId +
            '&pluginId=' + encodeURIComponent(pluginId) + "&version=" + version,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            $('#' + divName).html($(data).find('token').text() + '<hr />');
        }
    });
}


function toggleFilterDemoTenants() {
    var filterState = 0;

    if ($('#filterDemoTenants:checked').val() == '1') {
        filterState = 1;
    }
    setHttpParm("filterDemoTenants", filterState);
}


function setHttpParm(parmName, value) {
    var regexp = new RegExp("[\\&\\?]" + parmName + "=[0-9]+", "g");
    var newUrl = this.document.location.href.replace(regexp, '');
    var parmMode = "";
    if (newUrl.indexOf('?') != -1 && newUrl.length != newUrl.indexOf('?') + 1) {// true = ein fragezeichen gefunden und es ist nicht am schluss
        parmMode = "&";
    } else if (newUrl.length != newUrl.indexOf('?') + 1) { //fr agezeichen ist nicht  am schluss
        parmMode = "?";
    }

    this.document.location.href = newUrl + parmMode + parmName + "=" + value;
}