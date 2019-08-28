package com.insightmachines.pio;


import java.net.URL;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.InternalBrowserEditorInstance;
import org.eclipse.ui.internal.browser.InternalBrowserInstance;
import org.eclipse.ui.internal.browser.Messages;
import org.eclipse.ui.internal.browser.Trace;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

public class PioWorkbenchBrowserSupport extends AbstractWorkbenchBrowserSupport {

	@Override
	public IWebBrowser createBrowser(int style, String browserId, String name, String tooltip) throws PartInitException {
		return new PioBrowser(browserId, style, name, tooltip);
	}

	@Override
	public IWebBrowser createBrowser(String browserId) throws PartInitException {
		return new PioBrowser(browserId, 0, null, null);
	}
	
	private static class PioBrowser extends InternalBrowserInstance {
		
		PioBrowser(String browserId, int style, String name, String tooltip) {
			super(browserId, style, name, tooltip);
		}


		@Override
		public void openURL(URL url) throws PartInitException {
			WebBrowserEditorInput input = new WebBrowserEditorInput(url, style);
			input.setName(this.name);
			input.setToolTipText(this.tooltip);
			WebBrowserEditor editor = (WebBrowserEditor) part;
			
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = null;
			if (workbenchWindow != null)
				page = workbenchWindow.getActivePage();

			if (page == null)
				throw new PartInitException(Messages.errorCouldNotLaunchInternalWebBrowser);

			if (editor != null) {
				editor.init(editor.getEditorSite(), input);
				page.activate(editor);
			} else {
				try {
					IEditorPart editorPart = page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
					hookPart(page, editorPart);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error opening Web browser", e); //$NON-NLS-1$
				}
			}
		}

		@Override
		public boolean close() {
			try {
				return ((WebBrowserEditor)part).close();
			} catch (Exception e) {
				return false;
			}
		}
		
	}

}
