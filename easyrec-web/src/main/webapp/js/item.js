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
// This class manages items

/*
 *  Deactivates an item
 */
function deactivateItem(operatorId, tenantId, itemId)
{
    $('#item_status-' + itemId).html('<img src="' + webappPath + 'img/wait16.gif"/>');

    url = webappPath + "item/deactivate?" + "operatorId=" + operatorId + "&" + "tenantId=" + tenantId + "&" +
            "itemId=" + itemId;

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            // Operation Successfull!
            location.reload(true);
        }
    });
}


/*
 *  Activates an item
 */
function activateItem(operatorId, tenantId, itemId)
{
    $('#item_status-' + itemId).html('<img src="' + webappPath + 'img/wait16.gif"/>');

    url = webappPath + "item/activate?" + "operatorId=" + operatorId + "&" + "tenantId=" + tenantId + "&" + "itemId=" +
            itemId;

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            // Operation Successfull!
            location.reload(true);
        }
    });
}

/*
 *  Removes items from tenant
 */
function removeItems(operatorId, tenantId)
{
    $('#removeItems').html('<img src="' + webappPath + 'img/wait16.gif"/>');

    url = webappPath + "item/remove?" + "operatorId=" + operatorId + "&tenantId=" + tenantId;

    $.ajax({
        url:url,
        cache: false,
        dataType: 'xml',
        success: function(data)
        {
            if ($(data).find('success').attr('code') == '904') {  // Operation Successfull!
                //sucess
                $('#removeItems').html('items removed!');
                viewItems(webappPath + 'item/view?tenantId=' + tenantId);
            } else {
                //fail
                $('#removeItems').html('failed to remove items');
            }
        }
    });
}

function viewItemDetails(url)
{
    $('#itemdetails').html('<img src="' + webappPath + 'img/wait16.gif"/>');

    $.get(url, function(data)
    {
        $('#itemdetails').html(data);
    });
}

function viewItems(url)
{
    $('#myItems').html('<img src=' + webappPath + '"img/wait16.gif"/>');

    $.get(url, function(data)
    {
        $('#itemdmyItemsetails').html(data);
    });
}

function waitForImage(itemId)
{
    if (IsImageOk($('#imagePreLoad' + itemId))) {
        $('#image' + itemId).attr('src', $('#imagePreLoad' + itemId).attr('src'));
        $('#image' + itemId).attr('height', '150');
    } else {
        $('#image' + itemId).hide();
    }
}

function IsImageOk(img)
{
    // During the onload event, IE correctly identifies any images that
    // weren't downloaded as not complete. Others should too. Gecko-based
    // browsers act like NS4 in that they report this incorrectly.
    if (!img.complete) {
        return false;
    }

    // However, they do have two very useful properties: naturalWidth and
    // naturalHeight. These give the true size of the image. If it failed
    // to load, either of these should be zero.
    if (typeof img.naturalWidth != "undefined" && img.naturalWidth == 0) {
        return false;
    }

    // No other way of checking: assume it's ok.
    return true;
}

/*
 * Dynamically changes the view of Most viewed/bought/rated items
 * with the given timerange parameter (ALL, WEEK, MONTH, DAY)
 */
function changeView(operatorId, tenantId)
{
    $('#redirect').src = webappPath + "img/wait16.gif";

    window.location = webappPath + 'tenant/viewmostvieweditems?tenantId=' + tenantId + '&operatorId=' + operatorId +
            '&timerange=' + $('#timerange').val() + '&assoc=' + $('#assoc').val();
}

/*
 * Query for Items for a tenant with a given descritpion
 */
function searchItems(operatorId, tenantId, searchText)
{
    if (searchText != "") {
        window.location = webappPath + 'tenant/items?operatorId=' + operatorId + '&tenantId=' + tenantId +
                '&description=' + searchText;
    }
}

/*
 * Toggle between item details
 */
function showHide(operatorId, tenantId, itemId)
{
    var hide;
    if ($('#imageToggle' + itemId).attr('src').indexOf('minus.gif') > 0) {
        $('#imageToggle' + itemId).attr('src', webappPath + 'img/plus.gif');
        hide = false;
    } else {
        $('#imageToggle' + itemId).attr('src', webappPath + 'img/minus.gif');
        hide = true;
        if ($('#itemDetails' + itemId).html().indexOf('loading item details') >= 0) {
            $.ajax({
                url: webappPath + 'item/viewitemdetails?operatorId=' + operatorId + '&tenantId=' + tenantId +
                        '&itemId=' + itemId,
                cache: false,
                dataType: 'html',
                success: function(data)
                {
                    $('#itemDetails' + itemId).html(data);
                    //setTimeout("waitForImage('"+itemId+"');",2000);
                }
            });
        }
    }

    $('#itemDetails' + itemId).slideToggle('fast');

    return hide;
}

/*
 * Toggle between item statistics
 */
function showHideItemStatistics(operatorId, tenantId, itemId)
{
    if ($('#itemStatisticsToggle' + itemId).html().indexOf('hide') >= 0) {
        $('#itemStatisticsToggle' + itemId).html('show item statistics... ');
    } else {
        $('#itemStatisticsToggle' + itemId).html('hide item statistics... ');
        if ($('#itemStatisitcs' + itemId).html().indexOf('img') > 0) {
            $.ajax({
                url: webappPath + 'item/viewitemstatistics?operatorId=' + operatorId + '&tenantId=' + tenantId +
                        '&itemId=' + itemId,
                cache: false,
                dataType: 'html',
                success: function(data)
                {
                    $('#itemStatisitcs' + itemId).html(data);
                }
            });
        }
    }

    $('#itemStatisitcs' + itemId).slideToggle('slow');
}

/*
 * Toggle between item statistics
 */
function showHideItemEdit(operatorId, tenantId, itemId, tenantUrl)
{
    imageTenantUrl = "";
    if ($('#itemEditToggle' + itemId).html().indexOf('apply') >= 0) {
        $.ajax({
            url: webappPath + 'item/edit?operatorId=' + operatorId + '&tenantId=' + tenantId + '&itemId=' + itemId +
                    '&description=' + $('#editItemDescription' + itemId).val() + '&url=' +
                    $('#editItemUrl' + itemId).val() + '&imageUrl=' + $('#editItemImageUrl' + itemId).val(),
            cache: false,
            success: function()
            {
                // set new item attributes
                $('#itemDescription' + itemId).html($('#editItemDescription' + itemId).val());

                if ($('#editItemImageUrl' + itemId).val().indexOf("http://") < 0 &&
                        $('#editItemImageUrl' + itemId).val().indexOf("https://") < 0) {
                    imageTenantUrl = tenantUrl;
                }

                $('#itemImageUrl' + itemId).attr('href', imageTenantUrl + $('#editItemImageUrl' + itemId).val());
                $('#itemImageUrl' + itemId).attr('title',
                        'opens the url of the item image (' + imageTenantUrl + $('#editItemImageUrl' + itemId).val() +
                                ') in a new window');
                $('#image' + itemId).attr('src', imageTenantUrl + $('#editItemImageUrl' + itemId).val());

                if ($('#editItemUrl' + itemId).val().indexOf("http://") >= 0 ||
                        $('#editItemUrl' + itemId).val().indexOf("https://") >= 0) {
                    tenantUrl = "";
                }

                $('#itemUrl' + itemId).attr('href', tenantUrl + $('#editItemUrl' + itemId).val());
                $('#itemUrl' + itemId).attr('title',
                        'opens the url of the item  (' + tenantUrl + $('#editItemUrl' + itemId).val() +
                                ') in a new window');
                $('#itemEditToggle' + itemId).html('edit... ');
            }
        });
    } else {
        $('#itemEditToggle' + itemId).html('apply... ');
    }

    $('#itemEdit' + itemId).slideToggle('slow');
}

/**
 * Toggle between viewing/hinding rule to an item.
 */
function showHideRules(operatorId, tenantId, itemId)
{
    if (showHide(operatorId, tenantId, itemId)) {
        $.ajax({
            url: webappPath + 'tenant/rulestoitem?operatorId=' + operatorId + '&tenantId=' + tenantId + '&itemId=' +
                    itemId,
            cache: false,
            dataType: 'html',
            success: function(data)
            {
                $('#rulesDetails' + itemId).html(data);
            }
        });
    }

    $('#rulesDetails' + itemId).slideToggle('fast');
}