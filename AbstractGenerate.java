import java.util.ArrayList;
import java.util.List;

/**
 *
 * Abstract Generate methods for 312 exercise.  This class provides an interface for arbitrary code generators to accept syntax from the rest of the system.
 * 
 * This class has been provided to students
 *
 * @Author: Roger Garside, John Mariani, John Vidler
 *
 *
 **/

public abstract class AbstractGenerate
{

    /**
    *
    * insertTerminal
    *
    **/

    public void insertTerminal( Token token ) {
        String tt = Token.getName( token.symbol );
        
        if( (token.symbol == Token.identifier) || (token.symbol == Token.numberConstant) || (token.symbol == Token.stringConstant) )
            tt += " '" + token.text + "'";

        tt += " on line " + token.lineNumber;

        System.out.println( "rggTOKEN " + tt );
    } // end of method insertTerminal

    /**
     * Should return a single variable object, if the variable is known to the compiler, otherwise null.
     * 
     * @param identifier The identifier to match
     * @return A variable object matching the supplied identifier, or null if non exists.
     */
    public Variable getVariable( String identifier ) {
        return null;
    }

    /**
     * Add a variable to the current symbol list.
     * 
     * @param v The variable to add
     */
    public void addVariable( Variable v ) {
        System.out.println( "rggDECL " + v );
    }

    /**
     * Remove a variable from the current symbol list.
     * 
     * @param v The variable to remove
     */
    public void removeVariable( Variable v ) {
        System.out.println( "rggDROP " + v );
    }

    /**
    *
    * commenceNonterminal
    *
    **/
    public void commenceNonterminal( String name ) {
        System.out.println( "rggBEGIN " + name );
    } // end of method commenceNonterminal

    /**
    *
    * finishNonterminal
    *
    **/
    public void finishNonterminal( String name ) {
        System.out.println( "rggEND " + name );
    } // end of method finishNonterminal

    /**
    *
    * reportSuccess
    *
    **/
    public void reportSuccess()
    {
        System.out.println( "rggSUCCESS" );
    } // end of method reportSuccess


    /** Report an error to the user. */
    public abstract void reportError( Token token, String explanatoryMessage ) throws CompilationException;

} // end of class "AbstractGenerate"
