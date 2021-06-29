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
package org.platformio.eclipse.ide.core.installer;

import org.eclipse.osgi.util.NLS;

public class Messages {

	private static final String BUNDLE_NAME = "org.platformio.eclipse.ide.installer.messages"; //$NON-NLS-1$
	public static String Python_installation_message;
	public static String Virtualenv_installation_message;
	public static String Setting_workspace_message;
	public static String Installing_Platformio_message;
	public static String Launching_Platformio_home_message;
	public static String Installing_module_message;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
