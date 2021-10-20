package com.hll.app.statemachine.event;

import lombok.Data;

@Data
public class EventRes<T> {
    private final T request;

    public T get(Class<T> calzz) {
        return this.request;
    }

    public static <T> EventRes<T> of(T args) {
        return new EventRes<>(args);
    }
    public static <T> EventRes<T> instance(T t) {
        return new EventRes<>(t);
    }

}
