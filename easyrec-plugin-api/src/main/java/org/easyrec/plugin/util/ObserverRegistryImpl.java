package org.easyrec.plugin.util;

import java.util.LinkedList;
import java.util.List;

public class ObserverRegistryImpl<T> implements ObserverRegistry<T> {
    private List<Observer<T>> observers = new LinkedList<Observer<T>>();

    private final T target;

    public ObserverRegistryImpl(T target) {
        super();
        this.target = target;
    }

    public synchronized void notifyObservers() {
        for (Observer<T> o : observers) {
            o.stateChanged(this.target);
        }
    }

    /* (non-Javadoc)
     * @see org.easyrec.plugin.util.ObserverRegistry#addObserver(org.easyrec.plugin.util.Observer)
     */
    public synchronized void addObserver(Observer<T> o) {
        this.observers.add(o);
    }

    /* (non-Javadoc)
     * @see org.easyrec.plugin.util.ObserverRegistry#removeObserver(org.easyrec.plugin.util.Observer)
     */
    public synchronized void removeObserver(Observer<T> o) {
        this.observers.remove(o);
    }

    /* (non-Javadoc)
     * @see org.easyrec.plugin.util.ObserverRegistry#removeObservers()
     */
    public synchronized void removeObservers() {
        this.observers.clear();
    }
}
