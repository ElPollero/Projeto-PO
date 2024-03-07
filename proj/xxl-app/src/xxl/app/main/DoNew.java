package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Calculator;


/**
 * Open a new file.
 */
class DoNew extends Command<Calculator> {
    DoNew(Calculator receiver) {
        super(Label.NEW, receiver);
    }


    @Override
    protected final void execute() throws CommandException {
        if (_receiver.getSpreadsheet() != null && _receiver.getSpreadsheet().hasChanged()){
            if (Form.confirm(Prompt.saveBeforeExit())){
                DoSave saveAction = new DoSave(_receiver);
                saveAction.execute();
            }
        }
        int num_lines = Form.requestInteger(Prompt.lines());
        int num_columns = Form.requestInteger(Prompt.columns());
        _receiver.createSheet(num_lines, num_columns);
    }

}
