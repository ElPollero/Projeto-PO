package xxl.Content;

/**
 * Represents a LiteralInt content within a cell of a spreadsheet-like structure.
 */
public class LiteralInt extends Conteudo {
    private int value; // The integer value of the literal

    public LiteralInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value); 
    }

    @Override
    public String getType() {
        return "LiteralInt";
    }
}
