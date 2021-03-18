package chess.domain.piece;

import chess.domain.ChessBoard;
import chess.domain.Direction;
import chess.domain.Position;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, position);
        this.type = Type.KNIGHT;
    }

    @Override
    public List<Position> getMovablePositions(ChessBoard chessBoard) {
        List<Position> movablePositions = new ArrayList<>();
        List<Direction> directions = Direction.knightDirection();
        for (Direction direction : directions) {
            int xDegree = direction.getXDegree();
            int yDegree = direction.getYDegree();

            movablePositions.add(getMovablePosition(chessBoard, xDegree, yDegree));
        }
        return movablePositions;
    }

    private Position getMovablePosition(ChessBoard chessBoard, int xDegree, int yDegree) {
        if (chessBoard.hasNextPossibleSquare(position, xDegree, yDegree)) {
            Position nextPosition = new Position(position.getRow() + yDegree,
                position.getColumn() + xDegree);
            if (chessBoard.isBlank(nextPosition) || chessBoard.isAttackMove(this, nextPosition)) {
                return nextPosition;
            }
        }
        return null;
    }
}
