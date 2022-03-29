package chess.command;

import chess.domain.board.Board;
import java.util.Optional;

public abstract class CommandChain {

    private static final String CANNOT_IMPLEMENT_COMMAND = "현재 실행할 수 없는 명령입니다.";
    private static final int COMMAND = 0;

    protected Optional<CommandChain> nextCommand;

    public CommandChain(final Optional<CommandChain> nextCommand) {
        this.nextCommand = nextCommand;
    }

    public void doCommandAction(final String[] rawCommand, final Board board) {
        final Command command = Command.from(rawCommand[COMMAND]);
        if (canDoAction(command, board)) {
            doAction(rawCommand, board);
            return;
        }
        nextCommand.orElseThrow(() -> new IllegalArgumentException(CANNOT_IMPLEMENT_COMMAND))
                .doCommandAction(rawCommand, board);
    }

    protected abstract boolean canDoAction(final Command command, final Board board);

    protected abstract void doAction(final String[] rawCommand, final Board board);
}
