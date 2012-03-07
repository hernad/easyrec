/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.service.core.impl;


import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.TenantVO;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author szavrel
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/core/TenantService_AllInOne.xml",
        "spring/core/idMapping.xml",
        "spring/core/service/ItemAssocService.xml",
        "spring/core/dao/ProfileDAO.xml",
        "spring/core/dao/itemAssocDAO.xml",
        "spring/core/service/ClusterStrategies.xml",
        "spring/core/service/ClusterService.xml"})
@DataSet("/dbunit/core/service/cluster.xml")
public class ClusterServiceTest {

    private static final Log logger = LogFactory.getLog(ClusterServiceTest.class);

    private final TenantVO TENANT_EASYREC = new TenantVO(1, "cluster", "cluster tenant", 1, 10, 5.5d);

    @SpringBeanByName
    private ClusterServiceImpl clusterService;

    @Before
    public void before() {
         clusterService.afterPropertiesSet();
    }
    
    @Test
    public void testGetClustersForTenant() {
//        before();

        Tree <ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();

        logger.info("name: " + root.getName());
        logger.info("descr: " + root.getDescription());
        logger.info("id: " + root.getItem().getItem());
        logger.info("tenant: " + root.getItem().getTenant());
        logger.info("type: " + root.getItem().getType());

//        ClusterVO child = new ClusterVO(root.getItem().getTenant(),1000, root.getItem().getType(),"Cluster1", "The first Cluster!");
//        try {
//        clusterService.addCluster(1, "testCluster","This is a testCluster", ClusterService.ROOT);
//        clusterService.addCluster(1, "testCluster2","This is testCluster2", ClusterService.ROOT);
//        clusterService.addCluster(1, "testCluster3","This is testCluster3", ClusterService.ROOT);
//
//        clusterService.addCluster(1, "testCluster4","This is testCluster4", "testCluster");
//        
//        } catch (ClusterException ce) {
//            logger.error(ce);
//        }
//        
//        logger.info(clusters.containsVertex(child));
//        logger.info(clusters.getChildCount(root));
//        logger.info(clusters.getChildCount(child));
//        logger.info(clusters.getParent(child).getName());
//        Collection<ClusterVO> children = clusters.getChildren(root);
//        for (ClusterVO clusterVO : children) {
//            logger.info(clusterVO.getName());
//        }
//
//
//        logger.info(clusters.toString());

    }
    
    @Test
    public void testLoadClusters() {
         DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
         ClusterVO root = clusters.getRoot();
         printCluster(clusters, root);
         assertThat(clusters.getHeight(), is(2));
         assertThat(clusters.getChildCount(root), is(2));
         assertThat(clusters.getVertexCount(), is(5));

    }

    @Test
    public void testRemoveCluster() {
         Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
         ClusterVO root = clusters.getRoot();
         printCluster(clusters, root);
         assertThat(clusters.getHeight(), is(2));
         assertThat(clusters.getChildCount(root), is(2));
         assertThat(clusters.getVertexCount(), is(5));
         clusterService.removeCluster(1, "CLUSTER1");
         assertThat(clusters.getHeight(), is(1));
         assertThat(clusters.getChildCount(root), is(1));
         assertThat(clusters.getVertexCount(), is(2));
         printCluster(clusters, root);
    }

    @Test
    public void testMoveCluster() {
         Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
         ClusterVO root = clusters.getRoot();
         printCluster(clusters, root);
         assertThat(clusters.getHeight(), is(2));
         assertThat(clusters.getChildCount(root), is(2));
         assertThat(clusters.getVertexCount(), is(5));
         clusterService.moveCluster(1, "CLUSTER1", "CLUSTER2");
         assertThat(clusters.getHeight(), is(3));
         assertThat(clusters.getChildCount(root), is(1));
         assertThat(clusters.getVertexCount(), is(5));
         printCluster(clusters, root);
    }


    @Test
    public void testGetSiblings() {
         DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
         List<ClusterVO> siblings = clusterService.getSiblings(1, "CLUSTER1");
         for (ClusterVO clusterVO : siblings) {
            logger.info("Sibling of CLUSTER1: " + clusterVO.getName());
         }
         printCluster(clusters,clusters.getRoot());
         assertThat(siblings.size(), is(1));
         assertThat(siblings.get(0).getName(), is("CLUSTER2"));
    }

    @Test
    public void testRenameCluster() {
         Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
         ClusterVO root = clusters.getRoot();
         printCluster(clusters, root);
         assertThat(clusters.getHeight(), is(2));
         assertThat(clusters.getChildCount(root), is(2));
         assertThat(clusters.getVertexCount(), is(5));
        assertThat(clusterService.loadCluster(1,"CLUSTER3"), IsNull.nullValue());
         try {
            clusterService.renameCluster(1, "CLUSTER1", "CLUSTER3");
         } catch (ClusterException ce) {
             logger.info(ce);
         }
         printCluster(clusters, root);
         assertThat(clusters.getHeight(), is(2));
         assertThat(clusters.getChildCount(root), is(2));
         assertThat(clusters.getVertexCount(), is(5));
         assertThat(clusterService.loadCluster(1,"CLUSTER3"), IsNull.notNullValue());
    }
    
    @Test
    public void addItemToCluster_shouldAddItem_removeItemFromCluster_shouldRemoveItem() throws ClusterException {
        Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();

        ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(TENANT_EASYREC.getId(), 1,
                1);

        int rowsModified = clusterService.addItemToCluster(root, item);

        assertThat(rowsModified, is(1));

        rowsModified = clusterService.removeItemFromCluster(root, item);

        assertThat(rowsModified, is(1));
    }

    @Test(expected =  ClusterException.class)
    public void addItemToCluster_shouldThrowWhenNotAdded() throws ClusterException {
        Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();
        ItemVO<Integer, Integer> newItem = new ItemVO<Integer, Integer>(TENANT_EASYREC.getId(), 1, 1);

        int rowsModified = clusterService.addItemToCluster(root, newItem);

        assertThat(rowsModified, is(1));

        // this should throw
        rowsModified = clusterService.addItemToCluster(root, newItem);
    }

    @Test(expected = ClusterException.class)
    public void removeItemFromCluster_shouldThrowWhenNotRemoved() throws ClusterException {
        Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();
        ItemVO<Integer, Integer> newItem = new ItemVO<Integer, Integer>(TENANT_EASYREC.getId(), 1, 1);

        clusterService.removeItemFromCluster(root, newItem);
    }

    @Test
    public void getItemsOfCluster_shouldReturnItems() {
        Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();

        List<ItemVO<Integer, Integer>> items = clusterService.getItemsOfCluster(root);

        assertThat(items.size(), is(2));
    }

    @Test
    public void getItemsOfCluster_shouldReturnItems_withFallback() {
        ClusterVO subcluster2 = clusterService.loadCluster(1, "SUBCLUSTER2");

        List<ItemVO<Integer, Integer>> items = clusterService.getItemsOfCluster(subcluster2, null, true, 6);
        assertThat(items.size(), is(6));

        items = clusterService.getItemsOfCluster(subcluster2, null, true, 2);
        assertThat(items.size(), is(2));

        items = clusterService.getItemsOfCluster(subcluster2, null, true, 3);
        assertThat(items.size(), is(3));
    }

    @Test
    public void getItemsOfCluster_shouldReturnItems_withRandomStrategy() {
        ClusterVO cluster = clusterService.loadCluster(1, "SUBCLUSTER2");

        //fetch same number of items with random strategy twice and see if the results differ. Do that 1000 times,
        // abort when the first difference is found
        boolean foundADifference = false;
        OUTER:
        for (int i = 0; i < 1000; i++) {
            int count = 9;
            List<ItemVO<Integer, Integer>> items = clusterService.getItemsOfCluster(cluster, "RANDOM", true, count);
            assertThat(items.size(), is(count));
            List<ItemVO<Integer, Integer>> items2 = clusterService.getItemsOfCluster(cluster, "RANDOM", true, count);
            assertThat(items.size(), is(count));
            for (ItemVO<Integer, Integer> item : items) {
                if (!items2.contains(item)) {
                    foundADifference = true;
                    break OUTER;
                }
            }
        }
        assertTrue(foundADifference);

        List<ItemVO<Integer, Integer>> items = clusterService.getItemsOfCluster(cluster, "RANDOM", true, 2);
        assertThat(items.size(), is(2));

        items = clusterService.getItemsOfCluster(cluster, "RANDOM", true, 3);
        assertThat(items.size(), is(3));
    }

    private void printCluster(Tree<ClusterVO, ItemAssocVO<Integer,Integer>> tree, ClusterVO vertex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tree.getDepth(vertex); i++) {
            sb.append("  ");
        }
        logger.info(sb.toString() + vertex.getName());
        Collection<ClusterVO> children = tree.getChildren(vertex);
        for (ClusterVO clusterVO : children) {
            printCluster(tree, clusterVO);
        }
    }


    @Test
    public void testAddCluster() {
        //clusterService
    }

    @Test
    public void testGetClustersForItem() throws ClusterException {
        Tree<ClusterVO, ItemAssocVO<Integer,Integer>> clusters = clusterService.getClustersForTenant(1);
        ClusterVO root = clusters.getRoot();
        ItemVO<Integer, Integer> newItem = new ItemVO<Integer, Integer>(TENANT_EASYREC.getId(), 1, 1);

        int rowsModified = clusterService.addItemToCluster(root, newItem);
        
        List<ClusterVO> clusterList = clusterService.getClustersForItem(newItem);
        for (ClusterVO clusterVO : clusterList) {
            logger.info(clusterVO.getName());
        }
    }
    
}
