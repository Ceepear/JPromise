package org.jpromise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @param <T> 是成功调用回调的参数类型
 */
public class Promise<T> {
    private List<Handler<T>> handlers = new ArrayList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(12);
    private final int FULFILLED = -1;
    private final int PENDING = 0;
    private final int REJECTED = 1;
    private void microQueueRun(Runnable runnable){
        executorService.submit(runnable);
    }
    private int state = PENDING;
    private Object result;
    private T t;
    private PromiseOnResolve<T> resolve = t -> {
        changeState(FULFILLED,t);
    };
    private PromiseOnReject reject = k->{
        changeState(REJECTED,k);
    };
    private void changeState(int state,Object result){
        if(this.state != PENDING)return;
        this.state = state;
        this.result = result;
        microQueueRun(this::run);
    }
    public Promise<T> call(PromiseExecutor<T> promiseExecutor){
        try{
            promiseExecutor.onExecutor(this.resolve,this.reject);
        }catch(Exception e){
            reject.call(new PromiseException(e.getMessage()));
        }
        return this;
    }
    public Promise(){

    }
    public synchronized T await(){
        while(this.result == null){
            try {
                this.wait(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.notify();
        return (T) this.result;
    }
    private PromiseOnCatch onCatch = null;
    public void thisCatch(PromiseOnCatch onCatch){
        this.onCatch = onCatch;
    }
    public void thisFinally(){

    }
    private class Handler<T>{
        PromiseOnReject rejectd;
        PromiseOnFulfilled<T> fulfilled;
        PromiseOnReject reject;
        PromiseOnResolve<T> resolve;
    }

    private void run(){
        if (this.state == PENDING)return;
        for(Handler<T> handler:handlers){
            if(this.state == FULFILLED){
                if(handler.fulfilled == null){
                    handler.resolve.call((T)this.result);
                }
                else{
                    try{
                        if(handler.fulfilled instanceof PromiseOnFulfilledNoCallback){
                            handler.resolve.call((T)this.result);
                        }else{
                            Object object = handler.fulfilled.call((T)this.result);
                            if(object instanceof Promise){
                                Promise promise = (Promise) object;
                                promise.then(handler.resolve,handler.rejectd);
                                handlers.remove(handler);
                                return;
                            }
                            handler.resolve.call((T)object);
                        }
                    }catch(Exception e){
                        handler.reject.call(new PromiseException(e.getMessage()));
                        handlers.remove(handler);
                    }
                }
            }else{
                if(handler.rejectd == null){
                    handler.reject.call((PromiseException)this.result);
                }else{
                    try{
                        handler.rejectd.call((PromiseException)this.result);
                    }catch(Exception e){
                        handler.rejectd.call(new PromiseException(e.getMessage()));
                    }
                }
            }
            handlers.remove(handler);
        }
    }

    public Promise<T> then(PromiseOnFulfilled<T> onFulfilled, PromiseOnReject onReject){
        Promise<T> promise = new Promise<>();
        promise.call((resolve, reject) -> {
            Handler handler = new Handler();
            handler.fulfilled = onFulfilled;
            handler.resolve = resolve;
            handler.reject = reject;
            handler.rejectd = onReject;
            handlers.add(handler);
            microQueueRun(this::run);
        });
        return promise;
    }
    public Promise<T> then(PromiseOnFulfilledNoCallback<T> onFulfilled, PromiseOnReject onReject){
        Promise<T> promise = new Promise<>();
        promise.call((resolve, reject) -> {
            Handler handler = new Handler();
            handler.fulfilled = onFulfilled;
            handler.resolve = resolve;
            handler.reject = reject;
            handler.rejectd = onReject;
            handlers.add(handler);
            microQueueRun(this::run);
        });
        return promise;
    }
    public Promise<T> then(PromiseOnFulfilledNoCallback<T> onFulfilled){
        Promise<T> promise = new Promise<>();
        promise.call((resolve, reject) -> {
            Handler handler = new Handler();
            handler.fulfilled = onFulfilled;
            handler.resolve = resolve;
            handler.reject = reject;
            handler.rejectd = null;
            handlers.add(handler);
            microQueueRun(this::run);
        });
        return promise;
    }
    public Promise<T> then(PromiseOnFulfilled<T> onFulfilled){
        Promise<T> promise = new Promise<>();
        promise.call((resolve, reject) -> {
            Handler handler = new Handler();
            handler.fulfilled = onFulfilled;
            handler.resolve = resolve;
            handler.reject = reject;
            handler.rejectd = null;
            handlers.add(handler);
            microQueueRun(this::run);
        });
        return promise;
    }
}
