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
				try {
					installer.install(monitor);
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

	@Override
	public void stop(BundleContext context) throws Exception {
		installer.killPio();
		super.stop(context);
	}

}
