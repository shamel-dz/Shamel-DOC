package com.logicaldoc.util.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bouncycastle.cms.CMSException;
import org.junit.Test;

public class P7MTest {

	@Test
	public void testExtractOriginalFile() throws IOException, CMSException {
		File file = new File("target/text.xml");
		try {
			P7M p7m = new P7M(this.getClass().getResourceAsStream("/test.p7m"));
			assertNotNull(p7m.getCms());
			p7m.extractOriginalFile(file);
			String content = FileUtil.readFile(file);
			assertTrue(content.contains("p:FatturaElettronica xmlns"));
		} finally {
			FileUtil.strongDelete(file);
		}

		try {
			new P7M((byte[]) null).read();
			fail("no exception if null content was specified?");
		} catch (Exception e) {
			FileUtil.strongDelete(file);
		}

		try {
			P7M p7m = new P7M(ResourceUtil.readAsBytes("/test.p7m"));
			assertNotNull(p7m.getCms());
			p7m.extractOriginalFile(file);
			String content = FileUtil.readFile(file);
			assertTrue(content.contains("p:FatturaElettronica xmlns"));
		} finally {
			FileUtil.strongDelete(file);
		}

		try {
			final File source = new File("src/test/resources/test.p7m");
			P7M p7m = new P7M(source);
			p7m.setFile(source);
			assertNotNull(p7m.getFile());
			p7m.read();
			p7m.extractOriginalFile(file);
			String content = FileUtil.readFile(file);
			assertTrue(content.contains("p:FatturaElettronica xmlns"));
		} finally {
			FileUtil.strongDelete(file);
		}

		P7M p7m = new P7M(this.getClass().getResourceAsStream("/test.p7m"));
		try (InputStream is = p7m.extractOriginalFileStream();) {
			String content = IOUtil.readStream(is);
			assertTrue(content.contains("p:FatturaElettronica xmlns"));
		} finally {
			FileUtil.strongDelete(file);
		}
	}
}