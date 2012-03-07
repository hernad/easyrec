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
package org.easyrec.model.web.flot;

import java.util.List;
import java.util.Vector;

public class FlotSeries {

    private String title;
    private List<FlotEntry> data = new Vector<FlotEntry>();
    private Integer actions = null;
    private Integer averageActions = null;
    private int lastActive = 0;

    public void add(int x, int y) {
        FlotEntry newFlotEntry = new FlotEntry();
        newFlotEntry.setX(x);
        newFlotEntry.setY(y);
        data.add(newFlotEntry);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FlotEntry> getData() {
        return data;
    }

    public void setData(List<FlotEntry> data) {
        this.data = data;
    }

    public int getActions() {
        if (actions == null) {
            actions = 0;
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    FlotEntry flotEntry = data.get(i);
                    actions += flotEntry.y;
                    if (flotEntry.y > 0) {
                        lastActive = i;
                    }
                }
            }
        }
        return actions;
    }


    public int getAverageActions() {
        if (averageActions == null) {
            averageActions = 0;
            if (lastActive > 0) {
                averageActions = actions / lastActive;
            }
        }
        return averageActions;
    }


    @Override
    public String toString() {

        String returnString =
                "{\"actions\": " + getActions() + ",\"averageActions\": " + getAverageActions() + ","
                    + "\"label\": \"" + title + "\",\"data\": [";

        for (int i = 0; i < data.size(); i++) {
            FlotEntry flotEntry = data.get(i);
            returnString += flotEntry.toString();
            if (i != data.size() - 1) {
                returnString += ",";
            }
        }

        returnString += "]}";
        return returnString;
    }
}
