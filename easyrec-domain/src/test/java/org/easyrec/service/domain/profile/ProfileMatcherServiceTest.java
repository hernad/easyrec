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
package org.easyrec.service.domain.profile;

/**
 *
 * @author szavrel
 */

import org.easyrec.model.core.ItemVO;
import org.easyrec.service.domain.profile.impl.ProfileMatcherServiceImpl;
import org.easyrec.service.domain.profile.impl.ProfileServiceImpl;
import org.easyrec.store.dao.core.types.ProfiledItemTypeDAO;
import org.easyrec.store.dao.domain.TypedProfileDAO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.Set;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "/spring/easyrecDataSource.xml",
        "/spring/core/dao/ProfileDAO.xml",
        "/spring/core/dao/types/ActionTypeDAO.xml",
        "/spring/core/dao/types/AggregateTypeDAO.xml",
        "/spring/core/dao/types/AssocTypeDAO.xml",
        "/spring/core/dao/types/ItemTypeDAO.xml",
        "/spring/core/dao/types/SourceTypeDAO.xml",
        "/spring/core/dao/types/ViewTypeDAO.xml",
        "/spring/core/dao/types/ProfiledItemTypeDAO.xml",
        "/spring/domain/service/TypeMappingService.xml",
        "/spring/domain/dao/TypedProfileDAO.xml"})
@DataSet(ProfileMatcherServiceTest.DATA_FILENAME)
public class ProfileMatcherServiceTest {

    // constants
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/domain/profile.xml";

    private final ItemVO<Integer, String> ITEM1 = new ItemVO<Integer, String>(6, 1, "MOVIE");
    private final ItemVO<Integer, String> ITEM2 = new ItemVO<Integer, String>(6, 2, "MOVIE");

    private final String profile1 = "<movie><movieId>1</movieId><hans></hans><title>Toy Story (1995)</title><relDate>1995-01-01</relDate><IMDBUrl>http://us.imdb.com/M/title-exact?Toy%20Story%20(1995)</IMDBUrl><genre>Animation</genre><genre>Childrens</genre><genre>Comedy</genre></movie>";

    private ProfileMatcherService profileMatcherService;
    private ProfileService profileService;

    @SpringBeanByName
    private ProfiledItemTypeDAO profiledItemTypeDAO;

    @SpringBeanByName
    private TypedProfileDAO typedProfileDAO;

    @Before
    public void setUp() throws Exception {
        this.profileService = new ProfileServiceImpl(typedProfileDAO, profiledItemTypeDAO);
        this.profileMatcherService = new ProfileMatcherServiceImpl(profiledItemTypeDAO, profileService);
    }

    //    @Test
    //    public void testMatch()
    //    {
    //  	float f = profileMatcherService.match(ITEM1, ITEM2);
    //    	System.out.println(f);
    //    }

    //    @Test
    //    public void testInsert()
    //    {
    //        profileService.storeProfile(6, 1, "MOVIE", profile1);
    //    	//System.out.println(f);
    //    }

    //    @Test
    //    public void testinsertOrUpdateSimpleDimension()
    //    {
    //        //profileService.insertOrUpdateSimpleDimension(6, 1, "MOVIE", "movieId" , "testValue");
    //        profileService.insertOrUpdateSimpleDimension(6, 1, "MOVIE", "/movie/extension/actor" , "hudri");
    //    	//System.out.println(f);
    //    }

    //    @Test
    //    public void testinsertOrUpdateMulitDimension()
    //    {
    //        Vector<String> values = new Vector<String>();
    //        values.add("Animation");
    //        values.add("Action");
    //        values.add("Comedy");
    //        //profileService.insertOrUpdateSimpleDimension(6, 1, "MOVIE", "movieId" , "testValue");
    //        profileService.insertOrUpdateMultiDimension(6, 1, "MOVIE", "/movie/genre" , values);
    //    	//System.out.println(f);
    //    }

    @Test
    @Ignore
    public void testGetSimpleDimensionValue() {
        String temp = profileService.getSimpleDimensionValue(6, 1, "MOVIE", "/movie/title");
        System.out.println("TITLE: " + temp);
    }

    @Test
    @Ignore
    public void testGetMultiDimensionValue() {
        Set<String> temp = profileService.getMultiDimensionValue(6, 1, "MOVIE", "/movie/genre");
        for (String s : temp) {
            System.out.println("GENRE: " + s);
        }
    }
}
