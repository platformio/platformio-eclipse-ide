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
package org.platformio.eclipse.ide.installer.python;

import java.nio.file.Path;

import org.platformio.eclipse.ide.installer.api.CommandResult;

public interface Python {

	Path executable();

	void installModule(String name);

	boolean moduleInstalled(String module);

	CommandResult execute(String module, String... args);

	void executeLasting(String module, String... args);

	void killProcess(String module);

}
