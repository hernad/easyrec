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
package org.easyrec.utils.io.tabular.input.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Imports csv data. Easiest usage:
 * <pre>
 *  File myfile = new File(...);
 *  CsvInput in = new CsvInput(); //using default csv separators
 *  in.setSource(myfile);
 *  while(in.hasNext()){
 *  	List<String> fields = in.next(); //read one line
 *  }
 * </pre>
 * Note: the CsvInput object can be re-used to read another file.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class CsvInput extends AbstractTabularInput {

    private String fieldSeparator = ";";
    private String fieldQuote = null;
    private String splitRegex = null;


    /**
     * Creates a new CsvInput object, using ';' as field separator, and no field
     * quotes.
     */
    public CsvInput() {
        this.fieldSeparator = ";";
        this.fieldQuote = null;
        setup();
    }

    /**
     * Creates a new CsvInput object, using the specified quotes and separators.
     *
     * @param fieldSeparator necessary.
     * @param fieldQuote     use null to parse csv without field quotes.
     */
    public CsvInput(String fieldSeparator, String fieldQuote) {
        assert fieldSeparator != null;
        this.fieldSeparator = fieldSeparator;
        this.fieldQuote = fieldQuote;
        setup();
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.impl.AbstractTabularInput#setup()
      */
    private void setup() {
        this.splitRegex = createSplitRegex();
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.impl.AbstractTabularInput#parseLine(java.lang.String)
      */
    @Override
    protected List<String> parseLine(String line) {
        if (logger.isDebugEnabled()) {
            logger.debug("parsing line: " + line);
        }
        String[] fields = line.split(this.splitRegex, -1);
        if (logger.isTraceEnabled()) {
            logger.debug("fields: " + Arrays.toString(fields));
        }
        int size = fields.length;
        int startInd = 0;
        int endInd = size - 1;
        if (fieldsAreQuoted()) {
            size = size - 2; //first field is always empty when using quotes
            startInd = 1;
            endInd--;
        }
        List<String> ret = new ArrayList<String>(size);
        for (int i = startInd; i <= endInd; i++) {
            ret.add(fields[i]);
        }
        if (logger.isTraceEnabled()) {
            logger.debug("returning list: " + ret);
        }
        return ret;
    }


    private boolean fieldsAreQuoted() {
        return this.fieldQuote != null;
    }

    /**
     *
     */
    private String createSplitRegex() {
        String regex = null;
        if (fieldsAreQuoted()) {
            //if sep==, and quote == ', the regex looks like this: "(^'|','|'$)"
            //and the first extracted field is omitted.
            StringBuilder sb = new StringBuilder();
            sb.append("(^").append(Pattern.quote(this.fieldQuote)).append("|").append(Pattern.quote(this.fieldQuote))
                    .append(Pattern.quote(this.fieldSeparator)).append(Pattern.quote(this.fieldQuote)).append("|")
                    .append(Pattern.quote(this.fieldQuote)).append("$)");
            regex = sb.toString();
        } else {
            regex = this.fieldSeparator;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("created split regex:" + regex);
        }
        return regex;
    }

    public static void main(String[] args) {
        String line = "'ab','cd','efg','','h'";
        String[] fields = line.split("(^'|','|'$)", -1);
        System.out.println(Arrays.toString(fields));
        line = "ab,cd,efg,,h";
        fields = line.split(",", -1);
        System.out.println(Arrays.toString(fields));
        line = ",a,,,";
        fields = line.split(",", -1);
        System.out.println(Arrays.toString(fields));
        line = "'ab','cd','efg','','h'";
        fields = line.split("(\\Q'\\E\\Q,\\E\\Q'\\E)", -1);
        System.out.println(Arrays.toString(fields));
        line = "'ab'";
        fields = line.split("(^'|','|'$)", -1);
        System.out.println(Arrays.toString(fields));
    }

}
