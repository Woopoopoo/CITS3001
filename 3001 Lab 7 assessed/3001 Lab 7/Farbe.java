/**
 * Enumerates the colours in Bohnenspiel.
 */
public enum Farbe
{
    WEISSE, SCHWARZ;
    
    /**
     * Returns the first player to move.
     */
    public static Farbe first()
    {
        return WEISSE;
    }
    
    /**
     * Returns the colour opposite to f. 
     */
    public static Farbe flip(Farbe f)
    {
        if (f == WEISSE) return SCHWARZ;
        else             return WEISSE;
    }
}
