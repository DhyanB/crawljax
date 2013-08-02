package com.crawljax.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.crawljax.core.CrawljaxException;
import com.crawljax.test.BrowserTest;
import com.crawljax.util.DomUtils;
import com.google.common.collect.ImmutableSortedSet;

@Category(BrowserTest.class)
public class WebDriverBackedEmbeddedBrowserTest {

	private final File index = new File("src/test/resources/site/iframe/index.html");

	@Test
	public void testGetDocument() throws Exception {
		// TODO Stefan; refactor out the direct use of the FirefoxDriver
		WebDriverBackedEmbeddedBrowser driver =
		        WebDriverBackedEmbeddedBrowser.withDriver(new FirefoxDriver(),
		                ImmutableSortedSet.<String> of(), 100, 100);

		Document doc;
		try {
			driver.goToUrl(URI.create("file://" + index.getAbsolutePath()));

			doc = DomUtils.asDocument(driver.getStrippedDom());
			NodeList frameNodes = doc.getElementsByTagName("IFRAME");
			assertEquals(2, frameNodes.getLength());

			doc = DomUtils.asDocument(driver.getStrippedDomWithoutIframeContent());
			frameNodes = doc.getElementsByTagName("IFRAME");
			assertEquals(2, frameNodes.getLength());
		} finally {
			driver.close();
		}

	}

	@Test
	public void saveScreenShot() throws CrawljaxException, IOException {
		// TODO Stefan; refactor out the direct use of the FirefoxDriver
		WebDriverBackedEmbeddedBrowser browser =
		        WebDriverBackedEmbeddedBrowser.withDriver(new FirefoxDriver(),
		                ImmutableSortedSet.<String> of(), 500, 500);

		File f = File.createTempFile("webdriverfirefox-test-screenshot", ".png");
		if (!f.exists()) {
			browser.goToUrl(URI.create("file://" + index.getAbsolutePath()));
			browser.saveScreenShot(f);
			assertTrue(f.exists());
			assertTrue(f.delete());
		}

		browser.close();
	}
}
