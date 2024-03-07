package xxl.Content;

/**
 * Represents a LiteralString content within a cell of a spreadsheet-like structure.
 */
public class LiteralString extends Conteudo {
    private String value; // The string value of the literal

    public LiteralString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return value;
    }

    @Override
    public String getType() {
        return "LiteralString";
    }
}

