package org.easyrec.plugin.util;

public interface ObserverRegistry<T> {

    public abstract void addObserver(Observer<T> o);

    public abstract void removeObserver(Observer<T> o);

    public abstract void removeObservers();

}