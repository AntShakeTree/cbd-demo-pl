package com.hll.app.consumer;

import com.google.common.base.Functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TaskFactory {

    public static <T, R> ITask builder(String name, Function<T, R> function, Supplier<T> supplier) {
        return new Task<T, R>(name, fromFunction(function), Callback.Non(), supplier);
    }

    public static <T, R> ITask builder(String name, Consumer<T> function, Supplier<T> supplier) {
        return new Task<T, R>(name, fromConsumer(function), Callback.Non(), supplier);
    }

    public static <T, R> ITask<T, R> builder(String name, Function<T, R> action, Callback<T, R> callback, T context) {
        return new Task<T, R>(name, fromFunction(action), callback, context);
    }


    public static <T, R> Task<T, R> lazyBuilder(String name, Function<T, R> function, Callback<T, R> callback, Class<T> req) {
        return new Task(name, fromFunction(function), callback);
    }

    public static <T, R> Task<T, R> lazyBuilder(String name, Consumer<T> function, Callback<T, R> callback, Class<T> req) {
        return new Task<T, R>(name, fromConsumer(function), callback);
    }

    private static <T> BiFunction fromConsumer(Consumer<T> consumer) {
        return (o, o2) -> {
            consumer.accept((T) o);
            return o2;
        };
    }

    public static <T, R> Task<T, R> lazyBuilder(String name, Consumer<T> action, Class<T> t) {
        return new Task<T, R>(name, fromConsumer(action), Callback.Non());
    }


    public static <T, R> Task<T, R> builderSupplier(String name, Function<T, R> action, Callback<T, R> callback, Supplier<T> context) {
        return new Task<T, R>(name, fromFunction(action), callback, context);
    }

    public static <T, R> Task<T, R> builderSupplier(String name, Function<T, R> action, Supplier<T> context) {
        return new Task<T, R>(name, fromFunction(action), Callback.Non(), context);
    }


    private static <T, R> BiFunction<T, Task<T, R>, R> fromFunction(Function<T, R> function) {
        return (t, task) -> function.apply(t);
    }
}
