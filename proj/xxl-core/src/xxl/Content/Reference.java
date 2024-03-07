package xxl.Content;

/**
 * Represents a reference content within a cell of a spreadsheet-like structure.
 *
 * This class is used to encapsulate references to other cells within a spreadsheet grid.
 */
public class Reference extends Conteudo{
    private int referencedRow;
    private int referencedColumn;
    private String _reference;

    public Reference(String reference) {
        _reference = reference;
        // Check if the reference starts with '='
        if (reference.startsWith("=")) {
            // Remove the '=' character
            reference = reference.substring(1);

            // Split the reference into parts using ';'
            String[] parts = reference.split(";");
            
            // Ensure that we have exactly two parts
            if (parts.length == 2) {
                // Parse the row and column from the parts
                try {
                    referencedRow = Integer.parseInt(parts[0]);
                    referencedColumn = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // Handle the case where parts are not valid integers
                    // You may want to throw an exception or handle it in another way
                }
            }
        }
    }

    public int getReferencedRow() {
        return referencedRow;
    }

    public int getReferencedColumn() {
        return referencedColumn;
    }

    @Override
    public String toString() {
        return _reference; 
    }

    @Override
    public String getType() {
        return "Reference";
    }
}

