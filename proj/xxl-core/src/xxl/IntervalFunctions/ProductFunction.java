package xxl.IntervalFunctions;
/**
 * Represents the intervalfunction PRODUCT
 */
public class ProductFunction extends IntervalFunctions {
    public ProductFunction(String functionBody) {
        super(functionBody);
    }

    @Override
    public String calculate(String[] args){
        int product = 1;  // Initialize the product to 1

        for (String arg : args) {
            if (arg.startsWith("'")) {
                return "#VALUE";  // If any element starts with "'", return "#VALUE"
            }
            int number = Integer.parseInt(arg);
            product *= number;  // Multiply the numbers together
        }

        return String.valueOf(product);
    }
}
