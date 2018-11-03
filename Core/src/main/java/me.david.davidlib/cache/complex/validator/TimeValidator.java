package me.david.davidlib.cache.complex.validator;

import me.david.davidlib.cache.Cache;

public class TimeValidator<T> implements CacheValidator<T> {

    private long started = System.currentTimeMillis();

    private long delay;

    public TimeValidator(long delay) {
        this.delay = delay;
    }

    @Override
    public boolean isValid(Cache<T> cache, T value) {
        long delay = System.currentTimeMillis() - started;
        return delay <= this.delay;
    }

    @Override
    public void valueChange(T value) {
        started = System.currentTimeMillis();
    }
}
