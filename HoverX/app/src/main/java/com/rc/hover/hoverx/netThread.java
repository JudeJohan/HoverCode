package com.rc.hover.hoverx;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    Handler updateConversationHandler;

    public netThread(boolean isServer, int port, InetAddress serverAddr) {
        _isServer = isServer;
        _serverAddr = serverAddr;
        _port = port;
    }

    @Override
    public void run() {
        if (_isServer) {
            try {
                serverSocket = new ServerSocket(_port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                socket.connect(new InetSocketAddress(_serverAddr, _port), 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (!Thread.currentThread().isInterrupted()) {
            if(_isServer) {
                try {
                    socket = serverSocket.accept();
                    commThread comThread = new commThread(socket);
                    new Thread(comThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class commThread implements Runnable {
        private Socket _clientSocket;
        private BufferedReader _input;

        public commThread(Socket clientSocket) {
            _clientSocket = clientSocket;
            try {
                _input = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));
            } catch (IOException e) {

            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String read = _input.readLine();
                    //updateConversationHandler.post(new updateUIThread(read));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}