package com.khoray.gomoku.model;

import com.khoray.gomoku.UI.MyDialog;

import java.io.*;
import java.net.Socket;

public class RemoteGame implements Runnable {

    public interface EventHandler {
        void handleOpponentMove(Game.Move move);
        void handleOpponentJoin();
        void handleOpponentLeave();
        void handleOpponentPrepare();
        void handleStartGame(int playerType);
        void handleAccept(int playerNum, int playerStatus);
        void handleDisconnected();
        void handleOpponentUndo();
        void handleUndo(boolean isAccept);
    }
    Socket socket;
    EventHandler handler;
    String roomID;
    boolean quiting = false;
    boolean inRoom = false;

    public RemoteGame(EventHandler handler, String roomID) {
        this.handler = handler;
        this.roomID = roomID;
    }

    public void sendMsg(String msg) {
        Thread sendThread = (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write(msg + "\n");
                    bw.flush();
                } catch(Exception e) {
                    e.printStackTrace();
                    if(!quiting) handler.handleDisconnected();
                    return;
                }
            }
        }));
        sendThread.start();
        try {
            sendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        try {
            socket = new Socket("120.46.178.129", 8888);
            System.err.println("建立链接成功");
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            sendMsg("join " + roomID);
            while(true) {
                String msg = br.readLine();
                if(msg == null) {
                    if(!quiting) handler.handleDisconnected();
                    return;
                }

                if(msg.startsWith("join ok")) {
                    int playerNum = Integer.parseInt(msg.split(" ")[2]);
                    int playerStatus = Integer.parseInt(msg.split(" ")[3]);
                    handler.handleAccept(playerNum, playerStatus);
                }

                if(msg.startsWith("full room")) {
                    handler.handleAccept(0, 0);
                    socket.close();
                    return;
                }

                if(msg.startsWith("opponent join")) {
                    handler.handleOpponentJoin();
                }

                if(msg.startsWith("move")) {
                    int x = Integer.parseInt(msg.split(" ")[1]), y = Integer.parseInt(msg.split(" ")[2]);
                    int type = Integer.parseInt(msg.split(" ")[3]);
                    handler.handleOpponentMove(new Game.Move(x, y, type));
                }

                if(msg.startsWith("opponent prepare")) {
                    handler.handleOpponentPrepare();
                }

                if(msg.startsWith("opponent leave")) {
                    handler.handleOpponentLeave();
                }

                if(msg.startsWith("start")) {
                    int playerType = Integer.parseInt(msg.split(" ")[1]);
                    handler.handleStartGame(playerType);
                }

                if(msg.startsWith("undo")) {
                    String alias = msg.split(" ")[1];
                    if(alias.equals("query")) {
                        handler.handleOpponentUndo();
                    } else {
                        handler.handleUndo(alias.equals("accepted"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if(!quiting) handler.handleDisconnected();
        }
    }

    public void sendMove(Game.Move move) {
        sendMsg("move " + move.x + " " + move.y + " " + move.mover);
    }

    public void sendLeave() {
        sendMsg("quit");
    }

    public void sendPrepare() {
        sendMsg("prepare");
    }

    public void sendGameEnd() {
        sendMsg("game end");
    }

    public void sendUndo() { sendMsg("undo query"); }

    public void sendIsAcceptedUndo(boolean isAccepted) { sendMsg("undo " + (isAccepted ? "accepted" : "denied")); }

    public void close() {
        quiting = true;
        sendLeave();
        try {
            if(socket != null) socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
