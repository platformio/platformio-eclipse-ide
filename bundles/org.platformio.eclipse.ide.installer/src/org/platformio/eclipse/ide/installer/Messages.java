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
package org.platformio.eclipse.ide.installer;

import org.eclipse.osgi.util.NLS;

public class Messages {

	private static final String BUNDLE_NAME = "org.platformio.eclipse.ide.installer.messages"; //$NON-NLS-1$
	public static String Python_installation_message;
	public static String Virtualenv_installation_message;
	public static String Setting_workspace_message;
	public static String Installing_Platformio_message;
	public static String Launching_Platformio_home_message;
	public static String Installing_module_message;
	public static String Installation_successful_message;
	public static String Installation_failed_message;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
