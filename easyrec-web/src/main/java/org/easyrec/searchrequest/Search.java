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
package org.easyrec.searchrequest;

/**
 * This Class implements a Basic Search Container with attributes
 * for handling a Result Set.
 * <p/>
 * A Result Set can either be displayed
 * - as one list with a defined number of entries
 * ( = scrolling)
 * - or as a page with a defined number of entries per page
 * ( = next page/prev page)
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 11:04:49 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17656 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public abstract class Search {

    private final static int INITIAL_LIMIT = 0;
    private final static int INITIAL_ENTRIES_PER_PAGE = 0;
    private final static int INITIAL_PAGE = 0;

    /**
     * Orders the Result Set descending
     */
    public static final String ORDER_BY_DESC = " desc ";

    /**
     * Orders the Result Set ascending (Default)
     */
    public static final String ORDER_BY_ASC = " asc ";

    private int iOrder;
    private boolean bRandomizedOrder;
    private String sDirection;
    private int iLimit;

    private int iPage;
    private int iEntriesPerPage;

    /**
     * The search request is initialized like this:
     * The number of returned items is set to
     * INITIAL_LIMIT and the sort order is descending.
     */
    public Search() {
        iLimit = INITIAL_LIMIT;
        sDirection = ORDER_BY_ASC;
        iPage = INITIAL_PAGE;
        iEntriesPerPage = INITIAL_ENTRIES_PER_PAGE;
    }

    /**
     * @return The sort direction (ASC or DESC)
     */
    public String getDirection() {

        return sDirection;
    }

    /**
     * Set the sort direction (ASC or DESC)
     *
     * @param direction
     */
    public void setDirection(String direction) {
        sDirection = direction;
    }

    /**
     * @return the OrderID of how to Sort the result.
     */
    public int getSortOrder() {
        return iOrder;
    }

    /**
     * Set the OrderID of how to Sort the result.
     *
     * @param order
     */
    public void setSortOrder(int order) {
        iOrder = order;
    }

    /**
     * @return True if the result should be ordered randomly.
     */
    public boolean isRandomizedOrder() {
        return bRandomizedOrder;
    }

    /**
     * Set this True if the result should be ordered randomly.
     *
     * @param randomizedOrder
     */
    public void setRandomizedOrder(boolean randomizedOrder) {
        bRandomizedOrder = randomizedOrder;
    }

    /**
     * @return The maximum Number of Records in
     *         the Result Set.
     */
    public int getLimit() {
        return iLimit;
    }

    /**
     * Set the maximum Number of Records in
     * the Result Set.
     *
     * @param limit
     */
    public void setLimit(int limit) {
        iLimit = limit;
    }

    /**
     * @return The Page of the Result Set.
     */
    public int getPage() {
        return iPage;
    }

    /**
     * Set the Result Set Page.
     *
     * @param page
     */
    public void setPage(int page) {
        this.iPage = page;
    }

    /**
     * @return The number of Records in a Result Page.
     */
    public int getEntriesPerPage() {
        return iEntriesPerPage;
    }

    /**
     * Set the number Records on a Result Page.
     *
     * @param entriesPerPage
     */
    public void setEntriesPerPage(int entriesPerPage) {
        this.iEntriesPerPage = entriesPerPage;
    }
}
