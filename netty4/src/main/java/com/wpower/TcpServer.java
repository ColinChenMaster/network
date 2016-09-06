package com.wpower ;

import com.sun.net.httpserver.HttpServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TcpServer {

    private static Logger log = LoggerFactory.getLogger(HttpServer.class);

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                            // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            ch.pipeline().addLast(new ProtobufDecoder(StudentModel.Student.getDefaultInstance()));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg)
                                        throws Exception {
                                    if (msg instanceof StudentModel.Student) {
                                        // 读取客户端传过来的Student对象
                                        StudentModel.Student student = (StudentModel.Student) msg;
                                        System.out.println("ID:" + student.getId());
                                        System.out.println("Name:" + student.getName());
                                        System.out.println("Email:" + student.getEmail());
                                        System.out.println("Friends:");
                                        List<String> friends = student.getFriendsList();
                                        for(String friend : friends) {
                                            System.out.println(friend);
                                        }


                                    }
                                    // 新建一个Student对象传到客户端

                                    StudentModel.Student.Builder builder = StudentModel.Student.newBuilder();
                                    builder.setId(9);
                                    builder.setName("服务器");
                                    builder.setEmail("123@abc.com");
                                    builder.addFriends("X");
                                    builder.addFriends("Y");
                                    StudentModel.Student student2 = builder.build();
                                    ctx.writeAndFlush(student2);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    log.error(cause.getMessage());
                                    ctx.close();
                                }

                            });
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        TcpServer server = new TcpServer();
        log.info("Http Server listening on 8900 ...");
        server.start(8900);
    }
}
