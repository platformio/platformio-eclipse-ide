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
package org.platformio.eclipse.ide.home.core;

import org.eclipse.osgi.util.NLS;

public class Messages {

	private static final String BUNDLE_NAME = "org.platformio.eclipse.ide.home.core.messages"; //$NON-NLS-1$
	public static String PlatformIO_installation_message;
	public static String PlatformIO_stop_message;
	public static String Virtualenv_creation_message;
	public static String Core_installation_message;
	public static String Installation_successful_message;
	public static String Installation_failed_message;
	public static String View_Tooltip_text;
	public static String View_Not_Opened_Error_text;
	public static String Select_Project_Title;
	public static String Select_Project_Message;
	public static String Terminal_Top_Message;
	public static String Terminal_Unknown_Message;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
