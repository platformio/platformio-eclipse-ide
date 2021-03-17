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
package org.platformio.eclipse.ide.installer.python;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.net.RemoteResource;

public class PythonDistribution {

	private final Supplier<String> version;
	private final Environment environment;

	public PythonDistribution(Environment environment, Supplier<String> version) {
		this.version = version;
		this.environment = environment;
	}

	public void install(Path target) {
		try {
			Path packagePath = environment.cache().resolve("downloads").resolve("python3.tar.gz"); //$NON-NLS-1$ //$NON-NLS-2$
			new RemoteResource(source()) //
					.download(packagePath) //
					.extract(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String source() {
		return "https://dl.registry.platformio.org/download/platformio/tool/python-portable/" + version.get() //$NON-NLS-1$
				+ "/python-portable-" + distribution() + "-" + version.get() + ".tar.gz"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private String distribution() {
		return Arrays
				.asList(Platform.getExtensionRegistry()
						.getExtensionPoint("org.platformio.eclipse.ide.installer.prerequisites").getExtensions()) //$NON-NLS-1$
				.stream() //
				.flatMap(extension -> Stream.of(extension.getConfigurationElements())) //
				.filter(element -> "architecture".equals(element.getName())).findFirst() //$NON-NLS-1$
				.map(element -> element.getAttribute("url")) //$NON-NLS-1$
				.get();
	}

}
