package com.hll.app.statemachine.factory;

import com.hll.app.consumer.EventContext;
import com.hll.app.consumer.EventContextSupport;
import com.hll.app.consumer.ExecutorCallback;
import com.hll.app.statemachine.StateMachine;
import com.hll.app.statemachine.event.EventReq;
import com.hll.app.statemachine.event.EventRes;
import com.hll.app.statemachine.state.State;
import com.hll.exceptions.ExceptionFunction;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

import java.util.function.Function;

public abstract class StateMachineFactory {


    public static <E, REQ> BiConsumer<EventReq, State<EventReq<REQ>, E, EventRes>> convertBiConsumerFromFunction(Function<EventReq, EventRes> function) {
        return (t1, state) -> function.apply(t1);
    }


    public static <E, REQ> BiConsumer<EventContext, State<EventContext, E, ExecutorCallback>> fireCircuitBiConsumer(E e, REQ req) {
        return (t1, state) -> t1.fireCircuit(e, req);
    }


    public static <E> BiFunction<EventContext, State<EventContext, E, ExecutorCallback>, ExecutorCallback> fireCircuitBiFunction(final EventContext text, E e) {

        return (t1, state) -> text.fireCircuit(e);
    }

    public static <E> BiFunction<EventContext, State<EventContext, E, ExecutorCallback>, ExecutorCallback> fireCircuitBiFunction(E e) {

        return (t1, state) -> t1.fireCircuit(e);
    }

    public static <E, REQ> BiFunction<EventContext, State<EventContext, E, ExecutorCallback>, ExecutorCallback> fireCircuitBiFunction(final EventContext text, E e, REQ req) {

        return (t1, state) -> text.fireCircuit(e, req);
    }

    public static <E, REQ> BiFunction<EventContext, State<EventContext, E, ExecutorCallback>, ExecutorCallback> fireCircuitBiFunction(E e, REQ req) {

        return (t1, state) -> t1.fireCircuit(e, req);
    }

    public static <E> BiConsumer<EventContext, State<EventContext, E, ExecutorCallback>> fireCircuitBiConsumer(final EventContext context, E e) {
        return (t1, state) -> context.fireCircuit(e);
    }

    public static <REQ, E> BiConsumer<EventContext, State<EventContext, E, ExecutorCallback>> fireCircuitBiConsumer(final EventContext context, E e, REQ req) {
        return (t1, state) -> context.fireCircuit(e, req);
    }

    public static <E> BiConsumer<EventContext, State<EventContext, E, ExecutorCallback>> fireCircuitBiConsumer(E e) {
        return (t1, state) -> t1.fireCircuit(e);
    }

    public static <T, E, R> BiConsumer<E, State<T, E, R>> biConsumerFromFunction(Function<T, R> function) {
        return (t1, state) -> function.apply((T) t1);
    }

    public static <E> BiConsumer<E, State<Object, E, Object>> biConsumer(Consumer function, Class<E> e) {
        return (t1, state) -> function.accept(t1);
    }


    public static StateMachine stateMachineConnect(State state) {
        StateMachine stateMachine = StateMachine.instance(EventContextSupport.createForString(EventReq.class, EventRes.class), state);
        stateMachine.connect().subscribe();
        return stateMachine;
    }

    public static <T> StateMachine stateMachineConnect(T context, State state) {
        StateMachine stateMachine = StateMachine.instance(context, state);
        stateMachine.connect().subscribe();
        return stateMachine;
    }


//    CamelHelloRequest request = CamelHelloRequest.newBuilder().setName("Camel").build();
//
//    from("timer://foo?period=2000&repeatCount=3")
//                .
//
//    process(exchange ->exchange.getIn().
//
//    setBody(request, CamelHelloRequest .class))
//            .
//
//    to("grpc://localhost:50051/org.apache.camel.examples.CamelHello?method=sayHelloToCamel&synchronous=true")
//                .
//
//    process(exchange ->System.out.println("grpc的返回值是:  "+exchange.getIn().
//
//    getBody()));





    static StateMachine newBuilder() {
        return StateMachine.instance();
    }


    public static <E, R, T> BiConsumer<T, State<T, E, R>> biConsumer(Consumer o) {
        return (t, state) -> o.accept(t);
    }

    public static BiFunction biFunction(ExceptionFunction function) {
        return (t, state) -> function.applyThrow(t);
    }
}

