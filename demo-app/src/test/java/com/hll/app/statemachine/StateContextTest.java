//package com.hll.app.statemachine;
//
//import com.hll.app.consumer.EventContext;
//import com.hll.app.consumer.event.Event;
//
//class StateContextTest {
//
//
//    @org.junit.jupiter.api.Test
//    void add() {
//        EventContext<Event, UserContextDto, String> stateContext = EventContext.INSTANCE(UserContextDto.class, String.class);
//        UserContextDto userContextDto = new UserContextDto();
//        userContextDto.setMobile("123");
//        stateContext.add(Event.A, u -> test(u));
//        stateContext.add(Event.A, u -> testA(u));
//        stateContext.fire(Event.A, userContextDto);
//        stateContext.clear();
//    }
//
//    private String test(UserContextDto userContextDto) {
//        userContextDto.valid();
//        System.out.println("===========" + userContextDto.getMobile());
//        return userContextDto.getMobile();
//    }
//
//    private String testA(UserContextDto userContextDto) {
//        userContextDto.valid();
//        System.out.println("===========" + userContextDto.getMobile());
//        return userContextDto.getMobile();
//    }
//
//
//    @org.junit.jupiter.api.Test
//    void fire() {
//
//    }
//}