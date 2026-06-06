import java.util.ArrayList;

// This JSON serialializer is the file that creates the JSON file which is the single string payload that is sent to the client so the other player can update their local version of the game state 
public class JsonSerializer {


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

    // Takes in the arraylists of all gamestate objects listed in the player and associated classes and appends it to the payload
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

    // Takes in the arraylist of properties and appends it to the payload
    private static String serializeNameList(ArrayList<Properties> props) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < props.size(); i++) {
            sb.append(jsonString(props.get(i).getPropName()));
            if (i < props.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // Methods to return value types

    // int
    private static String field(String key, int value) {
        return jsonString(key) + ":" + value;
    }

    // boolean
    private static String field(String key, boolean value) {
        return jsonString(key) + ":" + value;
    }

    // string
    private static String field(String key, String value) {
        return jsonString(key) + ":" + jsonString(value);
    }

    // Wraps the parsed json strings in quotes and slashes to protect from any kind of injection attacks or formatting issues as a results of unexpected characters
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