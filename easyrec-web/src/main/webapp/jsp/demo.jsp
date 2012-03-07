<div class="upperbody">
    <table width="100%">
        <tr>
            <td>
                <div class="headline">${description}</div>
                <img width="300px" alt="${description}" src="../img/covers/${image}"/>
            </td>
            <td>
                <div id="recommendation">
                </div>
            </td>
        </tr>
    </table>
</div>

<script type="text/javascript">

    $.getJSON("../api/1.0/json/otherusersalsoviewed?apikey=${apikey}&tenantid=EASYREC_DEMO&itemid=${itemId}",
             function(transport)
             {

                 var json = eval(transport);

                 if ("undefined" == typeof(json.error)) { // if no error show recommendations

                     var items = json.recommendeditems.item;

                     /* when the object is already in array format, this block will not execute */
                     if ("undefined" == typeof(items.length)) {
                         items = new Array(items);
                     }

                     // display recommendations in the DIV layer 'recommendation'
                     if (items.length > 0) {
                         $("#recommendation").html("<div class='headline'>Other users also viewed...</div>");

                         for (x = 0; x < 5 && x < items.length; x++) {
                             $("#recommendation").append("<img width='50px' alt='" + items[x].description + "'" +
                                     "     src='" + items[x].imageUrl + "'/>&nbsp;" + "<a href='" + items[x].url +
                                     "'>" + items[x].description + "</a>" + "<br/>");
                         }
                         $("#recommendation")
                                 .append("<br/><p>The recommendations above where retrieved with the JSON request:<br/><br/>" +
                                 "<a href='../api/1.0/json/otherusersalsoviewed?apikey=${apikey}&tenantid=EASYREC_DEMO&itemid=${itemId}'>" +
                                 "../api/1.0/json/otherusersalsoviewed?apikey=${apikey}&" +
                                 "<br/>tenantid=EASYREC_DEMO&itemid=${itemId}</a></p>");
                     }
                 }
             })
</script>



