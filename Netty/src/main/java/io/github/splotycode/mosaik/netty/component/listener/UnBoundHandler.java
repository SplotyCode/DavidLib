package io.github.splotycode.mosaik.netty.component.listener;

import io.github.splotycode.mosaik.util.listener.Listener;

import java.util.concurrent.Future;

public interface UnBoundHandler extends Listener {


    void unBound(Future channel);

}
