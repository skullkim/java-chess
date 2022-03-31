package chess.command;

import chess.domain.board.Board;
import java.util.Optional;

public class End extends CommandChain {

    public End(final CommandChain commandChain) {
        super(Optional.ofNullable(commandChain));
    }

    @Override
    protected boolean canDoAction(final Command command, final Board board) {
        return command == Command.END;
    }

    @Override
    protected void doAction(final ParsedCommand parsedCommand, final Board board) {
        board.terminateGame();
    }
}
