package xxl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import xxl.exceptions.ImportFileException;
import xxl.exceptions.MissingFileAssociationException;
import xxl.exceptions.NonExistentFunctionException;
import xxl.exceptions.UnavailableFileException;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class representing a spreadsheet application.
 */
public class Calculator {

    /** The current spreadsheet. */
    private Spreadsheet _spreadsheet = null;

    private String _filename = "";

    public void createSheet(int num_rows, int num_columns){
        _spreadsheet = new Spreadsheet(num_rows, num_columns);
    }

    /**
     * Saves the serialized application's state into the file associated to the current network.
     *
     * @throws FileNotFoundException if for some reason the file cannot be created or opened. 
     * @throws MissingFileAssociationException if the current network does not have a file.
     * @throws IOException if there is some error while serializing the state of the network to disk.
     */
    public void save() throws FileNotFoundException, MissingFileAssociationException, IOException {
        if (!_spreadsheet.hasChanged())
            return;

        if (_filename == "") {
			throw new MissingFileAssociationException();
		}
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
			oos.writeObject(_spreadsheet);

            _spreadsheet.setChanged(false);
			
		}
    }

    /**
     * Saves the serialized application's state into the specified file. The current network is
     * associated to this file.
     *
     * @param filename the name of the file.
     * @throws FileNotFoundException if for some reason the file cannot be created or opened.
     * @throws MissingFileAssociationException if the current network does not have a file.
     * @throws IOException if there is some error while serializing the state of the network to disk.
     */
    public void saveAs(String filename) throws FileNotFoundException, MissingFileAssociationException, IOException {
        _filename = filename;
		save();
    }

    /**
     * Loads a spreadsheet from a file and sets it as the active spreadsheet.
     * @param filename name of the file containing the serialized application's state
     *        to load.
     * @throws UnavailableFileException if the specified file does not exist or there is
     *         an error while processing this file.
     */
    public void load(String filename) throws UnavailableFileException {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
			_filename = filename;
			_spreadsheet = (Spreadsheet) ois.readObject();
			_spreadsheet.setChanged(false);
		} catch (IOException | ClassNotFoundException e) {
			throw new UnavailableFileException(filename);
		}
    }

    /**
     * Read text input file and create domain entities..
     *
     * @param filename name of the text input file
     * @throws ImportFileException
     */
    public void importFile(String filename) throws ImportFileException {
        try{
            String currentLine;
		    String[] splitLine;
            int row = -1;
            int column= -1;
		
		    BufferedReader reader = new BufferedReader(new FileReader(filename));

            // Read and handle the first two lines
            for (int i = 0; i < 2; i++) {
                currentLine = reader.readLine();
                if (currentLine != null) {
                    // Split the line and process accordingly
                    splitLine = currentLine.split("=");
                    if (splitLine.length == 2) {
                        String content = splitLine[1].trim();
                        if (i == 0) {
                            // First line, store lines
                            row = Integer.parseInt(content);
                        } else if (i == 1) {
                            // Second line, store columns
                            column = Integer.parseInt(content);
                        }
                    }
                    else{
                        throw new UnrecognizedEntryException(currentLine);
                    }
                }
            }
            if ( row > 0 && column > 0)
                createSheet(row, column);
            reader.close();
            _spreadsheet.importFile(filename);

        }catch (IOException | UnrecognizedEntryException | NonExistentFunctionException /* FIXME maybe other exceptions */ e) {
            throw new ImportFileException(filename, e);
        }

    }

    public Spreadsheet getSpreadsheet() {
        return _spreadsheet;
    }

}
