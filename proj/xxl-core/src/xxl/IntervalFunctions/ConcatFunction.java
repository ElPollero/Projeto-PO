package xxl.IntervalFunctions;
/**
 * Represents the intervalfunction CONCAT
 */
public class ConcatFunction extends IntervalFunctions{
    public ConcatFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public String calculate(String[] args){
        StringBuilder result = new StringBuilder();
        boolean hasString = false; // Flag to track if any string value is present

        if (args.length == 1)
            return args[0];

        for (String arg : args) {
            if (arg.startsWith("'") && !arg.substring(1).isEmpty()) {
                if (!hasString)
                    hasString = true;
                else
                    arg = arg.substring(1);
                result.append(arg);
            }
        }
        return hasString ? result.toString() : "'"; // Return concatenated strings or single quote
    }
}
