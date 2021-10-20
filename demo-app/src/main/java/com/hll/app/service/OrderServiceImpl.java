package com.hll.app.service;

import com.hll.app.consumer.ITask;
import com.hll.app.statemachine.event.EventReq;
import com.hll.app.statemachine.event.EventRes;
import com.hll.app.statemachine.state.State;

import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService{
    @Override
    public Object check(EventReq args) {
        System.out.println("OrderService::chck");
        return "check";
    }

    @Override
    public Object placeOrder(List<EventRes> args) {
        System.out.println("OrderService::placeOrder");
        System.out.println(args);
        for (EventRes eventRes:args){
            if (eventRes.getRequest() instanceof String ){
                System.out.println(eventRes.getRequest()+"==========placeOrder================");
            }

        }
        return "placeOrder";
    }

    @Override
    public Object placeOrder(Map<State, List<EventRes>> args) {

        for (Map.Entry eventRes:args.entrySet()){
            State state=(State) eventRes.getKey();
            System.out.println("OrderService::placeOrder===="+state);
            System.out.println("OrderService::placeOrder===="+eventRes.getValue().toString());

        }
        return "placeOrder";
    }

    @Override
    public Object payOrder(EventReq args) {
        System.out.println("OrderService::payOrder");
        return "payOrder";
    }

    @Override
    public Object reduceStock(EventReq args) {
        System.out.println("OrderService::reduceStock");
        return "reduceStock";
    }

    @Override
    public String delivery(EventReq f) {
        System.out.println("OrderService::delivery");
        return f.toString();
    }

    @Override
    public void deliveryCallback(ITask<Object, Object> t) {
        System.out.println("OrderService::deliveryCallback");

    }

    @Override
    public Object receive(EventReq f) {
        System.out.println("OrderService::receive");

        return "receive";
    }

    @Override
    public void reduceStockCallBack(ITask a) {
        System.out.println("reduceStockCallBack");
    }

    @Override
    public void payOrderCallBack(ITask a) {
        System.out.println("payOrderCallBack");
    }
}
