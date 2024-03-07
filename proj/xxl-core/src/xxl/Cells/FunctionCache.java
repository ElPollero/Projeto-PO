package xxl.Cells;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import xxl.Content.Function;
/**
 * A class representing a cache for storing the results of functions associated with a spreadsheet.
 * This cache allows efficient retrieval of function results without reevaluating the same function.
 * Functions are used as keys, and their corresponding results as values.
 */
public class FunctionCache implements Serializable {
    private Map<Function, String> cache = new HashMap<>();

    // Add a function and its result to the cache
    public void put(Function function, String result) {
        cache.put(function, result);
    }

    // Retrieve the result for a given function
    public String get(Function function) {
        return cache.get(function);
    }

    // Check if a function is in the cache
    public boolean contains(Function function) {
        return cache.containsKey(function);
    }
}

