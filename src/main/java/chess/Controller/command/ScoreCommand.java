package chess.Controller.command;

import chess.Controller.dto.ScoreDto;
import chess.domain.Score;
import chess.domain.board.Board;

public abstract class ScoreCommand {

    private static final String CANNOT_IMPLEMENT_COMMAND = "현재 실행할 수 없는 명령입니다.";

    public ScoreDto doCommandAction(final ParsedCommand parsedCommand, final Board board, final int userId) {
        if (canDoAction(parsedCommand.getCommand(), board)) {
            return ScoreDto.fromEntity(doAction(parsedCommand, board, userId));
        }
        throw new IllegalArgumentException(CANNOT_IMPLEMENT_COMMAND);
    }

    protected abstract boolean canDoAction(final Command command, final Board board);

    protected abstract Score doAction(final ParsedCommand parsedCommand, final Board board, final int userId);

}
