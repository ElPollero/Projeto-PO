package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.NonExistentFunctionException;
// FIXME import classes
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Cut command.
 */
class DoCut extends Command<Spreadsheet> {

    DoCut(Spreadsheet receiver) {
        super(Label.CUT, receiver);
        addStringField("adress", Prompt.address());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.cutCommand(stringField("adress"));
        }catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("adress"));
        }catch(NonExistentFunctionException e){
            
        }
    }
}
