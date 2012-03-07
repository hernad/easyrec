package org.easyrec.vocabulary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Constants for use in WebService (WS) classes
 *
 * @author patrick
 */
public class WS implements InitializingBean {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    public static Integer DEFAULT_NUMBER_OF_RESULTS;
    public static int MAX_NUMBER_OF_RANKING_RESULTS;
    public static int ACTION_HISTORY_DEPTH;
            
    public static final String ACTION_OTHER_USERS_ALSO_VIEWED = "otherusersalsoviewed";
    public static final String ACTION_OTHER_USERS_ALSO_BOUGHT = "otherusersalsobought";
    public static final String ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS = "itemsratedgoodbyotherusers";
    public static final String ACTION_RECOMMENDATIONS_FOR_USER = "recommendationsforuser";
    public static final String ACTION_RECOMMENDED_ITEMS = "getrecommendeditems";
    public final static String ACTION_VIEW = "view";
    public final static String ACTION_BUY = "buy";
    public final static String ACTION_RATE = "rate";
    public static final String ACTION_SENDACTION = "sendaction";
    public static final String ACTION_MOST_VIEWED = "mostvieweditems";
    public static final String ACTION_MOST_BOUGHT = "mostboughtitems";
    public static final String ACTION_MOST_RATED = "mostrateditems";
    public static final String ACTION_BEST_RATED = "bestrateditems";
    public static final String ACTION_WORST_RATED = "worstrateditems";
    public static final String ACTION_IMPORT_RULE = "importrule";
    public static final String ACTION_IMPORT_ITEM = "importitem";
    public static final String ACTION_RELATED_ITEMS = "relateditems";
    public static final String ACTION_ITEMS_OF_CLUSTER = "itemsofcluster";
    public static final String ACTION_SET_ITEM_ACTIVE = "setitemactive:";
    public static final String ACTION_ITEMTYPES = "itemtypes";
    public static final String ACTION_CLUSTERS = "clusters";
    public static final String ACTION_HISTORY = "actionhistory";
    public final static String JSON_PATH = "1.0/json";
    public final static String RESPONSE_TYPE_XML = "application/xml";
    public final static String RESPONSE_TYPE_JSON = "application/json";
    public final static String RESPONSE_TYPE_JSCRIPT = "application/javascript";

    public WS(Integer DEFAULT_NUMBER_OF_RESULTS, Integer MAX_NUMBER_OF_RANKING_RESULTS, Integer ACTION_HISTORY_DEPTH) {
        setDEFAULT_NUMBER_OF_RESULTS(DEFAULT_NUMBER_OF_RESULTS);
        setMAX_NUMBER_OF_RANKING_RESULTS(MAX_NUMBER_OF_RANKING_RESULTS);
        setACTION_HISTORY_DEPTH(ACTION_HISTORY_DEPTH);
    }

    public static void setACTION_HISTORY_DEPTH(int ACTION_HISTORY_DEPTH) {
        WS.ACTION_HISTORY_DEPTH = ACTION_HISTORY_DEPTH;
    }

    public static void setDEFAULT_NUMBER_OF_RESULTS(Integer DEFAULT_NUMBER_OF_RESULTS) {
        WS.DEFAULT_NUMBER_OF_RESULTS = DEFAULT_NUMBER_OF_RESULTS;
    }

    public static void setMAX_NUMBER_OF_RANKING_RESULTS(int MAX_NUMBER_OF_RANKING_RESULTS) {
        WS.MAX_NUMBER_OF_RANKING_RESULTS = MAX_NUMBER_OF_RANKING_RESULTS;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Using default number of results: " + DEFAULT_NUMBER_OF_RESULTS);
        logger.info("Using max number of ranking results: " + MAX_NUMBER_OF_RANKING_RESULTS);
        logger.info("Using action history depth: " + ACTION_HISTORY_DEPTH);
    }
    
    
    
}
