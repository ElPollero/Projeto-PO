package xxl.Cells;

/**
 * Represents an interval of cells in a spreadsheet.
 */
public class CellInterval {
    private Cell startCell;
    private Cell endCell;

    public CellInterval(Cell startCell, Cell endCell) {
        this.startCell = startCell;
        this.endCell = endCell;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }

}
