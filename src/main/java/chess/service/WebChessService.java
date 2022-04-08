package chess.service;

import chess.Controller.ChessController;
import chess.Controller.command.Command;
import chess.Controller.command.ParsedCommand;
import chess.Controller.dto.PiecesDto;
import chess.Controller.dto.ScoreDto;
import chess.util.ViewUtil;
import chess.util.json.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import spark.Request;
import spark.Route;

public class WebChessService {

    public static final Route renderMainPage = (req, res) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(model, "/index.html");
    };

    public static final Route findUserHistory = (req, res) -> {
        final String userName = req.params(":userName");
        final ChessController chess = new ChessController();
        final int userId = chess.initGame(userName);
        final PiecesDto pieces = chess.getCurrentBoardState(userId);
        req.session().attribute("user-id", userId);
        return JsonParser.getPiecesAndGameStatus(pieces);
    };

    public static final Route runCommand = (req, res) -> {
        final int userId = req.session().attribute("user-id");
        final ParsedCommand parsedCommand = parseRequestToCommand(req);
        return doCommandAction(userId, parsedCommand);
    };

    private static ParsedCommand parseRequestToCommand(final Request req) {
        final String command = req.params(":command");
        final Optional<String> startPosition = Optional.ofNullable(req.queryParams("start"));
        final Optional<String> endPosition = Optional.ofNullable(req.queryParams("end"));
        final String rawCommand = command + " " + startPosition.orElse("") + " " + endPosition.orElse("");
        return new ParsedCommand(rawCommand);
    }

    private static String doCommandAction(final int userId, final ParsedCommand parsedCommand) {
        final ChessController chess = new ChessController();
        final Command command = parsedCommand.getCommand();
        if (command == Command.START || command == Command.MOVE) {
            return doActionAboutPieces(userId, parsedCommand, chess);
        }
        return doActionAboutScore(userId, parsedCommand, chess);

    }

    private static String doActionAboutPieces(final int userId, final ParsedCommand parsedCommand,
                                              final ChessController chess) {
        final PiecesDto piecesDto = chess.doActionAboutPieces(parsedCommand, userId);
        if (parsedCommand.getCommand() == Command.MOVE) {
            return JsonParser.getPiecesAndGameStatus(piecesDto);
        }
        return JsonParser.makePiecesToJsonArray(piecesDto);
    }

    private static String doActionAboutScore(final int userId, final ParsedCommand parsedCommand,
                                             final ChessController chess) {
        final ScoreDto scoreDto = chess.doActionAboutScore(parsedCommand, userId);
        final String responseObject = JsonParser.scoreToJson(scoreDto);
        if (parsedCommand.getCommand() == Command.END) {
            chess.finishGame(userId);
        }
        return responseObject;
    }

}
