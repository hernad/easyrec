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

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class can do fancy things with strings.
 *
 * @author phlavac
 */
public class Text {
    /**
     * This function returns the given string without the last character.
     * e.g.
     * trimLast("peter")-->"pete"
     *
     * @param s
     * @return
     */
    public static String removeLast(String s) {
        if (Strings.isNullOrEmpty(s)) return s;

        return s.substring(0, s.length() - 1);
    }

    /**
     * Generates a 32 char MD5 hash from a string:
     * "hallo" --> "b6834520c5cf3df80886803e1af41b47"
     *
     * @param key
     * @return
     */
    public static String generateHash(String key) {

        // note: changing the key fucks up the DEFAULT API KEY
        // so use it with caution
        key += "use_your_key_here";

        MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("MD5");
            md.reset();
            md.update(key.getBytes());
            byte[] bytes = md.digest();
            // buffer to write the md5 hash to
            StringBuffer buff = new StringBuffer();
            for (int l = 0; l < bytes.length; l++) {
                String hx = Integer.toHexString(0xFF & bytes[l]);
                // make sure the hex string is correct if 1 character
                if (hx.length() == 1) buff.append("0");
                buff.append(hx);
            }
            return buff.toString().trim();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function return a given String with the first char converted to Uppercase
     * e.g. peter --> Peter
     *
     * @param s
     * @return
     */
    public static String capitalize(String s) {
        if (Strings.isNullOrEmpty(s)) return s;

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * This function matches 2 given strings on their maximum concurrence.
     * e.g.
     * http://satsrv01.researchstudio.at/wiki/index.php/Hauptseite
     * /wiki/index.php/article1
     * -->
     * http://satsrv01.researchstudio.at/wiki/index.php/article1
     *
     * @param left
     * @param right
     */
    public static String matchMax(String left, String right) {

        if (!Strings.isNullOrEmpty(right)) {
            // in case an absolute url is given return the absolute url
            // CAUTION: because bad people can insert spam urls
            if (right.startsWith("http://") || right.startsWith("https://")) {
                return right;
            } else {
                if (!Strings.isNullOrEmpty(left)) {
                    int offset = 0;
                    for (int i = 0; i < left.length(); i++) {
                        if (left.charAt(i) != right.charAt(offset)) {
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                    return new StringBuilder(left).append(right.substring(offset, right.length())).toString();
                } else {
                    return right;
                }
            }
        } else {
            return right;
        }
    }


    /**
     * This function returns true if the given string
     * contains any of the chars %,>,<,',".
     *
     * @param stringToCheck
     * @return
     */
    public static boolean containsEvilSpecialChar(String stringToCheck) {
        return CharMatcher.anyOf("%<>'\"").countIn(stringToCheck) > 0;
    }

    /**
     * Extract a Substring between two Strings from the given String.
     *
     * @param sTest
     * @param prefix
     * @param suffix
     * @return
     */
    public static String containingString(String str, String prefix, String suffix) {
        if(Strings.isNullOrEmpty(str)) return str;

        Preconditions.checkNotNull(prefix);
        Preconditions.checkNotNull(suffix);

        int prefixIdx = str.indexOf(prefix);
        int suffixIdx = str.indexOf(suffix, prefixIdx + 1);

        if(prefixIdx < 0 || suffixIdx < 0) return "";

        return str.substring(prefixIdx + 1, suffixIdx);
    }
}
