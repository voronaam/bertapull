package ca.vorona.bertapull;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BertaConnection {

    private final String host;
    private final int port;
    private final Indexer indexer;
    private EventLoopGroup group;
    private Channel channel;
    private BertaClientHandler handler;

    public BertaConnection(String host, int port, Indexer indexer) {
        this.host = host;
        this.port = port;
        this.indexer = indexer;
    }

    public synchronized void connect() throws Exception {
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
    
    public synchronized void disconnect() throws Exception {
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

    public synchronized void setTest(String name) throws Exception {
        if(channel == null) {
            throw new LogicException("Not connected");
        }
        name = name.replace('\n', '.'); // Make sure we don't break our simple text API
        channel.writeAndFlush(name + "\n");
        String testName = handler.getResponse();
        List<String> methods = new ArrayList<String>(2000);
        while(true) {
            String method = handler.getResponse();
            if(method.equals("--END--")) {
                break;
            }
            methods.add(method);
        }
        // TODO: Need to make that part asynchronous
        indexer.indexMethods(testName, methods, new Date());
    }

}
