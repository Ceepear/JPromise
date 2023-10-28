package org.jpromise;

public interface PromiseExecutor<T> {
    void onExecutor(PromiseOnResolve<T> resolve,PromiseOnReject reject);
}
