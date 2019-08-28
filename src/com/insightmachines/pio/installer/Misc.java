package com.insightmachines.pio.installer;

import static com.insightmachines.pio.installer.Core.getEnvBinDir;
import static com.insightmachines.pio.installer.Core.getEnvDir;
import static com.insightmachines.pio.installer.Core.getHomeDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class Misc {

	private static final Logger LOGGER = Logger.getLogger(Misc.class.getName());

	public final static boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

	public final static boolean IS_64_BIT;

	static {
		if (System.getProperty("os.name").contains("Windows")) {
			IS_64_BIT = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			IS_64_BIT = (System.getProperty("os.arch").indexOf("64") != -1);
		}
	}

	public static Path getPythonExecutable(boolean useBuiltinPIOCore, Path... customDirs) {
		List<String> exeNames = IS_WINDOWS ? Arrays.asList("python.exe")
				: Arrays.asList("python2.7", "python2", "python");
		List<Path> locations = new ArrayList<Path>(Arrays.asList(customDirs));

		if (useBuiltinPIOCore) {
			locations.add(getEnvBinDir());
			locations.add(getEnvDir()); // conda
		}
		if (IS_WINDOWS) {
			// isolated Python 2.7 in PlatformIO Home directory
			locations.add(getHomeDir().resolve("python27"));
		}

		// extend with paths from env.PATH
		Collections.list(new StringTokenizer(System.getenv("PATH"), File.pathSeparator)).forEach(item -> {
			String itemString = ((String) item).replaceAll("\"", "");
			Path itemPath = Paths.get(itemString);
			if (!locations.contains(item)) {
				locations.add(itemPath);
			}
		});
		
		for (Path location : locations) {
		    for (String exeName : exeNames) {		    	
		      Path executable = location.resolve(exeName).toAbsolutePath();
		      if ( Files.exists(executable) && isPython2(executable) ) {
		    	  return executable;
		      }
		    }
		  }

		return null;
	}

//export async function getPythonExecutable(useBuiltinPIOCore=true, customDirs = undefined) {
//  const exenames = IS_WINDOWS ? ['python.exe'] : ['python2.7', 'python2', 'python'];
//  const locations = customDirs || [];
//
//  if (useBuiltinPIOCore) {
//    locations.push(getEnvBinDir());
//    locations.push(getEnvDir()); // conda
//  }
//  if (IS_WINDOWS) {
//    // isolated Python 2.7 in PlatformIO Home directory
//    locations.push(path.join(getHomeDir(), 'python27'));
//  }
//  // extend with paths from env.PATH
//  process.env.PATH.split(path.delimiter).forEach(item => {
//    if (!locations.includes(item)) {
//      locations.push(item);
//    }
//  });
//
//  for (const location of locations) {
//    for (const exename of exenames) {
//      const executable = path.normalize(path.join(location, exename)).replace(/"/g, '');
//      if (fs.isFileSync(executable) && (await isPython2(executable))) {
//        return executable;
//      }
//    }
//  }
//  return undefined;
//}

	public static boolean isPython2(Path executable) {
		List <String> pythonLines = new ArrayList<String>();
		pythonLines.add("import os, sys");
		pythonLines.add("assert sys.platform != \"cygwin\"");
		pythonLines.add("assert not sys.platform.startswith(\"win\") or not any(s in sys.executable.lower() for s in (\"msys\", \"mingw\", \"emacs\"))");
		pythonLines.add("assert not sys.platform.startswith(\"win\") or os.path.isdir(os.path.join(sys.prefix, \"Scripts\"))");
		pythonLines.add("assert sys.version_info < (3, 0, 0)");
		
		if (IS_WINDOWS) {
			pythonLines.add("assert sys.version_info >= (2, 7, 9)");
		} else {
		    pythonLines.add("assert sys.version_info >= (2, 7, 5)");
		}
		List<String> args = Arrays.asList("-c", String.join(";", pythonLines));

		return runCommand(
			executable.toString(), 
			args, 
			(code, stdout, stderr) -> code == 0
		);
	}

//function isPython2(executable) {
//  const pythonLines = [
//    'import os, sys',
//    'assert sys.platform != "cygwin"',
//    'assert not sys.platform.startswith("win") or not any(s in sys.executable.lower() for s in ("msys", "mingw", "emacs"))',
//    'assert not sys.platform.startswith("win") or os.path.isdir(os.path.join(sys.prefix, "Scripts"))',
//    'assert sys.version_info < (3, 0, 0)'
//  ];
//  if (IS_WINDOWS) {
//    pythonLines.push('assert sys.version_info >= (2, 7, 9)');
//  } else {
//    pythonLines.push('assert sys.version_info >= (2, 7, 5)');
//  }
//  const args = ['-c', pythonLines.join(';')];
//  return new Promise(resolve => {
//    runCommand(
//      executable,
//      args,
//      code => {
//        resolve(code === 0);
//      }
//    );
//  });
//}

	public static int runCommand(String... commandWithArgs) {
		Integer code = runCommand(Arrays.asList(commandWithArgs), (c, o, e) -> c);
		return code != null ? code : -100;
	}

	public static <T> T runCommand(Path command, List<String> args, RunCommandOutputHandler<T> handler) {
		return runCommand(command.toAbsolutePath().toString(), args, handler);
	}

	public static <T> T runCommand(String command, List<String> args, RunCommandOutputHandler<T> handler) {
		List<String> commandWithArgs = new ArrayList<String>(args.size() + 1);
		commandWithArgs.add(command);
		commandWithArgs.addAll(args);
		return runCommand(commandWithArgs, handler);
	}

	public static <T> T runCommand(List<String> commandWithArgs, RunCommandOutputHandler<T> handler) {
		return runCommand(commandWithArgs, new HashMap<String, String>(), handler);
	}

	public static <T> T runCommand(List<String> commandWithArgs, Map<String, String> environment,
			RunCommandOutputHandler<T> handler) {
		LOGGER.info("Running: " + String.join(" ", commandWithArgs));
		ProcessBuilder pb = new ProcessBuilder(commandWithArgs);
		pb.environment().putAll(environment);
		Process p = null;
		try {
			p = pb.start();
			if (handler != null) {
				StringBuilder stdOutBuilder = new StringBuilder();
				try (@SuppressWarnings("resource") Scanner s = new Scanner(p.getInputStream()).useDelimiter("\n")) {
					while (s.hasNext()) stdOutBuilder.append(s.next());
				}
				int code = p.waitFor();
				return handler.handle(code, stdOutBuilder.toString(), ""); // TODO: Consider running two scanners independently for stdout and stderr  
			} else {
				return null;
			}
		} catch (RuntimeException ex1) {
			throw ex1;
		} catch (Exception e) {
//			LOGGER.log(Level.SEVERE, "Failed to run command: " + String.join(" ", commandWithArgs), e);
			throw new RuntimeException("Failed to run command: " + String.join(" ", commandWithArgs), e);
		}
	}

	public static Optional<String> getEnv(String key) {
		return Optional.ofNullable(System.getenv(key));
	}

}
