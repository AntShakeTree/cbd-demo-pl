package com.hll.app.consumer;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface ITask<T, R> extends Callable<R>,Serializable {
    boolean isFail();

    public R actionAndCallback(T req);

    public R actionAndCallback();

    public R action();

    public R action(T req);

    public T get();

    public Callback<T,R> getCallback();

    public ITask context(T context);

    public default boolean lazy() {
        if (get() != null) {
            return false;
        } else {
            return true;
        }
    }




}