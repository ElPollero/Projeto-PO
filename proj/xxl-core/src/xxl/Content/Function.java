package xxl.Content;

import java.util.List;

import xxl.Cells.Cell;
import xxl.Cells.FunctionCache;

/**
 * Represents a function content within a cell of a spreadsheet-like structure.
 *
 * It provides methods to retrieve the function body, determine if the function is a binary function,
 * and obtain the function type (e.g., "ADD," "SUB," "MUL," or "DIV"). .
 */
public class Function extends Conteudo {
    private String _functionBody; // The body of the function
    private List<Cell> _argumentCells; // List of cells that are arguments of this function
    private boolean isDirty = false;
    private FunctionCache _functionCache; // Reference to the FunctionCache

    public Function(String functionBody) {
        _functionBody = functionBody;
    }

    public Function(String functionBody, List<Cell> argumentCells, FunctionCache functionCache) {
        _functionBody = functionBody;
        _argumentCells = argumentCells;
        _functionCache = functionCache;

        // Register this function as the observer for its argument cells
        for (Cell cell : argumentCells) {
            cell.registerAsArgumentOf(this);
        }
    }
    
    public String getFunctionBody() {
        return _functionBody;
    }

    public boolean isBinaryFunction() {
        // Logic to determine if the function is a binary function
        return _functionBody.contains("ADD") || _functionBody.contains("SUB") || _functionBody.contains("MUL") || _functionBody.contains("DIV");
    }

    public boolean isIntervalFunction() {
        // Logic to determine if the function is an interval function
        return _functionBody.contains("AVERAGE") || _functionBody.contains("PRODUCT") || _functionBody.contains("CONCAT") || _functionBody.contains("COALESCE");
    }

    public String getFunctionType(String functionBody) {
        if (functionBody.contains("ADD")) {
            return "ADD";
        } else if (functionBody.contains("SUB")) {
            return "SUB";
        } else if (functionBody.contains("MUL")) {
            return "MUL";
        } else if (functionBody.contains("DIV")) {
            return "DIV";
        } else if (functionBody.contains("AVERAGE")) {
            return "AVERAGE";
        } else if (functionBody.contains("PRODUCT")) {
            return "PRODUCT";
        } else if (functionBody.contains("CONCAT")) {
            return "CONCAT";
        } else if (functionBody.contains("COALESCE"))
            return "COALESCE";  
        return ""; 
    }

    public void update(Cell changedCell) {
        if (_functionCache.contains(this) && _argumentCells.contains(changedCell) ) {
            // The function is in the cache and is not already marked as dirty,
            // so mark it as dirty and trigger recalculation as needed.
            setDirty(true);
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        // Update the dirty flag and handle recalculation logic
        isDirty = dirty;
    }

    @Override
    public String toString(){
        return _functionBody;
    }

    @Override
    public String getType() {
        return "Function";
    }
}

