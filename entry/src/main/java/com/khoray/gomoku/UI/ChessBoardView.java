package com.khoray.gomoku.UI;

import com.khoray.gomoku.model.Game;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.app.Context;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class ChessBoardView {
    DirectionalLayout boardLayout;
    Context context;
    public final int chessWidth = AttrHelper.vp2px(22, context);
    public final int chessMargin = AttrHelper.vp2px(1, context);
    public final int maxRow = 15, maxCol = 15;
    ChessPieceComponent chesses[][];

    public ChessBoardView(DirectionalLayout boardLayout, Context context, ChessPieceComponent.ClickedListener listener) {
        this.boardLayout = boardLayout;
        this.context = context;
        boardLayout.setWidth(MATCH_CONTENT);
        boardLayout.setHeight(MATCH_CONTENT);
        boardLayout.setOrientation(Component.VERTICAL);
//        ShapeElement se = new ShapeElement();
//        se.setRgbColor(RgbColor.fromArgbInt(Color.getIntColor("#FE952C")));
//        boardLayout.setBackground(se);
        chesses = new ChessPieceComponent[maxRow][maxCol];

        for(int i = 0; i < maxRow; i++) {
            DirectionalLayout row = new DirectionalLayout(context);
            row.setOrientation(Component.HORIZONTAL);
            row.setWidth(MATCH_CONTENT);
            row.setHeight(MATCH_CONTENT);
//            ShapeElement rse = new ShapeElement();
//            rse.setRgbColor(new RgbColor(255, 0, 0, 0));
//            row.setBackground(rse);

            for(int j = 0; j < maxCol; j++) {
                chesses[i][j] = new ChessPieceComponent(context, listener);
                chesses[i][j].posX = i;
                chesses[i][j].posY = j;
                ShapeElement ce = new ShapeElement();
                ce.setRgbColor(RgbColor.fromArgbInt(Color.getIntColor("#FE952C")));
                chesses[i][j].setBackground(ce);
                chesses[i][j].setHeight(chessWidth - 1);
                chesses[i][j].setWidth(chessWidth - 1);
//                chesses[i][j].setMarginsLeftAndRight(chessMargin, chessMargin);
//                chesses[i][j].setMarginsTopAndBottom(chessMargin, chessMargin);
                row.addComponent(chesses[i][j]);
            }
            boardLayout.addComponent(row);
        }
    }
    public DirectionalLayout getBoardLayout() {
        return boardLayout;
    }

    public void clear() {
        for(int i = 0; i < maxRow; i++) {
            for(int j = 0; j < maxCol; j++) {
                chesses[i][j].isFocusing = false;
                chesses[i][j].type = 0;
                chesses[i][j].invalidate();
            }
        }
    }

    public boolean makeMove(Game.Move move) {
        int x = move.x, y = move.y, type = move.mover;
        if(chesses[x][y].type != 0) return false;
        chesses[x][y].type = type;
        chesses[x][y].invalidate();
        return true;
    }

    public boolean clearMove(Game.Move lastMove) {
        int x = lastMove.x, y = lastMove.y;
        if(chesses[x][y].type == 0) return false;
        chesses[x][y].type = 0;
        chesses[x][y].invalidate();
        return true;
    }
}
