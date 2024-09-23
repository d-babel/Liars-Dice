import java.util.ArrayList;
import java.util.Scanner;

public class LiarDiceGame {
    private ArrayList<player> players;
    private int currentBidQuantity;
    private int currentBidFace;
    private Scanner scanner;
    private Die die;
    private int currentPlayerIndex;

    // game initialize
    public LiarDiceGame() {
        players = new ArrayList<>();
        scanner = new Scanner(System.in);
        die = new Die(6);
        currentBidQuantity = 0;
        currentBidFace = 0;
        currentPlayerIndex = 0;
    }

    // start game
    public void startGame() {
        boolean playAgain = true;
        while (playAgain) {
            showRules();
            initializePlayers();
            playRounds();
            System.out.println();
            String response = getValidYesNoResponse("Do you want to play another game? (yes/no)");
            if (!response.equalsIgnoreCase("yes")) {
                playAgain = false;
            } else {
                // reset game state
                players.clear();
                currentBidQuantity = 0;
                currentBidFace = 0;
                currentPlayerIndex = 0;
            }
        }
        System.out.println();
        System.out.println("Thanks for playing Liar's Dice!");
    }

    // game rules
    private void showRules() {
        clearConsole();
        
        System.out.println(".____     .__                          ________   .__                 ");
        System.out.println("|    |    |__|_____   _______   ______ \\______ \\  |__|  ____    ____  ");
        System.out.println("|    |    |  |\\__  \\  \\_  __ \\ /  ___/  |    |  \\ |  |_\\ ___\\ _/ __ \\ ");
        System.out.println("|    |___ |  | / __ \\_ |  | \\/ \\___ \\   |    `   \\|  |\\  \\___ \\  ___/ ");
        System.out.println("|_______ \\|__|(____  / |__|   /____  > /_______  /|__| \\___  > \\___  >");
        System.out.println("        \\/         \\/              \\/          \\/          \\/     \\/ ");      
        
        System.out.println();
        System.out.println("Welcome to Liar's Dice!");
        System.out.println();
        System.out.println("Here are the rules:");
        System.out.println();
        System.out.println("Players start with five dice each, hidden from others.");
        System.out.println("On your turn, either bid on the total number of a specific face value among all dice,");
        System.out.println("or call \"liar\" to challenge the previous bid;");
        System.out.println("the last player with dice remaining wins.");
        System.out.println();
        String response = getValidYesNoResponse("Do you understand the rules? (yes/no)");
        if (!response.equalsIgnoreCase("yes")) {
            System.out.println();
            System.out.println("Please read the rules carefully before playing.");
            System.out.println();
            showRules();
        }
    }

    // initialize players/opponents
    private void initializePlayers() {
        clearConsole();
        System.out.println("Enter your name:");
        String name = scanner.nextLine();
        player humanPlayer = new player(name);
        players.add(humanPlayer);

        System.out.println();
        System.out.println("Choose difficulty level for AI opponents (easy/medium/hard):");
        String difficulty = getValidDifficulty();

        System.out.println();
        int numAI = getValidAIPlayerCount();

        System.out.println();
        // adding AI opponents
        for (int i = 1; i <= numAI; i++) {
            players.add(new player("AI_Player_" + i, difficulty));
        }

        // initializing dice for each player
        for (player player : players) {
            rollDiceForPlayer(player);
        }
    }

    // rolls dice for a player
    private void rollDiceForPlayer(player player) {
        player.clearDice();
        for (int i = 0; i < player.getDiceCount(); i++) {
            player.addDie(die.roll());
        }
    }

    // plays the rounds until there is a winner
    public void playRounds() {
        boolean gameOn = true;

        while (gameOn) {
            for (player player : players) {
                rollDiceForPlayer(player);
            }

            currentBidQuantity = 0;
            currentBidFace = 0;

            boolean roundOngoing = true;

            while (roundOngoing) {
                player currentPlayer = players.get(currentPlayerIndex);
                clearConsole();
                System.out.println("It's " + currentPlayer.getName() + "'s turn.");
                System.out.println();

                if (currentPlayer.isHuman()) {
                    humanTurn(currentPlayer);
                } else {
                    aiTurn(currentPlayer);
                }

                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

                // check if the last bid was challenged
                if (currentBidQuantity == -1) {
                    roundOngoing = false;
                    currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
                }
            }

            gameOn = checkForWinner();
        }

        System.out.println();
        System.out.println("Game Over!");
        System.out.println("Winner is: " + players.get(0).getName());
        System.out.println();
    }

    // human players turn
    private void humanTurn(player player) {
        System.out.println("Your dice: " + player.getDice());
        System.out.println();
        if (currentBidQuantity > 0) {
            System.out.println("Current bid is " + currentBidQuantity + " of face " + currentBidFace);
        } else {
            System.out.println("No bids have been made yet.");
        }
        System.out.println();
        String choice = getValidChoice();

        if (choice.equals("1")) {
            makeBid(player);
        } else if (choice.equals("2")) {
            if (currentBidQuantity == 0) {
                System.out.println("No bid to challenge. You must make a bid.");
                System.out.println();
                humanTurn(player);
            } else {
                callLiar(player);
            }
        }
    }

    // human player bid
    private void makeBid(player player) {
        System.out.println();
        System.out.println("Enter your bid in the format 'quantity face' (e.g., '3 2' for three twos):");
        String[] bidInput = scanner.nextLine().split(" ");
        try {
            int bidQuantity = Integer.parseInt(bidInput[0]);
            int bidFace = Integer.parseInt(bidInput[1]);

            if (isValidBid(bidQuantity, bidFace)) {
                currentBidQuantity = bidQuantity;
                currentBidFace = bidFace;
            } else {
                System.out.println("Invalid bid. Your bid must increase either the quantity or face value within dice parameters.");
                System.out.println();
                makeBid(player);
            }
        } catch (Exception e) {
            System.out.println("Invalid input format. Please try again.");
            System.out.println();
            makeBid(player);
        }
    }

    // opponents turn
    private void aiTurn(player player) {
        System.out.println(player.getName() + " is thinking...");
        // Small delay to simulate thinking
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Do nothing
        }

        // difficulty level
        String difficulty = player.getDifficulty();
        boolean willCallLiar = false;

        // decide to call 'liar'
        if (currentBidQuantity > 0 && Math.random() < 0.2) {
            willCallLiar = true;
        }

        if (willCallLiar) {
            System.out.println(player.getName() + " calls 'liar'!");
            System.out.println();
            callLiar(player);
        } else {
            int bidQuantity;
            int bidFace;

            if (difficulty.equalsIgnoreCase("easy")) {
                bidQuantity = currentBidQuantity + 1;
                bidFace = (int) (Math.random() * 6) + 1;
            } else if (difficulty.equalsIgnoreCase("medium")) {
                // medium AI considers its own dice
                int[] faceCounts = new int[6];
                for (int dieFace : player.getDice()) {
                    faceCounts[dieFace - 1]++;
                }
                int maxCount = 0;
                int maxFace = 1;
                for (int i = 0; i < 6; i++) {
                    if (faceCounts[i] > maxCount) {
                        maxCount = faceCounts[i];
                        maxFace = i + 1;
                    }
                }
                bidQuantity = Math.max(currentBidQuantity + 1, maxCount);
                bidFace = maxFace;
            } else {
                // hard AI uses more advanced strategy
                int totalDice = 0;
                for (player p : players) {
                    totalDice += p.getDiceCount();
                }
                int estimatedFaceCount = totalDice / 6;
                bidQuantity = Math.max(currentBidQuantity + 1, estimatedFaceCount);
                bidFace = currentBidFace;
                if (bidFace > 6) {
                    bidFace = 1;
                    bidQuantity++;
                }
            }

            // ensure the bid is valid
            if (!isValidBid(bidQuantity, bidFace)) {
                bidQuantity = currentBidQuantity;
                bidFace = currentBidFace + 1;
                if (bidFace > 6) {
                    bidQuantity++;
                    bidFace = 1;
                }
            }

            System.out.println(player.getName() + " bids " + bidQuantity + " of face " + bidFace);
            currentBidQuantity = bidQuantity;
            currentBidFace = bidFace;
            System.out.println();
        }
    }

    // validates the new bid
    private boolean isValidBid(int bidQuantity, int bidFace) {
        if (bidFace < 1 || bidFace > 6) {
            return false;
        }
        if (bidQuantity < 1) {
            return false;
        }
        if (currentBidQuantity == 0) {
            return true;
        }
        if (bidQuantity > currentBidQuantity) {
            return true;
        } else if (bidQuantity == currentBidQuantity && bidFace > currentBidFace) {
            return true;
        } else {
            return false;
        }
    }

    // allows a player to call 'liar'
    private void callLiar(player challenger) {
        System.out.println(challenger.getName() + " has called 'liar' on the previous bid!");
        System.out.println();

        // reveal dice
        ArrayList<Integer> allDice = new ArrayList<>();
        for (player player : players) {
            allDice.addAll(player.getDice());
            System.out.println(player.getName() + "'s dice: " + player.getDice());
        }

        System.out.println();

        // count number of dice showing bid face
        int count = 0;
        for (int dieFace : allDice) {
            if (dieFace == currentBidFace) {
                count++;
            }
        }

        System.out.println("There are " + count + " dice showing face " + currentBidFace + ".");
        System.out.println();

        // determine who loses a die
        player previousPlayer = players.get((currentPlayerIndex - 1 + players.size()) % players.size());
        if (count >= currentBidQuantity) {
            System.out.println(challenger.getName() + " loses a die.");
        } else {
            System.out.println(previousPlayer.getName() + " loses a die.");
        }

        System.out.println();

        // remove die from the player who lost
        if (count >= currentBidQuantity) {
            challenger.removeDie();
        } else {
            previousPlayer.removeDie();
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();

        currentBidQuantity = -1;
    }

    // checks if there is a winner
    private boolean checkForWinner() {
        // remove players with no dice left
        players.removeIf(player -> player.getDiceCount() == 0);

        // check if only one player remains
        if (players.size() == 1) {
            return false;
        } else {
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
            return true;
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // gets valid yes or no response
    private String getValidYesNoResponse(String prompt) {
        System.out.println(prompt);
        String response = scanner.nextLine().trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            clearConsole();
            System.out.println("Invalid input. Please answer 'yes' or 'no'.");
            System.out.println();
            System.out.println(prompt);
            response = scanner.nextLine().trim().toLowerCase();
        }
        return response;
    }

    // gets valid difficulty level
    private String getValidDifficulty() {
        String difficulty = scanner.nextLine().trim().toLowerCase();
        while (!difficulty.equals("easy") && !difficulty.equals("medium") && !difficulty.equals("hard")) {
            clearConsole();
            System.out.println("Invalid input. Please choose 'easy', 'medium', or 'hard'.");
            System.out.println();
            difficulty = scanner.nextLine().trim().toLowerCase();
        }
        return difficulty;
    }

    // gets valid number of AI opponents
    private int getValidAIPlayerCount() {
        int numAI = 0;
        while (numAI < 1 || numAI > 5) {
            try {
                System.out.println("How many AI opponents do you want to play with? (1-5)");
                numAI = Integer.parseInt(scanner.nextLine().trim());
                if (numAI < 1 || numAI > 5) {
                    clearConsole();
                    System.out.println("Invalid number. Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                clearConsole();
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return numAI;
    }

    // gets valid choice for human action
    private String getValidChoice() {
        System.out.println("Do you want to (1) make a bid or (2) call 'liar'?");
        String choice = scanner.nextLine().trim();
        while (!choice.equals("1") && !choice.equals("2")) {
            clearConsole();
            System.out.println("Invalid choice. Please enter '1' or '2'.");
            System.out.println();
            System.out.println("Do you want to (1) make a bid or (2) call 'liar'?");
            choice = scanner.nextLine().trim();
        }
        return choice;
    }

    public static void main(String[] args) {
        LiarDiceGame game = new LiarDiceGame();
        game.startGame();
    }
}
