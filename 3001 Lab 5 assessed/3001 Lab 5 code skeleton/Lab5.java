/**
 * Plays Word Chess on a file of puzzles. 
 * Both of the input files are in the Java folder; 
 * if you move them, just change the variables below. 
 */
import java.util.*;

public class Lab5
{
    // the file of puzzles
    private static final String puzzleFile = "Puzzles.txt";
    // the (cut-down) file of words from Words With Friends
    private static final String wordsFile = "WWF uppercase.txt";
    
    // the list of legal words
    public static final ArrayList<String> wordsList = new FileIO(wordsFile).getLines();
    
    private Lab5(){}

    /**
     * Exercises the solver(s) on the file of puzzles.
     */
    public static void main(String[] args)
    {
        for (String l : new FileIO(puzzleFile).getLines())
            // ignore blank lines and lines that start with //
            if (!l.isBlank() && l.length() >= 2 && !l.substring(0,2).equals("//"))
            {
               String[] ws = l.split(" ");
               assert ws.length >= 2 : "illegal line in " + puzzleFile;
               WordChess wc = new WordChess(ws[0], ws[1]);
               for (String s : wc.solve()) System.out.print(s + " ");
               System.out.println();
            }
        System.out.println();
    }
}
