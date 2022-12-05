package com.khoray.gomoku.slice;

import com.khoray.gomoku.ResourceTable;
import com.khoray.gomoku.UI.ChessBoardView;
import com.khoray.gomoku.UI.ChessPieceComponent;
import com.khoray.gomoku.UI.MyDialog;
import com.khoray.gomoku.model.Game;
import com.khoray.gomoku.model.GameAI;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Text;

public class AIGameAbilitySlice extends AbilitySlice {
    Text nowGo;
    Button startBtn, retBtn, undoBtn;
    ChessBoardView chessBoardView;
    Game game;
    GameAI gameAI;

    int playerType;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ai_game);

        initComponent();
        initGame();
    }

    private void initGame() {
        game = null;
        gameAI = null;
        chessBoardView.clear();
        playerType = 0;
        nowGo.setText("");
    }

    private void startGame() {
        game = new Game();
        chessBoardView.clear();
        gameAI = new GameAI(15, 15);
        new MyDialog(getContext(), "选择你的棋子", "先手X 或者 后手O", "先手X", "后手O", new MyDialog.ClickedListener() {
            @Override
            public void click(MyDialog myDialog) {
                playerType = 1;
                nowGo.setText("" + Game.piece[playerType]);
                myDialog.destroy();
            }
        }, new MyDialog.ClickedListener() {
            @Override
            public void click(MyDialog myDialog) {
                playerType = 2;
                nowGo.setText("" + Game.piece[playerType]);
                chessBoardView.makeMove(new Game.Move(8, 8, game.queryMover()));
                game.makeMove(8, 8);
                gameAI.updateValue(game.boardToAIArray());
                myDialog.destroy();
            }
        }, null);

    }

    private boolean reportEnd() {
        if(game.checkEnd() != 0) {
            new MyDialog(getContext(), "游戏结束", (game.checkEnd() == playerType ? "玩家" : "AI") + " win!", "结束", "继续", new MyDialog.ClickedListener() {
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
    private void initComponent() {
        nowGo = findComponentById(ResourceTable.Id_ai_now_go);
        startBtn = findComponentById(ResourceTable.Id_ai_game_btn);
        undoBtn = findComponentById(ResourceTable.Id_ai_undo);
        retBtn = findComponentById(ResourceTable.Id_ai_ret_btn);
        DirectionalLayout layout = findComponentById(ResourceTable.Id_ai_board_layout);
        layout.removeAllComponents();
        startBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                startGame();
            }
        });
        chessBoardView = new ChessBoardView(layout, getContext(), new ChessPieceComponent.ClickedListener() {
            @Override
            public void click(ChessPieceComponent cpc) {
                int x = cpc.posX, y = cpc.posY;
                if(game != null && game.makeMove(x, y)) {
                    chessBoardView.makeMove(game.getLastMove());
                    if(reportEnd()) return;
                    gameAI.updateValue(game.boardToAIArray());
                    Game.Move aiMove = gameAI.getNextCoordinate(game.boardToAIArray());
                    aiMove.mover = game.queryMover();
                    game.makeMove(aiMove.x, aiMove.y);
                    chessBoardView.makeMove(aiMove);

                    System.out.println("aiMove:" + aiMove.mover + "\ngame:" + game);
                    reportEnd();
                }
            }
        });
        undoBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                undo();
                undo();
            }
        });
        retBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                AIGameAbilitySlice.this.terminate();
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
