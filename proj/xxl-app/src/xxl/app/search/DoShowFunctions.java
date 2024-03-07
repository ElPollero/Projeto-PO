package xxl.app.search;

import pt.tecnico.uilib.menus.Command;
import xxl.Spreadsheet;
// FIXME import classes

/**
 * Command for searching function names.
 */
class DoShowFunctions extends Command<Spreadsheet> {

    DoShowFunctions(Spreadsheet receiver) {
        super(Label.SEARCH_FUNCTIONS, receiver);
        addStringField("Function", Prompt.searchFunction());
        // FIXME add fields
    }

    @Override
    protected final void execute() {
        // FIXME implement command
        _display.popup(_receiver.searchFunctionsCommand(stringField("Function")));
    }

}
