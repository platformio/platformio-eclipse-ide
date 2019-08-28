package com.insightmachines.pio.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenPioHandler extends AbstractHandler {

	private static final String HTTP_HOST = "127.0.0.1";
	private static final int HTTP_PORT_BEGIN = 8010;
	private static final int HTTP_PORT_END = 8100;
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		URL url = findPlatformIoHomeUrl();
		
		if ( url == null ) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(
				shell, 
				"Can't find PlatformIO Home", 
				"Could not find any running instance of the PlatformIO Home service. "
				+ "Scanned localhost ports from " + HTTP_PORT_BEGIN + " to " + HTTP_PORT_END
			);
			return null;
		}
		
		IWorkbenchBrowserSupport browserSupport= PlatformUI.getWorkbench().getBrowserSupport();		
		try {
			IWebBrowser browser = browserSupport.createBrowser("piobrowser");
			browser.openURL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return null;
	}
		
	private static URL findPlatformIoHomeUrl() {
		for ( int port=HTTP_PORT_BEGIN; port<=HTTP_PORT_END; port++ ) {
			if ( tcpPortUsed(port) ) {
				URL url = assemblePlatformIoHomeUrl(port);
				if ( isPlatformIoHome(url) ) {
					return url;
				}
			}
		}
		return null;
	}
	
	private static URL assemblePlatformIoHomeUrl(int port) {
		try {
			return new URL("http://" + HTTP_HOST + ":" + port + "/");
		} catch (MalformedURLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	private static boolean isPlatformIoHome( URL url ) {
		if ( url == null ) return false;
		try (InputStream is = url.openConnection().getInputStream();
		     BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		     Stream<String> stream = reader.lines()) {
		    return stream.anyMatch( line-> line.contains("PlatformIO") );
		} catch (IOException e) {
			return false;
		} 
	}
	
	private static boolean tcpPortUsed(int port) {
  	  boolean portUsed;
  	  try (ServerSocket ignored = new ServerSocket(port)) {
  	      portUsed = false;
  	  } catch (IOException e) {
  	      portUsed = true;
  	  }
  	  return portUsed;
  	}
	
}
