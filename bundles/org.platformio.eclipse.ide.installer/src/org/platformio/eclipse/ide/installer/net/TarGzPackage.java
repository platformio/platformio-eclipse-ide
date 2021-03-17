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
package org.platformio.eclipse.ide.installer.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public final class TarGzPackage {

	private final Path location;

	public TarGzPackage(Path location) {
		this.location = location;
	}

	public void extract(Path target) throws IOException {
		File out = target.toFile();
		try (TarArchiveInputStream fin = new TarArchiveInputStream(
				new GzipCompressorInputStream(Files.newInputStream(location)))) {
			TarArchiveEntry entry;
			while ((entry = fin.getNextTarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				File curfile = new File(out, entry.getName());
				File parent = curfile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				try (OutputStream o = new FileOutputStream(curfile)) {
					IOUtils.copy(fin, o);
				}
			}
		}
	}
}
