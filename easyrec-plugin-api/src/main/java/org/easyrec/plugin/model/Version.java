/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is a model class (dataholder) for the database <code>Version</code>. <p/> <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p/> <p><b>Copyright:&nbsp;</b> (c) 2010</p> <p/> <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/> $Date: 2011-03-24 15:21:34 +0100 (Do, 24 Mär 2011) $<br/> $Revision: 18036 $</p>
 *
 * @author Roman Cerny
 */
public class Version implements Comparable<Version> {
    public static class StringAdapter extends XmlAdapter<String, Version> {

        @Override
        public Version unmarshal(String v) throws Exception {
            return Version.parseVersion(v);
        }

        @Override
        public String marshal(Version v) throws Exception {
            return v.toString();
        }
    }

    // ------------------------------ FIELDS ------------------------------

    private static final String PATTERN_ERROR_MESSAGE =
            "Wrong format, 'version' has to fit the pattern 'major.minor[.misc]'\n\n" + " where:\n" +
                    "   major = any number >= 0 (leading zeros allowed)\n" +
                    "   minor = any number >= 0 (leading zeros allowed)\n" +
                    "   misc = any number >= 0 (leading zeros allowed)\n" +
                    "       any of major, minor, or misc has to be > 0, at least major and minor need to be specified\n\n";

    private int major;
    private int minor;
    private Integer misc;

    // -------------------------- STATIC METHODS --------------------------

    public static Version parseVersion(String version) {
        return new Version(version);
    }

    public static Version valueOf(String version) {
        return new Version(version);
    }

    // --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Creates a new <code>Version</code> object.
     * <p/>
     * In order to be valid the String representation of a <code>Version</code> must follow this pattern:
     * <p/>
     * major.minor.misc
     * <p/>
     * Where: major = any number >= 0 (leading zeros allowed) minor = any number >= 0 (leading zeros allowed) misc = any
     * number >= 0 (leading zeros allowed) any of major, minor, or misc has to be > 0
     * <p/>
     * Some valid versions: 0.1 1.0 1.0.10 1.7.21 20.1.0
     * <p/>
     * Some illegal versions: 1 0.0 0.0.0 -1.0.0 0.-1.0 0.0.-1 1.0.0.0
     *
     * @param version as String representation
     * @throws IllegalArgumentException when the given version, does not fit the version pattern
     */
    public Version(String version) {
        if (version == null) throw new IllegalArgumentException("'version' must not be 'null'");

        String[] splitVersion = version.split("\\.");

        if (splitVersion.length < 2) throw new IllegalArgumentException(PATTERN_ERROR_MESSAGE + "missing '.'");
        else if (splitVersion.length > 3) throw new IllegalArgumentException(PATTERN_ERROR_MESSAGE + "too many '.'");

        try {
            major = Integer.parseInt(splitVersion[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'major' has to be a number", e);
        }

        try {
            minor = Integer.parseInt(splitVersion[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'minor' has to be a number", e);
        }

        if (splitVersion.length > 2) {
            try {
                misc = Integer.parseInt(splitVersion[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'misc' has to be a number", e);
            }
        } else {
            misc = null;
        }

        testValidity();
    }

    private void testValidity() {
        if (major < 0) throw new IllegalArgumentException("'major' has to be a number > 0");

        if (minor < 0) throw new IllegalArgumentException("'minor' has to be a number > 0");

        if (isMiscAvailable()) {
            if (misc < 0) throw new IllegalArgumentException("'misc' has to be a number > 0");

            if (major == 0 && minor == 0 && misc == 0)
                throw new IllegalArgumentException("any of 'major', 'minor', or 'misc' has to be > 0");
        } else {
            if (major == 0 && minor == 0) throw new IllegalArgumentException("any of 'major' or 'minor' has to be > 0");
        }
    }

    public Version(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
        this.misc = null;

        testValidity();
    }

    public Version(final int major, final int minor, final int misc) {
        this.major = major;
        this.minor = minor;
        this.misc = misc;

        testValidity();
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public int getMajor() { return major; }

    public int getMinor() { return minor; }

    public Integer getMisc() { return misc; }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;

        final Version version = (Version) o;

        return major == version.major && minor == version.minor &&
                !(misc != null ? !misc.equals(version.misc) : version.misc != null);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + (misc != null ? misc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append(major).append('.').append(minor);

        if (isMiscAvailable()) result.append('.').append(misc);

        return result.toString();
    }

    public boolean isMiscAvailable() { return misc != null; }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface Comparable ---------------------

    public int compareTo(Version that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = BEFORE * -1;

        if (this == that) {
            return EQUAL;
        }

        if (this.major > that.major) return AFTER;
        if (this.major < that.major) return BEFORE;

        if (this.minor > that.minor) return AFTER;
        if (this.minor < that.minor) return BEFORE;

        if (this.misc != null && that.misc != null) {
            if (this.misc > that.misc) return AFTER;
            if (this.misc < that.misc) return BEFORE;
        } else if (this.misc == null && that.misc != null) {
            return BEFORE;
        } else if (this.misc != null && that.misc == null) {
            return AFTER;
        }

        assert this.equals(that) : "compareTo(...) inconsistent with equals(...)";

        return EQUAL;
    }
}
