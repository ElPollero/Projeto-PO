package xxl;

import xxl.BinFunctions.AddFunction;
import xxl.BinFunctions.BinaryFunctions;
import xxl.BinFunctions.DivFunction;
import xxl.BinFunctions.MulFunction;
import xxl.BinFunctions.SubFunction;
import xxl.Cells.Cell;
import xxl.Cells.CellEditActions;
import xxl.Cells.CellInterval;
import xxl.Cells.CellSearchActions;
import xxl.Cells.Earmaz;
import xxl.Cells.FunctionCache;
import xxl.Content.Conteudo;
import xxl.Content.Function;
import xxl.Content.LiteralInt;
import xxl.Content.LiteralString;
import xxl.Content.Reference;
import xxl.IntervalFunctions.AverageFunction;
import xxl.IntervalFunctions.CoalesceFunction;
import xxl.IntervalFunctions.ConcatFunction;
import xxl.IntervalFunctions.IntervalFunctions;
import xxl.IntervalFunctions.ProductFunction;
import xxl.exceptions.NonExistentFunctionException;
import xxl.exceptions.UnrecognizedEntryException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Class representing a spreadsheet.
 */
public class Spreadsheet implements Serializable {

    @Serial
    private static final long serialVersionUID = 202308312359L;

    private Earmaz e_armazenamento;
    private Earmaz _cutbuffer;

    private FunctionCache _functionCache;

    private int _row;
    private int _column;

    private boolean _check = false;

    private User root;
    private ArrayList <User> _users = new ArrayList<>();

    public Spreadsheet(int row, int column){
        _row = row;
        _column = column;
        e_armazenamento = new Earmaz(_row, _column);
        _cutbuffer = new Earmaz();
        _functionCache = new FunctionCache();
    }

    /**
    * Imports data from a file and inserts it into the spreadsheet.
    * Imports only the data related to entries of each cell.
    *
    * @param filename The name of the file to import data from.
    * @throws UnrecognizedEntryException If an entry in the file is not recognized.
    */
    void importFile(String filename) throws UnrecognizedEntryException, IOException, NonExistentFunctionException{
        String currentLine;
		String[] splitLine;
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		while ((currentLine = reader.readLine()) != null) {
			splitLine = currentLine.split("\\|");
			
			if (splitLine.length == 2){
                String coordinates = splitLine[0];
                String content = splitLine[1];

                insertContents(coordinates, content);
            }
		}
        reader.close();
	}

    /**
    * Searches and performs specified actions on a range of cells or a single cell.
    *
    * This method searches for cells based on the provided input,  
    * and calls the method that chooses the specified action 
    * that needs to be applied to them, such as copying, cutting, pasting, or showing.
    *
    * @param gama The input representing a cell address or cell interval (e.g., "A1", "A1:B2").
    * @param action The action to be performed (e.g., CellEditActions.ACTION_COPY, CellEditActions.ACTION_CUT).
    * @param content The content to apply in the action (used in some actions).
    * @return A string representing the result of the action or an empty string in some cases.
    * @throws UnrecognizedEntryException If the provided input is not recognized.
    * @throws NonExistentFunctionException If a function does not exist in the provided input.
    */
    public String searchAndPerformCellAction(String gama, int action, String content) throws UnrecognizedEntryException , NonExistentFunctionException{
        if (gama.contains(":")){
            //its a cell interval
            StringBuilder outputBuilder = new StringBuilder();
            String[] intervalParts = gama.split(":");
            int[] intervalCoords = parseCellInterval(intervalParts);
            CellInterval cellInterval = new CellInterval(e_armazenamento.getCell(intervalParts[0]), e_armazenamento.getCell(intervalParts[1]));
            int gamaDimension = 0;
            if (cellInterval.getStartCell() != null && cellInterval.getEndCell() != null) {
                if (intervalCoords[0] == intervalCoords[2]) {
                    int row = intervalCoords[0];
                    int startColumn = Math.min(intervalCoords[1], intervalCoords[3]);
                    int endColumn = Math.max(intervalCoords[1], intervalCoords[3]);
                    gamaDimension = endColumn - startColumn + 1;
                    //create an horizontal cutbuffer
                    if (action == CellEditActions.ACTION_COPY || action == CellEditActions.ACTION_CUT)
                        _cutbuffer = new Earmaz(1, _column);

                    if (action == CellEditActions.ACTION_PASTE ){
                        if (_cutbuffer.isVertical() || (gamaDimension != _cutbuffer.getCutBufferSize()) || _cutbuffer.isEmpty())
                            return ""; // cutbuffer size != userInterval size
                    }
                    // Iterate through the cells within the interval
                    for (int currentColumn = startColumn; currentColumn <= endColumn; currentColumn++) {
                        String currentInput = row + ";" + currentColumn;
                        Cell currentCell = e_armazenamento.getCell(currentInput);
                        if (currentCell != null)
                            if (action == CellEditActions.ACTION_SHOW){
                                outputBuilder.append(applyActiontoCells(currentCell, action, currentInput, content)); 
                                if (currentColumn < endColumn) 
                                    outputBuilder.append("\n");
                            }else
                                applyActiontoCells(currentCell, action, currentInput, content);
                        else
                            throw new UnrecognizedEntryException(gama);    
                    }
                }
                else if (intervalCoords[1] == intervalCoords[3]) {
                    int column = intervalCoords[1];
                    int startRow = Math.min(intervalCoords[0], intervalCoords[2]);
                    int endRow = Math.max(intervalCoords[0], intervalCoords[2]);
                    gamaDimension = endRow - startRow + 1;
                    //create new vertical cutbuffer
                    if (action == CellEditActions.ACTION_COPY || action == CellEditActions.ACTION_CUT)
                        _cutbuffer = new Earmaz(_row, 1);
                    
                    if (action == CellEditActions.ACTION_PASTE ){
                        if (_cutbuffer.isHorizontal() || (gamaDimension != _cutbuffer.getCutBufferSize())){
                            return ""; // cutbuffer size != userInterval size
                        }
                    }
                    // Iterate through the cells within the interval
                    for (int currentRow = startRow; currentRow <= endRow; currentRow++) {
                        String currentInput = currentRow + ";" + column;
                        Cell currentCell = e_armazenamento.getCell(currentInput);
                        
                        if (currentCell != null)
                            if (action == CellEditActions.ACTION_SHOW){
                                outputBuilder.append(applyActiontoCells(currentCell, action, currentInput, content)); 
                                if (currentRow < endRow) 
                                    outputBuilder.append("\n");
                            }else
                                applyActiontoCells(currentCell, action, currentInput, content);
                        else
                            throw new UnrecognizedEntryException(gama);       
                    }
                }
                else
                    throw new UnrecognizedEntryException(gama);
            }
            else{
                throw new UnrecognizedEntryException(gama);
            }
        outputBuilder.append("\n" + "n de celulas:" + gamaDimension);
        return outputBuilder.toString();
        }
        else{
            //its an unique cell
            Cell cell = e_armazenamento.getCell(gama);
            if (action == CellEditActions.ACTION_COPY || action == CellEditActions.ACTION_CUT)
                _cutbuffer = new Earmaz(1, 1);
            if (cell != null)
                return applyActiontoCells(cell, action, gama, content);
            else
                throw new UnrecognizedEntryException(gama);
        }
    }

    /**
    * Applies a specified action to a cell and performs the necessary operations.
    *
    * This method applies a specified action, such as showing cell content, inserting content,
    * copying, deleting, cutting, pasting, or pasting from the cut buffer, to a given cell.
    *
    * @param cell The cell to which the action is applied.
    * @param action The action to be performed (e.g., CellEditActions.ACTION_SHOW).
    * @param cellAdress The cell address for the action (e.g., "A1").
    * @param content The content to be inserted in the cell (used in some actions).
    * @return An empty string or specific result based on the action.
    * @throws UnrecognizedEntryException If the cell address is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public String applyActiontoCells(Cell cell, int action, String cellAdress, String content) throws UnrecognizedEntryException, NonExistentFunctionException {
        switch (action) {
            case CellEditActions.ACTION_SHOW:
                return uniqueCellContent(cell, cellAdress);
            case CellEditActions.ACTION_INSERT:
                insertContents(cellAdress, content);
                break;
            case CellEditActions.ACTION_COPY:
                copyContents(cell);
                break;
            case CellEditActions.ACTION_DELETE:
                cell.setContent(null);
                setChanged(true);
                break;
            case CellEditActions.ACTION_CUT:
                cutContents(cell);
                break;
            case CellEditActions.ACTION_PASTE:
                pasteContents(cell);
                break;
            case CellEditActions.ACTION_PASTE_ALL_CUTBUFFER:
                if (!_cutbuffer.isEmpty())
                    pasteAllCutBuffer(cellAdress);
                break;
        }
        return "";
    }

    /**
    * Retrieves and formats the content of cells based on the provided input.
    *
    * @param input The input specifying a single cell or a cell interval (e.g., "1;1", "1;1:1;4").
    * @return A formatted string containing the content of the specified cell(s).
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public String showCommand(String gama) throws UnrecognizedEntryException, NonExistentFunctionException{
       int action = CellEditActions.ACTION_SHOW;
       return searchAndPerformCellAction(gama, action, null);
    }

    /**
    * This method inserts the provided content into the specified cell or cell interval.
    *
    * @param gama The cell address or cell interval to insert content into (e.g., "A1" or "A1:B5").
    * @param content The content to insert into the cell(s).
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public void insertCommand(String gama, String content) throws UnrecognizedEntryException, NonExistentFunctionException {
        int action = CellEditActions.ACTION_INSERT;
        searchAndPerformCellAction(gama, action, content);
    }

    /**
    * This method copies the content of the specified cell or cell interval to the cut buffer.
    *
    * @param gama The cell address or cell interval to copy content from (e.g., "A1" or "A1:B5").
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public void copyCommand(String gama) throws UnrecognizedEntryException, NonExistentFunctionException  {
        int action = CellEditActions.ACTION_COPY;
        searchAndPerformCellAction(gama, action, null);
    }

    /**
    * This method deletes the content of the specified cell or cell interval.
    *
    * @param gama The cell address or cell interval to delete content from (e.g., "A1" or "A1:B5").
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public void deleteCommand(String gama) throws UnrecognizedEntryException, NonExistentFunctionException  {
        int action = CellEditActions.ACTION_DELETE;
        searchAndPerformCellAction(gama, action, null);
    }

    /**
    * This method cuts the content of the specified cell or cell interval to the cut buffer.
    *
    * @param gama The cell address or cell interval to cut content from (e.g., "A1" or "A1:B5").
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public void cutCommand(String gama) throws UnrecognizedEntryException, NonExistentFunctionException  {
        int action = CellEditActions.ACTION_CUT;
        searchAndPerformCellAction(gama, action, null);
    }

    /**
    * This method pastes the content from the cut buffer into the specified cell or cell interval.
    *
    * @param gama The cell address or cell interval to paste content into (e.g., "A1" or "A1:B5").
    * @throws UnrecognizedEntryException If the cell address or interval is not recognized.
    * @throws NonExistentFunctionException If a function does not exist within the content.
    */
    public void pasteCommand(String gama) throws UnrecognizedEntryException, NonExistentFunctionException{
        int action = 0;
        if (gama.contains(":"))
            action = CellEditActions.ACTION_PASTE;
        else
            action = CellEditActions.ACTION_PASTE_ALL_CUTBUFFER;
        searchAndPerformCellAction(gama, action, null);
        _cutbuffer.resetCounter();
        setChanged(true);
    }

    /**
     * Insert specified content in specified range.
     *
     * @param rangeSpecification
     * @param contentSpecification
     */
    public void insertContents(String coordinates, String content) throws UnrecognizedEntryException, NonExistentFunctionException /* FIXME maybe add exceptions */ {
        Cell cell = e_armazenamento.getCell(coordinates);

        if (content.isEmpty()){
            cell.setContent(null);
        // Check if content starts with an integer
        }else if (Character.isDigit(content.charAt(0)) || (content.charAt(0) == '-' && Character.isDigit(content.charAt(1)))) {
            // It's a numeric literal
            content = content.trim();
            int numericValue = Integer.parseInt(content);
            // Create a LiteralInt instance
            Conteudo literalInt = new LiteralInt(numericValue);
            // Store the literalInt accordingly
            cell.setContent(literalInt);
        } else if (content.startsWith("=")) {
            // It starts with '=', check the second character
            content = content.trim();
            char secondChar = content.charAt(1);

            if (Character.isLetter(secondChar)) {
                // It's a function, e.g., "=ADD(3;1,3;2)"
                String formula = content;
                List<Cell> argumentCells = getfunctionArgCells(formula);
                Conteudo function = new Function(formula, argumentCells, _functionCache);
                Function f1 = (Function) function;
                if (!(f1.isBinaryFunction()) && !(f1.isIntervalFunction()))
                    throw new NonExistentFunctionException(formula);
                cell.setContent(function);
            } else if (Character.isDigit(secondChar)) {
                // It's a reference, e.g., "=3;1"
                String ref = content;
                Conteudo reference = new Reference(ref);
                cell.setContent(reference);
            }
        } else if (content.startsWith("'")) {
                    // String (e.g., 'string)
                    Conteudo literalString = new LiteralString(content);
                    cell.setContent(literalString);
        }
        else{
            //exception
            cell.setContent(null);
        }
        setChanged(true);
    }

     /**
    * Generates a unique string representation of the content within a given cell, including its address.
    * This method is used to display the content of a cell in a specific format: "address|content".
    *
    * @param cell  The cell for which to generate the unique content representation.
    * @param input The address of the cell.
    * @return A formatted string containing the cell's address and content or an empty string if the cell is empty.
    */
    public String uniqueCellContent(Cell cell, String input){
        Conteudo content = cell.getContent();
        if (content != null){
            String type = content.getType();
            if ("LiteralInt".equals(type)){
                return input + '|' + cell.getContent();
            } else if ("Reference".equals(type)) {
                return input + '|' + getContentString(cell) + cell.getContent();

            } else if("LiteralString".equals(type)){
                return input + '|' + cell.getContent();

            } else if("Function".equals(type)){
                Function function = (Function) content;
                if (_functionCache.contains(function)){
                    if (function.isDirty()){
                        // Recalculate and update the cache if the function is marked as dirty
                        String result = input + '|' + getContentString(cell) + cell.getContent();
                        _functionCache.put(function, result);
                        function.setDirty(false);
                        return result;
                    }
                    else{
                        // Return the cached result if the function is not dirty
                        return _functionCache.get(function);
                    }
                }else{
                    // Calculate the result and store it in the cache
                    String result = input + '|' + getContentString(cell) + cell.getContent();
                    _functionCache.put(function, result);
                    return result;
                }
            }
        } else
            return input + '|';
        return "";
    }

    /**
    * Retrieves and formats the content of a cell, which can be of various types such as
    * LiteralInt, Reference, LiteralString, or Function. This method handles the content
    * type and returns a formatted string representation of the content.
    *
    * @param cell The Cell object for which to retrieve the content.
    * @return A formatted string representation of the content of the cell.
    */
    private String getContentString(Cell cell) {
        Conteudo content = cell.getContent();
        String type = content.getType();
        
        if ("LiteralInt".equals(type)) {
            return String.valueOf(cell.getContent());

        } else if ("Reference".equals(type)) {
            Reference reference = (Reference) content;
            String refAdress = reference.toString().substring(1);
            Cell referencedCell = e_armazenamento.getCell(refAdress);
            
            if (referencedCell != null) {
                if (referencedCell.getContent() != null) {
                    return getContentString(referencedCell);
                } else {
                    return "#VALUE";
                }
            }
            else
                return "#VALUE";

        } else if ("LiteralString".equals(type)) {
            return String.valueOf(cell.getContent());

        } else if ("Function".equals(type)){
            Function function = (Function) content;
            String functionBody = function.getFunctionBody();
            if (function.isBinaryFunction()) {
                String functionType = function.getFunctionType(functionBody);
                return handleBinaryFunction(functionBody, functionType);
            }
            else if (function.isIntervalFunction()){
                String functionType = function.getFunctionType(functionBody);
                return handleIntervalFunction(functionBody, functionType);
            }

        }
        return ""; 
    }

    /**
    * Handles the evaluation of a binary function specified by its function type.
    *
    * @param functionBody The entire function expression.
    * @param functionType The type of binary function to evaluate (e.g., "ADD," "SUB," "MUL," "DIV").
    * @return The result of the binary function evaluation as a string or "#VALUE" if any arguments are invalid.
    */
    public String handleBinaryFunction(String functionBody, String functionType) {
        String binArgs[] = {};
        BinaryFunctions binF = null;

        switch (functionType) {
            case "ADD":
                // Handle addition
                binF = new AddFunction(functionBody);
                binArgs = getBinaryFunctionArguments(functionBody);
                if (binArgs[0].contains("'") | binArgs[1].contains("'"))
                    return "#VALUE";
                else
                    break;
            case "SUB":
                // Handle subtraction
                binF = new SubFunction(functionBody);
                binArgs = getBinaryFunctionArguments(functionBody);
                if (binArgs[0].contains("'") | binArgs[1].contains("'"))
                    return "#VALUE";
                else
                    break;
            case "MUL":
                // Handle multiplication
                binF = new MulFunction(functionBody);
                binArgs = getBinaryFunctionArguments(functionBody);
                if (binArgs[0].contains("'") | binArgs[1].contains("'"))
                    return "#VALUE";
                else
                    break;
            case "DIV":
                // Handle division
                binF = new DivFunction(functionBody);
                binArgs = getBinaryFunctionArguments(functionBody);
                if (binArgs[1].equals("0") | binArgs[0].contains("'") | binArgs[1].contains("'"))
                    return "#VALUE";
                else
                    break;
        }
        if (binArgs[0].equals("#VALUE")| binArgs[1].equals("#VALUE"))
            return "#VALUE";
        else{
            int result = binF.calculate(binArgs[0], binArgs[1]);
            return String.valueOf(result);
        }                    
    }

    /**
    * Parses a binary function's arguments from a function body and processes them. Binary functions
    * operate on two arguments. This method extracts and processes these arguments, converting cell
    * references to their actual content values, or marking them as "#VALUE" if they cannot be resolved.
    *
    * @param functionBody The function body containing the binary function's arguments.
    * @return An array of two strings representing the processed binary function arguments.
    */
    private String[] getBinaryFunctionArguments(String functionBody){
        String arguments = functionBody.substring(functionBody.indexOf('(') + 1, functionBody.indexOf(')'));
        String[] argumentParts = arguments.split(",");
        String arg1 = argumentParts[0];
        String arg2 = argumentParts[1];
    
        if (arg1.contains(";")) {
            // Process arg1 as a cell reference
            Cell arg1Cell = e_armazenamento.getCell(arg1);
            if (arg1Cell != null){
                if (arg1Cell.getContent() != null)
                    arg1 = getContentString(arg1Cell);
                else
                    arg1 = "#VALUE";
            }
            else
                arg1 = "#VALUE";
        }
    
        if (arg2.contains(";")) {
            // Process arg2 as a cell reference
            Cell arg2Cell = e_armazenamento.getCell(arg2);
            if (arg2Cell != null){
                if (arg2Cell.getContent() != null)
                    arg2 = getContentString(arg2Cell);
                else
                    arg2 = "#VALUE";
            }
            else
                arg2 = "#VALUE";
            
        }
    
        return new String[]{arg1, arg2};
    }

    /**
    * Handles the evaluation of an interval function specified by its function type.
    *
    * @param functionBody The entire function expression.
    * @param functionType The type of interval function to evaluate (e.g., "AVERAGE," "CONCAT,").
    * @return The result of the binary function evaluation as a string or "#VALUE" if any arguments are invalid.
    */
    public String handleIntervalFunction(String functionBody, String functionType) {
        String intArgs[] = {};
        IntervalFunctions intF = null;

        switch (functionType) {
            case "AVERAGE":
                // Handle Average operation
                intF = new AverageFunction(functionBody);
                break;
            case "PRODUCT":
                // Handle Product operation
                intF = new ProductFunction(functionBody);
                break;
            case "CONCAT":
                // Handle Concat operation
                intF = new ConcatFunction(functionBody);
                break;
            case "COALESCE":
                // Handle Coalesce operation
                intF = new CoalesceFunction(functionBody);
                break;
        }
        intArgs = getIntervalFunctionArguments(functionBody, functionType);
        if (intArgs[0].equals("#VALUE")){
            return "#VALUE";
        }else{
            String result = intF.calculate(intArgs);
            return result;
        }                 
    }

    /**
    * Parses a interval function's arguments from a function body and processes them. Interval functions
    * operate on a variable number of arguments. This method extracts and processes these arguments, converting cell
    * references to their actual content values, or marking them as "#VALUE" if they cannot be resolved.
    *
    * @param functionBody The function body containing the binary function's arguments.
    * @param functionType The type of interval function to evaluate (e.g., "AVERAGE," "CONCAT,").
    * @return An array strings representing the processed interval function arguments.
    */
    private String[] getIntervalFunctionArguments(String functionBody, String functionType){
        String arguments = functionBody.substring(functionBody.indexOf('(') + 1, functionBody.indexOf(')'));
        String[] argumentParts = arguments.split(":");
        int[] intervalCoords = parseCellInterval(argumentParts);
        CellInterval cellInterval = new CellInterval(e_armazenamento.getCell(argumentParts[0]), e_armazenamento.getCell(argumentParts[1])); 
        List<String> result = new ArrayList<>();
        
        if (cellInterval.getStartCell() != null && cellInterval.getEndCell() != null){
            if (intervalCoords[0] == intervalCoords[2]){
                int startColumn = Math.min(intervalCoords[1], intervalCoords[3]);
                int endColumn = Math.max(intervalCoords[1], intervalCoords[3]);
                for (int currentColumn = startColumn; currentColumn <= endColumn; currentColumn++) {
                    String currentInput = intervalCoords[0] + ";" + currentColumn;
                    Cell currentCell = e_armazenamento.getCell(currentInput);
                    if (currentCell != null){
                        if (currentCell.getContent() == null) {
                            if (functionType.equals("COALESCE")) {
                                continue;
                            }
                            return new String[]{"#VALUE"};
                        } 
                        result.add(getContentString(currentCell));
                    }else
                        return new String[]{"#VALUE"};    
                }
            }else if (intervalCoords[1] == intervalCoords[3]){
                int startRow = Math.min(intervalCoords[0], intervalCoords[2]);
                int endRow = Math.max(intervalCoords[0], intervalCoords[2]);
                for (int currentRow = startRow; currentRow <= endRow; currentRow++) {
                    String currentInput = currentRow + ";" + intervalCoords[1];
                    Cell currentCell = e_armazenamento.getCell(currentInput);
                    if (currentCell != null){
                        if (currentCell.getContent() == null) {
                            if (functionType.equals("COALESCE")) {
                                continue;
                            }
                            return new String[]{"#VALUE"};
                        } 
                        result.add(getContentString(currentCell));
                    }else
                        return new String[]{"#VALUE"};    
                }
            }else
                return new String[]{"#VALUE"};
        }
        else
            return new String[]{"#VALUE"};
    
        return result.toArray(new String[result.size()]);
    }

    /**
    * Searches for cells containing values that match the provided search criteria.
    *
    * @param searchCriteria The criteria to search for within cell values.
    * @return A string containing matching cell values, separated by newlines.
    */
    public String searchValuesCommand(String searchCriteria){
        int action = CellSearchActions.ACTION_SEARCH_VALUE;
        return searchCells(searchCriteria, action);
    }

    /**
    * Searches for cells containing functions that match the provided search criteria.
    *
    * @param searchCriteria The criteria to search for within cell functions.
    * @return A string containing matching cell functions, sorted alphabetically and separated by newlines.
    */
    public String searchFunctionsCommand(String searchCriteria){
        int action = CellSearchActions.ACTION_SEARCH_FUNCTION;
        return searchCells(searchCriteria, action);
    }

    /**
    * Searches for cells that match the provided search criteria and action type.
    *
    * @param searchCriteria The criteria to search for within cells.
    * @param action The type of search action (e.g., value or function search).
    * @return A string containing matching cell contents, sorted alphabetically for functions.
    */
    public String searchCells(String searchCriteria, int action) {
        List<String> matches = new ArrayList<>();
    
        for (int currentRow = 1; currentRow <= _row; currentRow++) {
            for (int currentCol = 1; currentCol <= _column; currentCol++) {
                String position = currentRow + ";" + currentCol;
                Cell cell = e_armazenamento.getCell(position);
    
                if (cell != null && cell.getContent() != null) {
                    if (matchesCriteria(cell, searchCriteria, action)) 
                        matches.add(uniqueCellContent(cell, position));
                }
            }
        }
        if (action == CellSearchActions.ACTION_SEARCH_FUNCTION) {
            // Sort the matches alphabetically
            Collections.sort(matches, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    // Extract the part after '=' for comparison
                    String content1 = s1.substring(s1.indexOf('=') + 1, s1.indexOf('('));
                    String content2 = s2.substring(s2.indexOf('=') + 1, s2.indexOf('('));
                    return content1.compareTo(content2);
                }
            });
        }
        // Construct the result string 
        StringBuilder result = new StringBuilder();
        int flag = 0;
        for (String match : matches) {
            if (flag == 1) {
                result.append("\n");
            }
            result.append(match);
            flag = 1;
        }
        return result.toString();
    }
    
    /**
    * Checks if the given cell matches the search criteria based on the specified action type.
    *
    * @param currentCell The cell to check for a match.
    * @param searchCriteria The criteria to match against.
    * @param action The type of search action (e.g., value or function search).
    * @return True if the cell matches the criteria for the specified action, otherwise false.
    */
    private boolean matchesCriteria(Cell currentCell, String searchCriteria, int action) {
        if ( action == CellSearchActions.ACTION_SEARCH_VALUE){
            String cellValue = getContentString(currentCell);
            return cellValue.equals(searchCriteria);
        }
        else if (action == CellSearchActions.ACTION_SEARCH_FUNCTION){
            Conteudo content = currentCell.getContent();
            String type = content.getType();
            if ("Function".equals(type)){
                Function function = (Function) content;
                String cellcontent = function.getFunctionBody();
                return cellcontent.contains(searchCriteria);
            }
        }
        return false;
    }

    /**
    * Parses the start and end cell positions from the given cell interval string.
    *
    * @param intervalParts An array of two cell addresses representing the interval.
    * @return An integer array with [startRow, startColumn, endRow, endColumn].
    */
    private int[] parseCellInterval(String[] intervalParts) {
        String[] cell1Address = intervalParts[0].split(";");
        String[] cell2Address = intervalParts[1].split(";");

        int startCellRow = Integer.valueOf(cell1Address[0]);
        int startCellColumn = Integer.valueOf(cell1Address[1]);
        int endCellRow = Integer.valueOf(cell2Address[0]);
        int endCellColumn = Integer.valueOf(cell2Address[1]);
    
        return new int[] { startCellRow, startCellColumn, endCellRow, endCellColumn };
    }

    /**
    * Copies contents from the spreadsheet to the cutbuffer.
    *
    * @param spreadCell The cell in the spreadsheet to copy contents from.
    */
    public void copyContents(Cell spreadCell){
        if (_cutbuffer.isHorizontal()){
            for (int currentCol = 1; currentCol <= _column; currentCol++) {
                String position = "1" + ";" + currentCol;
                Cell cutbufferCell = _cutbuffer.getCell(position);
                if (cutbufferCell.getContent() == null){
                    if (spreadCell.getContent() == null){
                        Conteudo literalString = new LiteralString("");
                        cutbufferCell.setContent(literalString);
                        break;
                    }
                    else{
                        cutbufferCell.setContent(spreadCell.getContent());
                        break;
                    }
                }
            }
        } else if (_cutbuffer.isVertical()){
            for (int currentRow = 1; currentRow <= _row; currentRow++) {
                String position = currentRow + ";" + "1";
                Cell cutbufferCell = _cutbuffer.getCell(position);
                if (cutbufferCell.getContent() == null){
                    if (spreadCell.getContent() == null){
                        Conteudo literalString = new LiteralString("");
                        cutbufferCell.setContent(literalString);
                        break;
                    }
                    else{
                        cutbufferCell.setContent(spreadCell.getContent());
                        break;
                    }
                }
            }
        }else if (_cutbuffer.is1cell()){
            String position = "1" + ";" + "1";
            Cell cutbufferCell = _cutbuffer.getCell(position);
            cutbufferCell.setContent(spreadCell.getContent());
        }
    }

    /**
    * Cuts the contents from the specified spreadsheet cell and stores them in the cut buffer.
    *
    * @param spreadCell The cell in the spreadsheet to cut contents from.
    */ 
    public void cutContents(Cell spreadCell){
        copyContents(spreadCell);
        spreadCell.setContent(null);
        setChanged(true);
    }

    /**
    * Pastes contents from the cut buffer into the specified spreadsheet cell.
    *
    * @param spreadCell The cell in the spreadsheet to paste contents into.
    */
    public void pasteContents(Cell spreadCell){
        if ( _cutbuffer.isHorizontal()){
            String position = "1" + ";" + _cutbuffer.getCounter();
            Cell cutbufferCell = _cutbuffer.getCell(position);
            spreadCell.setContent(cutbufferCell.getContent());
            _cutbuffer.incrementCounter();
        } else if (_cutbuffer.isVertical()){
            String position = _cutbuffer.getCounter() + ";" + "1";
            Cell cutbufferCell =  _cutbuffer.getCell(position);
            spreadCell.setContent(cutbufferCell.getContent());
            _cutbuffer.incrementCounter();                        
        }
    }

    /**
    * Pastes contents from the entire cut buffer into a range of spreadsheet cells starting from the specified cell address.
    *
    * @param spreadCellAdress The address of the first cell in the spreadsheet to paste the cut buffer contents into.
    */
    public void pasteAllCutBuffer(String spreadCellAdress){
        String []coordinates = spreadCellAdress.split(";");
        if ( _cutbuffer.isHorizontal()){
            int startingColumn = Integer.valueOf(coordinates[1]);
            for(int currentCol = startingColumn;currentCol <= _column; currentCol++){
                String spreadPosition = coordinates[0] + ";" + currentCol;
                Cell currentSpreadCell = e_armazenamento.getCell(spreadPosition);
                String cutbufferPosition = "1" + ";" + _cutbuffer.getCounter();
                Cell cutbufferCell = _cutbuffer.getCell(cutbufferPosition);
                currentSpreadCell.setContent(cutbufferCell.getContent());
                _cutbuffer.incrementCounter();
            }
        } else if (_cutbuffer.isVertical()){
            int startingRow = Integer.valueOf(coordinates[0]);
            for(int currentRow = startingRow;currentRow <= _row; currentRow++){
                String spreadPosition = currentRow + ";" + coordinates[1];
                Cell currentSpreadCell = e_armazenamento.getCell(spreadPosition);
                String cutbufferPosition = _cutbuffer.getCounter() + ";" + "1";
                Cell cutbufferCell = _cutbuffer.getCell(cutbufferPosition);
                currentSpreadCell.setContent(cutbufferCell.getContent());
                _cutbuffer.incrementCounter();
            }
        } else if (_cutbuffer.is1cell()){
            Cell cutbufferCell = _cutbuffer.getCell("1;1");
            Cell spreadCell = e_armazenamento.getCell(spreadCellAdress);
            spreadCell.setContent(cutbufferCell.getContent());
        }
    }

    /**
    * Generates a string representation of the contents in the cut buffer.
    *
    * @return A string containing the contents in the cut buffer.
    */
    public String showCutBuffer(){
        StringBuilder result = new StringBuilder();
        int flag = 0;
    
        for (int currentRow = 1; currentRow <= _row; currentRow++) {
            for (int currentCol = 1; currentCol <= _column; currentCol++) {
                String position = currentRow + ";" + currentCol;
                Cell cell = _cutbuffer.getCell(position);
    
                if (cell != null && cell.getContent() != null) {
                    if (flag == 1) {
                        result.append("\n");
                    }
                    result.append(uniqueCellContent(cell, position));
                    flag = 1;
                }
            }
        }
        return result.toString();
    }

    /**
    * Parses the function body to extract the argument cell addresses and returns a list of associated cells.
    *
    * @param functionBody The function body containing argument cell addresses.
    * @return A list of cells corresponding to the function's argument addresses.
    */
    public List<Cell> getfunctionArgCells(String functionBody){
        List<Cell> _argumentCells = new ArrayList<>();
        String args = functionBody.substring(functionBody.indexOf('(') + 1, functionBody.indexOf(')'));
        if (args.contains(":")){
            //intervalFunction
            String[] argumentParts = args.split(":");
            int[] intervalCoords = parseCellInterval(argumentParts);
            if (intervalCoords[0] == intervalCoords[2]){
                int startColumn = Math.min(intervalCoords[1], intervalCoords[3]);
                int endColumn = Math.max(intervalCoords[1], intervalCoords[3]);
                for (int currentColumn = startColumn; currentColumn <= endColumn; currentColumn++) {
                    String currentInput = intervalCoords[0] + ";" + currentColumn;
                    Cell currentCell = e_armazenamento.getCell(currentInput);
                    if (currentCell != null)
                        _argumentCells.add(currentCell);
                }
            }
            else if (intervalCoords[1] == intervalCoords[3]) {
                int startRow = Math.min(intervalCoords[0], intervalCoords[2]);
                int endRow = Math.max(intervalCoords[0], intervalCoords[2]);
                for (int currentRow = startRow; currentRow <= endRow; currentRow++) {
                    String currentInput = currentRow + ";" + intervalCoords[1];
                    Cell currentCell = e_armazenamento.getCell(currentInput);
                    if (currentCell != null)
                        _argumentCells.add(currentCell);
                }
            }
            
        }else if(args.contains(",")){
            String[] argumentParts = args.split(",");
            if (argumentParts[0].contains(";")){
                Cell currentCell = e_armazenamento.getCell(argumentParts[0]);
                if (currentCell != null)
                        _argumentCells.add(currentCell);
            }
            if (argumentParts[1].contains(";")){
                Cell currentCell = e_armazenamento.getCell(argumentParts[1]);
                if (currentCell != null)
                        _argumentCells.add(currentCell);
            }
        }
        return _argumentCells;
    }

    public void addUser(User user) {
        _users.add(user);
        user.addSpreadsheet(this); // Notify the user about the spreadsheet
    }

    public void removeUser(User user) {
        _users.remove(user);
        user.removeSpreadsheet(this); // Notify the user about the spreadsheet removal
    }

    public void checkUser(){
        if(_users.isEmpty()){
            _users.add(root);
        }
    }

    public void setChanged(Boolean check){
        _check = check;
    }

    public Boolean hasChanged(){
        return _check;
    }
    
}
