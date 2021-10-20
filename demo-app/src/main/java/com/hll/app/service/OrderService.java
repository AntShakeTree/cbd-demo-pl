package com.hll.app.service;

import com.hll.app.consumer.ITask;
import com.hll.app.statemachine.event.EventReq;
import com.hll.app.statemachine.event.EventRes;
import com.hll.app.statemachine.state.State;

import java.util.List;
import java.util.Map;

/**
 * order 领域事件
 */
public interface OrderService{
    Object check(EventReq args);
    void payOrderCallBack(ITask<Object, Object> a);
    void reduceStockCallBack(ITask<Object, Object> a);
    Object placeOrder(List<EventRes> args);
    Object placeOrder(Map<State,List<EventRes>> args);
    Object payOrder(EventReq args);
    Object reduceStock(EventReq args);
    String delivery(EventReq f);
    void deliveryCallback(ITask<Object, Object> t);
    Object receive(EventReq f);
}
