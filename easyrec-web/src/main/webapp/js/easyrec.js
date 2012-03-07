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

var itemDetailsChanged = 0;
var detailXY = 0;


function followingSteps(tenantId, reloadPage) {
    $(".tenantId").html(tenantId);

    if (typeof(reloadPage) == "undefined") {
        reloadPage = false;
    }

    $('#followingSteps').dialog(
    {
        modal:true,
        title:"Integration Information",
        width:600,
        height:500,
        resizable:false,
        beforeClose:function(event, ui) {
            if (reloadPage) {
                location.href = webappPath + "easyrec/overview?tenantId=" + tenantId;
            }
        }
    });
}


function saveItem(operatorId, tenantId, itemId, tenantUrl, detailBoxUniqueId) {
    $.ajax({
        url: webappPath + 'item/edit?operatorId=' + operatorId + '&tenantId=' + tenantId + '&itemId=' + itemId +
                '&description=' + encodeURIComponent($('#editItemDescription' + itemId).val()) + '&url=' +
                encodeURIComponent($('#editItemUrl' + itemId).val()) + '&imageUrl=' + encodeURIComponent($('#editItemImageUrl' + itemId).val()),
        cache: false,
        success: function() {
            // set new item attributes
            $('#itemDescription' + itemId).html($('#editItemDescription' + itemId).val());

            if ($('#editItemImageUrl' + itemId).val().indexOf("http://") < 0 &&
                    $('#editItemImageUrl' + itemId).val().indexOf("https://") < 0) {
                var imageTenantUrl = tenantUrl;
            }

            $('#itemImageUrl' + itemId).html(imageTenantUrl + $('#editItemImageUrl' + itemId).val());
            $('#image' + itemId).attr('src', imageTenantUrl + $('#editItemImageUrl' + itemId).val());

            if ($('#editItemUrl' + itemId).val().indexOf("http://") >= 0 ||
                    $('#editItemUrl' + itemId).val().indexOf("https://") >= 0) {
                tenantUrl = "";
            }

            $('#itemUrl' + itemId).html(tenantUrl + $('#editItemUrl' + itemId).val());
            $('#itemDetailHelperDiv' + detailBoxUniqueId).dialog({title:$('#editItemDescription' + itemId).val()});
            $('#editItem' + detailBoxUniqueId + ' .success').html("The changes were successfully saved.").show().delay(5000).fadeOut('slow');
            itemDetailsChanged = detailBoxUniqueId;
        }
    });
}

function loadItem(operatorId, tenantId, itemId, itemTitle, editEnabled, startTab) {

    var detailBoxUniqueId = new Date().getTime();
    if (typeof(editEnabled) == "undefined") {
        editEnabled = false;
    }

    if (typeof(startTab) == "undefined") {
        startTab = (editEnabled) ? 1 : 0;
    }

    $('<div></div>', {id:'itemDetailHelperDiv' + detailBoxUniqueId}).appendTo("body");

    detailXY += 80;


    if (detailXY + 600 > $(window).width()) {
        detailXY = 0;
    }


    $.ajax({
        url: webappPath + 'item/viewitemdetails?operatorId=' + operatorId + '&tenantId=' + tenantId +
                '&itemId=' + itemId + '&detailBoxUniqueId=' + detailBoxUniqueId + '&editEnabled=' + editEnabled,
        cache: false,
        dataType: 'html',
        success: function(data) {
            $('#itemDetailHelperDiv' + detailBoxUniqueId).html(data).dialog(
            {
                modal:false,
                title:itemTitle,
                width:700,
                height:500,
                resizable:false,
                position:[detailXY,detailXY],
                open:function() {
                    $("#itemTabs" + detailBoxUniqueId).tabs({ selected: startTab });
                },
                beforeClose:function(event, ui) {
                    $("#itemTabs" + detailBoxUniqueId).tabs("destroy");
                    if (itemDetailsChanged == detailBoxUniqueId) {
                        location.reload(true);
                    }
                }
            });

        }
    });
}

/*
 * view tenants
 */
function viewTenants(operatorId) {
    $.get(webappPath + "tenant/view?operatorId=" + operatorId, function(data) {
        $('#myeasyrec').html(data);
    });
}

/*
 * Creates a new tenant for a given operator
 */
function registerTenant(operatorId) {

    $('#error').hide();
    $('#error').html("");

    tenantId = $('#tenantId').val();
    tenantId = tenantId.replace(/\?/g, "");

    url = webappPath + "tenant/register?operatorId=" + operatorId + "&tenantId=" + tenantId + "&url=" +
            $('#url').val() + "&description=" + $('#easyrec_description').val();

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            if ($(data).find('success').attr('code') == '200') { // Tenant Created
                //successfully created a tenant
                followingSteps(tenantId, true);
            } else {
                //failed to create the new tenant
                $('#error').html('');
                if ($(data).find('error').attr('code') == '203') {   // invalid URL
                    $('#error').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '207') {   // invalid Tenant Name
                    $('#error').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '204') {   // tenant alread exists
                    $('#error').html($(data).find('error').attr('message'));
                }
                $('#error').show();
            }
        }
    });
}

/*
 * update a tenant description or url
 */
function updateTenant(operatorId, tenantId) {
    updateUrl = webappPath + "tenant/update?tenantId=" + tenantId + "&url=" + $('#url').val() + "&description=" +
            $('#easyrec_description').val() + "&operatorId=" + operatorId;

    $.ajax({
        url:updateUrl,
        cache: false,
        dataType:'xml',
        success: function(data) {

            if ($(data).find('success').attr('code') == '208') { // tenant updated
                //success
                location.href = webappPath + "easyrec/overview?menu=x&tenantId=" + tenantId + "&operatorId=" + operatorId;
            } else {
                //failed to create the new tenant
                $('#error').html('');
                if ($(data).find('error').attr('code') == '203') {   // invalid URL
                    $('#error').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '207') {   // invalid Tenant Name
                    $('#error').html($(data).find('error').attr('message'));
                }
                if ($(data).find('error').attr('code') == '204') {   // tenant already exists
                    $('#error').html($(data).find('error').attr('message'));
                }
                $('#error').show();
            }
        }
    });
}

/*
 * Refreshes the statistics for a given tenant
 */
function refreshStatistics(tenantId, operatorId) {
    url = webappPath + "tenant/refreshstatistics?operatorId=" + operatorId + "&tenantId=" + tenantId;

    $('#tenantrefreshstatistics').html('updating statistics... <img src="' + webappPath + 'img/wait16.gif"/>');

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data) {
            if ($(data).find('success').attr('code') == '904') { // statistics successfully updated
                window.location.reload();
            } else {
                $('#tenantrefreshstatistics').html('updating statistics failed.'); // updating statistics failed.
            }
        }
    });
}


/**
 * Updates the Action Diagramm
 */
function updateTenantFlot(tenantId) {
    $('#updateflotwait').attr('src', webappPath + 'img/wait16.gif');

    flotUrl = webappPath + "statistics?flot=1&onlyData=1" + "&month=" + $('#month').val() + "&year=" +
            $('#year').val() + "&actionType=" + $('#actionType').val() + "&tenant=" + tenantId + "&_=" + new Date().getTime();

    try {
        $.getJSON(flotUrl, function(data) {
            if (data.length != 0) {
                $("#legend_days").show();
                $("#legend_actions").show();
                $(function () {
                    $.plot($("#placeholder" + tenantId), eval(data), {grid: { hoverable: true, clickable: true },
                        actualMonth:$('#month').val(), actualYear:$('#year').val(), legend: {container:"#legendContainer", noColumns: 3}});

                    function showTooltip(x, y, contents) {
                        $('<div id="tooltip">' + contents + '</div>').css({
                            position: 'absolute',
                            display: 'none',
                            top: y + 5,
                            left: x + 5,
                            border: '1px solid #fdd',
                            padding: '2px',
                            'background-color': '#fee',
                            opacity: 0.80
                        }).appendTo("body").fadeIn(200);
                    }

                    var previousPoint = null;
                    $("#placeholder" + tenantId).bind("plothover", function (event, pos, item) {


                        if (item) {
                            if (previousPoint != item.datapoint) {
                                previousPoint = item.datapoint;

                                $("#tooltip").remove();
                                var x = item.datapoint[0].toFixed(2),
                                        y = item.datapoint[1].toFixed(2);
                                var pre = "th";
                                if (x == 1) pre = "st";
                                if (x == 2) pre = "nd";
                                showTooltip(item.pageX, item.pageY,
                                        Math.round(x) + pre + " of " + $("#month :selected").text() + ": " +
                                                Math.round(y) + " " + item.series.label);
                            }
                        } else {
                            $("#tooltip").remove();
                            previousPoint = null;
                        }

                    });

                });
            } else {
                $("#placeholder" + tenantId).html("<br /><br /><br />No actions of this type available.");
                $("#legend_days").hide();
                $("#legend_actions").hide();
            }
            $('#updateflotwait').attr('src', webappPath + 'img/blank.gif');

        });
    } catch (e) {
        alert(e);
    }
}

function showPluginDescription(operatorId, tenantId, pluginId, version, divName) {
    $.ajax({
        url: webappPath + 'dev/viewplugindescription?operatorId=' + operatorId + '&tenantId=' + tenantId +
                '&pluginId=' + encodeURIComponent(pluginId) + "&version=" + version,
        cache: false,
        dataType:'xml',
        success: function(data) {
            $('#' + divName).html($(data).find('token').text() + '<hr />');
        }
    });
}


