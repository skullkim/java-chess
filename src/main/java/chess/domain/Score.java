package chess.domain;

import chess.domain.board.Board;
import chess.domain.piece.Color;

public class Score {

    private static final int TERMINATE_GAME = -1;

    private final double whiteScore;
    private final double blackScore;

    public Score(final Board board) {
        whiteScore = calculateScore(board, Color.WHITE);
        blackScore = calculateScore(board, Color.BLACK);
    }

    private double calculateScore(final Board board, final Color color) {
        if (board.isEnd(color)) {
            return TERMINATE_GAME;
        }

        return board.calculateScore(color);
    }

    public double getWhiteScore() {
        return whiteScore;
    }

    public double getBlackScore() {
        return blackScore;
    }
}
