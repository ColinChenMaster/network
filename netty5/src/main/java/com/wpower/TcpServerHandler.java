package com.wpower;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
/**
 * Created by chenlin on 16/9/6.
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

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

        // 新建一个Student对象传到客户端

        StudentModel.Student.Builder builder = StudentModel.Student.newBuilder();
        builder.setId(9);
        builder.setName("服务器");
        builder.setEmail("123@abc.com");
        builder.addFriends("X");
        builder.addFriends("Y");
        StudentModel.Student student2 = builder.build();
        ChannelFuture future = ctx.writeAndFlush(student2);
        future.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    System.out.println("write操作成功!");
                }else{
                    System.out.println("write操作失败!");
                }
                ChannelFuture future = ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}