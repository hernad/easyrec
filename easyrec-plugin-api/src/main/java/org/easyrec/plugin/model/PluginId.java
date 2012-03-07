/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;

/**
 * @author pmarschik
 */
@XmlRootElement(name = "pluginId")
public class PluginId {
    public static class URIAdapter extends XmlAdapter<URI, PluginId> {

        @Override
        public PluginId unmarshal(URI v) throws Exception {
            return PluginId.parsePluginId(v.toString());
        }

        @Override
        public URI marshal(PluginId v) throws Exception {
            return URI.create(v.toString());
        }
    }

    @XmlAttribute(required = true)
    private URI uri;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(Version.StringAdapter.class)
    private Version version;
    private transient String stringRepresentation;

    private PluginId() {}

    public PluginId(URI uri, Version version) {
        this.uri = Preconditions.checkNotNull(uri);
        this.version = Preconditions.checkNotNull(version);
    }

    public PluginId(String uri, String version) {
        this(URI.create(uri), Version.parseVersion(version));
    }

    public URI getUri() {
        return uri;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginId pluginId = (PluginId) o;

        if (!uri.equals(pluginId.uri)) return false;
        if (!version.equals(pluginId.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uri, version);
    }

    @Override
    public String toString() {
        if (this.stringRepresentation == null) {
            StringBuilder stringRepresentation = new StringBuilder(uri.toString());
            stringRepresentation.append('/');
            stringRepresentation.append(version.toString());

            this.stringRepresentation = stringRepresentation.toString();
        }

        return this.stringRepresentation;
    }

    public static PluginId parsePluginId(String string) {
        Preconditions.checkNotNull(string);

        int lastSlash = string.lastIndexOf('/');

        if (lastSlash < 0) throw new IllegalArgumentException("No version component found in \"" + string + "\".");

        String uriComponent = string.substring(0, lastSlash);
        String versionComponent = string.substring(lastSlash + 1);

        URI uri = URI.create(uriComponent);
        Version version = Version.parseVersion(versionComponent);

        return new PluginId(uri, version);
    }
}
