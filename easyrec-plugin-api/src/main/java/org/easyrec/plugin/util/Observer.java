package org.easyrec.plugin.util;

public interface Observer<T> {
    public void stateChanged(T target);
}
