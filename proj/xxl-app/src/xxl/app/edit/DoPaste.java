package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.NonExistentFunctionException;
// FIXME import classes
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Paste command.
 */
class DoPaste extends Command<Spreadsheet> {

    DoPaste(Spreadsheet receiver) {
        super(Label.PASTE, receiver);
        addStringField("adress", Prompt.address());
    }

    @Override
    protected final void execute() throws CommandException {
         try{
            _receiver.pasteCommand(stringField("adress"));
        }catch(UnrecognizedEntryException | NonExistentFunctionException e){
            throw new InvalidCellRangeException(stringField("adress"));
        }
    }
}
