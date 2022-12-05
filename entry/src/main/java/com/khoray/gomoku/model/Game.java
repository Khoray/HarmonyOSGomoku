package com.khoray.gomoku.model;

import java.util.ArrayList;

public class Game {
    public final int FIRST_MOVE = 1;
    public final int MAX_COL = 15;
    public final int MAX_ROW = 15;
    public static final char[] piece = new char[] {'.', 'X', 'O'};

    public static class Move {
        public int x, y, mover;
        public Move(int x, int y, int mover) {
            this.x = x;
            this.y = y;
            this.mover = mover;
        }
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    private char[][] board;
    private ArrayList<Move> record;

    public Game() {
        clearBoard();
    }

    public void clearBoard() {
        board = new char[MAX_ROW][MAX_COL];
        for(int i = 0; i < MAX_ROW; i++) {
            for(int j = 0; j < MAX_COL; j++) {
                board[i][j] = piece[0];
            }
        }
        record = new ArrayList<>();
    }

    public int checkEnd() {
        // check each row
        for(int i = 0; i < MAX_ROW; i++) {
            int cntX = 0, cntO = 0;
            for(int j = 0; j < MAX_COL; j++) {
                if(board[i][j] == 'X') {cntX++; cntO = 0;}
                else if(board[i][j] == 'O') {cntO++; cntX = 0;}
                else cntX = cntO = 0;
                if(cntX == 5) return 1;
                else if(cntO == 5) return 2;
            }
        }

        // check each col
        for(int i = 0; i < MAX_COL; i++) {
            int cntX = 0, cntO = 0;
            for(int j = 0; j < MAX_ROW; j++) {
                if(board[j][i] == 'X') {cntX++; cntO = 0;}
                else if(board[j][i] == 'O') {cntO++; cntX = 0;}
                else cntX = cntO = 0;
                if(cntX == 5) return 1;
                else if(cntO == 5) return 2;
            }
        }

        // check left up to right down
        for(int i = 0; i < MAX_ROW + MAX_COL - 1; i++) {
            int cntX = 0, cntO = 0;
            int initX = Math.max(MAX_ROW - 1 - i, 0);
            int initY = Math.max(i - MAX_COL + 1, 0);
            for(int j = 0; j < Math.min(MAX_COL, MAX_ROW); j++) {
                if(initX + j >= MAX_ROW || initY + j >= MAX_COL) {
                    break;
                }
                if(board[initX + j][initY + j] == 'X') {
                    cntX++;
                    cntO = 0;
                }
                else if(board[initX + j][initY + j] == 'O') {
                    cntO++;
                    cntX = 0;
                }
                else cntX = cntO = 0;
                if(cntX == 5) return 1;
                else if(cntO == 5) return 2;
            }
        }

        // check right up to left down
        for(int i = 0; i < MAX_ROW + MAX_COL - 1; i++) {
            int cntX = 0, cntO = 0;
            int initX = Math.max(i - MAX_ROW + 1, 0);
            int initY = Math.min(i, MAX_COL - 1);
            for(int j = 0; j < Math.min(MAX_COL, MAX_ROW); j++) {
                if(initX + j >= MAX_ROW || initY - j < 0) {
                    break;
                }
                if(board[initX + j][initY - j] == 'X') {
                    cntX++;
                    cntO = 0;
                }
                else if(board[initX + j][initY - j] == 'O') {
                    cntO++;
                    cntX = 0;
                }
                else cntX = cntO = 0;
                if(cntX == 5) return 1;
                else if(cntO == 5) return 2;
            }
        }

        return 0;
    }

    public boolean makeMove(int x, int y) {
        if(board[x][y] != '.') return false;
        int curMove = queryMover();
        record.add(new Move(x, y, curMove));
        board[x][y] = piece[curMove];
        curMove = 3 - curMove;
        return true;
    }

    public boolean undo() {
        if(record.isEmpty()) return false;
        Move p = record.get(record.size() - 1);
        board[p.x][p.y] = '.';
        record.remove(record.size() - 1);
        return true;
    }

    public int queryMover() {
        return (record.size() == 0 ? FIRST_MOVE : 3 - record.get(record.size() - 1).mover);
    }

    public char getBoard(int x, int y) {
        return board[x][y];
    }

    public Move getLastMove() {
        return (record.size() == 0 ? null : record.get(record.size() - 1));
    }

    public int[][] boardToAIArray() {
        int[][] ret = new int[MAX_ROW][MAX_COL];
        for(int i = 0; i < MAX_ROW; i++) {
            for(int j = 0; j < MAX_COL; j++) {
                ret[i][j] = (board[i][j] == 'X' ? 1 : board[i][j] == 'O' ? 2 : 0);
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "exist move: [";
        for(Move p : record) {
            ret += p + ",";
        }
        ret += "]\n";
        ret += "now move: " + piece[queryMover()] + "\n";
        ret += "chessboard:\n";
        for(int i = 0; i < MAX_ROW; i++) {
            for(int j = 0; j < MAX_COL; j++) {
                ret += board[i][j];
            }
            ret += "\n";
        }
        return ret;
    }


}