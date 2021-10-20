//package com.hll.app;
//
//
//import com.hll.app.consumer.Callback;
//import com.hll.app.consumer.EventContext;
//import com.hll.app.consumer.EventContextSupport;
//import com.hll.app.consumer.ExecutorCallback;
//import com.hll.app.service.OrderService;
//import com.hll.app.service.OrderServiceImpl;
//import com.hll.app.statemachine.StateMachine;
//import com.hll.app.statemachine.context.MachineContextSupport;
//import com.hll.app.statemachine.event.EventReq;
//import com.hll.app.statemachine.event.EventRes;
//import com.hll.app.statemachine.state.State;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import static com.hll.app.consumer.Task.lazyBuilder;
//
//class OrderEventManagerTest {
//
//
//    private static State NEW = new State("支付");
//
//    private static State PAYED = new State("付款");
//    private static State DEVILY = new State("发货");
//    //    private final static Map<OrderStatus, StateMachine> stateMachines = Maps.newConcurrentMap();
//    private static EventContext<EventReq, String, EventRes> eventContext;
//
////    private final static Map<>
//
//
//    static OrderService orderService = new OrderServiceImpl();
//
//    @BeforeAll
//    public static void init() {
//
//        eventContext = MachineContextSupport.createForString(EventReq.class, EventRes.class);
//        eventContext.add("测试1", lazyBuilder("orderService.check",
//                args -> {
//                    return EventRes.instance(orderService.check(args));
//                }
//                , Callback.Non()
//                , EventReq.class));
//
//
//        eventContext.add("测试2", lazyBuilder("测试2",
//                args -> EventRes.instance(orderService.payOrder(args))
//                , Callback.builder(a -> orderService.payOrderCallBack(a)), EventReq.class));
//
//
//        NEW.onEnter(MachineContextSupport.fireCircuitBiConsumer("测试1"));
//        NEW.onAction(MachineContextSupport.fireCircuitBiFunction("测试2"));
//
//        //只有一个发货
//        eventContext.add("测试3", lazyBuilder("orderService.delivery", f -> EventRes.instance(orderService.delivery(f)), Callback.builder(t -> orderService.deliveryCallback(t)), EventReq.class));
//        eventContext.add("测试5", lazyBuilder("orderService.receive", f -> EventRes.instance(orderService.receive(f)), Callback.builder(t -> orderService.deliveryCallback(t)), EventReq.class));
//
//        PAYED.onEnter(EventContext.fireCircuitBiConsumer("测试3"));
//        DEVILY.onEnter(EventContext.fireCircuitBiConsumer("测试5"));
//    }
//
//    @Test
//    void machine() {
//        StateMachine<EventContext, String, ExecutorCallback> stateMachine = StateMachine.instance(eventContext, NEW);
//        stateMachine.connect().subscribe();
//        NEW.transition(NEW.getName(), PAYED);
//        PAYED.transition(PAYED.getName(), DEVILY);
//        stateMachine.accept(NEW.getName());
//        stateMachine.accept(PAYED.getName());
//
//    }
//}
