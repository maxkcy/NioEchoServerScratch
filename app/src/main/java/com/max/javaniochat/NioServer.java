package com.max.javaniochat;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class NioServer {
    public static int BUFSIZE = 1024;
    public static int TIMEOUT = 1000;
    public final int PORT = 7033;
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    public static boolean run = true;
    ServerHandler serverHandler = new ServerHandler(BUFSIZE);

    public NioServer() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init() {
        try{
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            System.out.println("Server Started... listening on port: " + PORT);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            int waiting = 0;
            while(run){
                if(selector.select(TIMEOUT) == 0 ){
                    System.out.print(".");
                    waiting++;
                    if(waiting >= 1000){
                        System.out.println("\n");
                        waiting = 0;
                    }
                    continue;
                }

                Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
                while(keyIter.hasNext()){
                    SelectionKey key = keyIter.next();
                    selector.selectedKeys().remove(key);
                    if(key.isAcceptable()){
                        serverHandler.handleAccept(key);
                    }

                    if(key.isReadable()){
                        serverHandler.handleRead(key);
                    }

                    if(key.isValid() && key.isWritable()){ //key ops for writable. iterate thru all to echo to all eh? im canadian now
                        serverHandler.handleWrite(key);
                    } else {
                        key.interestOps(0);
                        key.channel().close();
                        key.cancel();
                    }
                }
            }

        }catch (Exception e){
            System.out.println(this + ": " + e);
        }
    }

}
