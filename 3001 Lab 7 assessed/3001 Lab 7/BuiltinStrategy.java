/**
 * Enumerates the strategies of the built-in Bohnenspiel players.
 */
public enum BuiltinStrategy
{
    RANDOM,    // non-deterministic     50-90
    LEFTMOST,  // deterministic         80-100
    RIGHTMOST, // deterministic        150-170
    HIGHEST,   // nearly deterministic  40-70
    LOWEST     // nearly deterministic 110-130
}
