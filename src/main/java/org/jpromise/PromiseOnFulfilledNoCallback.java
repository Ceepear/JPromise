package org.jpromise;

public interface PromiseOnFulfilledNoCallback<T> extends PromiseOnFulfilled<T>{
    @Override
    default Object call(T t){
        call2(t);
        return null;
    }
    void call2(T t);
}
