package xxl.exceptions;

import java.io.Serial;

public class NonExistentFunctionException extends Exception{
    @Serial
    private static final long serialVersionUID = 202310051200L;

    /** The name of the non-existent function. */
    private String functionName;

    /**
     * Creates a new NonExistentFunctionException.
     * 
     * @param functionName The name of the non-existent function.
     */
    public NonExistentFunctionException(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Gets the name of the non-existent function.
     * 
     * @return The name of the non-existent function.
     */
    public String getFunctionName() {
        return functionName;
    }
}
