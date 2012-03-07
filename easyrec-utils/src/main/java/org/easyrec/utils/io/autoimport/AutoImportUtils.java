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
package org.easyrec.utils.io.autoimport;

import org.apache.commons.logging.Log;
import org.easyrec.utils.io.tabular.output.impl.CSVOutput;

/**
 * This class provides methods to parse header data from a .CSV file.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */
public class AutoImportUtils {
    ///////////////////////////////////////////////////////////////////////////
    // constants
    public static final String CSV_SEPARATOR = Character.toString(CSVOutput.SEP_COMMA);
    public static final char CSV_DEF_SEPARATOR = CSVOutput.DEF_SEPARATOR;
    public static final char CSV_SEP_COMMENT_BEGIN = CSVOutput.SEP_COMMENT_BEGIN;
    public static final char CSV_COMMENT_CHAR = CSVOutput.COMMENT_CHAR;

    public static final String CSV_TYPE = "type";
    public static final String CSV_COMMAND = "command";
    public static final String CSV_DELIMITER = ":";
    public static final String COMMAND_INSERT = "insert";
    public static final String COMMAND_REMOVE = "remove";

    public static final String VALID_TYPE;
    public static final String VALID_TYPE_EXAMPLE;
    public static final String VALID_COMMAND;

    static {
        StringBuilder buf = new StringBuilder();
        buf.append(CSV_COMMENT_CHAR);
        buf.append(" ");
        buf.append(CSV_TYPE);
        buf.append(CSV_DELIMITER);
        buf.append(" <TYPE_OF_FILE>");
        VALID_TYPE = buf.toString();

        buf.delete(buf.length() - 15, buf.length());
        buf.append(" itemassoc");
        VALID_TYPE_EXAMPLE = buf.toString();

        buf = new StringBuilder();
        buf.append(CSV_COMMENT_CHAR);
        buf.append(" ");
        buf.append(CSV_COMMAND);
        buf.append(CSV_DELIMITER);
        buf.append(" ");
        buf.append(COMMAND_INSERT);
        buf.append("' or '");
        buf.append(CSV_COMMENT_CHAR);
        buf.append(" ");
        buf.append(CSV_COMMAND);
        buf.append(CSV_DELIMITER);
        buf.append(" ");
        buf.append(COMMAND_REMOVE);
        VALID_COMMAND = buf.toString();
    }

    // /////////////////////////////////////////////////////////////////////////
    // public methods
    public static String retrieveTypeFromLine(String line) {
        // syntax: '# type: <TYPE_OF_FILE>'
        StringBuilder errorMsgBuffer = new StringBuilder("this line doesn't contain a valid type, line must be: '");
        errorMsgBuffer.append(VALID_TYPE);
        errorMsgBuffer.append("', but was: '");
        errorMsgBuffer.append(line);
        errorMsgBuffer.append("'");
        String errorMsg = errorMsgBuffer.toString();

        // check '#'
        line = line.trim();
        String csvComment = Character.toString(CSV_COMMENT_CHAR);
        if (!line.startsWith(csvComment)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(csvComment) + csvComment.length());

        // check 'type'
        line = line.trim();
        if (!line.startsWith(CSV_TYPE)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(CSV_TYPE) + CSV_TYPE.length());

        // check ':'
        line = line.trim();
        if (!line.startsWith(CSV_DELIMITER)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(CSV_DELIMITER) + CSV_DELIMITER.length());

        // retrieve type of file, for e.g. 'itemassoc' or 'action'
        return line.trim();
    }

    public static String retrieveCommandFromLine(String line) {
        // syntax: '# command: insert/remove'
        String csvComment = Character.toString(CSV_COMMENT_CHAR);
        StringBuilder errorMsgBuffer = new StringBuilder("this line doesn't contain a valid command, line must be: '");
        errorMsgBuffer.append(VALID_COMMAND);
        errorMsgBuffer.append("', but was: '");
        errorMsgBuffer.append(line);
        errorMsgBuffer.append("'");
        String errorMsg = errorMsgBuffer.toString();

        // check '#'
        line = line.trim();
        if (!line.startsWith(csvComment)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(csvComment) + csvComment.length());

        // check 'command'
        line = line.trim();
        if (!line.startsWith(CSV_COMMAND)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(CSV_COMMAND) + CSV_COMMAND.length());

        // check ':'
        line = line.trim();
        if (!line.startsWith(CSV_DELIMITER)) {
            throw new IllegalArgumentException(errorMsg);
        }
        line = line.substring(line.indexOf(CSV_DELIMITER) + CSV_DELIMITER.length());

        // check command 'insert' or 'delete'
        line = line.trim();
        if (line.startsWith(COMMAND_INSERT)) {
            return COMMAND_INSERT;
        }

        if (line.startsWith(COMMAND_REMOVE)) {
            return COMMAND_REMOVE;
        }

        throw new IllegalArgumentException(errorMsg);
    }

    public static String getDefaultFromHeaderPart(String headerPart) {
        // no header value found
        if (headerPart == null || "".equals(headerPart)) {
            return null;
        }

        int indexFrom = headerPart.indexOf(CSV_DEF_SEPARATOR);

        // no separator char '=' found
        if (indexFrom == -1) {
            return null;
        }

        int indexTo = headerPart.indexOf(CSV_SEP_COMMENT_BEGIN);
        if (indexTo != -1) {
            return headerPart.substring(indexFrom + 1, indexTo);
        } else {
            return headerPart.substring(indexFrom + 1);
        }
    }

    public static void logSkippedLine(Log logger, int lineCounter, String line, String message) {
        if (logger.isInfoEnabled()) {
            StringBuilder s = new StringBuilder("skipped data line[");
            s.append(lineCounter);
            s.append("] '");
            s.append(line);
            s.append("', ");
            s.append(message);
            logger.info(s.toString());
        }
    }
}
