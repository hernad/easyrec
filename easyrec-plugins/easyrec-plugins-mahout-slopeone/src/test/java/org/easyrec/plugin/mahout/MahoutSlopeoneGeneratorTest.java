/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.easyrec.mahout.model.EasyrecDataModel;
import org.easyrec.mahout.store.MahoutDataModelMappingDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;


@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/easyrec-mahoutSlopeoneGenerator-test.xml"})
@DataSet("/dbunit/mahoutSlopeoneGeneratorTest.xml")
public class MahoutSlopeoneGeneratorTest {
    @SpringBeanByName
    protected MahoutDataModelMappingDAO mahoutDataModelMappingDAO;

    private static int TENANT_ID = 1;
    private static int RATE_ACTION_TYPE_ID = 2;

    @Test
    public void mahoutSlopeoneGeneratorTest_testRecommender() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, true, mahoutDataModelMappingDAO);
        Recommender recommender = new SlopeOneRecommender(easyrecDataModel);

        Assert.assertEquals(3, recommender.recommend(3, 1).get(0).getItemID());
        Assert.assertEquals(10, (int) recommender.recommend(3, 1).get(0).getValue());
    }

}
