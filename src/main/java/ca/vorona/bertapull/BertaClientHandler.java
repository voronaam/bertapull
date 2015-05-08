package ca.vorona.bertapull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class BertaClientHandler extends SimpleChannelInboundHandler<String> {
    
    private final BlockingQueue<String> responses = new LinkedBlockingQueue<>();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        responses.offer(msg);
        System.out.println(msg);
    }

    public String getResponse() throws InterruptedException {
        return responses.take();
    }


}
