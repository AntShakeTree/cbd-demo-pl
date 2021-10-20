//package com.hll.app;
//
//import com.hll.app.consumer.Callback;
//import com.hll.app.consumer.EventContext;
//import com.hll.app.consumer.ExecutorCallback;
//import com.hll.app.consumer.Task;
//import com.hll.app.statemachine.StateMachine;
//import com.hll.app.statemachine.state.State;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import org.junit.jupiter.api.Test;
//
//import static com.hll.app.statemachine.factory.MachineFactory.fireCircuitBiConsumer;
//import static com.hll.app.statemachine.factory.MachineFactory.fireCircuitBiFunction;
//
//
//public class StateMachineTest2 {
//
//    public static State<EventContext, String, ExecutorCallback> NEW = new State<>("订单创建");
//    public static State<EventContext, String, ExecutorCallback> PAYED = new State<>("订单支付完成");
//
//    @Test
//    void statamachineEvent() {
//        EventContext<SomeContext, String, String> context1 = EventContext.INSTANCE(SomeContext.class,String.class,  String.class);
//        context1.add("订单初始化", Task.lazyBuilder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)),SomeContext.class
//        ));
//        context1.add("订单初始化", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("订单初始化", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("执行支付", Task.builder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("123")
//        ));
//        context1.add("执行支付", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("执行支付", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("减少库存", Task.builder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("123")
//        ));
//        context1.add("减少库存", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.add("减少库存", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//
//        EventContext<String, SomeContext, String>
//                context2 =
//                EventContext.INSTANCE(String.class, SomeContext.class, String.class);
//
//        NEW.onEnter(fireCircuitBiConsumer(context1, "订单初始化")).onAction(fireCircuitBiFunction(context1, "执行支付")).onExit(fireCircuitBiConsumer(context1, "减少库存")).transition("PAYED", PAYED);
//
//
//        NEW.enter(context1);
//        System.out.println(NEW.action(context1));
//        NEW.exit(context1);
//
//    }
//
//    @Test
//    public void machine() {
//        EventContext<SomeContext,String,  String> context1 = EventContext.INSTANCE( SomeContext.class,String.class, String.class);
//        context1.add("订单初始化", Task.builder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("123")
//        ));
//        context1.add("订单初始化", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("订单初始化", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("执行支付", Task.builder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("123")
//        ));
//        context1.add("执行支付", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        context1.add("执行支付", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.add("执行支付", Task.builder("执行支付失败方法",
//                s -> fireTest2(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//
//        context1.add("减少库存", Task.builder("测试方法1",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("123")
//        ));
//        context1.add("减少库存", Task.builder("测试方法2",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.add("减少库存", Task.builder("测试方法3",
//                s -> fireTest(s), Callback.builder(t -> rollback((Task) t)), new SomeContext("123")
//        ));
//
//        NEW.onEnter(fireCircuitBiConsumer(context1, "订单初始化")).onAction(EventContext.fireCircuitBiFunction(context1, "执行支付")).onExit(fireCircuitBiConsumer(context1, "减少库存")).transition("订单初始化",NEW).transition("支付完成", PAYED);
//
//        EventContext<String, SomeContext, String> context2 = EventContext.INSTANCE(String.class, SomeContext.class, String.class);
//
//        PAYED.onEnter(fireCircuitBiConsumer(context1, "订单开始备货")).transition("订单初始化",NEW).transition("支付完成", PAYED);
//        context1.add("订单开始备货", Task.builder("订单开始备货方法",
//                s -> fireTest(s), Callback.builder(t -> rollback2((Task) t)), new SomeContext("456")
//        ));
//        StateMachine stateMachine = StateMachine.instance(context1, NEW);
//        stateMachine.connect().subscribe();
//        stateMachine.accept("订单初始化");
////        stateMachine.accept("支付完成");
//        //执行完收集执行结果
//        System.out.println(stateMachine.collector());
//    }
//
//
//
//
//    private void rollback2(Task t) {
//        System.out.println("事物已经回滚～" + t.getName());
//
//    }
//
//    private static String fireTest2(SomeContext s) throws Exception {
//        throw new Exception("测试错处");
//    }
//
//    private static String fireTest(SomeContext s) {
//        System.out.println(s.getContext());
//        return s.context;
//    }
//
//    @Data
//    @AllArgsConstructor
//    public static class SomeContext {
//        String context;
//    }
//
//    private void rollback(Task t) {
//        System.out.println("事物已经回滚～" + t.getName());
//    }
//
//
//}
