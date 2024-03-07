package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Calculator;
import xxl.exceptions.UnavailableFileException;

/**
 * Open existing file.
 */
class DoOpen extends Command<Calculator> {

    DoOpen(Calculator receiver) {
        super(Label.OPEN, receiver);
    }

    @Override
    protected final void execute() throws CommandException {
        try {
            if (_receiver.getSpreadsheet() != null && _receiver.getSpreadsheet().hasChanged()){
                if (Form.confirm(Prompt.saveBeforeExit())){
                    DoSave s = new DoSave(_receiver);
                    s.execute();
                }
            }
            String filename = Form.requestString(Prompt.openFile());
            _receiver.load(filename);
        } catch (UnavailableFileException e) {
            throw new FileOpenFailedException(e);
        }
    }

}
