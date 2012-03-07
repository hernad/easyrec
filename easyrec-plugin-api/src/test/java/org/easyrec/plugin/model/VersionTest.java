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

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link org.easyrec.plugin.model.Version}. <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class VersionTest {

    @Test
    public void stringCtor_shouldThrow() {
        try {
            new Version("");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("1");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.0.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("-1.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("-1.0.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.-1");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.-1.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.0.-1");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("a.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("a.0.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.b");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.b.0");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version("0.0.c");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void intCtor_shouldThrow() {
        try {
            new Version(0, 0);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(0, 0, 0);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(-1, 0);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(0, -1);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(-1, 0, 0);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(0, -1, 0);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new Version(0, 0, -1);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void stringCtor_shouldParseCorrectly() {
        Version version = new Version("1.2.3");

        assertThat(version.getMajor(), is(1));
        assertThat(version.getMinor(), is(2));
        assertThat(version.isMiscAvailable(), is(true));
        assertThat(version.getMisc(), is(3));

        version = new Version("1.2");

        assertThat(version.getMajor(), is(1));
        assertThat(version.getMinor(), is(2));
        assertThat(version.isMiscAvailable(), is(false));
        assertThat(version.getMisc(), is(nullValue()));
    }

    @Test
    public void toString_shouldOutputCorrectly() {
        String versionStr = "1.2.3";
        Version version = new Version(versionStr);

        assertThat(version.toString(), is(versionStr));

        versionStr = "1.2";
        version = new Version(versionStr);

        assertThat(version.toString(), is(versionStr));
    }

    @Test
    public void valueOf_shouldBeIdenticalToToString() {
        assertThat(Version.valueOf("1.2.3").toString(), is("1.2.3"));
        assertThat(Version.valueOf("1.2").toString(), is("1.2"));
    }

    @Test
    public void parseVersion_shouldBeIdenticalToToString() {
        assertThat(Version.parseVersion("1.2.3").toString(), is("1.2.3"));
        assertThat(Version.parseVersion("1.2").toString(), is("1.2"));
    }

    @Test
    public void compare_shouldWork() {
        Version v1 = new Version("0.0.1");
        Version v1b = new Version("0.0.2");

        Version v2 = new Version("0.1.0");
        Version v2b = new Version("0.2.0");
        Version v2c = new Version("0.1");

        Version v3 = new Version("1.1.0");
        Version v3b = new Version("2.1.0");
        Version v3c = new Version("1.1");

        assertThat(v1, lessThan(v1b));
        assertThat(v1b, greaterThan(v1));

        assertThat(v2, lessThan(v2b));
        assertThat(v2b, greaterThan(v2));

        assertThat(v3, lessThan(v3b));
        assertThat(v3b, greaterThan(v3));

        assertThat(v1, lessThan(v2));
        assertThat(v2, lessThan(v3));
        assertThat(v1, lessThan(v3));
        assertThat(v2, greaterThan(v1));
        assertThat(v3, greaterThan(v2));
        assertThat(v3, greaterThan(v1));

        assertThat(v2c, lessThan(v2));
        assertThat(v2, greaterThan(v2c));

        assertThat(v3c, lessThan(v3));
        assertThat(v3, greaterThan(v3c));
    }
}
