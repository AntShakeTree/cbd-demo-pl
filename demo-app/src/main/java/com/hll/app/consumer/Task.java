package com.hll.app.consumer;

import com.hll.exceptions.BizException;
import com.hll.exceptions.ErrorContext;
import com.hll.exceptions.ErrorType;
import com.hll.exceptions.ExceptionFunction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class Task<T, R> implements ITask<T, R> {
    private final BiFunction<T, Task<T, R>, R> action;
    @Getter
    private final Callback<T, R> callback;
    private Supplier<T> supplierContext;
    private T context;
    @Getter
    private final String name;
    @Getter
    private volatile boolean fail = false;

    Task(String name, BiFunction<T, Task<T, R>, R> action, Callback<T, R> callback) {
        this.name = name;
        this.action = action;
        this.callback = callback;
    }

    protected Task(String name, BiFunction<T, Task<T, R>, R> action, Callback<T, R> callback, Supplier<T> context) {
        this.name = name;
        this.action = action;
        this.callback = callback;
        this.supplierContext = context;
    }

    protected Task(String name, BiFunction<T, Task<T, R>, R> action, Callback<T, R> callback, T context) {
        this.name = name;
        this.action = action;
        this.callback = callback;
        this.context = context;
    }



    public ITask context(T context) {
        if (this.context == null) {
            this.context = context;
        }
        return this;
    }

    /**
     * 执行并 延迟执行callback，由上层业务统一处理
     *
     * @param req
     * @return
     */
    @Override
    public R actionAndCallback(T req) {
        try {
            R r = this.action.apply(req, this);
            this.fail = false;
            return r;
        } catch (Exception e) {
            log.error("actionAndCallback:{}", e);

            this.fail = true;
            //上下文中存储该异常，避免直接抛出影响后续流程
            ErrorContext.instance().addErrorMessage(callback.callback(this, e));
            return null;
        }
    }

    /**
     * 执行并 延迟执行callback，由上层业务统一处理
     *
     * @return
     */
    public R actionAndCallback() {
        try {
            R r = this.action.apply(this.get(),this);
            this.fail = false;
            return r;
        } catch (Exception e) {
            log.error("actionAndCallback:{}", e);
            this.fail = true;
            //上下文中存储该异常，避免直接抛出影响后续流程
            ErrorContext.instance().addErrorMessage(callback.callback(this, e));
            return null;
        }
    }

    /**
     * 执行出异常直接回调
     *
     * @return
     */
    public R action() {
        try {


            R r = this.action.apply(this.get(),this);
            this.fail = false;
            return r;
        } catch (Exception e) {
            log.error("{}", e);
            this.fail = true;
            this.callback.callback(this, e);
            ErrorContext.instance().addErrorMessage(callback.callback(this, e));
            return null;
        }
    }

    /**
     * 执行出异常直接回调
     *
     * @param req 请求上下文参数
     * @return
     */
    @Override
    public R action(T req) {
        try {
            R r = this.action.apply(req,this);
            this.fail = false;
            return r;
        } catch (Exception e) {
            this.fail = true;
            log.error("{}", e);
            this.callback.callback(this, e);
            return null;
        }
    }

    @Override
    public T get() {
        if (this.context != null) {
            return context;
        } else if (supplierContext != null) {
            return supplierContext.get();
        } else {
            throw new BizException("缺少请求上下文参数");
        }
    }




    @Override
    public R call() {
        return action();
    }

    @Override
    public String toString() {
        return "Task{" + ", context= " + ", name='" + name + '\'' + '}';
    }
}

