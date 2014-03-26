/**
 * 
 */
package org.csstudio.logbook.olog.property.shift;

import gov.bnl.shiftClient.Shift;

import org.csstudio.logbook.ui.LogTreeView;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 * Command to automatically search for log entries associated with the selected shift
 * 
 * The command opens if not already open the logTreeView with a query associated with the shift property.
 */
public class OpenShiftLogViewer extends AbstractHandler {

    public final static String ID = "org.csstudio.logbook.viewer.OpenShiftLogViewer";
    private String searchString = "";

    public OpenShiftLogViewer() {
	super();
    }

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
	final IWorkbench workbench = PlatformUI.getWorkbench();
	ISelection selection = HandlerUtil.getCurrentSelection(event);
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection strucSelection = (IStructuredSelection) selection;
	    if (strucSelection.getFirstElement() instanceof Shift) {
		searchString = "* " + ShiftPropertyWidget.propertyName + "."
			+ ShiftPropertyWidget.attrIdName + ":"
			+ ((Shift) strucSelection.getFirstElement()).getId();
	    } else {
		
	    }
	} else {
	    // TODO invalid selection
	}
	try {
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
	    LogTreeView logTreeView = (LogTreeView)page.showView(LogTreeView.ID);	   
	    if(logTreeView != null){ 
	    	logTreeView.setSearchString(searchString);
	    }
	} catch (final Exception ex) {
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
			     ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event),
			     "Error executing command...", ex);
			}
		});
	}
	return null;
    }
}
