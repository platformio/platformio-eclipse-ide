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
package org.platformio.eclipse.ide.installer.internal.linux;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.python.PythonsRegistry;

public final class LinuxPythonsRegistry implements PythonsRegistry {

	@Override
	public Optional<String> findPython() {
		try {
			if (Runtime.getRuntime().exec("python3 -V").waitFor() == 0) //$NON-NLS-1$
				return Optional.of("python3"); //$NON-NLS-1$
			if (Runtime.getRuntime().exec("python -V").waitFor() == 0) //$NON-NLS-1$
				return Optional.of("python"); //$NON-NLS-1$
		} catch (InterruptedException | IOException e) {
			Platform.getLog(getClass()).error(e.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public String executableSuffix() {
		return ""; //$NON-NLS-1$
	}

}
