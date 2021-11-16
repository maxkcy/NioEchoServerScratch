package com.max.javaniochat;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class JavaNioServerMain {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args){
        NioServer nioServer = new NioServer();
        nioServer.init();
    }
}
