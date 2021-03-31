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
package org.platformio.eclipse.ide.installer.json;

import java.lang.reflect.Type;
import java.nio.file.Paths;

import org.platformio.eclipse.ide.home.json.EnvironmentPaths;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class PathsDeserializer implements JsonDeserializer<EnvironmentPaths> {

	@Override
	public EnvironmentPaths deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		return new EnvironmentPaths(Paths.get(object.get("penv_dir").getAsString()), //$NON-NLS-1$
				Paths.get(object.get("penv_bin_dir").getAsString())); //$NON-NLS-1$
	}

}
