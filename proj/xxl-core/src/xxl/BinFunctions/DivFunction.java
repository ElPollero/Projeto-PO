package xxl.BinFunctions;

/**
 * Represents the binaryfunction DIV
 */
public class DivFunction extends BinaryFunctions {
    public DivFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public int calculate(String arg1, String arg2) {
        // Implement division logic here
        return Integer.valueOf(arg1) / Integer.valueOf(arg2);
    }
}
