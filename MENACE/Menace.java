import java.util.*;

class Board {
    private char[] board;

    public Board() {
        board = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    }

    public boolean isValidMove(int move) {
        return move >= 0 && move < 9 && board[move] == ' ';
    }

    public void makeMove(int move, char marker) {
        board[move] = marker;
    }

    public boolean isWinning() {
        return (board[0] != ' ' && (board[0] == board[1] && board[1] == board[2] || board[0] == board[3] && board[3] == board[6] || board[0] == board[4] && board[4] == board[8]))
                || (board[4] != ' ' && (board[1] == board[4] && board[4] == board[7] || board[3] == board[4] && board[4] == board[5] || board[2] == board[4] && board[4] == board[6]))
                || (board[8] != ' ' && (board[2] == board[5] && board[5] == board[8] || board[6] == board[7] && board[7] == board[8]));
    }

    public boolean isDraw() {
        for (char c : board) {
            if (c == ' ') return false;
        }
        return true;
    }

    public String getBoardState() {
        return new String(board);
    }

    public void printBoard() {
        System.out.printf(" %c | %c | %c \n---+---+---\n %c | %c | %c \n---+---+---\n %c | %c | %c \n",
                board[0], board[1], board[2], board[3], board[4], board[5], board[6], board[7], board[8]);
    }
}

class MenacePlayer {
    private Map<String, List<Integer>> matchboxes;
    private List<int[]> movesPlayed;
    private int wins, draws, losses;

    public MenacePlayer() {
        matchboxes = new HashMap<>();
        wins = draws = losses = 0;
    }

    public void startGame() {
        movesPlayed = new ArrayList<>();
    }

    public int getMove(Board board) {
        String state = board.getBoardState();
        List<Integer> beads = matchboxes.computeIfAbsent(state, s -> {
            List<Integer> newBeads = new ArrayList<>();
            for (int i = 0; i < 9; i++) if (board.isValidMove(i)) newBeads.add(i);
            return newBeads;
        });

        if (beads.isEmpty()) return -1; // resign if no moves left
        int move = beads.get(new Random().nextInt(beads.size()));
        movesPlayed.add(new int[]{state.hashCode(), move});
        return move;
    }

    public void winGame() {
        for (int[] move : movesPlayed) {
            List<Integer> beads = matchboxes.get(String.valueOf(move[0]));
            beads.add(move[1]);
            beads.add(move[1]);
            beads.add(move[1]);
        }
        wins++;
    }

    public void drawGame() {
        for (int[] move : movesPlayed) {
            List<Integer> beads = matchboxes.get(String.valueOf(move[0]));
            beads.add(move[1]);
        }
        draws++;
    }

    public void loseGame() {
        for (int[] move : movesPlayed) {
            List<Integer> beads = matchboxes.get(String.valueOf(move[0]));
            beads.remove(Integer.valueOf(move[1]));
        }
        losses++;
    }

    public void printStats() {
        System.out.printf("Matchboxes: %d, Wins: %d, Draws: %d, Losses: %d%n", matchboxes.size(), wins, draws, losses);
    }
}

class HumanPlayer {
    private Scanner scanner;

    public HumanPlayer() {
        scanner = new Scanner(System.in);
    }

    public int getMove(Board board) {
        while (true) {
            System.out.print("Your move (0-8): ");
            int move = scanner.nextInt();
            if (board.isValidMove(move)) return move;
            System.out.println("Invalid move, try again.");
        }
    }
}

public class Menace {
    public static void main(String[] args) {
        MenacePlayer menace = new MenacePlayer();
        HumanPlayer human = new HumanPlayer();
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 1000; i++) {
            playGame(menace, human, false);
        }

        menace.printStats();

        while (true) {
            playGame(menace, human, true);
            System.out.print("Play again? (yes/no): ");
            if (!scanner.next().equalsIgnoreCase("yes")) break;
        }
    }

    private static void playGame(MenacePlayer menace, HumanPlayer human, boolean display) {
        menace.startGame();
        Board board = new Board();
        boolean menaceTurn = true;

        if (display) {
            System.out.println("Starting new game!");
            board.printBoard();
        }

        while (true) {
            if (menaceTurn) {
                int move = menace.getMove(board);
                if (move == -1) {
                    if (display) System.out.println("MENACE resigns!");
                    menace.loseGame();
                    return;
                }
                board.makeMove(move, 'X');
            } else {
                int move = human.getMove(board);
                board.makeMove(move, 'O');
            }

            if (display) board.printBoard();

            if (board.isWinning()) {
                if (menaceTurn) {
                    menace.winGame();
                    if (display) System.out.println("MENACE wins!");
                } else {
                    menace.loseGame();
                    if (display) System.out.println("You win!");
                }
                return;
            }

            if (board.isDraw()) {
                menace.drawGame();
                if (display) System.out.println("It's a draw!");
                return;
            }

            menaceTurn = !menaceTurn;
        }
    }
}
