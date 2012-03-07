/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.utils.io.autoimport;

/**
 * Interface for the automatic import of special sat-type CSV files.
 * Defines some methods to activate/deactivate the automatic import, as well as changing parameters (timeout, directory).
 * <p/>
 * <p><b>XML File Data Definition</b></p>
 * <p/>
 * The first line must contain the <code>type</code> of the file:
 * <ul>
 * <li>a csv comment char (<code>#</code>),</li>
 * <li>the keyword <code>type</code>,</li>
 * <li>a colon separator (<code>:</code>) and</li>
 * <li>a <code>type_identifier</code>, eg: <code>itemassoc</code></li>
 * </ul>
 * <p/>
 * The second line must contain the <code>command</code> to process:
 * <ul>
 * <li>a csv comment char (<code>#</code>),</li>
 * <li>the keyword <code>command</code>,</li>
 * <li>a colon separator (<code>:</code>) and</li>
 * <li>a <code>command_idendtifier</code>, eg: <code>insert</code> or <code>remove</code></li>
 * </ul>
 * <p/>
 * The third line must contain an attribute list:
 * <ul>
 * <li>for each attribute of the value object
 * <ul>
 * <li>a description/identifier of the attribute,</li>
 * <li>optionally</li>
 * <ul>
 * <li>an equal symbol (<code>=</code>) and</li>
 * <li>and default value for the attribute</li>
 * <li>an optional type description</li>
 * <ul>
 * <li>an openening bracket <code>(</code></li>
 * <li>a type description</li>
 * <li>a closing bracket <code>)</code></li>
 * </ul>
 * </ul>
 * <li>and finally a comma delimiter (<code>,</code>)</li>
 * </ul>
 * </li>
 * </ul>
 * <p/>
 * All following lines must contain data
 * <ul>
 * <li>for each attribute of the value object
 * <ul>
 * <li>a data value and </li>
 * <li>a comma delimiter (<code>,</code>)</li>
 * </ul>
 * </li>
 * <li>Note: the number of data values must be consistent with the number of defined attributes via the attribute list</li>
 * </ul>
 * <p/>
 * Note: When defining a default value for an attribute (via the attribute list) a value for that attribute can be omitted.
 * <p/>
 * <p>an example of an itemassoc .CSV file</p>
 * <code>
 * # type: itemassoc<br />
 * # command: insert<br />
 * tenantId,itemFromId,itemFromTypeId=3(track),assocTypeId=1(isSimilarTo),assocValue,itemToId,itemToTypeId=8(prototypeTrack),sourceTypeId=1(fe),sourceInfo=NO_CLUSTERSYS_ID,viewTypeId=2(system)<br/>
 * 1,25722,,,1.5944828711436,113321,,,,<br />
 * 1,25723,,,3.5944828711436,113321,,,,<br />
 * 2,25726,,,2.5944828711436,113321,,,,<br />
 * 1,25726,4,1,3.5944828711436,113321,4,2,abc,2<br />
 * 2,25727,,,3.5944828711436,113321,,,,<br />
 * </code>
 * <p/>
 * <br/>
 * to enable this service for your application you need to put a spring bean definition file in your <code>/src/main/resources</code> folder.
 * an example can be found in the sat-recommender project.<br/>
 * see: <a href=https://svn.researchstudio.at/satsvn/wsvn/SAT/sat-recommender/trunk/src/main/resources/spring/core/autoimport/spring.sat-recommender.AutoImportService.xml?op=file&rev=0&sc=0>spring.sat-recommender.AutoImportService.xml</a>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */
public interface AutoImportService {
    ////////////////////////////////////////////////////////////////////////////
    // constants
    // automatic import from CSV
    public static final boolean DEFAULT__OVERWRITE_DUPLICATES = true;
    public static final int DEFAULT__REPORT__BLOCK_SIZE = 1000;

    public static final long MIN_TIMEOUT = 500; // in ms (alle 0.5 secs)
    public static final long MAX_TIMEOUT = 1000 * 60 * 24 * 7; // in ms (1x pro Woche)

    ////////////////////////////////////////////////////////////////////////////
    // methods
    public boolean isActive();

    public void activate();

    public void deactivate();

    public String getDirectory();

    public void setDirectory(String directory);

    public long getTimeout();

    public void setTimeout(long timeout);
}
