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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for the Text class.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-11-30 14:14:53 +0100 (Mi, 30 Nov 2011) $<br/>
 * $Revision: 18668 $</p>
 *
 * @author Florian Kleedorfer
 */
public class TextTest {

    @Test
    public void testMatchMax() {
        String content = Text.matchMax("http://www.emall.com.tr/",
                "/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("http://www.emall.com.tr/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus" +
                        "-05-05-sonras-9-dekor"));

        content = Text.matchMax("http://www.emall.com.tr",
                "/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("http://www.emall.com.tr/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus" +
                        "-05-05-sonras-9-dekor"));

        content = Text.matchMax("www.emall.com.tr",
                "/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("www.emall.com.tr/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05" +
                        "-sonras-9-dekor"));

        content = Text.matchMax("www.emall.com.tr",
                "product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("www.emall.com.trproduct/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05" +
                        "-sonras-9-dekor"));

        content = Text.matchMax("https://www.emall.com.tr",
                "/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("https://www.emall.com.tr/product/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus" +
                        "-05-05-sonras-9-dekor"));

        content = Text.matchMax("https://www.emall.com.tr",
                "aroduct/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus-05-05-sonras-9-dekor");
        assertThat(content,
                is("https://www.emall.com.traroduct/modacar-aliminyum-gorunum-torpido-kaplama-seti-331164-ford-focus" +
                        "-05-05-sonras-9-dekor"));

        content = Text.matchMax("ab", "cd");
        assertThat(content, is("abcd"));

        content = Text.matchMax("ptr", "pro");
        assertThat(content, is("ptrpro"));

        content = Text.matchMax("http://www.flimmit.com", "/download-stream/headline-story/");
        assertThat(content, is("http://www.flimmit.com/download-stream/headline-story/"));

        content = Text.matchMax("http://www.flimmit.com", "/download-stream/headline-story/");
        assertThat(content, is("http://www.flimmit.com/download-stream/headline-story/"));

        content = Text.matchMax("http://www.flimmit.com", "http://www.flimmit.com/download-stream/headline-story/");
        assertThat(content, is("http://www.flimmit.com/download-stream/headline-story/"));

        content = Text.matchMax("http://www.flimmit.com/download-stream", "/download-stream/headline-story/");
        assertThat(content, is("http://www.flimmit.com/download-stream/headline-story/"));

        content = Text.matchMax("http://www.flimmit.com/download-stream", null);
        assertNull(content);

        content = Text.matchMax(null, null);
        assertThat(content, is(nullValue()));

        content = Text.matchMax(null, "wqe");
        assertThat(content, is("wqe"));

        content = Text.matchMax(null, "http://t.com");
        assertThat(content, is("http://t.com"));

        content = Text.matchMax(null, "https://t.com");
        assertThat(content, is("https://t.com"));

        content = Text.matchMax("NOT_RETURNED", "https://t.com");
        //assertEquals("https://t.com", content);

        content = Text.matchMax("http://www.flimmit.com",
                "http://www.moviepilot.de/files/images/0000/4688/4688_poster_large.jpg");
        assertThat(content, is("http://www.moviepilot.de/files/images/0000/4688/4688_poster_large.jpg"));
    }

    @Test
    public void testCapitalize() {
        assertThat(Text.capitalize("doctor"), is("Doctor"));
        assertThat(Text.capitalize("docTor"), is("DocTor"));
        assertThat(Text.capitalize("a"), is("A"));
        assertThat(Text.capitalize(null), is(nullValue()));
        assertThat(Text.capitalize(""), is(""));
    }

    @Test
    public void testRemoveLast() {
        assertThat(Text.removeLast(null), is(nullValue()));
        assertThat(Text.removeLast(""), is(""));
        assertThat(Text.removeLast("a"), is(""));
        assertThat(Text.removeLast("abcdef"), is("abcde"));
    }

    @Test
    public void testContainsEvilSpecialChar() {
        assertThat(Text.containsEvilSpecialChar("abc , def"), is(false));
        assertThat(Text.containsEvilSpecialChar("abc > def"), is(true));
        assertThat(Text.containsEvilSpecialChar("abc < def"), is(true));
        assertThat(Text.containsEvilSpecialChar("abc % def"), is(true));
        assertThat(Text.containsEvilSpecialChar("abc ' def"), is(true));
        assertThat(Text.containsEvilSpecialChar("abc \" def"), is(true));
    }

    @Test
    public void testContainingString() {
        assertThat(Text.containingString("xabcy", "x", "y"), is("abc"));
        assertThat(Text.containingString("xabc", "x", "y"), is(""));
        assertThat(Text.containingString("xabc", "x", "y"), is(""));
        assertThat(Text.containingString(null, "x", "y"), is(nullValue()));
        assertThat(Text.containingString("", "x", "y"), is(""));
        assertThat(Text.containingString("xy", "x", "y"), is(""));

        try {
            Text.containingString("xabcy", null, "y");
            fail();
        } catch(Exception ignored) {}

        try {
            Text.containingString("xabcy", "x", null);
            fail();
        } catch(Exception ignored) {}
    }
}
