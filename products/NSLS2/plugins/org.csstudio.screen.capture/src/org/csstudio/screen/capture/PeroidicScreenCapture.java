/**
 * 
 */
package org.csstudio.screen.capture;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.csstudio.apputil.ui.swt.Screenshot;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Kunal Shroff
 * 
 */
public class PeroidicScreenCapture extends AbstractHandler {

    private final IPreferencesService preferenceService = Platform
	    .getPreferencesService();
    private final ScheduledExecutorService scheduler = Executors
	    .newScheduledThreadPool(1);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

	int delay = preferenceService.getInt(
		    "org.csstudio.screen.capture", "auto.refresh.rate",
		    5, null);
	final String resourcePath = preferenceService.getString(
		    "org.csstudio.screen.capture", "resource.folder",
		    System.getProperty("user.dir")+"/resources", null);	
	final String outputFolder = preferenceService.getString(
		    "org.csstudio.screen.capture", "file.output.folder",
		    "C:/Temp/test.png", null);
	
	final Map<String, IEditorPart> partRegistryMap = new HashMap<String, IEditorPart>();

	try {
	    File directory = new File(resourcePath);
	    if (directory.isDirectory()) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
		    IWorkbenchPage page = PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getActivePage();
		    IFile ifile = IFileUtil.getInstance().createFileResource(
			    files[i]);
		    IEditorDescriptor desc = PlatformUI.getWorkbench()
			    .getEditorRegistry()
			    .getDefaultEditor(files[i].getName());
		    IEditorPart part = page.openEditor(new FileEditorInput(
			    ifile), desc.getId());
		    partRegistryMap.put(ifile.getName(), part);
		}
	    }
	} catch (PartInitException e) {
	    e.printStackTrace();
	}

	final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	final Display display = Display.getCurrent();
	// Ensure the refresh rate isn't less than 5 seconds
	delay = delay >= 5 ? delay : 5;
	scheduler.scheduleWithFixedDelay(new Runnable() {
	    
	    @Override
	    public void run() {
		try {
		    for (final Entry<String, IEditorPart> entry : partRegistryMap.entrySet()) {
			display.asyncExec(new Runnable() {
			    
			    @Override
			    public void run() {
				try {
				    page.bringToTop(entry.getValue());	
				    Image image = Screenshot.getApplicationScreenshot();
				    ImageLoader loader = new ImageLoader();
				    loader.data = new ImageData[] { image.getImageData() };
				    loader.save(outputFolder+entry.getKey()+".png", SWT.IMAGE_PNG);
				    image.dispose();
				} catch (Exception e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			    }
			});		        
		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}, delay, delay, TimeUnit.SECONDS);
	

	return null;
    }

    @Override
    public void dispose() {
	super.dispose();
	scheduler.shutdown();
    }
}
