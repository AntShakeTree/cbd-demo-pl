package com.hll.app.statemachine.event;

import com.google.common.base.Function;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventReq<T> {
    final T request;




    public Function<EventReq<T>, EventRes> forFunction(Function<Object, Object> function) {
        return f -> new EventRes(function.apply(new EventReq<>(f)));
    }

}
