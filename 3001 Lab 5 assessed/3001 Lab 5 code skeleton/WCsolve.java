/**
 * Applies uninformed search algorithms to Word Chess puzzles. 
 * For a problem like SICK -> WELL, you need to return a solution 
 * in the form <"SICK", "SILK", "SILL", "WILL", "WELL">.
 */
import java.util.*;

public class WCsolve
{
    public WCsolve(){}

    /**
     * Solves the puzzle start -> target using breadth-first search. 
     * Returns one optimal solution. 
     */
    public static ArrayList<String> solve(String start, String target)
    {
        HashMap<String, String> parent = new LinkedHashMap<String, String>();
        ArrayList<String> visited = new ArrayList<>();
        
        ArrayDeque<String> queue = new ArrayDeque<String>();
        
        queue.add(start);
        visited.add(start);
        boolean found = false;
       
        while(!found && !queue.isEmpty()) {
        	String cWord = queue.pop();
        	for (int i = 0; i < cWord.length(); i++) {
        		char[] temp = cWord.toCharArray();
        		for (int j = 0; j < 26; j++) {
        			temp[i] = (char)('A' + j);
        			String nWord = new String(temp);
        			
        			if(WordChess.isWord(nWord) && !visited.contains(nWord) && isDiff(nWord, cWord)) {
        				parent.put(nWord, cWord);
        				queue.add(nWord);
        				visited.add(nWord);
        				if(nWord.equals(target)) {
        					found = true;
        					break;
        				}
        			}
        		}
        	}
        }
        
        ArrayList<String> solved = new ArrayList<>();
        String ans = target;
        if(!found) {
        	ans = "no solution";
        	solved.add(ans);
        }
        else {
        	solved.add(ans);
        	while (ans != start) {
        		ans = parent.get(ans);
        		solved.add(ans);
        	}
        }
        Collections.reverse(solved);
        return solved;
    }
    
    private static boolean isDiff(String a, String b) {
    	int numDiff = 0;
    	char[] t1 = a.toCharArray();
    	char[] t2 = b.toCharArray();
    	
    	for (int i = 0; i < a.length(); i++) {
    		if (t1[i] != t2[i]) {
    			numDiff++;
    		}
    	}
    	return numDiff != 0;
    }
}
