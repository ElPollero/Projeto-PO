package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.NonExistentFunctionException;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class for searching functions.
 */
class DoShow extends Command<Spreadsheet> {

    DoShow(Spreadsheet receiver) {
        super(Label.SHOW, receiver);
        addStringField("adress", Prompt.address());
    }

    @Override
    protected final void execute() throws CommandException, InvalidCellRangeException {
       try{
            _display.popup( _receiver.showCommand(stringField("adress")));
       }catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("adress"));
       }catch(NonExistentFunctionException e){}
    }

}
