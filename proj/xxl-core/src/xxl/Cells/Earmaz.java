package xxl.Cells;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a grid of cells in a spreadsheet-like structure.
 */
public class Earmaz implements Serializable{
    
    private Map<String, Cell> _cells = new TreeMap<>();

    private int _numRows = 0;
    private int _numColumns = 0;
    private int _counter; 

    public Earmaz(int numRows, int numColumns) {
        _numRows = numRows;
        _numColumns = numColumns;
        _counter = 1;

        // Create Cell instances for each position in the grid
        for (int row = 1; row <= _numRows; row++) {
            for (int column = 1; column <= _numColumns; column++) {
                String position = String.valueOf(row) + ';' + String.valueOf(column);
                Cell cell = new Cell(null); // You can pass null as content or any initial value.
                _cells.put(position, cell);
            }
        }
    }

    public Earmaz(){}

    public Cell getCell(String position) {
        return _cells.get(position);
    }

    public boolean isVertical(){
        return  _numColumns ==1 && _numRows != 1;
    }

    public boolean isHorizontal(){
        return  _numRows == 1 && _numColumns != 1;
    }

    public boolean is1cell(){
        return  _numColumns ==1 && _numRows == 1;
    }
    
    public boolean isEmpty(){
        return _numColumns== 0 && _numRows == 0;
    }

    public int getCutBufferSize() {
        int count = 0;
        for (Cell cell : _cells.values()) {
            if (cell.getContent() != null) 
                count ++;
        }
        return count;
    }
    

    public int getCounter(){
        return _counter;
    }

    public void incrementCounter(){
        _counter++;
    }

    public void resetCounter(){
        _counter = 1;
    }
}
