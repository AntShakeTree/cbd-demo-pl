//package com.hll.app.consumer;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.Executors;
//
//@Slf4j
//class EventContextTest {
//    @Data
//    @AllArgsConstructor
//    public static class SomeContext {
//        String context;
//    }
//
//
//    private String fireTest(SomeContext someContext) {
//        log.info("================================" + someContext.context);
//        return someContext.getContext();
//    }
//
//    private String fireTest2(SomeContext someContext) throws Exception {
//        throw new Exception("发生异常");
//
//    }
//
//
////    @Test
////    void fireLift() {
////        EventContext<String, SomeContext, String> context1 = EventContext.INSTANCE(String.class, SomeContext.class, String.class);
////        context1.add("订单初始化", Task.builder("测试方法1",
////                s -> fireTest(s),  Callback.builder(t->rollback((Task) t)), new SomeContext("123")
////        ));
////        context1.add("订单初始化", Task.builder("测试方法2",
////                s -> fireTest2(s), Callback.builder(t->rollback((Task) t)), new SomeContext("123")
////        ));
////
////        ExecutorResult<String> executorResult = context1.parallelFireLift("订单初始化");
////        System.out.println(executorResult);
////
////
////
////    }
//
//    private void rollback(Task t) {
//        System.out.println("事物已经回滚～"+t.getName());
//    }
//
//
//
//    @Test
//    void fireCircuit() {
//
//    }
//
//    @Test
//    void fireCallback() {
//
//    }
//    @Test
//    void asynFireCallback(){
//        EventContext< SomeContext, String,String> context1 = EventContext.INSTANCE(SomeContext.class, String.class, String.class);
//        context1.add("订单初始化", Task.builder("测试方法1",
//                s -> fireTest(s),  Callback.builder(t->rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.add("订单初始化", Task.builder("测试方法2",
//                s -> fireTest2(s), Callback.builder(t->rollback((Task) t)), new SomeContext("123")
//        ));
//        System.out.println(context1.asynFireCallback("订单初始化", Executors.newSingleThreadExecutor()));
//
//    }
//
//    @Test
//    void asynFire(){
//        EventContext<SomeContext,String,  String> context1 = EventContext.INSTANCE(SomeContext.class,String.class,  String.class);
//        context1.add("订单初始化", Task.builder("测试方法1",
//                s -> fireTest(s),  Callback.builder(t->rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.add("订单初始化", Task.builder("测试方法2",
//                s -> fireTest2(s), Callback.builder(t->rollback((Task) t)), new SomeContext("123")
//        ));
//        context1.asynFire("订单初始化", Executors.newSingleThreadExecutor());
//
//    }
//}