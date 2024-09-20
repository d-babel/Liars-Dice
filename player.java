import java.util.ArrayList;

public class player {
    private ArrayList<Integer> dice;
    private String name;
    private String difficulty;
    private boolean isHuman;
    private int diceCount;

    public player(String name) {
        this.name = name;
        this.isHuman = true;
        this.dice = new ArrayList<>();
        this.diceCount = 5; 
    }

    public player(String name, String difficulty) {
        this.name = name;
        this.difficulty = difficulty;
        this.isHuman = false;
        this.dice = new ArrayList<>();
        this.diceCount = 5; 
    }

    // adds die
    public void addDie(int value) {
        dice.add(value);
    }

    // remove die
    public void removeDie() {
        if (diceCount > 0) {
            diceCount--;
        }
    }

    public int getDiceCount() {
        return diceCount;
    }

    public ArrayList<Integer> getDice() {
        return dice;
    }

    public void clearDice() {
        dice.clear();
    }

    public boolean isHuman() {
        return isHuman;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
