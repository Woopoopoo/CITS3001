/**
 * Implements the Nearest Neighbour algorithm for the TSP, and
 * an iterative improvement method that uses 2-OPT greedily.
 * Results are returned as an array of indices into the table argument, 
 * e.g. if the table has length four, a valid result would be {2,0,1,3}. 
 */
import java.util.*;

public class NearestNeighbour
{
    private NearestNeighbour(){}

    /**
     * Returns the shortest tour found by exercising the NN algorithm 
     * from each possible starting city in table.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tspnn(double[][] table)
    {
        int numCity = table.length;
        int[] sp = new int [numCity];
        double spLength = Double.MAX_VALUE;
        
        //initiate loop for each city.
        for (int start = 0; start < numCity; start++) {
        	//tracks cities visited and current city
        	int numVisited = 1;
        	int nextCity = -1;
        	
        	int[] path = new int[numCity];
        	path[0] = start;
        	double min = Double.MAX_VALUE;
        	double dist = 0;
        	
        	boolean[] visited = new boolean [numCity];
        	Arrays.fill(visited, false);
        	visited[start] = true;
        		
        	for (int i = 0; i < numCity; i++) {
        		if (table[start][i] < min && !visited[i]) {
        			min = table[start][i];
        			nextCity = i;
        		}
        	}
        	
        	path[numVisited] = nextCity;
        	visited[nextCity] = true;
        	dist += min;
        	numVisited++;
        	
        	while (numVisited < numCity) {
        		int curCity = nextCity;
        		min = Double.MAX_VALUE;
        		
        		for (int i = 0; i < numCity; i++) {
        			if (table[curCity][i] < min && !visited[i]) {
        				min = table[curCity][i];
        				nextCity = i;
        			}
        		}
        		
        		path[numVisited] = nextCity;
        		visited[nextCity] = true;
        		dist += min;
        		numVisited++;
        	}
        	
        	dist += table[nextCity][start];
        	if(dist < spLength) {
        		spLength = dist;
        		sp = path.clone();
        	}
        }
        
        return sp;
    }
    
    /**
     * Uses 2-OPT repeatedly to improve cs, choosing the shortest option in each iteration.
     * You can assume that cs is a valid tour initially.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tsp2opt(int[] cs, double[][] table)
    {
    	int numCity = cs.length;
        double cost = 0;
        
        //Calculate Cost of initial tour.
        for (int i = 1; i < numCity; i++) {
        	cost += table[cs[i-1]][cs[i]];
        }
        cost += table[cs[0]][cs[numCity - 1]];
        
        double bestCost = cost;
        int[] bestRoute = cs.clone();
        
        //iterates till no more possible swaps
        int swap = 1;
        while (swap!=0) {
        	swap = 0;
        	for (int i = 1; i < numCity - 1; i++) {
        		for (int j = i + 1; j < numCity; j++) {
        			int[] newTour = cs.clone();
        			
        			double o = 0;
        			double n = 0;
        			if (j == numCity -1) {
        				o = table[cs[i-1]][cs[i]] + table[cs[0]][cs[j]];
        				n = table[cs[i-1]][cs[j]] + table[cs[0]][cs[i]];
        			}
        			else {
        				o = table[cs[i-1]][cs[i]] + table[cs[j]][cs[j+1]];
        				n = table[cs[i-1]][cs[j]] + table[cs[j+1]][cs[i]];
        			}
        			
        			if (n < o) {
        				int dec = 0;
        				for (int x = i; x <= j; x++) {
        					newTour[x] = cs[j-dec];
        					dec++;
        				}

        				double newCost = table[newTour[0]][newTour[numCity-1]];
        				for (int y = 1; y < numCity; y++) {
        					newCost += table[newTour[y-1]][newTour[y]];
        				}

        				if (newCost < bestCost) {
        					bestRoute = newTour.clone();
        					bestCost = newCost;
        					swap++;
        				}
        			}
        		}
        	}
        	cs = bestRoute;
        	cost = bestCost;	
        }
        return cs;
    }
}
