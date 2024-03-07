package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.NonExistentFunctionException;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Copy command.
 */
class DoCopy extends Command<Spreadsheet> {

    DoCopy(Spreadsheet receiver) {
        super(Label.COPY, receiver);
        addStringField("adress", Prompt.address());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.copyCommand(stringField("adress"));
        }catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("adress"));
        }catch (NonExistentFunctionException e){}
    }

}
