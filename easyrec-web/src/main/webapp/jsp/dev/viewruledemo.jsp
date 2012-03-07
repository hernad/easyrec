<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="appendbody">
    <h1>Rule Demo</h1>

    <p>
        Use the following links to simulate 'view'-actions by users A &amp; B.
        You can configure and execute the plugins and <br/>see the results by
        clicking the 'view items that were...' link.
        To clear all actions and rules press the 'reset tenant' link<br/>
        at the end of the page.
    </p>

    <h2>Simulate user actions</h2>
    <ul>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=42&itemdescription=Fatboy Slim - The Rockafeller Skank&itemurl=/item/fatboyslim&itemimageurl=${extendedWebappPath}/img/covers/fatboyslim.jpg&userid=A&sessionid=a">user
            A views item 42</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=43&itemdescription=Beastie Boys - Intergalactic&itemurl=/item/beastieboyz&itemimageurl=${extendedWebappPath}/img/covers/beastieboys.jpg&userid=A&sessionid=a">user
            A views item 43</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=44&itemdescription=Gorillaz - Clint Eastwood&itemurl=/item/gorillaz&itemimageurl=${extendedWebappPath}/img/covers/gorillaz.jpg&userid=A&sessionid=a">user
            A views item 44</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=42&itemdescription=Fatboy Slim - The Rockafeller Skank&itemurl=/item/fatboyslim.html&itemimageurl=${extendedWebappPath}/img/covers/fatboyslim.jpg&userid=B&sessionid=b">user
            B views item 42</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=43&itemdescription=Beastie Boys - Intergalactic&itemurl=/item/beastieboyz&itemimageurl=${extendedWebappPath}/img/covers/beastieboys.jpg&userid=B&sessionid=b">user
            B views item 43</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/view?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=44&itemdescription=Gorillaz - Clint Eastwood&itemurl=/item/gorillaz&itemimageurl=${extendedWebappPath}/img/covers/gorillaz.jpg&userid=B&sessionid=b">user
            B views item 44</a></li>
    </ul>
    <h2>Create rules</h2>
    <ul>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="viewpluginconfig?operatorId=${operator.operatorId}&tenantId=EASYREC_DEMO">plugin config</a>
        </li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../PluginStarter?operatorId=${operator.operatorId}&tenantId=EASYREC_DEMO">start plugins</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="../api/1.0/otherusersalsoviewed?apikey=${operator.apiKey}&tenantid=EASYREC_DEMO&itemid=42&userid=C">view
            items that where also viewed with item 42</a></li>
        <li><a onclick='window.open(this.href,"blank");return false;'
               href="resettenant?operatorId=${operator.operatorId}&tenantId=EASYREC_DEMO">reset tenant</a></li>
    </ul>
</div>		
