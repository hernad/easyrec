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
package org.easyrec.utils.io;

import com.google.common.base.Strings;

/**
 * This class manipulates mysql query strings.
 *
 * @author phlavac
 */
public class MySQL {

    /**
     * This function adds a Like clause to an SQL statement.
     * The Like clause starts with the keyword "AND" meaning that given sql string
     * has to end with the keyword
     * "WHERE" and a clause (e.g. 1=1) before the like clause can be appended.
     * If the attributeValue has less than 3 charactars a equal comparison is
     * performed.
     *
     * @param sql
     * @param attribute
     * @param attributeValue
     * @return
     */
    public static StringBuilder addLikeClause(StringBuilder sql, String attribute, String attributeValue) {
        if (Strings.isNullOrEmpty(attributeValue)) return sql;

        if (attributeValue.length() > 2) {
            return sql.append(" AND ").append(attribute).append(" LIKE '%").append(attributeValue).append("%' ");
        } else {
            return sql.append(" AND ").append(attribute).append(" = '").append(attributeValue).append("' ");
        }
    }

    /**
     * This function adds a limit clause to a given mysql statement (e.g. LIMIT 200,50)
     * show the rows from 200 to 250.
     *
     * @param sql
     * @param offset
     * @param number
     * @return
     */
    public static StringBuilder addLimitClause(StringBuilder sql, int offset, int number) {
        if (offset < 0 || number <= 0) return sql;

        return sql.append(" LIMIT ").append(offset).append(", ").append(number);
    }
}
