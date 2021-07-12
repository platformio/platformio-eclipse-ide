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
package org.platformio.eclipse.ide.core.core;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.core.json.Dump;

public abstract class PIOExecutable implements Supplier<String> {

	public static final class OfDump extends PIOExecutable {

		private final Dump dump;

		public OfDump(Dump dump) {
			this.dump = dump;
		}

		@Override
		public String get() {
			String suffix = suffix();
			return dump.envBinDir().resolve("pio" + suffix).toString(); //$NON-NLS-1$
		}

		private String suffix() {
			return Arrays
					.asList(Platform.getExtensionRegistry()
							.getExtensionPoint("org.platformio.eclipse.ide.core.installer.prerequisites") //$NON-NLS-1$
							.getExtensions()).stream() //
					.flatMap(extension -> Stream.of(extension.getConfigurationElements())) //
					.filter(element -> "executable".equals(element.getName())).findFirst() //$NON-NLS-1$
					.map(element -> element.getAttribute("extension")) //$NON-NLS-1$
					.get();
		}

	}

}
