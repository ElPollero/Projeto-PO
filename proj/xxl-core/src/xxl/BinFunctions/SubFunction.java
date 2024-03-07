package xxl.BinFunctions;

/**
 * Represents the binaryfunction SUB
 */
public class SubFunction extends BinaryFunctions {
    public SubFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public int calculate(String arg1, String arg2) {
        // Implement subtraction logic here
        return Integer.valueOf(arg1) - Integer.valueOf(arg2);
    }
}
