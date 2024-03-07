package xxl.BinFunctions;

/**
 * Represents the binaryfunction MUL
 */
public class MulFunction extends BinaryFunctions {
    public MulFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public int calculate(String arg1, String arg2) {
        // Implement multiplication logic here
        return Integer.valueOf(arg1) * Integer.valueOf(arg2);
    }
}
