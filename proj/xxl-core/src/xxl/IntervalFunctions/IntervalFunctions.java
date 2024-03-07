package xxl.IntervalFunctions;

/**
 * An abstract class representing interval-based functions in a spreadsheet.
 * Interval functions operate on a range of cells defined by a cell interval.
 * Subclasses must implement the 'calculate' method to define the specific behavior of the function.
 */
import xxl.Content.Function;
public abstract class IntervalFunctions extends Function {
    
    public IntervalFunctions(String functionBody) {
        super(functionBody);
    }

    public abstract String calculate(String[] args);
}
