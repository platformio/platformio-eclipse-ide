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
package org.platformio.eclipse.ide.base.installer.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.base.installer.json.Distribution;
import org.platformio.eclipse.ide.base.installer.net.RemoteResource;
import org.platformio.eclipse.ide.base.paths.CacheDirectory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public final class PythonDistribution {

	private static final String DISTRIBUTION_SITE_URL = "https://api.registry.platformio.org/v3/packages/platformio/tool/python-portable"; //$NON-NLS-1$

	public void install(Path target) {
		try {
			Path packagePath = new CacheDirectory().get().resolve("downloads").resolve("python3.tar.gz"); //$NON-NLS-1$ //$NON-NLS-2$
			new RemoteResource(distributionUrl()) //
					.download(packagePath) //
					.extract(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String distributionUrl() throws IOException {
		List<Distribution> readDistributives = readDistributives();
		for (Distribution distributive : readDistributives) {
			for (String supported : distributive.system()) {
				if (supported.equals(system())) {
					return distributive.url();
				}
			}
		}
		throw new IOException();
	}

	private List<Distribution> readDistributives() throws IOException {
		Path target = new CacheDirectory().get().resolve("info"); //$NON-NLS-1$
		new RemoteResource(DISTRIBUTION_SITE_URL).download(target);
		try (BufferedReader fileReader = Files.newBufferedReader(target);) {
			JsonElement element = JsonParser.parseReader(fileReader);
			return new Gson().fromJson(
					element.getAsJsonObject().get("version").getAsJsonObject().get("files").getAsJsonArray(), //$NON-NLS-1$ //$NON-NLS-2$
					new TypeToken<List<Distribution>>() {
					}.getType());

		}

	}

	private String system() {
		return Arrays
				.asList(Platform.getExtensionRegistry()
						.getExtensionPoint("org.platformio.eclipse.ide.base.installer.prerequisites").getExtensions()) //$NON-NLS-1$
				.stream() //
				.flatMap(extension -> Stream.of(extension.getConfigurationElements())) //
				.filter(element -> "architecture".equals(element.getName())).findFirst() //$NON-NLS-1$
				.map(element -> element.getAttribute("url")) //$NON-NLS-1$
				.get();
	}

}
