/**
 * Manages a single game of Bohnenspiel between an "intelligent" player and a built-in player. 
 */
public class ManageBohnenspielGame
{
    // the game
    private Bohnenspiel game;
    // the players
    private BohnenspielPlayer        player;
    private BohnenspielPlayerBuiltin builtin;
    // the result: 2 if player wins, 0 if builtin wins, 1 if it's a draw
    int result;

    /**
     * Constructs and plays a game of Bohnenspiel between p and b.
     */
    public ManageBohnenspielGame(BohnenspielPlayer p, BohnenspielPlayerBuiltin b)
    {
        player = p;
        builtin = b;
        game = new Bohnenspiel();
        if (Lab7.RECORDINGGAMES) game.display(0);
        result = playGame();
        String s;
        if (result == 1) s = "Draw: ";
        else 
        if (result == 2) s =  player.getName() + " wins: ";
        else             s = builtin.getName() + " wins: ";
        System.out.print(s);
    }
    
    /** 
     * Returns the result in points.
     */
    public int getResult()
    {
        return result;
    }
    
    /**
     * Plays a game of Bohnenspiel between p and b. 
     * Returns the points for the player.
     */
    public int playGame()
    {
        int move = 0;
        Farbe turn = Farbe.first();
        while (!game.isOver() && move >= 0)
        {
            Bohnenspiel g = game.copyGame(turn);
            if (turn ==  player.getFarbe()) 
            {
                try 
                {
                    move = player.chooseMove(g);
                    game.move(move);
                }
                catch (IllegalArgumentException e) {move = -1;}
            }
            else                          
            if (turn == builtin.getFarbe()) 
            {
                try 
                {
                    move = builtin.chooseMove(g);
                    game.move(move);
                }
                catch (IllegalArgumentException e) {move = -2;}
            }
            else
            assert false : "Something wrong in playGame";
            if (Lab7.RECORDINGGAMES) game.display(move);
            try {Thread.sleep(0);} catch (Exception e){};
            turn = Farbe.flip(turn);
        }
        if (move < 0) 
           System.out.println("Illegal move played by " + turn + " " + move);
        if (move == -2 ||
            game.getStores()[player.getFarbe().ordinal()] > game.getStores()[builtin.getFarbe().ordinal()]) return 2;
        else
        if (move == -1 ||
            game.getStores()[player.getFarbe().ordinal()] < game.getStores()[builtin.getFarbe().ordinal()]) return 0;
        else
        return 1;
    }
}
