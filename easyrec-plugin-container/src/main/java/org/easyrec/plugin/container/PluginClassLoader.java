/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.plugin.container;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 *
 * @author szavrel
 */
public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public URL getResource(String name) {
        URL resource = super.findResource(name);
        if (resource != null) {
            return resource;
        } else {
            return super.getResource(name);
        }
    }
   
}
