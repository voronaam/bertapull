package ca.vorona.bertapull;

import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class BertaConnection {

    private final String host;
    private final int port;
    private EventLoopGroup group;
    private Channel channel;
    private BertaClientHandler handler;

    public BertaConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        group = new NioEventLoopGroup();
        handler = new BertaClientHandler();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast("frameDecoder", new LineBasedFrameDecoder(8000));
                    ch.pipeline().addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                    ch.pipeline().addLast(handler);
                    ch.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                }
            });

            // Start the connection attempt.
            channel = b.connect(host, port).sync().channel();
            if(!handler.getResponse().equals("Welcome to Berta!")) {
                throw new Exception("Could not reach Berta on the given host and port");
            }
        } catch(Exception e) {
            group.shutdownGracefully();
            channel = null;
            group = null;
            throw e;
        }
    }
    
    public void disconnect() throws Exception {
        try {
            if(channel != null) {
                channel.close().get();
            }
        } finally {
            if(group != null) {
                group.shutdownGracefully();
            }
            channel = null;
            group = null;
        }
    }

    public synchronized boolean check() throws InterruptedException {
        if(channel == null) {
            return false;
        }
        channel.writeAndFlush("BMAGIC\n");
        return handler.getResponse().equals("Magic is here!");
    }

}
