/**
 * Implements a built-in player for Bohnenspiel. 
 * Move-choice is determined by the field strategy. 
 */
import java.util.*;

public class BohnenspielPlayerBuiltin
{
    // what's my name?
    private final String name;
    // what colour do I have?
    private final Farbe farbe;
    // used to index the board
    private final int turn;
    // how do I play?
    private final BuiltinStrategy strategy;

    /**
     * Constructs a Bohnenspiel player.
     */
    public BohnenspielPlayerBuiltin(BuiltinStrategy s, Farbe f)
    { 
        strategy = s;
        name = strategy + "";
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
     * DO NOT RETURN AN ILLEGAL MOVE - that's an automatic loss. 
     */
    public int chooseMove(Bohnenspiel game)
    {
        ArrayList<Integer> xs;
        switch (strategy)
        {
           // generate random numbers until one works
           case RANDOM : 
           while (true)
           {
               int h = (int) (Math.random() * Bohnenspiel.numberofhouses);
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 0) return h + 1;
           }
           
           // search from the left
           case LEFTMOST : 
           for (int h = 0; ; h++)
           {
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 0) return h + 1;
           }
           
           // search from the right
           case RIGHTMOST :
           for (int h = Bohnenspiel.numberofhouses - 1; ; h--)
           {
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 0) return h + 1;
           }
           
           // make a list of the equal-highest, then choose randomly
           case HIGHEST :
           xs = new ArrayList<>();
           // doesn't matter if this one is empty when we are using >
           xs.add(0); 
           for (int h = 1; h < Bohnenspiel.numberofhouses; h++)
           {
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 
                   game.getBoard()[xs.get(xs.size()-1) + turn * Bohnenspiel.numberofhouses])
               {
                  xs.clear();
                  xs.add(h);
               }
               else
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] == 
                   game.getBoard()[xs.get(xs.size()-1) + turn * Bohnenspiel.numberofhouses])
                  xs.add(h);
           }
           return xs.get((int) (Math.random() * xs.size())) + 1;
           
           // make a list of the equal-lowest, then choose randomly
           // got to be careful to exclude empty houses
           case LOWEST :
           xs = new ArrayList<>();
           // find the first non-empty house
           for (int h = 0; xs.isEmpty(); h++) 
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 0) 
                  xs.add(h);
           for (int h = xs.get(xs.size()-1) + 1; h < Bohnenspiel.numberofhouses; h++)
           {
               // must avoid empty houses
               if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] > 0)
                  if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] <
                      game.getBoard()[xs.get(xs.size()-1) + turn * Bohnenspiel.numberofhouses])
                  {
                     xs.clear();
                     xs.add(h);
                  }
                  else
                  if (game.getBoard()[h + turn * Bohnenspiel.numberofhouses] == 
                      game.getBoard()[xs.get(xs.size()-1) + turn * Bohnenspiel.numberofhouses])
                     xs.add(h);
           }
           return xs.get((int) (Math.random() * xs.size())) + 1;
        }
        assert false : "Strange value in chooseMove"; 
        return 0;
    }
}
