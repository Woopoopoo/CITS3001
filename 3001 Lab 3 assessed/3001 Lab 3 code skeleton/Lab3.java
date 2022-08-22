/**
 * Exercises the TSP algorithms for a folder of problem instances.
 */
import java.io.*;

public class Lab3
{
    // the folder of test files
    private static String tsptestdata = "../TSPtestdata"; 
    // one problem instance
    private static TSP tsp;
    // the output file
    private static String outfile = "../outTSP.txt";
    // the output file handle
    private static PrintWriter out;

    private Lab3(){}
    
    /**
     * Reads in each problem in the folder, runs the algorithms, and displays the output.
     */
    public static void main(String[] args) 
    {
        try
        {
            out = new PrintWriter(outfile);
            for (File file : new File(tsptestdata).listFiles()) 
                if (file.isFile()) // uses every file in the folder
                {
                    out.println(file.getName());
                    tsp = new TSP(tsptestdata + "/" + file.getName());
                    int[] cs = tsp.tspnn();
                    display("Nearest neighbour", cs);
                    cs = tsp.tsp2opt(cs);
                    display("Improved", cs);
                    out.println();
                }
            out.close();
        }
        catch (FileNotFoundException e) 
        {
            System.out.println("Something wrong with " + outfile);
        }
    }
    
    /**
     * Displays s, cs, and its performance. 
     * cs is standardised to start from 0 and so that the next city is a smaller index.
     */
    private static void display(String s, int[] cs)
    {
        out.println(s);
        // find City 0
        int k = 0;
        while (cs[k] != 0) k++;
        // select direction based on k+1 and k-1
        if (cs[(k + 1) % cs.length] < cs[(k - 1 + cs.length) % cs.length])
            for (int c = 0; c < cs.length; c++) 
                out.print(cs[(c + k) % cs.length] + " ");
        else
            for (int c = cs.length; c > 0; c--) 
                out.print(cs[(c + k) % cs.length] + " ");
        out.println();
        out.println("cost " + String.format("%.2f", tsp.validate(cs)));
    }
}
