public class Variable implements Comparable<Variable> {

    public enum Type {
        UNKNOWN( 0, "BadType" ),
        NUMBER( 1, "Number" ),
        STRING( 2, "String" );

        public final int value;
        public final String name;
        private Type( int v, String n ) {
            this.value = v;
            this.name = n;
        }
    };


    public final String identifier;
    public final Variable.Type type;
    public Variable( String identifier, Variable.Type type ) {
        this.identifier = identifier;
        this.type = type;
    }

    public int compareTo( Variable v ) {
        return this.type.compareTo(v.type) * this.identifier.compareTo(v.identifier);
    }

    @Override
    public String toString() {
        return "Variable: " +this.identifier+ " <" +this.type.name+ ">";
    }
}