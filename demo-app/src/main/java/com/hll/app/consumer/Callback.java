package com.hll.app.consumer;

import com.hll.exceptions.BizException;
import com.hll.exceptions.ErrorContext;
import com.hll.exceptions.ErrorFormat;
import com.hll.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class Callback<T, R> {

    final Consumer<T> consumer;

    private Callback(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public static <T, R> Callback builder(Consumer<ITask<T, R>> task) {
        return new Callback(task);
    }

    public ErrorMessage callback(ITask<T, R> t, Exception e) {
        //可注入数据库回滚
        this.consumer.accept(t.get());
        return ErrorFormat.error(new RuntimeException(e));
    }

    public ErrorMessage callback(T t, Exception e) {
        //可注入数据库回滚
        this.consumer.accept(t);
        return ErrorFormat.error(new RuntimeException(e));
    }


    public ErrorMessage callback(ITask t) {
        //可注入数据库回滚
        this.consumer.accept((T) t.get());
        return ErrorFormat.error(new BizException(t.toString()));
    }

    public ErrorMessage callback(T t) {
        //可注入数据库回滚
        this.consumer.accept(t);
        return ErrorFormat.error(new BizException(t.toString()));
    }


    //donating
    public static Callback Non() {
        return new Callback<>(triTask -> {
            log.warn("默认的回调方法，可能存在风险。");
        });
    }

    public static Callback immaThrow() {
        return new Callback<>(triTask -> {
            if (ErrorContext.find().isPresent()) {
                throw ErrorContext.find().get().getCause();
            }
        });
    }
}
