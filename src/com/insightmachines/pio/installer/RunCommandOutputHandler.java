package com.insightmachines.pio.installer;

@FunctionalInterface
public interface RunCommandOutputHandler<T> {
	T handle(int code, String stdOut, String stdErr);
}
