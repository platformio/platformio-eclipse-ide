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
package org.platformio.eclipse.ide.home.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class OpenEditorData {

	private final String path;
	private final int line;

	public OpenEditorData(JsonElement element) {
		JsonObject result = element.getAsJsonObject().get("params").getAsJsonObject(); //$NON-NLS-1$
		this.path = result.get("path").getAsString(); //$NON-NLS-1$
		this.line = result.get("line").getAsInt(); //$NON-NLS-1$
	}

	public String path() {
		return path;
	}

	public int line() {
		return line;
	}

}
