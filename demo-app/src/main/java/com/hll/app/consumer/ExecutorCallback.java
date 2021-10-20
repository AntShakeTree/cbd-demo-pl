package com.hll.app.consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ExecutorCallback<T,R> {
    private final List<R> results;
    private final List<Task<T,R>> callbacks;
    @Setter
    @Getter
    private  volatile boolean fail;

    public ExecutorCallback() {
        this.results = new ArrayList<>();
        this.callbacks = new ArrayList<>();
        this.fail=false;
    }


    public static ExecutorCallback INSTANCE() {
        return new ExecutorCallback();
    }

    public ExecutorCallback add(R r) {
        this.results.add(r);
        return this;
    }

    public ExecutorCallback addCallback(Task<T,R> r) {
        this.callbacks.add(r);
        return this;
    }

}
