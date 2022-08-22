/**
 * Represents the state of a Bohnenspiel game. 
 * https://en.wikipedia.org/wiki/Das_Bohnenspiel
 */
import java.util.*;
import java.awt.*;

public class Bohnenspiel
{
    public static final int numberofhouses = 6;
    public static final int numberofstores = 2;
    public static final int boardsize      = numberofstores * numberofhouses;
    public static final int beansperhouse  = 6;
    
    // the board is an array of length boardsize denoting
    // the number of beans in each house 
    private int[] board;
    // the stores denote the number of beans in each store
    private int[] stores;
    // whose turn is it? 
    private int turn;
    // is the game over?
    private boolean gameOver;

    /**
     * Constructs the starting position of a game of Bohnenspiel. 
     */
    public Bohnenspiel()
    {
        board = new int[boardsize];
        for (int h = 0; h < boardsize; h++) board[h]= beansperhouse;
        stores = new int[numberofstores];
        turn = 0;
        gameOver = false;
    }
    
    /**
     * Sets up an arbitrary Bohnenspiel position. 
     * Checks the position for plausibility and makes new copies of the arrays. 
     */
    public Bohnenspiel(int[] b, int[] ss, Farbe f) throws IllegalArgumentException
    {
        // validate b
        if (b.length != boardsize) throw new IllegalArgumentException("Wrong number of houses: " + b.length);
        board = Arrays.copyOf(b, boardsize);
        int total = 0;
        for (int k = 0; k < boardsize; k++)
        {
            if (board[k] < 0) throw new IllegalArgumentException("Negative beans in house " + k);
            total += board[k];
        }
        // validate ss
        if (ss.length != numberofstores) throw new IllegalArgumentException("Wrong number of stores: " + ss.length);
        stores = Arrays.copyOf(ss, numberofstores);
        for (int k = 0; k <= 1; k++)
        {
            if (stores[k] < 0) throw new IllegalArgumentException("Negative beans in store " + k);
            total += stores[k];
        }
        // validate the total number of beans
        if (total != boardsize * beansperhouse) throw new IllegalArgumentException("Wrong total number of beans: " + total);
        turn = f.ordinal();
        gameOver();
    }
    
    /**
     * Returns a copy of the board.
     */
    public int[] getBoard()
    {
        return Arrays.copyOf(board, boardsize);
    }
    
    /**
     * Returns a copy of the stores.
     */
    public int[] getStores()
    {
        return Arrays.copyOf(stores, numberofstores);
    }
    
    /**
     * Returns the game status.
     */
    public boolean isOver()
    {
        return gameOver;
    }
    
    /**
     * Returns a copy of this game.
     */
    public Bohnenspiel copyGame(Farbe farbe)
    {
        return new Bohnenspiel(getBoard(), getStores(), farbe);
    }
    
    /**
     * Sets the game status to true iff the game is over. 
     */
    private void gameOver()
    {
        // if either side has a winning lead 
        if (stores[turn] > boardsize * beansperhouse / 2 || stores[1 - turn] > boardsize * beansperhouse / 2)
           gameOver = true;
        else
           for (int t = 0; t < 2 && !gameOver; t++)
           {
               boolean g = true;
               for (int k = 0; k < numberofhouses; k++)
                   if (board[numberofhouses * t + k] > 0) g = false;
               gameOver = g;
               if (gameOver)
                  // move the beans appropriately 
                  for (int k = 0; k < numberofhouses; k++)
                      stores[1 - t] += board[numberofhouses * (1 - t) + k];
           }
    }
    
    /**
     * Performs move h for player turn. 
     */
    public void move(int h) throws IllegalArgumentException
    {
        if (gameOver) return;
        if (h < 1 || h > numberofhouses) 
           throw new IllegalArgumentException ("Illegal move: " + h  + " is out of range");
        // the index of the house being moved from
        int house = turn * numberofhouses + h - 1;
        // the number of beans picked up
        int beans = board[house];
        if (beans == 0) 
           throw new IllegalArgumentException ("Illegal move: " + h + " is empty");
        board[house] = 0;
        for (int k = 1; k <= beans; k++)
            board[(house + k) % boardsize] += 1; 
        // check for a capture
        int finalhouse = (house + beans) % boardsize;
        while (board[finalhouse] == 2 || board[finalhouse] == 4 || board[finalhouse] == 6)
        {
            stores[turn] += board[finalhouse];
            board[finalhouse] = 0;
            finalhouse = (finalhouse - 1 + boardsize) % boardsize;
        }
        turn = 1 - turn;
        gameOver();
    }
    
    /**
     * Displays the current board after move m.
     */
    public void display(int m)
    {
        if (m > 0) System.out.println("turn = " + (1 - turn) + ", move = " + m);
        String s;
        if (turn == 0) s = "             ==>";
        else           s = "  <==           ";
        for (int h = boardsize - 1; h >= numberofhouses; h--)
            System.out.print(" " + pad(board[h]));
        System.out.println();
        System.out.println("    " + pad(stores[1]) + s + pad(stores[0]));
        for (int h = 0; h < numberofhouses; h++)
            System.out.print(" " + pad(board[h]));
        System.out.println();
        System.out.println("----------------------------------");
    }
    
    /**
     * Returns n >= 0 set in four spaces.
     */
    private String pad(int n)
    {
        if (n < 10) return "   " + n;
        else        return  "  " + n;
    }
    
    /**
     * Displays the current board state on sc.
     */
    public void drawBoard(SimpleCanvas sc, int moved)
    {
        int house    = Lab7.HOUSERADIUS;
        int store    = Lab7.STORESIZE;
        int fontsize = Lab7.FONTSIZE;
        int[][] hcs  = Lab7.HOUSECENTRES;
        int[][] stl  = Lab7.STORETOPLEFTS;
        Color back   = Lab7.BACKCOLOR;
        Color line   = Lab7.LINECOLOR;
        Color col;
        // draw each house and its number of beans 
        for (int h = 0; h < boardsize; h++)
        {
            sc.drawDisc(hcs[h][0], hcs[h][1], fontsize * 3 / 4, back);
            if (h == moved)    col = Color.BLUE;
            else
            if (board[h] == 0) col = Color.RED;
            else               col = line;
            sc.drawCircle(hcs[h][0], hcs[h][1], house, col);
            sc.drawString(board[h], hcs[h][0] - fontsize / 4 - fontsize / 3 * ((board[h] + "").length() - 1), 
                                    hcs[h][1] + fontsize / 3, line);
        }
        // clear the middle area for writing
        sc.drawRectangle(stl[0][0] + store, stl[0][1], stl[1][0], stl[1][1] + store, back); 
        // draw the diagonal divider
        col = Color.MAGENTA;
        for (int i = 0; i < 4; i++)
        {
            sc.drawLine(stl[0][0] + store,     stl[0][1] + store - i, stl[1][0] - i, stl[1][1],     col);
            sc.drawLine(stl[0][0] + store + i, stl[0][1] + store,     stl[1][0],     stl[1][1] + i, col);
        }
        int x = (hcs[0][0] + hcs[1][0]) / 2;
        int y = (hcs[0][1] + hcs[numberofhouses][1]) / 2;
        // draw the no. of beans in store 1 
        sc.drawDisc(x, y, fontsize * 3 / 4, back);
        sc.drawString(stores[1], x - fontsize / 4 - fontsize / 3 * ((stores[1] + "").length() - 1), y + fontsize / 3, line);
        // draw the move-marker
        if (turn == 1) sc.drawString("<==", x + store / 2, y + fontsize / 3, line); 
        x = (hcs[numberofhouses - 2][0] + hcs[numberofhouses - 1][0]) / 2;
        // draw the no. of beans in store 0 
        sc.drawDisc(x, y, fontsize * 3 / 4, back);
        sc.drawString(stores[0], x - fontsize / 4 - fontsize / 3 * ((stores[0] + "").length() - 1), y + fontsize / 3, line);
        // draw the move-marker
        if (turn == 0) sc.drawString("==>", x - store * 77 / 64, y + fontsize / 3, line);
    }
}
