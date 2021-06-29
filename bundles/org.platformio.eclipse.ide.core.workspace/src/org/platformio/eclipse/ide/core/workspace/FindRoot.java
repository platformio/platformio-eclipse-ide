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
package org.platformio.eclipse.ide.core.workspace;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

/**
 * An utility to find project's root by a single file it contains.
 */
public final class FindRoot implements Function<String, Optional<String>> {

	@Override
	public Optional<String> apply(String path) {
		Path segment = Paths.get(path);
		IsRoot isRoot = new IsRoot();
		while (segment.getParent() != null) {
			segment = segment.getParent();
			if (isRoot.test(segment.toString())) {
				return Optional.of(segment.toString());
			}
		}
		return Optional.empty();
	}

}
