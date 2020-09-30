import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * SyntaxAnalyser methods which extends AbstractSyntaxAnalyser.
 * Implement a syntax analyser (SA) for the language using recursive descent recogniser
 *
 * @Author: Bikash Kumar Tiwary
 *
 **/

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    private String file;
	private ArrayList<String> list = new ArrayList<String>();

    public SyntaxAnalyser(String file) {
        this.file = file;
        try {
			// Initalise lexical analyser
            lex = new LexicalAnalyser(file);

        } catch (Exception e) {
            System.err.println("Failed to load lexical analyser.");
        }
    }

    /**
     * This meathod Generate the error string 
     * passed as paarameter inside all the catch blocks CompilationException error
     * @param String  
     * @param Token
     *
     */ 
    private String generateErrorString(String expected, Token next) {
        return "line " + next.lineNumber + " in " + this.file + ":\n\t\t\t- Expected token(s) " + expected + " but found (" + Token.getName(next.symbol) + ").\n";
    }

    /**
	 * Begin processing the first (top level) token.
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        // Begins parsing
		myGenerate.commenceNonterminal("statementPart");
		try {
			// Finds 'begin'
			acceptTerminal(Token.beginSymbol);
			// Enters statementList
            statementList(); 
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString(" statementList ", nextToken), nextToken.lineNumber ,e);
        }
        acceptTerminal(Token.endSymbol); // Parsing ends
        myGenerate.finishNonterminal("statementPart");
    }

    /**
     * Begin processing the statement list
	 * @throws IOException
     * @throws CompilationException
     */
    private void statementList() throws IOException, CompilationException {
		// Starts reading statementList
        myGenerate.commenceNonterminal("statementList");
        try {
			//Enters statement
            statement();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statementList", nextToken), nextToken.lineNumber ,e);
        }
		
		// If nextToken symbol is semicolon then accpets semicolon and enters statementList using recursion
        while (nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);
            try {
				// Enters statementList
                statementList();
            } catch (CompilationException e) {
                throw new CompilationException(generateErrorString("statementList", nextToken), nextToken.lineNumber ,e);
            }
        }
		//Finishes reading statementList
        myGenerate.finishNonterminal("statementList");
    }

    /**
	 * Begins processing statement
     * selects non-terminals
	 * @throws IOException
     * @throws CompilationException
     */
    private void statement() throws IOException, CompilationException {
		// Starts reading statement
        myGenerate.commenceNonterminal("statement");
        try {
            switch (nextToken.symbol) {
                case Token.callSymbol:                  // Procedure Statement 
                    procedure();
                    break;
                case Token.identifier: 					// Assignment Statement	
                    assignment();
                    break;
                case Token.whileSymbol:					// While Statement
                    while_st();
                    break;
                case Token.ifSymbol:					// If Statement
                    if_st();
                    break;
                case Token.untilSymbol:					// Until Statement
                    until_st();
                    break;
                case Token.forSymbol:					// For Statement	
                    for_st();
                    break;    
                default:                                // Error Handling
                    myGenerate.reportError(nextToken, "Expected <if statement>, <assignment statement>, <until statement>, <while statement> or <procedure statement>");
                    break;
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<if>, <assignment>, <until>, <while> or <procedure>", nextToken), nextToken.lineNumber, e);
        }
		//Finishes reading statement
        myGenerate.finishNonterminal("statement");
    }

	
    /**
	 * Begins processing assignment
     * 
	 * @throws IOException
     * @throws CompilationException
     */	
    private void assignment() throws IOException, CompilationException {
		// Starts reading assignment
        myGenerate.commenceNonterminal("AssignmentStatement");
		
		String str = nextToken.text;
        // accepts identifier and then :=
		acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
		
		//Checks if next symbol is StringConstant and add variable(declare) given its not been declared before
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
			if(list.contains(str)== false){
			// if variable not declared thyen declare variable here	
			 Variable v = new Variable(str, Variable.Type.STRING);   // String Variable
			 myGenerate.addVariable(v);
			 list.add(str);       // adds declared variables to ArrayList
			}
            myGenerate.finishNonterminal("AssignmentStatement");
            return;
        }else{
		
		 try {
            expression();
			if(list.contains(str)== false){
			 Variable v = new Variable(str, Variable.Type.NUMBER);    // Number Variable
			 myGenerate.addVariable(v);
			 list.add(str);
			}
         } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("expression", nextToken), nextToken.lineNumber, e);
         }
		}
		//Finishes reading assignment
        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     * Begins processing IF statement
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void if_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("if statement");

        acceptTerminal(Token.ifSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition>", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.thenSymbol);
        
        try {
            statementList();

            if (nextToken.symbol == Token.elseSymbol) {
                
                acceptTerminal(Token.elseSymbol);
                statementList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statement list", nextToken), nextToken.lineNumber, e);
        }
        
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("if statement");
    }

    /**
     * Begins processing While statement
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void while_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("WhileStatement");
       
        acceptTerminal(Token.whileSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.loopSymbol);

        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statementList", nextToken), nextToken.lineNumber, e);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * Begins processing procedure
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void procedure() throws IOException, CompilationException {
        // Starts reading procedure
        myGenerate.commenceNonterminal("ProcedureStatement");
        // callSymbol and identifier
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        
        // ( argumentList )
        acceptTerminal(Token.leftParenthesis);
        try {
            argumentList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("argument list", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.rightParenthesis);
        //Finishes reading procedure
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * Begins processing Until statement
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void until_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("until statement");

        acceptTerminal(Token.doSymbol);

        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statement list", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.untilSymbol);

        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition", nextToken), nextToken.lineNumber, e);
        }
        myGenerate.finishNonterminal("until statement");
    }

    /**
     * Begins processing for statement
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void for_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("for statement");
        
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        try{    
         assignment();  
         acceptTerminal(Token.semicolonSymbol);
         condition();
         acceptTerminal(Token.semicolonSymbol);
         assignment();
         
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statement list", nextToken), nextToken.lineNumber, e);
        }  
        acceptTerminal(Token.rightParenthesis);
  
        acceptTerminal(Token.doSymbol);
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("statement list", nextToken), nextToken.lineNumber, e);
        }
        
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        
        myGenerate.finishNonterminal("for statement");
    }

     /**
     * Begins processing argumentList
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void argumentList() throws IOException, CompilationException {
        // Starts reading factor
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        try {
            // checks comma, and the enters argumentList (recursive)
            if(nextToken.symbol == Token.commaSymbol) {
                acceptTerminal(Token.commaSymbol);
                argumentList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("ArgumentList", nextToken), nextToken.lineNumber, e);
        }
        //Finishes reading factor
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
    * Begins processing condition
    * 
    * @throws IOException
    * @throws CompilationException
    */ 
    private void condition() throws IOException, CompilationException {
        // Starts reading condition
        myGenerate.commenceNonterminal("condition");
        acceptTerminal(Token.identifier);
        try {
            // Enters conditionalOperator
            conditionalOperator();

            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.stringConstant:
                    acceptTerminal(Token.stringConstant);
                    break;
                default:
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition operator", nextToken), nextToken.lineNumber, e);
        }
        //Finishes reading condition
        myGenerate.finishNonterminal("condition");
    }

    /**
     * Begins processing conditionalOperator
     * 
     * @throws IOException
     * @throws CompilationException
     */ 
    private void conditionalOperator() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ConditionalOperator");
        switch (nextToken.symbol) {
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            default:
        }
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * Return boolean based on whether the next token is part of an expression
     *
     * @return boolean
     */
    private boolean plusOrMinus(Token token) {
        return token.symbol == Token.plusSymbol
                || token.symbol == Token.minusSymbol;
    }

    /**
     * Return boolean based on whether the next token is part of a factor
     *
     * @return boolean
     */
    private boolean mulOrDiv(Token token) {
        return token.symbol == Token.divideSymbol
                || token.symbol == Token.timesSymbol;
    }

    /**
	 * Begins processing expression
     * 
	 * @throws IOException
     * @throws CompilationException
     */	
	
    private void expression() throws IOException, CompilationException {
		// Starts reading expression
        myGenerate.commenceNonterminal("expression");

        try {
			// Enters term
            term();
			
			// checks if next token is + or -	
            if(plusOrMinus(nextToken)){
				// accepts the symbol(+ or -)
			    acceptTerminal(nextToken.symbol);
                expression();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("term", nextToken), nextToken.lineNumber, e);
        }
		//Finishes reading expression
        myGenerate.finishNonterminal("expression");
    }

    /**
	 * Begins processing term
     * 
	 * @throws IOException
     * @throws CompilationException
     */	
    private void term() throws IOException, CompilationException {
	    // Starts reading term
        myGenerate.commenceNonterminal("Term");

        try {
			// Enters factor
            factor();
			
			// checks if next token is * or /
            while (mulOrDiv(nextToken)) {
				// accepts the symbol(* or /)
                acceptTerminal(nextToken.symbol);
				// Enters term, recursive
                term();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("factor", nextToken), nextToken.lineNumber, e);
        }
		//Finishes reading term
        myGenerate.finishNonterminal("Term");
    }
	
	
    /**
	 * Begins processing factor
     * 
	 * @throws IOException
     * @throws CompilationException
     */	
    private void factor() throws IOException, CompilationException {
		// Starts reading factor
        myGenerate.commenceNonterminal("Factor");
        try {
			// Can switch to identifier or numberConstant or (expression)
            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
					acceptTerminal(Token.numberConstant);	
					break;
                case Token.leftParenthesis:
                    acceptTerminal(Token.leftParenthesis);
                    expression();
                    acceptTerminal(Token.rightParenthesis);
                default:
                    myGenerate.reportError(nextToken, "Error on factor, expected IDENTIFIER, NUMBER or (expression), but found " + Token.getName(nextToken.symbol));
                    break;
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("identifier, number constant or ( <expression> )", nextToken), nextToken.lineNumber, e);
        }
		//Finishes reading factor
        myGenerate.finishNonterminal("Factor");
    }

    /**
     * @param symbol
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

        Token actual = nextToken;
        if (symbol == actual.symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            return;
        }
        myGenerate.reportError(nextToken, generateErrorString("<" + Token.getName(symbol) + ">", nextToken));
    }
	
}
