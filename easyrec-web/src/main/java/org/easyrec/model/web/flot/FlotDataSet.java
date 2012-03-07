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

public class FlotDataSet {
    List<FlotSeries> data = new Vector<FlotSeries>();

    public void add(FlotSeries series) {
        data.add(series);
    }

    public List<FlotSeries> getData() {
        return data;
    }


    /**
     * Creates a JSON formated return String containing the plot Data
     *
     * @return
     */
    @Override
    public String toString() {

        String returnString = "[";

        for (int i = 0; i < data.size(); i++) {

            FlotSeries flotSerie = data.get(i);

            returnString += flotSerie.toString();

            if (i != data.size() - 1) {
                returnString += ",";
            }
        }

        returnString += "]";

        return returnString;
    }

}
