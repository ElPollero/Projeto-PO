package xxl.Cells;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xxl.Content.Conteudo;
import xxl.Content.Function;

/**
 * Represents a cell in a spreadsheet.
 *
 * This class is used to encapsulate the content of a single cell within a spreadsheet. Cells
 * can contain various types of data or formulas, and this class provides methods for managing
 * and retrieving the content of the cell.
 */
public class Cell implements Serializable{
    
    private Conteudo _content;
    private List<Function> argumentFunctions = new ArrayList<>();

    public Cell( Conteudo content){
        _content = content;
    }
    
    public void setContent(Conteudo content) {
        _content = content;
        for (Function function : argumentFunctions) {
            function.update(this);
        }
    }

    public Conteudo getContent() {
        return _content;
    }

    // Register this cell as an argument of a function
    public void registerAsArgumentOf(Function function) {
        argumentFunctions.add(function);
    }
}
