package org.jpromise;

public interface PromiseOnResolve<K> extends PromiseOnFulfilled<K>{
    @Override
    default K call(K o){
        call2(o);
        return null;
    }
    void call2(K t);
}
