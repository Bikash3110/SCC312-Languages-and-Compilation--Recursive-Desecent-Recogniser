
/**
 *
 * compilation exception analyser for 254 exercise.
 * 
 * This class has been provided to students
 *
 * @Author: Roger Garside, John Vidler
 *
 *
 **/

public class CompilationException extends Exception
{
	private static final int MAX_TRACE_DEPTH = 20;

	private final int lineNumber;

	public CompilationException( String message, int lineNumber ) {
		super( message );
		this.lineNumber = lineNumber;
	}

	public CompilationException( String message, int lineNumber, CompilationException cause ) {
		super( message, cause );
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public String toTraceString() {
		StringBuffer buffer = new StringBuffer();
		Throwable err = this;
		int maxDepth = MAX_TRACE_DEPTH;
		while( err != null && maxDepth-- > 0 ) {

			String computedLine = "???";
			if( err instanceof CompilationException ) {
				computedLine = Integer.toString(((CompilationException)err).getLineNumber());
			}

			buffer.append( "\tCaused by " ).append( err.getMessage() ).append( " on line " ).append( computedLine ).append( "\r\n" );
			err = err.getCause();

			if( err != null )
				System.out.println( buffer.toString() );
		}

		if( maxDepth < 1 )
			buffer.append( "\t ... etc.\r\n" );

		return buffer.toString();
	}
} // end of class CompilationException
