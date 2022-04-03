package chess.util;

import chess.Controller.dto.PieceDto;
import chess.Controller.dto.PiecesDto;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonParser {

    public static JSONArray makePiecesToJsonArray(final PiecesDto piecesDto) {
        final List<PieceDto> currentPieces = piecesDto.getPieces();
        JSONArray jsonArray = new JSONArray();
        for (PieceDto piece : currentPieces) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("position", piece.getPosition());
            jsonObject.put("piece_url", piece.getImageUrl());
            jsonObject.put("symbol", piece.getSymbol());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
