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
package org.platformio.eclipse.ide.installer.api;

import java.nio.file.Path;
import java.util.List;

public interface Environment {

	CommandResult execute(String command, List<String> arguments);

	CommandResult execute(String command, List<String> arguments, String path);

	void executeLasting(String command, List<String> arguments, String path);

	void killProcess(String command);

	OS os();

	Path home();

	Path cache();

	Path env();

}
