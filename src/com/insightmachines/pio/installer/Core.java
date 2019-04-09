package com.insightmachines.pio.installer;

import static com.insightmachines.pio.installer.Misc.IS_WINDOWS;
import static com.insightmachines.pio.installer.Misc.runCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Core {

	private static final Pattern VERSION_PATTERN = Pattern.compile("[\\d+\\.]+.*$");

	public static Path getHomeDir() {
		String userHomeDir = System.getProperty("user.home");
		Path result = Misc.getEnv("PLATFORMIO_HOME_DIR").map( s -> Paths.get(s) ).orElse( Paths.get(userHomeDir, ".platformio") );
		if ( IS_WINDOWS ) {
			// Make sure that all path characters have valid ASCII codes.
			if ( result.toAbsolutePath().toString().chars().anyMatch( ch -> ch > 127 ) ) {
				// If they don't, put the pio home directory into the root of the disk.
				result = result.getRoot().resolve(".platformio");
			}
		}
		return result;
	}
	
	public static Path getEnvDir() {
		return getHomeDir().resolve("penv");
	}

	public static Path getEnvBinDir() {
		return getEnvDir().resolve(IS_WINDOWS ? "Scripts" : "bin");
	}

	public static Path getCacheDir() {
		Path dir = getHomeDir().resolve(".cache");
		if (!Files.isDirectory(dir)) {
			try {
				Files.createDirectories(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dir;
	}

	public static String getVersion() {
		return runCommand("platformio", Arrays.asList("--version"), (code, stdOut, stdErr) -> {
			if (code == 0) {
				Matcher m = VERSION_PATTERN.matcher(stdOut.trim());
				if (m.find()) {
					return m.group(0);
				}
				throw new RuntimeException("Failed to find platformio version");
			}
			throw new RuntimeException(stdErr);
		});
	}

	public static void runPIOCommand(List<String> args, RunCommandOutputHandler<Void> handler) {
		List<String> baseArgs = new ArrayList<>();
		baseArgs.add("-f");

		getEnv("PLATFORMIO_CALLER").ifPresent(v -> {
			baseArgs.add("-c");
			baseArgs.add(v);
		});
		baseArgs.addAll(args);

		runCommand("platformio", baseArgs, handler);
	}

	private static Optional<String> getEnv(String key) {
		return Optional.ofNullable(System.getenv(key));
	}

}