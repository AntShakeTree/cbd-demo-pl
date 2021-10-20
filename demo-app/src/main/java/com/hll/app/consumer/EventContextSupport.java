package com.hll.app.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
public class EventContextSupport {
    private static Map<Object, List<ITask>> firesFunctions = new ConcurrentSkipListMap<>();

    public static <REQ, R> EventContext<REQ, String, R> createForString(Class<REQ> reqClass, Class<R> rClass) {
        return EventContext.INSTANCE(reqClass, String.class, rClass).cache();
    }

    public static <REQ, R> EventContext<REQ, String, R> create() {
        return EventContext.INSTANCE().cache();
    }


    public static <E, REQ, R> List<ITask> getTasks(E event) {
        return firesFunctions.get(event);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static <E> boolean containsTask(E event) {
        return firesFunctions.containsKey(event);
    }

    public static class Builder {
        private Builder() {
        }

        public <E, REQ, R> Builder add(E event, ITask<REQ, R> task) {
            if (!firesFunctions.containsKey(event)) {
                firesFunctions.put(event, new ArrayList<>());
                List list = firesFunctions.get(event);
                list.add(task);
            } else {
                List list = firesFunctions.get(event);
                list.add(task);
            }
            return this;
        }
    }


}

