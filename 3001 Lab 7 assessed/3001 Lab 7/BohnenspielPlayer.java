/**
 * Implements an intelligent player for Bohnenspiel. 
 */
import java.util.*;

public class BohnenspielPlayer
{
    // what's my name?
    private final String name;
    // what colour do I have?
    private final Farbe farbe;
    // used to index the board
    private final int turn;

    private int ply;

    /**
     * Constructs a Bohnenspiel player.
     */
    public BohnenspielPlayer(Farbe f)
    {
        name = "Agent Smith";
        farbe = f;
        turn = farbe.ordinal();
    }

    /**
     * Returns the player's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the player's colour.
     */
    public Farbe getFarbe()
    {
        return farbe;
    }

    /**
     * Returns a legal move in game, i.e. a number h in [1, 6]. 
     * h must denote a non-empty house on this player's side of the board. 
     * You can assume that at least one legal move is available. 
     * DO NOT RETURN AN ILLEGAL MOVE - that's an automatic loss of game. 
     */
    public int chooseMove(Bohnenspiel game)
    {
        // COMPLETE THIS 
        // Placeholder simply plays a random move
        int depth = ply = 3;
        
        int[] results = new int[6];
        int bestEv = Integer.MIN_VALUE;
        int bestMove = 0;
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        for (int i = 0; i < 6; i++)
        {
            Bohnenspiel copy = game.copyGame(this.farbe);
            if(game.getBoard()[i + turn * Bohnenspiel.numberofhouses] > 0) {
                copy.move(i+1);
            }
            else {
                continue;
            }
            results[i] = minimax(copy, depth, a, b, true);
            a = Math.max(a, results[i]);
            if (bestEv < results[i]){
                bestEv = results[i];
                bestMove = i+1;
            }
        }
        return bestMove;
    }

    /**
     * Executes the Minimax algorithm.
     */
    private int minimax(Bohnenspiel game, int depth, int a, int b, boolean maximisingPlayer)
    {
        if(depth == 0 || game.isOver()) return evaluate(game);
        int[] board = game.getBoard();
        if (maximisingPlayer){
            int maxEv = Integer.MIN_VALUE;
            for (int i = 0; i < 6; i++){
                Bohnenspiel copy = game.copyGame(this.farbe);
                if(depth != ply)
                {
                    if(board[i + turn * Bohnenspiel.numberofhouses] > 0) copy.move(i+1);
                    else continue;
                }
                int ev = minimax(copy, depth - 1, a, b, false);
                maxEv = Math.max(maxEv, ev);
                a = Math.max(a, ev);
                if (b <= a) break;
            }
            return maxEv;
        }
        else{
            int minEv = Integer.MAX_VALUE;
            for (int i = 0; i < 6; i++){
                Bohnenspiel copy = game.copyGame(farbe.flip(this.farbe));
                if (board[i + farbe.flip(this.farbe).ordinal() * Bohnenspiel.numberofhouses] > 0) copy.move(i+1);
                else continue;
                int ev = minimax(copy, depth - 1, a, b, true);
                minEv = Math.min(minEv, ev);
                b = Math.min(b, ev);
                if (b <= a) break;
            }
            return minEv;
        }
    }

    /**
     * Evaluates position on the board and returns a number
     */
    private int evaluate(Bohnenspiel game)
    {
        int[] scores = game.getStores();
        int[] board = game.getBoard();
        int count = 0;
        if(turn == 0) {
            if (scores[0] > 36) return Integer.MAX_VALUE;
            else if (scores[1] > 36) return Integer.MIN_VALUE;
            for(int i = 1; i < 6; i++){
                if (board[i] == board[i-1] && board[i] == 0){
                    count++;
                }
            }
            if (count == 5) return Integer.MIN_VALUE;
            return scores[0] - scores[1];
        }
        else {
            if (scores[1] > 36) return Integer.MAX_VALUE;
            else if (scores[0] > 36) return Integer.MIN_VALUE;
            for(int i = 1; i < 6; i++){
                if (board[i + Bohnenspiel.numberofhouses] == board[i-1 + Bohnenspiel.numberofhouses] && board[i + Bohnenspiel.numberofhouses] == 0){
                    count++;
                }
            }
            if (count == 5) return Integer.MIN_VALUE;
            return scores[1] - scores[0];
        }
    }
}