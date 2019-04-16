package com.insightmachines.pio.installer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class CoreTests {

//	@Test
	void should_find_platformio_version() {
		assertNotNull(Core.getVersion());		
	}
	
//	@Test
	void should_retrieve_content_length() {
		int contentLength = Helpers.getContentLength("https://files.pythonhosted.org/packages/45/ae/8a0ad77defb7cc903f09e551d88b443304a9bd6e6f124e75c0fbbf6de8f7/pip-18.1.tar.gz");
		assertEquals(1259370, contentLength);		
	}
	
	@Test
	void test() throws IOException {
		PlatformIoCore platformIoCore = new PlatformIoCore();
		platformIoCore.install();
	}

}
