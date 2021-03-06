package com.wpower;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TcpServer {

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();

        // 指定protobuf的编码器和解码器
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new MinaProtobufEncoder(), new MinaProtobufDecoder()));

        acceptor.setHandler(new TcpServerHandle());
        acceptor.bind(new InetSocketAddress(8900));
    }
}