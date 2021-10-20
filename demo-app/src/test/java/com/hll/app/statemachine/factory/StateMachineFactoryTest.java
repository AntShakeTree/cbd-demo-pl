package com.hll.app.statemachine.factory;

import com.hll.app.consumer.EventContext;
import com.hll.app.consumer.EventContextSupport;
import com.hll.app.statemachine.StateMachine;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class StateMachineFactoryTest {
    @Test
    public  void init() {
        StateMachineFactory.newBuilder()
                .state("aState").from(t -> System.out.println(t.toString())).process(s->test((String) s+"action"))
                .context("aaaaaaaaaaaaaaaa").start()
                .to("to", "bState")
                .from(t->test((String) t+"sdafasdfjaksljdfkljasdf")).process(s->test((String) s+"action"));


        EventContextSupport.getTasks()


        StateMachine.action("to").close();
    }
    public static String test(String s){
        System.out.println(s);
        return s;
    }


}
