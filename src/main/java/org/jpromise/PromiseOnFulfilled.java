package org.jpromise;

public interface PromiseOnFulfilled<A>  {
    Object call(A a);
}
