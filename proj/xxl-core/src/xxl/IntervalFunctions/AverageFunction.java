package xxl.IntervalFunctions;
/**
 * Represents the intervalfunction AVERAGE
 */
public class AverageFunction extends IntervalFunctions{
    public AverageFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public String calculate(String[] args){
        int sum = 0;  // Initialize the product to 1

        for (String arg : args) {
            if (arg.startsWith("'")) {
                return "#VALUE";  // If any element starts with "'", return "#VALUE"
            }
            int number = Integer.parseInt(arg);
            sum += number;  // Multiply the numbers together
        }
        int average = sum / args.length;

        return String.valueOf(average);
    }
}
