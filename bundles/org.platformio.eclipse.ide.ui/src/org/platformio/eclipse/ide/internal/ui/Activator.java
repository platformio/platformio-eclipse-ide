/*******************************************************************************
 * Copyright (c) 2021 ArSysOp and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.internal.ui;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;
import org.osgi.framework.BundleContext;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.installer.Installer;

public class Activator extends AbstractUIPlugin {

	private PlatformIO pio;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Job installJob = new Job(Messages.PlatformIO_installation_message) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.Virtualenv_creation_message, IProgressMonitor.UNKNOWN);

				try {
					pio = new Installer().install(monitor, path -> importProject(path));
					pio.home();
					return new Status(IStatus.OK, getClass(), Messages.Installation_successful_message);
				} catch (IOException e) {
					return new Status(IStatus.ERROR, getClass(),
							String.format(Messages.Installation_failed_message, e.getMessage()));
				} catch (CoreException e) {
					return e.getStatus();
				}
			}
		};
		installJob.setPriority(Job.LONG);
		installJob.setUser(true);
		installJob.schedule();
	}

	private void importProject(String path) {
		Display.getDefault().syncExec(() -> {
			ExternalProjectImportWizard importWizard = new ExternalProjectImportWizard(path);
			importWizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
			Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
			new WizardDialog(shell, importWizard).open();
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (pio != null) {
			pio.stop();
		}
		super.stop(context);
	}

}
