package com.khoray.gomoku.slice;

import com.khoray.gomoku.ResourceTable;
import com.khoray.gomoku.UI.ChessBoardView;
import com.khoray.gomoku.UI.ChessPieceComponent;
import com.khoray.gomoku.UI.MyDialog;
import com.khoray.gomoku.model.Game;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

public class SingleGameAbilitySlice extends AbilitySlice {
    Text nowGo;
    Button startBtn, retBtn, undoBtn;
    ChessBoardView chessBoardView;
    Game game;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_single_game);

        initComponent();
        initGame();
    }
    private void initGame() {
        nowGo.setText("游戏未开始");
        game = null;
        chessBoardView.clear();
    }
    private void startGame() {
        game = new Game();
        updateMover();
        chessBoardView.clear();
    }

    private void undo() {
        if(game != null) {
            // undo
            Game.Move lastMove = game.getLastMove();
            if(lastMove != null) {
                game.undo();
                chessBoardView.clearMove(lastMove);
                updateMover();
            }
        }
    }

    private void updateMover() {
        nowGo.setText(game.queryMover() == 1 ? "黑棋⚫" : "白棋⚪");
    }

    private void initComponent() {
        nowGo = findComponentById(ResourceTable.Id_single_now_go);
        undoBtn = findComponentById(ResourceTable.Id_single_undo);
        undoBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                undo();
            }
        });
        startBtn = findComponentById(ResourceTable.Id_single_game_btn);
        startBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                startGame();
            }
        });
        retBtn = findComponentById(ResourceTable.Id_single_ret_btn);
        retBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                SingleGameAbilitySlice.this.terminate();
            }
        });
        DirectionalLayout boardLayout = findComponentById(ResourceTable.Id_single_board_layout);
        boardLayout.removeAllComponents();
        chessBoardView = new ChessBoardView(boardLayout, getContext(), new ChessPieceComponent.ClickedListener() {
            @Override
            public void click(ChessPieceComponent cpc) {
                int x = cpc.posX, y = cpc.posY;
                if(game != null && game.makeMove(x, y)) {
                    chessBoardView.makeMove(game.getLastMove());
                    // now mover here
                    updateMover();
                    if(game.checkEnd() != 0) {

                        new MyDialog(getContext(), "游戏结束", (game.checkEnd() == 1 ? "黑棋" : "白棋") + "胜利!", "新游戏", "取消", new MyDialog.ClickedListener() {
                            @Override
                            public void click(MyDialog myDialog) {
                                startGame();
                                myDialog.destroy();
                            }
                        }, new MyDialog.ClickedListener() {
                            @Override
                            public void click(MyDialog myDialog) {
                                myDialog.destroy();
                            }
                        }, null);
                        game = null;
                    }
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
