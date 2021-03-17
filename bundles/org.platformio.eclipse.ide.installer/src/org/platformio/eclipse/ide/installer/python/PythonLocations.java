package org.platformio.eclipse.ide.installer.python;

import java.nio.file.Path;
import java.util.List;

public interface PythonLocations {

	List<Path> customLocations();

	List<String> names();

}
