package com.khoray.gomoku.slice;

import com.khoray.gomoku.ResourceTable;
import com.khoray.gomoku.UI.ChessBoardView;
import com.khoray.gomoku.UI.MyDialog;
import com.khoray.gomoku.Utils.DebugUtil;
import com.khoray.gomoku.model.Game;
import com.khoray.gomoku.model.RemoteGame;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;

public class RemoteGameAbilitySlice extends AbilitySlice {
    String roomID;
    Text opponentNowGo, nowGo, roomIDText;
    Button startBtn, retBtn, undoBtn;
    ChessBoardView chessBoardView;
    Game game;
    RemoteGame remoteGame;
    Text remoteStatus;
    public int playerType;
    boolean undoing = false;

    private void toast(String msg) {
        getUITaskDispatcher().asyncDispatch(new Runnable() {
            @Override
            public void run() {
                DebugUtil.showToast(getContext(), msg);
            }
        });

    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_remote_game);
        roomID = intent.getStringParam("roomID");

        initComponent();
        initGame();
        initConnection();

    }

    private void initConnection() {
        remoteGame = new RemoteGame(new RemoteGame.EventHandler() {
            @Override
            public void handleOpponentMove(Game.Move move) {
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        game.makeMove(move.x, move.y);
                        chessBoardView.makeMove(move);
                        remoteStatus.setText("该你了");
                        reportEnd();
                    }
                });

            }

            @Override
            public void handleOpponentJoin() {
                toast("对手加入了");
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        opponentNowGo.setText("未准备");
                    }
                });

            }

            @Override
            public void handleOpponentLeave() {
                toast("对手退出了");
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        opponentNowGo.setText("未加入");
                        nowGo.setText("未准备");
                        undoBtn.setTextColor(Color.GRAY);
                        undoBtn.setEnabled(false);
                    }
                });

            }

            @Override
            public void handleOpponentPrepare() {
                toast("对手准备了");
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        opponentNowGo.setText("已准备");
                    }
                });
            }

            @Override
            public void handleStartGame(int playerType) {
                RemoteGameAbilitySlice.this.playerType = playerType;
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        nowGo.setText("" + Game.piece[playerType]);
                        opponentNowGo.setText("" + Game.piece[3 - playerType]);
                        game = new Game();
                        chessBoardView.clear();
                        undoBtn.setTextColor(Color.BLACK);
                        undoBtn.setEnabled(true);
                    }
                });
            }

            @Override
            public void handleAccept(int playerNum, int playerStatus) {
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        nowGo.setText("未准备");
                        if(playerNum == 0) {
                            toast("房间满员了");
                            RemoteGameAbilitySlice.this.terminate();
                        } else if(playerNum == 1){
                            toast("加入成功，现在房间没有人");
                            opponentNowGo.setText("未加入");
                        } else {
                            if(playerStatus == 1) {
                                opponentNowGo.setText("未准备");
                            } else if(playerStatus == 2) {
                                opponentNowGo.setText("已准备");
                            }
                        }
                    }
                });

            }

            @Override
            public void handleDisconnected() {
                toast("掉线了。。。");
                RemoteGameAbilitySlice.this.terminate();
            }

            @Override
            public void handleOpponentUndo() {
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        new MyDialog(getContext(), "悔棋请求", "对方请求悔棋，是否同意？", "同意", "拒绝", c -> {
                            // 接受
                            remoteGame.sendIsAcceptedUndo(true);
                            undo();
                            undo();
                            c.destroy();
                        }, c -> {
                            // 拒绝悔棋
                            remoteGame.sendIsAcceptedUndo(false);
                            c.destroy();
                        }, () -> {
                            // 直接返回取消对话框
                            remoteGame.sendIsAcceptedUndo(false);
                        });
                    }
                });
            }

            @Override
            public void handleUndo(boolean isAccept) {
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        if(isAccept) {
                            toast("对方接受悔棋");
                            undo();
                            undo();
                        } else {
                            toast("对方拒绝悔棋");
                        }
                        undoBtn.setTextColor(Color.BLACK);
                        undoing = false;
                        undoBtn.setEnabled(true);
                    }
                });
            }
        }, roomID);
        (new Thread(remoteGame)).start();
    }

    @Override
    protected void onStop() {
        if(remoteGame != null) remoteGame.close();
        super.onStop();
    }

    private void initGame() {
        game = null;
        chessBoardView.clear();
        nowGo.setText("未准备");
        opponentNowGo.setText("未准备");
        remoteStatus.setText("游戏尚未开始");
    }

    private void prepare() {
        remoteGame.sendPrepare();
    }

    private void queryUndo() {
        remoteGame.sendUndo();
        undoBtn.setEnabled(false);
        undoBtn.setTextColor(Color.GRAY);
        undoing = true; //保证悔棋的时候不能按棋盘
        toast("悔棋请求已发送，等待对方回复。。。");
    }

    private void undo() {
        if(game != null) {
            // undo
            Game.Move lastMove = game.getLastMove();
            if(lastMove != null) {
                game.undo();
                chessBoardView.clearMove(lastMove);
                nowGo.setText("" + game.piece[game.queryMover()]);
            }
        }
    }

    private boolean reportEnd() {
        if(game.checkEnd() != 0) {
            remoteGame.sendGameEnd();
            new MyDialog(getContext(), "游戏结束", (game.checkEnd() == playerType ? "你" : "对手") + "胜利!", "结束", "继续", new MyDialog.ClickedListener() {
                @Override
                public void click(MyDialog myDialog) {
                    myDialog.destroy();
                }
            }, new MyDialog.ClickedListener() {
                @Override
                public void click(MyDialog myDialog) {
                    myDialog.destroy();
                }
            }, null);
            game = null;
            return true;
        }
        return false;
    }


    private void initComponent() {
        remoteStatus = findComponentById(ResourceTable.Id_remote_status);
        opponentNowGo = findComponentById((ResourceTable.Id_remote_opponent_now_go));
        nowGo = findComponentById(ResourceTable.Id_remote_now_go);
        undoBtn = findComponentById(ResourceTable.Id_remote_undo);
        undoBtn.setEnabled(false);
        undoBtn.setTextColor(Color.GRAY);
        undoBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                queryUndo();
            }
        });
        startBtn = findComponentById(ResourceTable.Id_remote_game_btn);
        startBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                prepare();
                nowGo.setText("已准备");
            }
        });
        retBtn = findComponentById(ResourceTable.Id_remote_ret_btn);
        retBtn.setClickedListener(c -> {
            remoteGame.close();
            RemoteGameAbilitySlice.this.terminate();
        });
        roomIDText = findComponentById(ResourceTable.Id_room_id_text);
        roomIDText.setText(roomID);
        DirectionalLayout layout = findComponentById(ResourceTable.Id_remote_board_layout);
        layout.removeAllComponents();
        chessBoardView = new ChessBoardView(layout, getContext(), cpc -> {
            if(game != null && game.queryMover() == playerType && !undoing) {
                int x = cpc.posX, y = cpc.posY;
                if(game.makeMove(x, y)) {
                    chessBoardView.makeMove(game.getLastMove());
                    remoteGame.sendMove(game.getLastMove());
                    remoteStatus.setText("对方的回合");
                    reportEnd();

                }
            }
        });

    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
