package com.hll.app.statemachine;

import com.hll.app.statemachine.state.State;
import io.reactivex.functions.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class StateMachineTest {

    /**
     * 测试状态，正式环境不要用中文
     */
    public static enum Event {
        准备,
        开始执行,
        执行完成,
        执行失败,
        回滚
    }
    
    public static BiConsumer<SomeContext, State<SomeContext, Event,String>> log(final String text) {
        return (t1, state) -> System.out.println("" + t1 + ":" + state + ":" + text);
    }
    public static String exec(final String text) {
        System.out.println(text);
        return text;
    }

    public static class SomeContext {
        @Override
        public String toString() {
            return "Foo []";
        }
    }
    
    public static State<SomeContext, Event,String> IDLE        = new State<SomeContext, Event,String>("准备");
    public static State<SomeContext, Event,String> CONNECTING  = new State<SomeContext, Event,String>("执行中");
    public static State<SomeContext, Event,String> CONNECTED   = new State<SomeContext, Event,String>("成功");
    public static State<SomeContext, Event,String> QUARANTINED = new State<SomeContext, Event,String>("隔离");
    public static State<SomeContext, Event,String> REMOVED     = new State<SomeContext, Event,String>("回滚");
    
    @BeforeAll
    public  static void beforeClass() {
        IDLE
            .onEnter(log("enter"))
            .onExit(log("exit"))
            .transition(Event.开始执行, CONNECTING);
//            .transition(Event.回滚,  REMOVED);
        
        CONNECTING
            .onEnter(log("enter"))
            .onExit(log("exit"))
            .transition(Event.执行完成, CONNECTED)
            .transition(Event.回滚,  QUARANTINED);
//            .transition(Event.回滚,  REMOVED);
    
        CONNECTED
            .onEnter(log("enter"))
            .onExit(log("exit"))
            .transition(Event.准备,    IDLE)
            .transition(Event.执行完成,  QUARANTINED)
            .transition(Event.回滚,  REMOVED);
    
        QUARANTINED
            .onEnter(log("enter"))
            .onExit(log("exit"))
            .transition(Event.准备,    IDLE)
            .transition(Event.回滚,  REMOVED);
    
        REMOVED
            .onEnter(log("enter"))

            .onExit(log("exit"))
            .transition(Event.开始执行, CONNECTING);
    }
    @Test
    public void machine() {
        
        StateMachine<SomeContext, Event,String> sm = new StateMachine<SomeContext, Event,String>(new SomeContext(), IDLE);
        sm.connect().subscribe();

        sm.accept(Event.开始执行);
        sm.accept(Event.执行完成);
        sm.accept(Event.执行失败);
        sm.accept(Event.回滚);
    }


}