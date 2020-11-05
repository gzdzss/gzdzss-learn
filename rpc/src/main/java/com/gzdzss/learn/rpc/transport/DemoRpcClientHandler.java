package com.gzdzss.learn.rpc.transport;

import com.gzdzss.learn.rpc.Constants;
import com.gzdzss.learn.rpc.protocol.Message;
import com.gzdzss.learn.rpc.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Andrew
 * @create 2020/11/5 5:45 下午
 */
public class DemoRpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Response> message) throws Exception {
        NettyResponseFuture responseFuture =
                Connection.IN_FLIGHT_REQUEST_MAP.remove(message.getHeader().getMessageId());
        Response response = message.getContent();
        // 心跳消息特殊处理
        if (response == null && Constants.isHeartBeat(message.getHeader().getExtraInfo())) {
            response = new Response();
            response.setCode(Constants.HEARTBEAT_CODE);
        }
        responseFuture.getPromise().setSuccess(response.getResult());
    }
}
