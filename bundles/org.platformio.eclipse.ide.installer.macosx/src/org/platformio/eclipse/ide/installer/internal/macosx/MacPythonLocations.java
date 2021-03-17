package org.platformio.eclipse.ide.installer.internal.macosx;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.platformio.eclipse.ide.installer.python.PythonLocations;

public class MacPythonLocations implements PythonLocations {

	@Override
	public List<Path> customLocations() {
		return Collections.emptyList();
	}

	@Override
	public List<String> names() {
		return Arrays.asList("python", "python3", "python39"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
