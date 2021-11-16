package com.max.javaniochat;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ServerHandler implements Handler{

    int bufSize;
    String message;

    public ServerHandler(int bufSize) {
        this.bufSize = bufSize;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        // Client channel
        System.out.println("new Connection");
        SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
        System.out.println("connection accepted");
        socketChannel.configureBlocking(false);
        System.out.println("socket channel configured blocking to false");
        socketChannel.register(key.selector(), (SelectionKey.OP_READ | SelectionKey.OP_WRITE), ByteBuffer.allocate(bufSize));
        System.out.println("socket chanel registered with selector, ByteBuffer allocated");
        //key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        //int interestOps = (SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//        System.out.println("more debug logs 3");
        //key.interestOps(interestOps);
//        System.out.println("New Socket Channel created and registered");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        System.out.println("\nchanel available and ready to read");
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();

        long bytesRead = socketChannel.read(buf);
        if (bytesRead == -1) {
            socketChannel.close();
            System.out.println("\nConnection to a client closed because nothing was read.");
//            System.out.println("Nothing was read but connection was not closed");
        } else if (bytesRead > 0) {
            //String message = new String(buf.array(), "UTF-8");
//            String message = buf.toString();
            socketChannel.read(buf);
            message = StandardCharsets.UTF_8.decode(buf).toString();
            System.out.println("Message from client: " + message);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
            buf.flip(); //eh this might (have been) be a problem too
            buf.clear();
            if(message != null){
                for(int i = 0; i < message.getBytes().length; i++){
                    buf.put(message.getBytes()[i]);
                } //this here might (have been) be the problem
            SocketChannel socketChannel = (SocketChannel) key.channel();
                buf.flip(); // ehh will this be needed?
            socketChannel.write(buf);
            System.out.println("Message sent back to client: " + message);
            message = null;
            }
            if (!buf.hasRemaining()) {
//                key.interestOps(SelectionKey.OP_READ);
            }else{
                buf.compact();
            }
    }
}
