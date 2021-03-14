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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.platformio.eclipse.ide.installer.Installer;

public class Activator extends AbstractUIPlugin {

	private final Installer installer;

	public Activator() {
		this.installer = new Installer();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Job installJob = new Job(Messages.PlatformIO_installation_message) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.Virtualenv_creation_message, IProgressMonitor.UNKNOWN);
				installer.createVirtualEnvironment(monitor);
				monitor.setTaskName(Messages.Core_installation_message);
				installer.installPlatformIOHome();
				return Status.OK_STATUS;
			}
		};
		installJob.setPriority(Job.LONG);
		installJob.setUser(true);
		installJob.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

}
