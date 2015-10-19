package com.rc.hover.hoverx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class netThread implements Runnable {

    private boolean _isServer = false;
    private InetAddress _serverAddr = null;
    private int _port = 0;
    public Socket socket = null;
    public ServerSocket serverSocket = null;

    public netThread(boolean isServer, int port, InetAddress serverAddr) {
        _isServer = isServer;
        _serverAddr = serverAddr;
        _port = port;
        if(_isServer) {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                socket.connect(new InetSocketAddress(_serverAddr, _port), 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {

        }
    }
}
