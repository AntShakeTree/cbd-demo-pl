package com.hll.app.consumer;


import com.google.common.collect.Lists;
import com.hll.app.statemachine.event.EventReq;
import com.hll.app.statemachine.event.EventRes;
import com.hll.app.statemachine.state.State;
import com.hll.exceptions.BizException;
import com.hll.exceptions.ErrorContext;
import com.hll.exceptions.ErrorType;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
public class EventContext<REQ, E, R> {
    private static final ThreadLocal<EventContext> LOCAL = new ThreadLocal<>();
    @Setter
    private REQ request;
    private final Map<E, List<R>> resultCaches;
    private EventContext() {
        this.resultCaches = new HashMap<>();
    }
    public static <REQ, E, R> EventContext<REQ, E, R> INSTANCE() {
        if (EventContext.find().isPresent()) {
            return EventContext.find().get();
        }

        return new EventContext();
    }

    public static <T> void request(EventReq<T> integerEventReq) {
        EventContext.find().ifPresent(eventContext -> eventContext.setRequest(integerEventReq));
    }

    public static <T> EventContext request(T cotenxt) {
        if (!EventContext.find().isPresent()) {
            EventContextSupport.create();
        }
        EventContext.find().ifPresent(eventContext -> eventContext.setRequest(cotenxt));
        return EventContext.find().get();
    }


    private final EventContext set() {
        LOCAL.set(this);
        if (this.resultCaches != null) {
            this.resultCaches.clear();
        }
        return this;
    }

    public EventContext add(E e, ITask iTask) {
        EventContextSupport.builder().add(e, iTask);
        return this;
    }

    public EventContext add(E e, String name, Function<REQ, R> function, Callback callback, REQ context) {
        EventContextSupport.builder().add(e,
                TaskFactory.builder(name,
                        function,
                        callback,
                        context)
        );
        return this;
    }


    public static void clear() {
        if (EventContext.find().isPresent()) {
            EventContext.find().get().getResultCaches().clear();
            LOCAL.remove();
        }
    }


    public static Optional<EventContext> find() {
        return Optional.ofNullable(LOCAL.get());
    }


    public static <REQ> REQ getRequest(Class<REQ> req) {
        return getRequest();
    }

    public static <REQ> REQ getRequest() {
        if (EventContext.find().isPresent()) {
            return (REQ) EventContext.find().get().request;
        }
        throw new BizException("?????????????????????");
    }

    /**
     * ??????????????????????????????
     * ????????????,????????????
     *
     * @param event
     * @return
     */

    public ExecutorCallback<REQ, R> fireCircuit(E event) {
        log.info("fireCircuit:{}", event);
        if (!EventContextSupport.containsTask(event)) {
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        List<ITask> failTask = new ArrayList<>();
        for (ITask<REQ, R> task : tasks) {
            Task task1 = (Task) task;
            REQ req = (REQ) task1.get();
            if (req == null) {
                req = EventContext.getRequest();
                if (req == null)
                    throw new BizException("??????????????????????????????????????????");
            }
            R r = (R) task1.action(req);
            executorResult.addCallback((Task<REQ, R>) task);
            if (((Task<?, ?>) task).isFail()) {
                failTask.add((Task) task);
                executorResult.setFail(true);
            } else {
                executorResult.add(r);
                putResults(event, r);
            }
        }
        /**
         * ??????
         */
        if (executorResult.isFail()) {
            executorResult.getCallbacks().stream().forEach(kv -> {
                kv.getCallback().callback(kv);

            });

            //????????????????????????
            ErrorContext.instance().cause(new BizException(ErrorType.ILLEGAL_ARGUMENT_ERROR.getCode(), String.join(":", "??????????????????", failTask.toString())));
        }
        return executorResult;
    }

    /**
     * ???????????????
     * ????????????????????????????????????????????????
     * ??????????????????????????????
     *
     * @param event
     * @return
     */
    public ExecutorCallback<REQ, R> fireCallback(E event) {
        if (!EventContextSupport.containsTask(event)) {
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        for (ITask<REQ, R> task : tasks) {
            REQ req = task.get();
            if (req == null) {
                req = EventContext.getRequest();
                if (req == null)
                    throw new BizException("??????????????????????????????????????????");
            }
            R r = (R) ((Task) task).action(req);
            executorResult.addCallback((Task<REQ, R>) task);
            if (task.isFail()) {
                executorResult.setFail(true);
            } else {
                executorResult.add(r);
            }
        }
        return executorResult;
    }

    /**
     * ????????????,??????????????????????????????????????????????????????????????????
     *
     * @param event
     * @return
     */
    public ExecutorCallback<REQ, R> asynFireCallback(E event, ExecutorService executorService) {
        if (!EventContextSupport.containsTask(event)) {
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);

        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        List<Future<R>> futures = new ArrayList<>();
        for (ITask<REQ, R> task : tasks) {
            if (task.get() == null) {
                if (getRequest() == null)
                    throw new BizException("??????????????????????????????????????????");
                task.context(request);
            }
            Future<R> future = executorService.submit((Task) task);
            futures.add(future);
        }
        futures.stream().forEach(f -> {
            try {
                R r = f.get();
                executorResult.add(r);
                putResults(event, r);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //donoting
            } catch (ExecutionException e) {
                e.printStackTrace();
                //donoting
            }
        });
        return executorResult;
    }

    /**
     * ?????????????????????????????????
     *
     * @param event
     * @param executorService
     */

    public void asynFire(E event, ExecutorService executorService) {
        if (!EventContextSupport.containsTask(event)) {
            return;
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        tasks.stream().forEach(REQ -> executorService.submit((Task) REQ));
    }


    /**
     * ??????????????????????????????
     * ????????????,????????????
     *
     * @param event
     * @return
     */

    public ExecutorCallback<REQ, R> fireCircuit(E event, REQ req) {
        log.info("fireCircuit:{}", event);
        if (!EventContextSupport.containsTask(event)) {
            log.info("fireCircuit exit:  {}", event);
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        List<ITask> failTask = new ArrayList<>();
        for (ITask<REQ, R> task : tasks) {
            if (this.request != null) {
                req = this.request;
            }
            if (executorResult.isFail()) {
                break;
            }
            if (req == null) {
                throw new BizException("fireCircuit###???????????????.");
            }

            R r = (R) task.action(req);
            executorResult.addCallback((Task<REQ, R>) task);
            if (task.isFail()) {
                failTask.add(task);
                executorResult.setFail(true);
            } else {
                executorResult.add(r);
                putResults(event, r);
            }
        }
        /**
         * ??????
         */
        if (executorResult.isFail()) {
            executorResult.getCallbacks().stream().forEach(kv -> {
                kv.getCallback().callback(kv);
            });
            EventContext.clear();
            //????????????????????????
            ErrorContext.find().ifPresent(errorContext -> {
                throw errorContext.getCause();
            });
        }
        return executorResult;
    }

    private void putResults(E e, R r) {
        if (EventContext.find().isPresent()) {
            Map<E, List<R>> map = EventContext.find().get().resultCaches;
            if (!map.containsKey(e)) {
                map.put(e, new ArrayList<>());
            }
            map.get(e).add(r);
        }
    }

    /**
     * ???????????????
     * ????????????????????????????????????????????????
     * ??????????????????????????????
     *
     * @param event
     * @return
     */
    public ExecutorCallback<REQ, R> fireCallback(E event, REQ req) {
        if (!EventContextSupport.containsTask(event)) {
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        for (ITask<REQ, R> task : tasks) {
            task.context(req);
            R r = (R) ((Task) task).action();
            executorResult.addCallback((Task<REQ, R>) task);
            putResults(event, r);
            if (task.isFail()) {
                EventContext.clear();
                executorResult.setFail(true);
            } else {
                executorResult.add(r);
            }
        }

        return executorResult;
    }

    /**
     * ????????????,??????????????????????????????????????????????????????????????????
     *
     * @param event
     * @return
     */
    public ExecutorCallback<REQ, R> asynFireCallback(E event, REQ req, ExecutorService executorService) {
        if (!EventContextSupport.containsTask(event)) {
            return ExecutorCallback.INSTANCE();
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);

        ExecutorCallback<REQ, R> executorResult = ExecutorCallback.INSTANCE();
        List<Future<R>> futures = new ArrayList<>();
        for (ITask<REQ, R> task : tasks) {
            task.context(req);
            Future<R> future = executorService.submit((Task) task);
            futures.add(future);
        }
        futures.stream().forEach(f -> {
            try {
                executorResult.add(f.get());
                putResults(event, f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        //


        return executorResult;
    }

    /**
     * ?????????????????????????????????
     *
     * @param event
     * @param executorService
     */

    public void asynFire(E event, REQ req, ExecutorService executorService) {
        if (!EventContextSupport.containsTask(event)) {
            return;
        }
        List<ITask> tasks = EventContextSupport.getTasks(event);
        tasks.stream().peek(REQ -> REQ.context(req)).forEach(REQ -> executorService.submit((Task) REQ));
    }


    public EventContext cache() {
        set();
        return this;
    }

    public static <E, R> Map<E, List<R>> getResultCaches() {
        if (EventContext.find().isPresent()) {
            return EventContext.find().get().resultCaches;
        } else {
            return new HashMap();
        }
    }


    public static <E, R> List<R> getResultCaches(E e, Class<R> rClass) {
        return getResultCaches(e);
    }

    public static <E, R> List<R> getResultCaches(E e) {
        if (EventContext.find().isPresent()) {
            Map<E, List> map = EventContext.find().get().resultCaches;
            if (map.get(e) == null) {
                return Lists.newArrayList();
            } else {
                return map.get(e);
            }
        } else {
            return Lists.newArrayList();
        }
    }

}
