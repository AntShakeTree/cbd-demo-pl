package com.hll.app.statemachine;

import com.hll.app.consumer.EventContext;
import com.hll.app.consumer.ITask;
import com.hll.app.statemachine.factory.StateMachineFactory;
import com.hll.app.statemachine.state.State;
import com.hll.exceptions.BizException;
import com.hll.exceptions.ErrorContext;
import com.hll.exceptions.ExceptionFunction;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StateMachine<T, E, R> implements Consumer<E> {
    private static final Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    @Getter
    private static final Map<Long, StateMachine> start = new HashMap<>(1);
    @Getter
    private volatile State<T, E, R> state;
    private volatile StateMachine<T, E, R> next;
    @Getter
    private T context;
    @Getter
    private final PublishSubject<E> events = PublishSubject.create();

    protected StateMachine(T context, State<T, E, R> initial) {
        this.state = initial;
        this.context = context;
    }

    protected StateMachine() {
    }

    public static <E> StateMachine action(E event) {
        start.get(Thread.currentThread().getId()).events.onNext(event);
        return start.get(Thread.currentThread().getId());
    }

    public StateMachine state(String name) {
        this.state = new State<>(name);
        return this;
    }

    public static <T, E, R> StateMachine instance(T context, State<T, E, R> initial) {
        return new StateMachine(context, initial);
    }

    public static StateMachine instance() {
        return new StateMachine();
    }


    public Observable<Void> connect() {
        return Observable.create(sub -> {
            state.enter(context);
            sub.setDisposable(events.collect(() -> context, (context, event) -> {
                final State<T, E, R> next = state.next(event);
                if (next != null) {
                    state.action(context);
                    state.exit(context);
                    state = next;
                    next.enter(context);
                    next.action(context);
                } else {
                    LOG.info("Invalid event : " + event);
                }
            }).subscribe());
        });
    }

    public StateMachine start() {
        if (this == null || this.context == null) {
            throw new BizException("事件应用的上下文为空，不能启动");
        }
        start.put(Thread.currentThread().getId(), this);

        this.connect().subscribe();
        return this;
    }


    private T getContext() {
        return this.context;
    }

    public void clearResult() {
        EventContext.clear();
    }

    /**
     * 收集执行结果
     *
     * @return
     */
    public List<R> collector() {
        if (EventContext.find().isPresent()) {
            Map<E, List<R>> rs = EventContext.find().get().getResultCaches();
            List<R> list = new ArrayList<>();
            for (Map.Entry<E, List<R>> entry : rs.entrySet()) {
                list.addAll(entry.getValue());
            }
        }
        return new ArrayList<>();
    }


    @Override
    public void accept(E event) {
        if (start.get(Thread.currentThread().getId()) != null) {
            start.get(Thread.currentThread().getId()).events.onNext(event);
        } else {
            events.onNext(event);
        }

    }

    public State<T, E, R> getState() {
        return state;
    }

    private void check(State<T, E, R> state) {
        try {
            if (state.isOnError()) {
                if (ErrorContext.find().isPresent()) {
                    throw ErrorContext.find().get().getCause();
                }
            }
        } finally {
            if (ErrorContext.find().isPresent())
                ErrorContext.find().get().reset();
        }
    }

    public StateMachine from(Consumer o) {
        this.state.onEnter(StateMachineFactory.biConsumer(o));
        return this;
    }

    public StateMachine exit(Consumer o) {
        this.state.onExit(StateMachineFactory.biConsumer(o));
        return this;
    }

    public StateMachine process(ExceptionFunction function) {
        this.getState().onAction(StateMachineFactory.biFunction(function));
        return this;
    }

    public StateMachine context(T context) {
        this.context = context;
        return this;
    }

    public StateMachine<T, E, R> to(E event, String stateName) {
        this.next = StateMachine.instance().state(stateName);
        this.state.transition(event, this.next.getState());
        return this.next;
    }

    public void close() {
        start.remove(Thread.currentThread().getId());
    }

}