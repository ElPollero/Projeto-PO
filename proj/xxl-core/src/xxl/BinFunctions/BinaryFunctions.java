package xxl.BinFunctions;

import xxl.Content.Function;

/**
 * Abstract class representing binary functions that operate on two arguments.
 * Subclasses of this class should implement the `calculate` method to perform
 * specific binary operations.
 *
 * Binary functions take two arguments and return a result based on the operation
 * defined by the specific function.
 */
public abstract class BinaryFunctions extends Function {

    public BinaryFunctions(String functionBody) {
        super(functionBody);
    }

    /**
     * Calculates the result of the binary function using the provided arguments.
     *
     * @param arg1 The first argument as a string.
     * @param arg2 The second argument as a string.
     * @return The result of the binary operation.
     */
    public abstract int calculate(String arg1, String arg2);
    
}
