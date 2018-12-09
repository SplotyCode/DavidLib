package me.david.webapi.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import lombok.AllArgsConstructor;
import me.david.webapi.request.Method;
import me.david.webapi.request.Request;
import me.david.webapi.response.Response;

import java.util.Map;

import static me.david.webapi.server.netty.NettyUtils.*;

@ChannelHandler.Sharable
@AllArgsConstructor
public class WebServerHandler extends SimpleChannelInboundHandler {

    private NettyWebServer server;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest nettyRequest = (FullHttpRequest) msg;
            if (nettyRequest.decoderResult() != DecoderResult.SUCCESS) {
                ctx.close();
                return;
            }
            QueryStringDecoder uri = new QueryStringDecoder(nettyRequest.uri());
            Request request = new Request(
                    uri.path(),
                    transformIpAddress(ctx.channel().remoteAddress().toString()),
                    new Method(nettyRequest.method().name()),
                    HttpUtil.isKeepAlive(nettyRequest),
                    nettyRequest.content().array()
            );
            request.setGet(uri.parameters());

            long start = System.currentTimeMillis();
            Response response = server.handleRequest(request);
            response.finish(request, server.getApplication());
            server.addTotalTime(System.currentTimeMillis() - start);

            ByteBuf content = Unpooled.buffer(response.getRawContent().available());
            content.writeBytes(response.getRawContent(), response.getRawContent().available());

            DefaultFullHttpResponse nettyResponse = new DefaultFullHttpResponse(
                    convertHttpVersion(response.getHttpVersion()),
                    HttpResponseStatus.valueOf(response.getResponseCode()),
                    content
            );
            for (Map.Entry<String, String> pair : response.getHeaders().entrySet()) {
                nettyResponse.headers().set(pair.getKey(), pair.getValue());
            }
            ctx.writeAndFlush(nettyResponse);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Response response = server.getErrorHandler().handleError(cause);
        response.finish(null, server.getApplication());
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(response.getRawContent(), response.getRawContent().available());
        DefaultFullHttpResponse nettyResponse = new DefaultFullHttpResponse(
                convertHttpVersion(response.getHttpVersion()),
                HttpResponseStatus.valueOf(response.getResponseCode()),
                byteBuf
        );
        for (Map.Entry<String, String> pair : response.getHeaders().entrySet()) {
            nettyResponse.headers().set(pair.getKey(), pair.getValue());
        }
        ctx.writeAndFlush(nettyResponse);
    }
}
