/**
 * Implements various TSP algorithms for the given problem instance. 
 */


public class TSP
{
    // file containing this problem instance
    private String filename;
    // number of cities in this instance
    private int n;
    // coordinates of the cities
    private double[][] cities;
    // table of distances between cities
    private double[][] table;

    /**
     * Reads in the city locations from filename, one per line.
     * Sets up n and cities, and calculates all inter-city distances for table.
     */
    public TSP(String filename)
    {
        this.filename = filename;
        FileIO f = new FileIO(this.filename);
        n = f.getLines().size();
        cities = new double[n][2];
        for (int k = 0; k < n; k++)
        {
            String[] xs = f.getLines().get(k).split(" ");
            cities[k][0] = Double.parseDouble(xs[0]);
            cities[k][1] = Double.parseDouble(xs[1]);
        }
        table = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                table[i][j] = cost(cities[i][0], cities[i][1], cities[j][0], cities[j][1]);
    }
    
    /**
     * Returns the shortest tour found using the NN algorithm, or an error indication.
     */
    public int[] tspnn()
    {
        int[] cs = NearestNeighbour.tspnn(table);
        double s = validate(cs);
        if (s <= 0) return new int[] {(int) s};
        else        return cs;
    } 
    
    /**
     * Returns the tour derived by applying 2-OPT to cs repeatedly in greedy fashion, 
     * or an error indication. 
     */
    public int[] tsp2opt(int[] cs)
    {
        cs = NearestNeighbour.tsp2opt(cs, table);
        double s = validate(cs);
        if (s <= 0) return new int[] {(int) s};
        else        return cs;
    }
    
    /**
     * Checks if cs is a valid tour and returns either its cost or an error indication.
     */
    public double validate(int[] cs)
    {
        if (cs.length != n) return 0;
        for (int c = 0; c < n; c++) 
            if (!ison(c, cs)) return -c;
        return roundtrip(cs);
    }
    
    /**
     * Returns true iff c is on cs.
     */
    private boolean ison(int c, int[] cs)
    {
        for (int k = 0; k < n; k++)
            if (cs[k] == c) return true;
        return false;
    }
    
    /**
     * Returns the total cost for the circular tour cs.
     */
    private double roundtrip(int[] cs)
    {
        double z = table[cs[0]][cs[n-1]];
        for (int k = 0; k < n-1; k++) z += table[cs[k]][cs[k+1]];
        return z;
    }

    /**
     * Returns the cost of travel between x1,y1 and x2,y2.
     */
    private static double cost(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
