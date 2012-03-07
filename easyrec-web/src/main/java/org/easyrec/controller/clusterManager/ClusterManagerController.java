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

package org.easyrec.controller.clusterManager;

import com.google.common.base.Strings;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONStringer;
import org.codehaus.jettison.json.JSONWriter;
import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.web.IDMappingService;
import org.easyrec.service.web.ViewInitializationService;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This Controller is used to create the basic functionality of the cluster manager
 * it handles create, displaying and deleting of clusters and adding and displaying items
 * of the cluster.
 *
 * @author dmann
 */
public class ClusterManagerController extends MultiActionController {

    private ClusterService clusterService;
    private RemoteTenantDAO remoteTenantDAO;
    private ItemDAO itemDAO;
    private ItemTypeDAO itemTypeDAO;
    private IDMappingDAO idMappingDAO;
    private IDMappingService idMappingService;
    private ViewInitializationService viewInitializationService;

    public ClusterManagerController(ClusterService clusterService, RemoteTenantDAO remoteTenantDAO, ItemDAO itemDAO,
                                    ItemTypeDAO itemTypeDAO, IDMappingDAO idMappingDAO,IDMappingService idMappingService, ViewInitializationService viewInitializationService) {
        this.clusterService = clusterService;
        this.remoteTenantDAO = remoteTenantDAO;
        this.itemDAO = itemDAO;
        this.itemTypeDAO = itemTypeDAO;
        this.idMappingDAO = idMappingDAO;
        this.idMappingService = idMappingService;
        this.viewInitializationService = viewInitializationService;
    }

    /*
    * This view is the cluster Manager overview. it combines the other views to the one you will se
    * at the webapp at the Cluster Manager section. The View gets loaded via a JQUERY ajax load and will
    * be displayed in a Modal Dialog.
    */
    public ModelAndView clustermanager(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/clustermanager");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        if (remoteTenant != null) {
            Set<String> itemTypes = itemTypeDAO.getTypes(remoteTenant.getId(), true);
            mav.addObject("availableItemTypes", itemTypes);
        }

        return mav;
    }

    /*
    * This view shows a basic help text when you double click the CLUSTERS cluster in the tree
    * (the one with the home symbol).
    */
    public ModelAndView help(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/help");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);
        return mav;
    }

    /*
    * This view show the user the user the name of the cluster and includes the clusteritemtable view
    * via AJAX so the user can view the contents of a cluster.
    * This view gets loaded when a user double clicks a cluster name in the cluster explorer.
    */
    public ModelAndView viewitems(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/viewitems");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");

        ClusterVO cluster = clusterService.loadCluster(remoteTenant.getId(), clusterId);

        mav.addObject("cluster", cluster);
        return mav;
    }

    /*
     * This view adds items to a given cluster and returns error messages if the Cluster service
     * cant save the item to the cluster. It takes String ids as a Parameter and resolves them to
     * Integer Id's for the clusterService which cant handle String Id's becouse it is in the easyrec
     * core Project.
     */
    public ModelAndView additemtocluster(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String itemId = ServletUtils.getSafeParameter(request, "itemId", "");
        String itemType = ServletUtils.getSafeParameter(request, "itemType", "");

        Integer itemTypeId = itemTypeDAO.getIdOfType(remoteTenant.getId(), itemType);
        Integer itemIdInt = idMappingDAO.lookup(itemId);

        try {
            clusterService.addItemToCluster(remoteTenant.getId(), clusterId, itemIdInt, itemTypeId);
        } catch (ClusterException e) {
            // This exception can be thrown when the given item already is in this cluster
            mav.addObject("text", e.getMessage());
            return mav;
        }

        return mav;
    }

    public ModelAndView removeitemfromcluster(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String itemId = ServletUtils.getSafeParameter(request, "itemId", "");
        String itemType = ServletUtils.getSafeParameter(request, "itemType", "");

        Integer itemTypeId = itemTypeDAO.getIdOfType(remoteTenant.getId(), itemType);
        Integer itemIdInt = idMappingDAO.lookup(itemId);

        try {
            clusterService.removeItemFromCluster(remoteTenant.getId(), clusterId, itemIdInt, itemTypeId);
        } catch (ClusterException e) {
            // This exception can be thrown when the given item already is in this cluster
            mav.addObject("text", e.getMessage());
            return mav;
        }

        return mav;
    }

    /*
    * This view updates the cluster information when the user changes it via the cluster edit form.
    */
    public ModelAndView updatecluster(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        // I used a hidden string in the web form which contains the original ID (for the rename usecase)
        String originalClusterId = ServletUtils.getSafeParameter(request, "originalClusterId", "");
        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String clusterDescription = ServletUtils.getSafeParameter(request, "clusterDescription", "");

        // here i use the original cluster id to load the cluster and update the object values with the
        // values from the webform.
        ClusterVO cluster = clusterService.loadCluster(remoteTenant.getId(), originalClusterId);
        cluster.setName(clusterId);
        cluster.setDescription(clusterDescription);

        try {
            clusterService.updateClusterDescription(remoteTenant.getId(), originalClusterId, clusterDescription);
        } catch (Exception e) {
            mav.addObject("text", e.getMessage());
            return mav;
        }

        if (!originalClusterId.equals(clusterId)) {
            try {
                clusterService.renameCluster(remoteTenant.getId(), originalClusterId, clusterId);
            } catch (Exception e) {
                mav.addObject("text", e.getMessage());
                return mav;
            }
        }
        return mav;
    }

    /*
    * This view shows the user a table which contains all items of a cluster(which are stored as ITEM ASSOC's)
    * The table is rendered via the DISPLAY TAGLIB and handles sorting and paging on his own.
    */
    public ModelAndView clusteritemtable(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/clusteritemtable");
        final RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);
        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");

        ClusterVO cluster = clusterService.loadCluster(remoteTenant.getId(), clusterId);
        List<ItemVO<Integer, Integer>> items = clusterService.getItemsOfCluster(cluster);

        mav.addObject("cluster", cluster);
        mav.addObject("items", idMappingService.mapListOfItemVOs(items,remoteTenant));
        return mav;
    }

    /*
     * This view stores a new parent for the given Node, its used via an Ajax call from the cluster manager
     */
    public ModelAndView changeclusterparent(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String newParent = ServletUtils.getSafeParameter(request, "newParent", "");


        if (remoteTenant == null) {
            logger.warn("no tenantId supplied");
            return mav;
        }

        if (clusterId.length() == 0) {
            logger.warn("no clusterId supplied");
            return mav;
        }

        clusterService.moveCluster(remoteTenant.getId(), clusterId, newParent);
        return mav;
    }

    /*
    *  This view gets called after a user created a cluster within the JS tree.
    *  It saves the new cluster to the database.
    */
    public ModelAndView createcluster(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String parent = ServletUtils.getSafeParameter(request, "parent", "");


        if (clusterId.contains(" ")) {
            mav.addObject("text", "You cannot use spaces in cluster names, sorry.");
            return mav;
        }

        if (remoteTenant == null) {
            logger.warn("no tenantId supplied");
            return mav;
        }

        if (clusterId.length() == 0) {
            logger.warn("no clusterId supplied");
            return mav;
        }

        try {
            clusterService.addCluster(remoteTenant.getId(), clusterId, "", parent);
        } catch (ClusterException e) {
            logger.warn("error occurred when adding cluster", e);

            mav.addObject("text", e.getMessage());
            return mav;
        }
        return mav;
    }

    /*
    *  This view gets called after a user clicked on the delete cluster button.
    */
    public ModelAndView deletecluster(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/message");
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String clusterId = ServletUtils.getSafeParameter(request, "clusterId", "");
        String parent = ServletUtils.getSafeParameter(request, "parent", "");

        if (clusterId.equals("CLUSTERS")) {
            mav.addObject("text", "You cannot delete the main container.");
            return mav;
        }

        if (clusterId.equals("")) {
            mav.addObject("text", "No Cluster selected to delete.");
            return mav;
        }

        if (remoteTenant == null) {
            logger.warn("no tenantId supplied");
            return mav;
        }

        if (clusterId.length() == 0) {
            logger.warn("no clusterId supplied");
            return mav;
        }

        clusterService.removeCluster(remoteTenant.getId(), clusterId);
        return mav;
    }

    /*
    * This view returns the contents of the tree view as an JSON string which will be requested at the loading
    * of the cluster manager and when the user creates a new cluster.
    */
    public ModelAndView loadtreedata(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/loadtreedata");

        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        if (remoteTenant == null) {
            logger.warn("no tenantId supplied");
            return mav;
        }

        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>>
                clusterTree = clusterService.getClustersForTenant(remoteTenant.getId());

        JsonTreeGraphWriter graphWriter = new JsonTreeGraphWriter();
        JSONWriter jsonWriter = new JSONStringer();

        try {
            graphWriter.save(clusterTree, jsonWriter);
        } catch (JSONException e) {
            logger.warn("failed to serialize cluster tree to JSON", e);
            return mav;
        }

        mav.addObject("treeJsonData", jsonWriter.toString());

        return mav;
    }

    /**
     * convert a JUNG tree to a format that can be handled by the jsonTree library
     *
     * @author pmarschik
     */
    private static class JsonTreeGraphWriter {
        public void save(Tree<ClusterVO, ItemAssocVO<Integer,Integer>> tree,
                         JSONWriter writer) throws JSONException {
            ClusterVO root = tree.getRoot();

            writeNode(tree, root, writer, root);
        }

        private void writeNode(Tree<ClusterVO, ItemAssocVO<Integer,Integer>> tree,
                               ClusterVO node, JSONWriter writer, ClusterVO root) throws JSONException {
            writer.object()
                    .key("data").value(node.getName())
                    .key("attr")
                    .object()
                    .key("id")
                    .value(node.getName())
                    .key("title")
                    .value(node.getDescription() == null ? node.getName() : node.getDescription());

            if (node == root)
                writer.key("rel").value("root");

            writer.endObject();

            if (!Strings.isNullOrEmpty(node.getDescription()))
                writer.key("description").value(node.getDescription());

            Collection<ClusterVO> children = tree.getChildren(node);

            if (children.size() > 0) {
                writer.key("children")
                        .array();

                for (ClusterVO child : children) {
                    writeNode(tree, child, writer, root);
                }

                writer.endArray();
            }

            writer.endObject();
        }
    }
}
