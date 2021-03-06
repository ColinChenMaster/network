package com.wpower;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.List;

/**
 * Created by chenlin on 16/9/6.
 */
public class TcpServerHandle extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        // 读取客户端传过来的Student对象
        StudentModel.Student student = (StudentModel.Student) message;
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
        WriteFuture future = session.write(student2);
        session.closeOnFlush();
        future.addListener(new IoFutureListener<WriteFuture>() {
            @Override
            public void operationComplete(WriteFuture future) {
                if(future.isWritten()){
                    System.out.println("write操作完成!");
                }else{
                    System.out.println("write操作失败!");

                }
            }
        });
    }
}