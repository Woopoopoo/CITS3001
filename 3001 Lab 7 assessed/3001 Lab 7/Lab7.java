/**
 * Assesses an intelligent Bohnenspiel player against a range of built-in players; 
 * and allows a human to play against the intelligent player. 
 */
import java.awt.*;
import java.awt.event.*; 

public class Lab7 implements MouseListener
{
    private static final int MINRADIUS   = 20; // don't change this
    public  static final int HOUSERADIUS = Math.max(MINRADIUS, 60);
    public  static final int STORESIZE   = HOUSERADIUS * 5 / 2;
    public  static final int OFFSET      = HOUSERADIUS * 3 / 2;
    public  static final int FONTSIZE    = HOUSERADIUS;
    public  static int[][] HOUSECENTRES  = new int[Bohnenspiel.boardsize][2];
    public  static int[][] STORETOPLEFTS = new int[Bohnenspiel.numberofstores][2];
    
    public static final Color   BACKCOLOR  = Color.WHITE; 
    public static final Color   LINECOLOR  = Color.BLACK; 
    public static final Color[] STORECOLOR = {Color.RED, Color.BLUE}; 
    
    private static BohnenspielPlayer opposition;
    private static Bohnenspiel game;
    private static SimpleCanvas sc;
    private static Farbe human; // the human's colour
    private static Farbe next;  // turn indicator
    
    // do you want to see the splash screen?
    private static final boolean YOUWANTSPLASH = true;
    // do you want to record the games?
    public  static final boolean RECORDINGGAMES = false;
    
    /**
     * Creates a game against the computer player, with the human playing farbe.
     */
    public Lab7(Farbe farbe)
    {
        human = farbe;
        next = Farbe.first();
        opposition = new BohnenspielPlayer(Farbe.flip(human));
        game = new Bohnenspiel();
        sc = new SimpleCanvas("Das Bohnenspiel", OFFSET * 2 + STORESIZE * (Bohnenspiel.numberofhouses - 1), 
                                                 STORESIZE + 4 * OFFSET + 40, BACKCOLOR);
        sc.addMouseListener(this);
        if (YOUWANTSPLASH) splash();
        // set up the screen coordinates
        for (int t = 0; t < Bohnenspiel.numberofstores; t++)
        {
            for (int h = 0; h < Bohnenspiel.numberofhouses; h++)
            {
                HOUSECENTRES[h + t * (Bohnenspiel.boardsize - 1 - 2 * h)][0] = OFFSET + h * STORESIZE;
                HOUSECENTRES[h + t * Bohnenspiel.numberofhouses][1]          = OFFSET + (1 - t) * (STORESIZE + OFFSET * 2);
            }
            STORETOPLEFTS[t][0] = OFFSET +  t * (Bohnenspiel.numberofhouses - 2) * STORESIZE;
            STORETOPLEFTS[t][1] = OFFSET * 2;
            // draw the store outlines
            sc.drawRectangle(STORETOPLEFTS[t][0],             STORETOPLEFTS[t][1], 
                             STORETOPLEFTS[t][0] + STORESIZE, STORETOPLEFTS[t][1] + STORESIZE, 
                             STORECOLOR[(human.ordinal() + t) % Bohnenspiel.numberofstores]);
            sc.setFont(new Font("Times", 1, FONTSIZE / 2));
            sc.drawString(new String[] {opposition.getName(), "Human"} [Math.abs(t - human.ordinal())], 
                          STORETOPLEFTS[t][0], STORETOPLEFTS[t][1] + (STORESIZE * 12 / 10) * t - 5 * (1 - t), LINECOLOR);
            sc.setFont(new Font("Times", 1, FONTSIZE));
        }
        // draw the initial board
        game.drawBoard(sc, -1);
        if (human != next) clickAnywhere();
    }
    
    /**
     * Creates a game against the computer player, with the human playing WEISSE.
     */
    public Lab7()
    {
        this(Farbe.WEISSE);
    }
    
    /**
     * Put up and then clear the splash screen.
     */
    private void splash()
    {
        sc.setFont(new Font("Times", 1, FONTSIZE / 2));
        sc.drawString("For a human move, click on one of your non-empty houses;", HOUSERADIUS / 2, OFFSET,                   LINECOLOR);
        sc.drawString("for a computer move, click anywhere in the window.",       HOUSERADIUS / 2, OFFSET + HOUSERADIUS,     LINECOLOR);
        sc.drawString("The human's store is in blue;",                            HOUSERADIUS / 2, OFFSET + HOUSERADIUS * 3, STORECOLOR[1]);
        sc.drawString("the computer's store is in red.",                          HOUSERADIUS / 2, OFFSET + HOUSERADIUS * 4, STORECOLOR[0]);
        sc.setFont(new Font("Times", 1, FONTSIZE));
        try{Thread.sleep(4000);}catch(Exception e){};
        sc.drawRectangle(0, 0, 1000, 1000, BACKCOLOR);
    }
    
    /**
     * Put up the click anywhere banner.
     */
    private void clickAnywhere()
    {
        if (true)
        {
            int p = Farbe.flip(next).ordinal();
            sc.setFont(new Font("Times", 1, FONTSIZE / 2));
            sc.drawString("click anywhere", Math.max(STORETOPLEFTS[0][0] + STORESIZE * 11 / 10, (STORETOPLEFTS[1][0] - STORESIZE * 3 / 2) * p), 
                                                     STORETOPLEFTS[0][1] + Math.max(STORESIZE * 17 / 20 * p, STORESIZE / 4), LINECOLOR);
            sc.setFont(new Font("Times", 1, FONTSIZE));
        }
    }
    
    /**
     * Runs the computer player against a range of built-in players.
     */
    public static void main(String[] args)
    {
        BohnenspielPlayer b;
        BohnenspielPlayerBuiltin opponent;
        int gamesPerOpponent = 10;
        int score = 0;
        // for each colour
        for (Farbe f : Farbe.values())
        {
           b = new BohnenspielPlayer(f);
           // for each strategy
           for (BuiltinStrategy k : BuiltinStrategy.values())
           {
               opponent = new BohnenspielPlayerBuiltin(k, Farbe.flip(f));
               if (f == Farbe.first()) System.out.println(b.getName() + " vs " + opponent.getName());
               else                    System.out.println(opponent.getName() + " vs " + b.getName());
               int[] results = {0, 0, 0}; // losses, draws, wins
               // for some number of repetitions
               for (int g = 0; g < gamesPerOpponent; g++)
                   results[new ManageBohnenspielGame(b, opponent).getResult()] += 1;
               System.out.println(results[2] + " wins, " + results[1] + " draws, " + results[0] + " losses");
               score += results[1] + 2 * results[2];
           }
        }
        System.out.println("Total points = " + score + "/" + 2 * 2 * BuiltinStrategy.values().length * gamesPerOpponent);
        System.out.println();
    }
    
   private double distance(double x1, double y1, double x2, double y2)
   {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
   }
    
    public void mousePressed(MouseEvent e) 
    {
        boolean illegalMove = false;
        if (next == human) // human's turn
        {
           boolean played = false; 
           // check each house for a legal click
           for (int h = 0; h < Bohnenspiel.numberofhouses && !played; h++)
               if (distance(e.getX(), e.getY(), HOUSECENTRES[h + Bohnenspiel.numberofhouses * next.ordinal()][0], 
                                                HOUSECENTRES[h + Bohnenspiel.numberofhouses * next.ordinal()][1]) < HOUSERADIUS &&
                   game.getBoard()[h + Bohnenspiel.numberofhouses * next.ordinal()] > 0)
               {
                  try 
                  {
                      game.move(h + 1);
                      game.drawBoard(sc, h + Bohnenspiel.numberofhouses * next.ordinal());
                      next = Farbe.flip(next);
                      clickAnywhere();
                  }
                  catch (Exception x) {illegalMove = true;}
                  played = true;
               }
        }
        else // computer's turn
        try 
        {
            int h = opposition.chooseMove(game.copyGame(next));
            game.move(h); 
            game.drawBoard(sc, h + Bohnenspiel.numberofhouses * next.ordinal() - 1);
            next = Farbe.flip(next);
        }
        catch (Exception x) {illegalMove = true;}
        if (game.isOver() || illegalMove) 
        {
            int x = (HOUSECENTRES[0][0] + HOUSECENTRES[1][0]) / 2 + STORESIZE * 9 / 10;
            int y = (HOUSECENTRES[0][1] + HOUSECENTRES[Bohnenspiel.numberofhouses][1]) / 2 + HOUSERADIUS / 3;
            sc.drawRectangle(STORETOPLEFTS[0][0] + STORESIZE, STORETOPLEFTS[0][1], 
                             STORETOPLEFTS[1][0], STORETOPLEFTS[1][1] + STORESIZE, BACKCOLOR);
            sc.drawString("Game over!", x, y, LINECOLOR);
        }
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
