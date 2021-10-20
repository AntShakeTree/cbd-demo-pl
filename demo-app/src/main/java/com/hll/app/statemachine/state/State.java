package com.hll.app.statemachine.state;

import com.hll.exceptions.BizException;
import com.hll.exceptions.ErrorContext;
import com.hll.exceptions.ErrorType;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @param <T>
 * @param <E>
 * @CaptionMa 利用RXJava
 */
@Slf4j
@EqualsAndHashCode
public class State<T, E, R> implements Comparable<State> {
    private static final Logger LOG = LoggerFactory.getLogger(State.class);
    @Getter
    private String name;
    private BiFunction<T, State<T, E, R>, R> action;
    private BiConsumer<T, State<T, E, R>> enter;
    private BiConsumer<T, State<T, E, R>> exit;
    @Getter
    @Setter
    protected StateType stateType;
    @Getter
    private List<Exception> errors;
    @Getter
    private volatile boolean onError = true;
    @Getter
    private volatile boolean fail = false;


    private Map<E, State<T, E, R>> transitions = new HashMap<>();

    public State(String name) {
        this.name = name;
        this.setStateType(StateType.DEFAULT);
    }


    public State<T, E, R> onEnter(BiConsumer<T, State<T, E, R>> func) {

        this.enter = func;
        return this;
    }





    public State<T, E, R> onExit(BiConsumer<T, State<T, E, R>> func) {

        this.exit = func;
        return this;
    }

    public State<T, E, R> onAction(BiFunction func) {
        this.action = func;
        return this;
    }

    public void enter(T context) {

        try {
            if (enter == null) {
                return;
            }
            enter.accept(context, this);
        } catch (Exception e) {
            System.err.println(e);
            fail = true;
            if (onError) {
                ErrorContext.instance().cause(new BizException(ErrorType.ILLEGAL_ARGUMENT_ERROR, e));
            } else if (errors != null) {
                errors.add(e);
            } else {
                log.warn("执行失败～");
            }
        }
    }

//    public R

    public void exit(T context) {
        try {
            if (exit == null || fail) {
                return;
            }
            exit.accept(context, this);
        } catch (Exception e) {
            System.err.println(e);
            fail = true;
            if (onError) {
                ErrorContext.instance().cause(new BizException(ErrorType.ILLEGAL_ARGUMENT_ERROR, e));
            } else if (errors != null) {
                errors.add(e);
            } else {
                log.warn("执行失败～");
            }
        }
    }

    public State<T, E, R> onError(boolean error) {
        this.onError = error;
        if (this.onError != true) {
            this.errors = new ArrayList<>();
        }
        return this;
    }

    public State<T, E, R> onError(List<Exception> errors, boolean error) {
        this.onError = error;
        this.errors = errors;

        return this;
    }

    public R action(T context) {
        try {
            if (action == null || fail) {
                log.warn("action is null");
                return null;
            }
            if (context == null) {
                log.warn("context is null");
                return null;
            }
            return action.apply(context, this);
        } catch (Exception e) {
            fail = true;
            if (onError) {
                System.err.println(e);
                ErrorContext.instance().cause(new BizException(ErrorType.ILLEGAL_ARGUMENT_ERROR, e));
                return null;
            } else if (errors != null) {
                errors.add(e);
                return null;
            } else {
                log.warn("执行失败～");
                return null;
            }
        }
    }


    public State<T, E, R> transition(E event, State<T, E, R> state) {
        if (!fail)
            transitions.put(event, state);
        return this;
    }

    public State<T, E, R> next(E event) {
        return transitions.get(event);
    }

    public String toString() {
        return name + ":  " + stateType;
    }


    public State enterType() {
        State state = new State<>(name);
        state.setStateType(StateType.ENTER);
        return state;
    }

    public State actionType() {
        State state = new State<>(name);
        state.setStateType(StateType.ACTION);
        return state;
    }

    public State exitType() {
        State state = new State<>(name);
        state.setStateType(StateType.EXIT);
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State<?, ?, ?> state = (State<?, ?, ?>) o;
        return name.equals(state.name) && stateType == state.stateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stateType);
    }

    @Override
    public int compareTo(State o) {
        if (this == null && o == null) {
            return 0;
        }
        if (this == null) {
            return -1;
        }
        if (o == null) {
            return 1;
        }

        if (name.equals(o.name)) {
            if (this.stateType != null && o.stateType != null) {
                this.stateType.compareTo(o.stateType);
            }
        }
        return name.compareTo(o.name);
    }
}
