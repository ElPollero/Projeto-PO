package xxl.BinFunctions;

/**
 * Represents the binaryfunction ADD
 */
public class AddFunction extends BinaryFunctions {
    public AddFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public int calculate(String arg1, String arg2) {
        // Implement addition logic here
        return Integer.valueOf(arg1) + Integer.valueOf(arg2);
        
    }
}
