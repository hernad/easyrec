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
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
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
        "spring/easyrec-mahoutBooleanGenerator-test.xml"})
@DataSet("/dbunit/mahoutBooleanGeneratorTest.xml")
public class MahoutBooleanGeneratorTest {
    @SpringBeanByName
    protected MahoutDataModelMappingDAO mahoutDataModelMappingDAO;

    private static int TENANT_ID = 1;
    private static int BUY_ACTION_TYPE_ID = 3;

    @Test
    public void mahoutSlopeoneGeneratorTest_testBoolRecommender() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, false, mahoutDataModelMappingDAO);
        /*TanimotoCoefficientSimilarity is intended for "binary" data sets  where a user either expresses a generic "yes" preference for an item or has no preference.*/
        UserSimilarity userSimilarity = new TanimotoCoefficientSimilarity(easyrecDataModel);

        /*ThresholdUserNeighborhood is preferred in situations where we go in for a  similarity measure between neighbors and not any number*/
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1d, userSimilarity, easyrecDataModel);

        /*GenericBooleanPrefUserBasedRecommender is appropriate for use when no notion of preference value exists in the data. */
        Recommender recommender = new GenericBooleanPrefUserBasedRecommender(easyrecDataModel, neighborhood, userSimilarity);

        Assert.assertEquals(30, recommender.recommend(3, 1).get(0).getItemID());
        Assert.assertEquals(1,(int) recommender.recommend(3, 1).get(0).getValue());
    }

}
