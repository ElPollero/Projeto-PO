package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.NonExistentFunctionException;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class for inserting data.
 */
class DoInsert extends Command<Spreadsheet> {

    DoInsert(Spreadsheet receiver) {
        super(Label.INSERT, receiver);
        addStringField("adress", Prompt.address());
        addStringField("content", Prompt.content());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.insertCommand(stringField("adress"), stringField("content"));
       }catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("adress"));
       }catch(NonExistentFunctionException e){
            String function = stringField("content").substring(stringField("content").indexOf('=') + 1, stringField("content").indexOf('('));
            throw new UnknownFunctionException(function);
       }
    }

}
