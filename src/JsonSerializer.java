import java.util.ArrayList;
import java.util.List;

/**
 * JsonSerializer — builds a single-line JSON string representing the full game state
 * after each turn. Used by the client to send an ACTION message to the server.
 *
 * No external dependencies. Output is always a single line (no newlines inside the JSON)
 * so it can be safely sent over a PrintWriter.println() call.
 *
 * Example output (pretty-printed here for readability):
 * {
 *   "turnCount": 5,
 *   "currentPlayerIndex": 1,
 *   "players": [
 *     {
 *       "name": "Player 1",
 *       "balance": 1340,
 *       "location": 6,
 *       "locationName": "Oriental Avenue",
 *       "inJail": false,
 *       "ownedProperties": ["Mediterranean Avenue", "Baltic Avenue"],
 *       "ownedRailroads": [],
 *       "ownedUtilities": []
 *     },
 *     ...
 *   ],
 *   "lastDice1": 3,
 *   "lastDice2": 4,
 *   "lastEvent": "Player 1 bought Oriental Avenue for $100"
 * }
 */
public class JsonSerializer {

    /**
     * Serializes the full game state into a single-line JSON string.
     *
     * @param players          all players in the game
     * @param board            the board (used to look up location names)
     * @param turnCount        how many turns have been played
     * @param currentPlayerIdx index of the player whose turn just ended
     * @param dice1            first die value from the last roll
     * @param dice2            second die value from the last roll
     * @param lastEvent        human-readable description of what happened this turn
     * @return a single-line JSON string safe for transmission over a socket
     */
    public static String serialize(
            ArrayList<Player> players,
            Board board,
            int turnCount,
            int currentPlayerIdx,
            int dice1,
            int dice2,
            String lastEvent) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append(field("turnCount", turnCount)).append(",");
        sb.append(field("currentPlayerIndex", currentPlayerIdx)).append(",");
        sb.append(field("lastDice1", dice1)).append(",");
        sb.append(field("lastDice2", dice2)).append(",");
        sb.append(field("lastEvent", lastEvent)).append(",");

        // Players array
        sb.append("\"players\":[");
        for (int i = 0; i < players.size(); i++) {
            sb.append(serializePlayer(players.get(i), board));
            if (i < players.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    /**
     * Serializes a single player into a JSON object string.
     */
    private static String serializePlayer(Player p, Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append(field("name",         p.getPlayerName())).append(",");
        sb.append(field("balance",      p.getMoneyAmount())).append(",");
        sb.append(field("location",     p.getLocation())).append(",");
        sb.append(field("locationName", board.getProperty(p.getLocation()).getPropName())).append(",");
        sb.append(field("inJail",       p.getInJail())).append(",");
        sb.append(field("jailCount",    p.getJailCount())).append(",");
        sb.append(field("hasChanceJailCard", p.getGetOutOfJailFreeChance())).append(",");
        sb.append(field("hasChestJailCard",  p.getGetOutOfJailFreeChest())).append(",");

        // Owned properties
        sb.append("\"ownedProperties\":").append(serializeNameList(p.getOwnedProperties())).append(",");
        sb.append("\"ownedRailroads\":") .append(serializeNameList(p.getOwnedRailroads())).append(",");
        sb.append("\"ownedUtilities\":") .append(serializeNameList(p.getOwnedUtilities()));

        sb.append("}");
        return sb.toString();
    }

    /**
     * Serializes a list of Properties into a JSON array of their names.
     */
    private static String serializeNameList(ArrayList<Properties> props) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < props.size(); i++) {
            sb.append(jsonString(props.get(i).getPropName()));
            if (i < props.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // ── Primitive field helpers ───────────────────────────────────────────────

    /** "key": intValue */
    private static String field(String key, int value) {
        return jsonString(key) + ":" + value;
    }

    /** "key": boolValue */
    private static String field(String key, boolean value) {
        return jsonString(key) + ":" + value;
    }

    /** "key": "stringValue" (with escaping) */
    private static String field(String key, String value) {
        return jsonString(key) + ":" + jsonString(value);
    }

    /**
     * Wraps a string in JSON double-quotes and escapes special characters.
     * Handles: backslash, double-quote, newline, carriage return, tab.
     */
    private static String jsonString(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}