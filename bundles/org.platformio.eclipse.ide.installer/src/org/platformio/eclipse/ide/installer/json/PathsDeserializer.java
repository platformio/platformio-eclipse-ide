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
