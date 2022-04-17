package chess;

import static spark.Spark.exception;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

import chess.Controller.WebChessController;
import chess.util.path.Web;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class WebApplication {

    public static String STATUS = "dev";

    public static void main(String[] args) {
        port(8080);

        if (STATUS.equals("dev")) {
            String projectDirectory = System.getProperty("user.dir");
            String staticDirectory = "/src/main/resources/static";
            externalStaticFileLocation(projectDirectory + staticDirectory);
        } else {
            staticFileLocation("/static");
        }

        get(Web.MAIN_PAGE, WebChessController.renderMainPage);

        get(Web.USER_HISTORY, WebChessController.findUserHistory);

        path(Web.COMMAND_ACTION, () -> {
            put(Web.START, WebChessController.startCommand);
            put(Web.STATUS, WebChessController.statusCommand);
            put(Web.MOVE, WebChessController.moveCommand);
            put(Web.END, WebChessController.endCommand);
        });

        exception(IllegalArgumentException.class, (exception, request, response) -> {
            final Map<String, String> error = new HashMap<>();
            final Gson gson = new Gson();
            error.put("error_message", exception.getMessage());
            response.status(400);
            response.body(gson.toJson(error));
        });

        exception(Exception.class, (exception, request, response) -> {
            final Map<String, String> error = new HashMap<>();
            final Gson gson = new Gson();
            error.put("error_message", "현재 실행할 수 없는 명령입니다.");
            response.status(400);
            response.body(gson.toJson(error));
        });

    }

}

