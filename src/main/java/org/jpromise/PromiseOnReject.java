package org.jpromise;

public interface PromiseOnReject {
    void call(PromiseException t);
}
