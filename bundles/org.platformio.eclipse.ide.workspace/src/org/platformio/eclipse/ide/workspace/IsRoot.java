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
package org.platformio.eclipse.ide.workspace;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;

/**
 * 
 * Applicable to path of directory that is supposed to be PlatformIO project
 * root.
 *
 */
public final class IsRoot implements Predicate<String> {

	private final String config = "platformio.ini"; //$NON-NLS-1$

	@Override
	public boolean test(String path) {
		return Files.exists(Paths.get(path).resolve(config));
	}

}
