/*******************************************************************************
 * Copyright (c) 2021 PlatformIO and ArSysOp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.home.internal.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.installer.Installer;

//FIXME: AF: "ensureServerStarted" should be implemented here
@Component(immediate = true)
public final class HomeAccess {

	// FIXME: AF: actually we don't need this field, we need to keep "ensure job"
	// running from "activate" till "deactivate";
	private PlatformIO pio;

	public void activate() {
		Job install = new Job(Messages.PlatformIO_installation_message) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.Virtualenv_creation_message, IProgressMonitor.UNKNOWN);

				try {
					pio = new Installer().install(monitor);
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
		install.setPriority(Job.LONG);
		install.setUser(true);
		install.schedule();
	}

	public void deactivate() {
		if (pio != null) {
			Job stop = new Job(Messages.PlatformIO_stop_message) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						pio.stop();
					} catch (IOException e) {
						return new Status(IStatus.ERROR, getClass(), e.getMessage(), e);
					}
					return Status.OK_STATUS;
				}
			};
			stop.schedule();
		}
	}

}
