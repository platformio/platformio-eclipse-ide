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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

public final class OpenFileTest {

	@Test
	public void insideWorkspace() {
		try {
			assertTrue(fileInWorkspace().isPresent());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void outsideWorkspace() {
		try {
			assertTrue(fileNotInWorkspace().isPresent());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void resourceNotInProject() {
		try {
			assertTrue(fileNotInProject().isEmpty());
		} catch (Exception e) {
			fail();
		}
	}

	private Optional<IFile> fileNotInProject() throws URISyntaxException, IOException {
		String file = Paths.get(sample()).getParent().toString();
		return new OpenFile(file).get();
	}

	private Optional<IFile> fileNotInWorkspace() throws URISyntaxException, IOException {
		String file = Paths.get(sample()).resolve("src").resolve("main.cpp").toString(); //$NON-NLS-1$ //$NON-NLS-2$
		return new OpenFile(file).get();
	}

	private String sample() throws URISyntaxException, IOException {
		URL resource = FrameworkUtil.getBundle(getClass()).getResource("sample/third"); //$NON-NLS-1$
		return new File(FileLocator.toFileURL(resource).getPath()).getAbsolutePath();
	}

	private Optional<IFile> fileInWorkspace() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("temp_project"); //$NON-NLS-1$
		if (!project.exists())
			project.create(null);
		if (!project.isOpen())
			project.open(null);
		IFolder folder = project.getFolder("temp_folder"); //$NON-NLS-1$
		if (!folder.exists())
			folder.create(IResource.NONE, true, null);
		IFile file = folder.getFile(new Path("temporal")); //$NON-NLS-1$
		if (!file.exists())
			file.create(new ByteArrayInputStream(new byte[0]), IResource.NONE, null);
		Optional<IFile> found = new OpenFile(file.getLocation().toString()).get();
		project.delete(true, false, null);
		return found;
	}

}
