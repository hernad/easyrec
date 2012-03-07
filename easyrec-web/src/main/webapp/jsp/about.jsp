<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>


<div class="upperbody">
    <span class="upperheadline">About</span>

    <div style="padding-top:15px; padding-right: 20px;">
        Add <span class="fartblack">easyrec</span> recommendations to your website within minutes!
        <span class="fartblack">easyrec</span> is a web application that provides recommendation services using
        standardized web technologies ready to be integrated into your site. To get started with easyrec we
        recommend to read the <easyrec:wikiLink name="get started guide"/> and the <a href="${webappPath}/API" title="REST API">API</a> page.
        <br/>
    </div>
    <br/><br/>

    <div style="width:90%;height: 150px;padding-top:10px;">
        <div style="float:left;padding-top: 10px">
            <img

                    src="img/easyrec_logo.gif"
                    alt="easyrec_logo"/>
        </div>
        <div style="float:left;padding-left: 40px;font-size: small">
            <span class="fartblack">easyrec</span> is...
            <ul>
                <li style="list-style-image: url('${webappPath}/img/icon_nav_light.gif')">
                    easy to use - personalize your application within minutes
                </li>
                <li style="list-style-image: url('${webappPath}/img/icon_nav_light.gif')">
                    easy to integrate - due to
                    a
                    <a href="${webappPath}/API" title="REST API">REST API</a>
                    or placing javascript
                    <a href="${webappPath}/API" title="API-JS">codesnippets</a>
                    in your web pages.
                </li>
                <li style="list-style-image: url('${webappPath}/img/icon_nav_light.gif')">
                    easy to scale - due to distributed architecture
                </li>
                <li style="list-style-image: url('${webappPath}/img/icon_nav_light.gif')">
                    easy to maintain - with the available administration tool
                </li>
                <li style="list-style-image: url('${webappPath}/img/icon_nav_light.gif')">
                    free - easyrec is Open Source!
                </li>
            </ul>
        </div>
    </div>
</div>
