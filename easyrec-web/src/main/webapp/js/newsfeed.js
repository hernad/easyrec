function showBlog() {
    $.ajax({
        url:webappPath + "RSSBlog",
        cache: false,
        dataType: "xml",
        complete: function(xml) {

            var transport = $.xml2json(xml.responseText);
            var items = transport.channel.item;

            if (transport.channel.item.length > 0) {
                iHTML = '<div class="headline">News and Updates</div><table width=500>';

                for (i = 0; i < 5 && i < items.length; i++) {

                    var pubDate = items[i].pubDate;
                    iHTML += '<tr><td><b>' + pubDate.substr(5, pubDate.length - 20) + '</b></td><td>' + items[i].title +
                            '&nbsp;<a target="_blank" href="' + items[i].link + '">more...</a></td></tr>';
                }

                iHTML += '</table>';
                $('#elArticles').html(iHTML);
            }

            iHTML += '</table>';
            $('#elArticles').html(iHTML);
        }
    });
}

// checks if update is available and displays the update link on startscreen.
function checkUpdate(updateUrl, token) {

    $.ajax({
        url:webappPath + "UpdateCheck?version=" + easyrecVersion + "&token=" + token,
        cache: false,
        success: function(transport) {
            latestVersion = parseFloat($(transport).find('latestVersion').text());
            releaseDate = $(transport).find('releaseDate').text();
            if (latestVersion > easyrecVersion) {
                $('#update').html(

                        '<br /><strong>' + releaseDate + ' Update to version <span class="new">' + latestVersion +
                                '</span> <a target="_blank" href="' + updateUrl + '">here</a> available!</strong>');
            }
        }
    });
}

