public class Generate extends AbstractGenerate {

    public Generate() {
        // Constructor
    }

    /**
     * This meathod report Error 
     * 
     * @param String  
     * @param Token
     *
     */

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage, token.lineNumber);
    }
}
