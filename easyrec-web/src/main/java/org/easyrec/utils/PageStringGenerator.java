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
package org.easyrec.utils;

/**
 * This class creates a html string for selecting pages in a table grid.
 * <p/>
 * e.g.  <<  1 ...  3 4 5 6 7 ... 27  >>
 * <p/>
 * a click on a page calls the following function, which may be included in every
 * website, where paging is used.
 * <p/>
 * function selectPage(url, siteNumber) {
 * window.location = url + siteNumber;
 * }
 * <p/>
 * Paging needs the coressponding DAO methods for retrieving the appropriate
 * rows. e.g.:
 * Integer myDAO.itemCount
 * List<Item> myDAO.getItems(int offset, int limit)
 *
 * @author phlavac
 */
public class PageStringGenerator {

    // TODO: move to vocabulary?
    public static final int DEFAULT_NUMBER_OF_ITEMS_PER_PAGE = 50;

    private String javascriptFunctionName = "selectPage";
    private int numberOfPagesToShow = 2;
    private int numberOfItemsPerPage = DEFAULT_NUMBER_OF_ITEMS_PER_PAGE;
    private String url;

    /**
     * To generate a page string the url of the website where the list
     * of items are displayed may be passed to the constructor
     *
     * @param url
     */
    public PageStringGenerator(String url) {
        url = url.replace("siteNumber=", "");
        this.url = url + (url.contains("?") ? "&siteNumber=" : "?siteNumber=");
    }


    /**
     * Sets the number of items on a page to show. The default value is set to
     * 50 item per page.
     *
     * @param numberOfItemsPerPage
     */
    public void setNumberOfItemsPerPage(int numberOfItemsPerPage) {
        this.numberOfItemsPerPage = numberOfItemsPerPage;
    }

    public int getNumberOfItemsPerPage() {
        return numberOfItemsPerPage;
    }

    /**
     * This number is the number of page links displayed on the left and right
     * of the current page.
     * The Default value is 2;
     *
     * @param numberOfPagesToShow
     */
    public void setNumberOfPagesToShow(int numberOfPagesToShow) {
        this.numberOfPagesToShow = numberOfPagesToShow;
    }

    /**
     * Returns the Html String displayed on top or the bottom of a table grid.
     *
     * @param numberOfPages
     * @param selectedSiteNumber
     * @return
     */
    public String getPageMenuString(int itemCount, int selectedSiteNumber) {

        int numberOfPages = (itemCount - 1) / numberOfItemsPerPage + 1;
        String pageMenuString = "";

        int pageStart = selectedSiteNumber - numberOfPagesToShow;
        int pageEnd = selectedSiteNumber + numberOfPagesToShow;

        if (pageStart < 0) pageStart = 0;

        if (pageEnd > numberOfPages) pageEnd = numberOfPages;

        for (int i = 0; i < numberOfPages; i++) {

            if (i == pageStart - 1) {
                pageMenuString +=
                        "<span onClick='" + javascriptFunctionName + "(\"" + url + "\"," + (selectedSiteNumber - 1) +
                                ")' class='clickable redLink'> ";
                pageMenuString += "&lt;&lt;&nbsp;";
                pageMenuString += "</span>";
            }

            if (i >= pageStart && i <= pageEnd) {
                pageMenuString += getMenuEntryString(i, selectedSiteNumber);
            } else if (i == pageEnd + 1) {
                pageMenuString += " ..." + getMenuEntryString(numberOfPages - 1, selectedSiteNumber);
                ;
            } else if (i == pageStart - 1) {
                pageMenuString += getMenuEntryString(0, selectedSiteNumber) + " ... ";
            }

            if (i == pageEnd + 1) {
                pageMenuString +=
                        "<span onClick='" + javascriptFunctionName + "(\"" + url + "\"," + (selectedSiteNumber + 1) +
                                ")' class='clickable redLink'> ";
                pageMenuString += "&nbsp;&gt;&gt;";
                pageMenuString += "</span>";
            }
        }
        return pageMenuString;
    }


    private String getMenuEntryString(int i, int selectedSiteNumber) {
        return "&nbsp;" + "<span onClick='" + javascriptFunctionName + "(\"" + url + "\"," + i +
                ")' class='clickable " + ((i == selectedSiteNumber) ? "selectedPage" : "page") + "'>" + (i + 1) +
                "</span>";
    }
}