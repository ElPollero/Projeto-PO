package xxl.IntervalFunctions;
/**
 * Represents the intervalfunction COALESCE
 */
public class CoalesceFunction extends IntervalFunctions {
    public CoalesceFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public String calculate(String[] args){

        for (String arg : args) {
            if (arg.startsWith("'") && !arg.substring(1).isEmpty()) 
                return arg; // Return the first string found
        }

        return "'"; // Return an empty string if no string value is found
    }
}
