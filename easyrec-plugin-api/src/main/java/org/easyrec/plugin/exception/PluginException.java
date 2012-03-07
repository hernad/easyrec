package org.easyrec.plugin.exception;

import org.easyrec.plugin.Plugin;

public class PluginException extends RuntimeException {

    private Plugin plugin;

    /**
     *
     */
    private static final long serialVersionUID = 2744239892190497818L;

    public PluginException(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginException(Plugin plugin, String message, Throwable cause) {
        super(createMessagePrefix(plugin) + message, cause);
        this.plugin = plugin;
    }

    public PluginException(Plugin plugin, String message) {
        super(createMessagePrefix(plugin) + message);
        this.plugin = plugin;
    }

    public PluginException(Plugin plugin, Throwable cause) {
        super(cause);
        this.plugin = plugin;
    }

    private static String createMessagePrefix(Plugin plugin) {
        return new StringBuilder().append("Exception from Plugin '").append(plugin.getDisplayName()).append("': ")
                .toString();
    }
}
