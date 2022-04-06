package chess.domain.board;

import static chess.domain.piece.PieceType.KNIGHT;
import static chess.domain.piece.PieceType.PAWN;

import chess.domain.GameState;
import chess.domain.piece.Color;
import chess.domain.piece.Direction;
import chess.domain.piece.EmptySpace;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceType;
import java.util.Arrays;
import java.util.Map;

public class Board {

    private static final String HAS_ANOTHER_PIECE_ERROR = "다른 말이 존재해 이동할 수 없습니다.";
    private static final String PAWN_CANNOT_MOVE_DIAGONAL = "폰은 상대 말을 공격할 때만 대각선으로 이동할 수 있습니다.";
    private static final String PIECE_DOES_NOT_EXIST = "해당 위치에 말이 존재하지 않습니다.";
    private static final String CANNOT_MOVE_OPPONENT_PIECE = "상대편 말은 욺직일 수 없습니다.";
    private static final String ANOTHER_PIECE_EXIST_IN_PATH = "다른 말이 경로에 존재해 이동할 수 없습니다.";
    private static final String ANOTHER_SAME_COLOR_PIECE_EXIST = "같은 팀의 다른 말이 존재해 이동할 수 없습니다.";
    private static final int NO_KING_EXIST = 0;
    private static final double PAWN_MINUS_SCORE = 0.5;

    private final Map<Position, Piece> pieces;
    private final GameState gameState;

    public Board(final Map<Position, Piece> pieces, final GameState gameState) {
        this.pieces = pieces;
        this.gameState = gameState;
    }

    public Piece move(final Position start, final Position target, final Color currentColor) {
        final Piece movingPiece = get(start);
        final Piece targetPiece = get(target);
        validatePieceExistIn(movingPiece, currentColor);
        validateMoving(start, target);
        pieces.put(target, movingPiece);
        pieces.remove(start);
        return targetPiece;
    }

    private void validatePieceExistIn(final Piece movingPiece, final Color color) {
        if (movingPiece.isEmpty()) {
            throw new IllegalArgumentException(PIECE_DOES_NOT_EXIST);
        }
        if (movingPiece.getColor() != color) {
            throw new IllegalArgumentException(CANNOT_MOVE_OPPONENT_PIECE);
        }
    }

    private void validateMoving(final Position start, final Position target) {
        final Piece movingPiece = get(start);
        if (movingPiece.isSamePiece(KNIGHT)) {
            validateKnight(start, target);
            return;
        }
        if (movingPiece.isSamePiece(PAWN)) {
            validatePawn(start, target);
            return;
        }
        validateCommonPiece(start, target);
    }

    private void validateKnight(final Position start, final Position target) {
        final Piece movingPiece = get(start);
        final Piece targetPiece = get(target);
        validateTarget(movingPiece, targetPiece);
    }

    private void validatePawn(final Position start, final Position target) {
        final Piece movingPiece = get(start);
        final Piece targetPiece = get(target);
        final Direction direction = movingPiece.findValidDirection(start, target);
        if (direction.isDiagonal()) {
            validatePawnDiagonalMove(movingPiece, targetPiece);
            return;
        }
        validatePath(movingPiece, start, target);
        if (!targetPiece.isEmpty()) {
            throw new IllegalArgumentException(HAS_ANOTHER_PIECE_ERROR);
        }
    }

    private void validatePawnDiagonalMove(final Piece movingPiece, final Piece targetPiece) {
        if (targetPiece.isEmpty() || movingPiece.hasSameColor(targetPiece)) {
            throw new IllegalArgumentException(PAWN_CANNOT_MOVE_DIAGONAL);
        }
    }

    private void validateCommonPiece(final Position start, final Position target) {
        final Piece movingPiece = get(start);
        validatePath(movingPiece, start, target);

        final Piece targetPiece = get(target);
        validateTarget(movingPiece, targetPiece);
    }

    private void validatePath(final Piece movingPiece, final Position start, final Position target) {
        final Direction direction = movingPiece.findValidDirection(start, target);
        Position current = start.move(direction);
        while (!current.equals(target)) {
            if (!get(current).isEmpty()) {
                throw new IllegalArgumentException(ANOTHER_PIECE_EXIST_IN_PATH);
            }
            current = current.move(direction);
        }
    }

    private void validateTarget(final Piece movingPiece, final Piece targetPiece) {
        if (!targetPiece.isEmpty() && movingPiece.hasSameColor(targetPiece)) {
            throw new IllegalArgumentException(ANOTHER_SAME_COLOR_PIECE_EXIST);
        }
    }

    private Piece get(final Position position) {
        return pieces.getOrDefault(position, EmptySpace.getInstance());
    }

    public int countPiece(final PieceType pieceType, final Color color) {
        return (int) pieces.values()
                .stream()
                .filter(piece -> piece.isSamePiece(pieceType) && piece.isSameColor(color))
                .count();
    }

    private boolean isPawnWith(final Piece piece, final Color color) {
        return piece.isSamePiece(PAWN) && piece.isSameColor(color);
    }

    private boolean hasAnotherPawnInSameColumn(final Position pawnPosition, final Piece piece) {
        return Arrays.stream(Row.values())
                .map(row -> new Position(pawnPosition.getColumn(), row))
                .anyMatch(position -> isAnotherPawnInSameColumn(pawnPosition, piece, position));
    }

    private boolean isAnotherPawnInSameColumn(final Position pawnPosition, final Piece pawn,
                                              final Position currentPawnPosition) {
        return !pawnPosition.equals(currentPawnPosition) && get(currentPawnPosition).equals(pawn);
    }

    public boolean isEnd(final Color color) {
        return countPiece(PieceType.KING, color) == NO_KING_EXIST;
    }

    public double calculateScore(Color color) {
        final double score = Arrays.stream(PieceType.values())
                .mapToDouble(piece -> piece.calculateScore(countPiece(piece, color)))
                .sum();
        return score - countDeductedPawns(color) * PAWN_MINUS_SCORE;
    }

    private int countDeductedPawns(final Color color) {
        return (int) pieces.entrySet()
                .stream()
                .filter(entry -> isPawnWith(entry.getValue(), color))
                .filter(entry -> hasAnotherPawnInSameColumn(entry.getKey(), entry.getValue()))
                .count();
    }

    public boolean isReady() {
        return gameState == GameState.READY;
    }

    public boolean isRunning() {
        return gameState.isRunning();
    }

    public GameState changeTurn() {
        return gameState.getOpposite();
    }

    public Color getCurrentColor() {
        if (gameState == GameState.BLACK_RUNNING) {
            return Color.BLACK;
        }
        return Color.WHITE;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Map<Position, Piece> getPieces() {
        return pieces;
    }
}

