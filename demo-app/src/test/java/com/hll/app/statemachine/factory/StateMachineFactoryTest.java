package com.hll.app.statemachine.factory;

import com.hll.app.consumer.*;
import com.hll.app.statemachine.StateMachine;
import org.junit.jupiter.api.Test;


import static com.hll.app.consumer.EventContext.getRequest;
import static org.junit.jupiter.api.Assertions.*;

class StateMachineFactoryTest {
    @Test
    public  void init() {
        StateMachineFactory.newBuilder()
                .state("aState").from(t -> System.out.println(t.toString())).process(s->test((String) s+"action"))
                .context("aaaaaaaaaaaaaaaa").start()
                .to("to", "bState")
                .from(t->test((String) t+"sdafasdfjaksljdfkljasdf")).process(s->test((String) s+"action"));





        EventContextSupport.create().add("ceshi",
                TaskFactory.builder("name",t->test(t), Callback.Non(),""));

        EventContextSupport.create().add("ceshi2",
                TaskFactory.builderSupplier("name",t->test(t+"attttttttttttttttt"), ()-> getRequest()));




        EventContext.find().get().setRequest("123123");
        EventContext.find().get().fireCircuit("ceshi2");
        StateMachine.action("to").close();
    }
    public static String test(String s){
        System.out.println(s);
        return s;
    }


}
