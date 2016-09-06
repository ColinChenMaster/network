package com.wpower;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class TcpClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {

            socket = new Socket("localhost", 8900);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // 创建一个Student传给服务器
            StudentModel.Student.Builder builder = StudentModel.Student.newBuilder();
            builder.setId(1);
            builder.setName("客户端");
            builder.setEmail("test@qq.com");
            builder.addFriends("A");
            builder.addFriends("B");
            StudentModel.Student student = builder.build();
            byte[] outputBytes = student.toByteArray(); // Student转成字节码
            out.writeInt(outputBytes.length); // write header
            out.write(outputBytes); // write body
            out.flush();

            // 获取服务器传过来的Student
            int bodyLength = in.readInt();  // read header
            byte[] bodyBytes = new byte[bodyLength];
            in.readFully(bodyBytes);  // read body
            StudentModel.Student student2 = StudentModel.Student.parseFrom(bodyBytes); // body字节码解析成Student
            System.out.println("Header:" + bodyLength);
            System.out.println("Body:");
            System.out.println("ID:" + student2.getId());
            System.out.println("Name:" + student2.getName());
            System.out.println("Email:" + student2.getEmail());
            System.out.println("Friends:");
            List<String> friends = student2.getFriendsList();
            for(String friend : friends) {
                System.out.println(friend);
            }

        } finally {
            // 关闭连接
            in.close();
            out.close();
            socket.close();
        }
    }
}