public class Die {
    private int numSides;

    public Die(int numSides) {
        if (numSides < 2) {
            this.numSides = 6;
        } else {
            this.numSides = numSides;
        }
    }

    public Die() {
        numSides = 6;
    }

    public int getSides() {
        return numSides;
    }

    // rolls the die and returns a random value between 1 and numSides
    public int roll() {
        return (int) (Math.random() * numSides) + 1;
    }

    // rolls die numRolls times and returns max value
    public int getMaxRoll(int numRolls) {
        int max = 0;
        for (int i = 0; i < numRolls; i++) {
            int roll = roll();
            if (roll > max) {
                max = roll;
            }
        }
        return max;
    }

    public String toString() {
        return "This is a " + numSides + "-sided die.";
    }
}
