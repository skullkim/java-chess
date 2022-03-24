package chess;

import chess.domain.board.Board;
import chess.domain.board.strategy.CreateBoard;

public class WebApplication {
	public static void main(String[] args) {
		// get("/", (req, res) -> {
		//     Map<String, Object> model = new HashMap<>();
		//     return render(model, "index.html");
		// });

		Chess chess = new Chess(new Board(new CreateBoard()));
		chess.start();
	}

	// private static String render(Map<String, Object> model, String templatePath) {
	//     return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
	// }
}
