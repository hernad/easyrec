<script src="js/register.js" type="text/javascript"></script>
<div class="upperbody">
    <div id="registerSuccess" style="display: none">
        <table>
            <tr>
                <td style="padding-top:15px">
                    <span class="headlineBig">Registration successful</span><br/><br/>
                </td>
                <td><img alt="success" src="img/success.gif"/></td>
            </tr>
        </table>
        <p>
            To activate your Account click on the activation
            link that has been sent to your email address.
        </p>
        <br/>

    </div>

    <form id="registerOperator" action="">
        <span class="headlineBig">Register a new Account</span><br/><br/>
        <table border="0" cellspacing="0" cellpadding="0">

            <tr class="registeroperator">
                <td></td>
                <td></td>
                <td class="red" id="register-error"></td>
            </tr>
            <tr class="registeroperator">
                <td>User Name</td>
                <td><input id="operatorId" onblur="checkOperatorId()" name="operatorId" type="text" size="40"/></td>
                <td class="red" id="fieldStatus-operatorId"></td>
            </tr>
            <tr class="registeroperator">
                <td>Password</td>
                <td><input id="password" name="password" type="password" size="40"/>&nbsp;(min. 5 chars)</td>
                <td class="red" id="fieldStatus-password"></td>
            </tr>
            <tr class="registeroperator">
                <td>Password Confirmation</td>
                <td><input id="passwordConfirm" name="passwordConfirm" type="password" size="40"/></td>
                <td class="red" id="fieldStatus-passwordConfirm"></td>
            </tr>
            <tr class="registeroperator">
                <td>First Name</td>
                <td><input id="firstName" name="firstName" type="text" size="40"/></td>
                <td class="red" id="fieldStatus-firstName"></td>
            </tr>
            <tr class="registeroperator">
                <td>Last Name</td>
                <td><input id="lastName" name="lastName" type="text" size="40"/></td>
                <td class="red" id="fieldStatus-lastName"></td>
            </tr>
            <tr class="registeroperator">
                <td>eMail</td>
                <td><input id="email" name="email" type="text" size="40"/></td>
                <td class="red" id="fieldStatus-email"></td>
            </tr>
            <tr class="registeroperator">
                <td>Phone</td>
                <td><input id="phone" name="phone" type="text" size="40"/>&nbsp;(optional)</td>
                <td></td>
            </tr>
            <tr class="registeroperator">
                <td>Company Name</td>
                <td><input id="company" name="company" type="text" size="40"/>&nbsp;(optional)</td>
                <td></td>
            </tr>
            <tr class="registeroperator">
                <td>Address</td>
                <td><input id="address" name="address" type="text" size="40"/>&nbsp;(optional)</td>
                <td></td>
            </tr>
            <!--  TODO:DM AntiRobot: bild einbauen, wo mann text rauslesen muss und diesen dann in ein text feld eingeben -->
            <tr class="registeroperator">
                <td>I agree to the Terms of Service</td>
                <td colspan="2">
                    <input type="checkbox" id="tos" name="tos" value="y"/>
                </td>
            </tr>
            <tr>
                <td/>
                <td class="red" id="fieldStatus-tos">&nbsp;</td>
                <td/>
            </tr>
        </table>
        <div id="fixedbutton">
            <a href="#" onClick="registerNewUser()"><img alt="register" BORDER=0 src="img/register.png"/></a>
        </div>
    </form>
</div>
<div class="body" style="height: 210px;" id="agbDiv">
    <span class="headline">Terms of Service</span><br/>
    <textarea cols="105" rows="10" id="terms">easyrec's Terms of Service, Oct 9, 2008

        API LICENSE AGREEMENT

        THIS IS A LEGALLY BINDING AGREEMENT. PLEASE READ IT CAREFULLY. BY CLICKING ACCEPT BELOW, YOU AGREE TO ABIDE BY
        THE TERMS AND CONDITIONS. IF YOU HAVE ANY QUESTIONS OR COMMENTS TO THIS AGREEMENT, PLEASE CONSULT YOUR ATTORNEY.
        YOU MAY ALSO CONTACT US AT sat@researchstudio.com, +43 (1) 904 21 65 - 333
        PRIOR TO ACCEPTING THESE TERMS.

        1. GRANT OF LICENSE - Subject to your ("Licensee's") full compliance with all of the terms and conditions of
        this API Agreement ("Agreement"), The easyrec, a product of Research Studios Austria Forschungsgesellschaft mbH
        (Studio Smart Agent Technologies) Thurngasse 8/2/16 A-1090 Vienna grants Licensee a non-exclusive, revocable,
        nonsublicensable, nontransferable license to use the following easyrec's application program interfaces and
        other materials provided by easyrec and the results and proceeds thereof (collectively, "APIs"): (a) "REST
        Webservices," The easyrec's recommendation application and the results and proceeds thereof, solely to develop
        consumer-facing web-based applications a single public website. "PLUGINS," The easyrec's plugins for open source
        web applications and the results and proceeds thereof (including xml files and all other renderings of the
        service) to develop, reproduce and distribute non-commercial applications that interoperate with.

        For the avoidance of doubt, display/distribution of any such program on a website with third party advertising
        is a commercial purpose not permitted by this license.

        Licensee may not install or use the APIs for any other purpose without easyrec's prior written consent. Licensee
        shall not use the APIs in connection with or to promote any products, services, or materials that constitute,
        promote or are used primarily for the purpose of illegal activities or activities in conflict with established
        industry norms (to be determined at easyrec's sole reasonable discretion). Use of the APIs for purposes that
        support, encourage or induce copyright infringement is strictly prohibited.

        1. ATTRIBUTION REQUIREMENTS. Licensee must display attribution in and on all embodiments and uses of the APIs,
        including, but not limited to Licensee's web site in a manner consistent with the following guidelines: All uses
        of the APIs must prominently display the easyrec Logo available at http://easyrec.researchstudio.at/logo.jpg
        along with a link to http://easyrec.researchstudio.at using the standard linking format on each page of such
        website displaying data provided by the APIs.

        2. OTHER RESTRICTIONS - Except as expressly and unambiguously authorized under this Agreement, Licensee may not
        (i) copy, rent, lease, sell, transfer, assign, sublicense, disassemble, reverse engineer or decompile (except to
        the limited extent expressly authorized by applicable statutory law), modify, create derivative works from, or
        alter any part of the APIs, (ii) crawl, spider, index or in any non-transitory manner store or cache information
        obtained from the APIs or (ii) otherwise use the APIs on behalf of any third party. EasyRec reserves the right
        to make any and all alterations to the APIs and applicable service levels. This Agreement does not include any
        right for Licensee to use any trademark, service mark, trade name or any other mark of easyrec or any other
        party or licensor except in accordance with attribution requirements herein. No rights or licenses are granted
        except as expressly and unambiguously set forth herein. If Licensee violates any of the foregoing restrictions,
        Licensee agrees that, as liquidated damages, easyrec shall be entitled to 100% of any and all revenue or other
        consideration earned and/or received by Licensee in connection with any such violation, without limiting other
        rights and remedies available. Licensee hereby agrees to make all assignments necessary to accomplish the
        foregoing.

        3. PROPRIETARY RIGHTS - As between easyrec and Licensee, the APIs and all intellectual property rights in and to
        the APIs are and shall at all times remain the sole and exclusive property of easyrec and are protected by
        applicable intellectual property laws and treaties. Licensee acknowledges that all recommendation data
        (including, without limitation, similar artists, recommended artists) provided by the APIs are the sole property
        of easyrec.

        4. LICENSE TO easyrec. Licensee hereby grants to easyrec the non-exclusive right and license to display, promote
        and link to all websites using the APIs, provided that easyrec shall (i) give Licensee notice of such
        display/distribution (ii) attribute the work to Licensee and include a url or other contact information for
        Licensee as reasonably determined by easyrec in its sole discretion. Licensee further grants to easyrec an
        irrevocable, non-exclusive license to any and all data provided by Licensee to easyrec in connection with
        Licensee's use of the APIs, including without limitation, any user data, text relating to Licensee's music
        catalog and/or website and any sound recordings, musical compositions or other copyrighted works. Licensee
        represents and warrants that all such data provided by Licensee to easyrec shall not violate any third party
        rights.

        5. WARRANTY DISCLAIMER - THE APIs ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. EXCEPT TO THE EXTENT
        REQUIRED BY APPLICABLE LAW, easyrec AND ITS VENDORS EACH DISCLAIM ALL WARRANTIES, WHETHER EXPRESS, IMPLIED OR
        STATUTORY, REGARDING THE APIs, INCLUDING WITHOUT LIMITATION ANY AND ALL IMPLIED WARRANTIES OF MERCHANTABILITY,
        ACCURACY, RESULTS OF USE, RELIABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, INTERFERENCE WITH QUIET
        ENJOYMENT, AND NON-INFRINGEMENT OF THIRD-PARTY RIGHTS. FURTHER, easyrec DISCLAIMS ANY WARRANTY THAT LICENSEE'S
        USE OF THE APIs WILL BE UNINTERRUPTED OR ERROR FREE.

        6. SUPPORT AND UPGRADES - This Agreement does not entitle Licensee to any support for the APIs, unless Licensee
        makes separate arrangements with easyrec in writing and meets all obligations under such separate arrangements.
        Any such support provided by easyrec shall be subject to the terms of this Agreement.

        7. LIABILITY LIMITATION - REGARDLESS OF WHETHER ANY REMEDY SET FORTH HEREIN FAILS OF ITS ESSENTIAL PURPOSE OR
        OTHERWISE, AND EXCEPT FOR BODILY INJURY, IN NO EVENT WILL easyrec OR ITS VENDORS, BE LIABLE TO LICENSEE OR TO
        ANY THIRD PARTY UNDER ANY TORT, CONTRACT, NEGLIGENCE, STRICT LIABILITY OR OTHER LEGAL OR EQUITABLE THEORY FOR
        ANY LOST PROFITS, LOST OR CORRUPTED DATA, COMPUTER FAILURE OR MALFUNCTION, INTERRUPTION OF BUSINESS, OR OTHER
        SPECIAL, INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES OF ANY KIND ARISING OUT OF THE USE OR INABILITY TO USE
        THE APIs, EVEN IF easyrec HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGES AND WHETHER OR NOT SUCH
        LOSS OR DAMAGES ARE FORESEEABLE. ANY CLAIM ARISING OUT OF OR RELATING TO THIS AGREEMENT MUST BE BROUGHT WITHIN
        ONE (1) YEAR AFTER THE OCCURRENCE OF THE EVENT GIVING RISE TO SUCH CLAIM. IN ADDITION, easyrec DISCLAIMS ALL
        LIABILITY OF ANY KIND OF easyrec'S VENDORS.

        8. INDEMNITY - Licensee agrees that easyrec shall have no liability whatsoever for any use Licensee makes of the
        APIs. Licensee shall indemnify and hold harmless easyrec from any and all claims, damages, liabilities, costs
        and fees (including reasonable attorneys' fees) arising from Licensee's use of the APIs.

        9. TERM AND TERMINATION - This Agreement shall continue until terminated as set forth in this Section. Either
        party may terminate this Agreement at any time, for any reason, or for no reason including, but not limited to,
        if Licensee violates any provision of this Agreement. Any termination of this Agreement shall also terminate the
        license granted hereunder. Upon termination of this Agreement for any reason, Licensee shall destroy and remove
        from all computers, hard drives, networks, and other storage media all copies of the APIs, and shall so certify
        to easyrec that such actions have occurred. EasyRec shall have the right to inspect and audit Licensee's
        facilities to confirm the foregoing and to conduct an audit to confirm any use of the APIs are non-commercial.
        Sections 6 through 11 and all accrued rights to payment shall survive termination of this Agreement.

        10. GOVERNMENT USE - If Licensee is part of an agency, department, or other entity of the United States
        Government ("Government"), the use, duplication, reproduction, release, modification, disclosure or transfer of
        the APIs are restricted in accordance with the Federal Acquisition Regulations as applied to civilian agencies
        and the Defense Federal Acquisition Regulation Supplement as applied to military agencies. The APIs are a
        "commercial item," "commercial computer software" and "commercial computer software documentation." In
        accordance with such provisions, any use of the APIs by the Government shall be governed solely by the terms of
        this Agreement.

        11. EXPORT CONTROLS - Licensee shall comply with all export laws and restrictions and regulations of the
        Department of Commerce, the United States Department of Treasury Office of Foreign Assets Control ("OFAC"), or
        other United States or foreign agency or authority, and Licensee shall not export, or allow the export or
        re-export of the APIs in violation of any such restrictions, laws or regulations. By downloading or using the
        APIs, Licensee agrees to the foregoing and represents and warrants that Licensee is not located in, under the
        control of, or a national or resident of any restricted country.

        12. MISCELLANEOUS - This Agreement constitutes the entire agreement between Licensee and easyrec pertaining to
        the subject matter hereof, and supersedes any and all written or oral agreements with respect to such subject
        matter. This Agreement, and any disputes arising from or relating to the interpretation thereof, shall be
        governed by and construed under Massachusetts law as such law applies to agreements between Massachusetts
        residents entered into and to be performed within Massachusetts by two residents thereof and without reference
        to its conflict of laws principles or the United Nations Conventions for the International Sale of Goods. Except
        to the extent otherwise determined by easyrec, any action or proceeding arising from or relating to this
        Agreement must be brought in the Federal District of Massachusetts or in state court in Massachusetts and each
        party irrevocably submits to the jurisdiction and venue of any such court in any such action or proceeding,
        waiving any objections to such venue, including forum non conveniens. The prevailing party in any action arising
        out of this Agreement shall be entitled to an award of its costs and attorneys' fees. This Agreement may be
        amended only by a writing executed by easyrec. If any provision of this Agreement is held to be unenforceable
        for any reason, such provision shall be reformed only to the extent necessary to make it enforceable. The
        failure of easyrec to act with respect to a breach of this Agreement by Licensee or others does not constitute a
        waiver and shall not limit easyrec's rights with respect to such breach or any subsequent breaches. This
        Agreement is personal to Licensee and may not be assigned or transferred for any reason whatsoever (including,
        without limitation, by operation of law, merger, reorganization, or as a result of an acquisition or change of
        control involving Licensee) without easyrec's prior written consent and any action or conduct in violation of
        the foregoing shall be void and without effect. EasyRec expressly reserves the right to assign this Agreement
        and to delegate any of its obligations hereunder.

        By clicking the "Register" button, you signal your acceptance of the Terms of Service governing the use of
        easyrec Developer Network's Web Services and APIs. You will then receive your Developer Key.</textarea>

</div>