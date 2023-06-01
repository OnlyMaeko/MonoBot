import java.util.ArrayList;

public class Player {

    private int location;
    private String playerName;
    private ArrayList<Properties> ownedProperties;
    private ArrayList<Properties> ownedRailroads;
    private ArrayList<Properties> ownedUtilities;
    private ArrayList<Properties> ownedMonopolies;
    private int moneyAmount;
    private int jailCount;
    private boolean inJail;

    public Player(String playerName, int locationOnBoard, ArrayList<Properties> ownedProperties,
                  ArrayList<Properties> ownedRailroads, ArrayList<Properties> ownedUtilities,
                  ArrayList<Properties> ownedMonopolies, int moneyAmount, int jailCount, boolean inJail) {
        this.playerName = playerName;
        this.location = locationOnBoard;
        this.ownedProperties = ownedProperties;
        this.ownedRailroads = ownedRailroads;
        this.ownedUtilities = ownedUtilities;
        this.ownedMonopolies = ownedMonopolies;
        this.moneyAmount = moneyAmount;
        this.jailCount = jailCount;
        this.inJail = inJail;
    }

    // Getters
    public int getLocation() {
        return location;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ArrayList<Properties> getOwnedProperties() {
        return ownedProperties;
    }

    public ArrayList<Properties> getOwnedRailroads() {
        return ownedRailroads;
    }

    public ArrayList<Properties> getOwnedUtilities() {
        return ownedUtilities;
    }

    public ArrayList<Properties> getOwnedMonopolies() {
        return ownedMonopolies;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public int getJailCount() {
        return jailCount;
    }

    public boolean getInJail() {
        return inJail;
    }

    // Setters
    public void setLocation(int newLocation) {
        this.location = newLocation;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setOwnedProperties(ArrayList<Properties> ownedProperties) {
        this.ownedProperties = ownedProperties;
    }

    public void setOwnedRailroads(ArrayList<Properties> ownedRailroads) {
        this.ownedRailroads = ownedRailroads;
    }

    public void setOwnedUtilities(ArrayList<Properties> ownedUtilities) {
        this.ownedUtilities = ownedUtilities;
    }

    public void setOwnedMonopolies(ArrayList<Properties> ownedMonopolies) {
        this.ownedMonopolies = ownedMonopolies;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public void changeMoney(int money) {
        this.moneyAmount += money;
    }

    public void setJailCount(int jailCount) {
        this.jailCount = jailCount;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }
}