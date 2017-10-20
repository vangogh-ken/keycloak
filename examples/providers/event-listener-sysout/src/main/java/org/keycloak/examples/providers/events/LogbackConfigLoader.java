package org.keycloak.examples.providers.events;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 
 *
 */
public class LogbackConfigLoader {

	public static void main(String[] args) throws IOException, JoranException {
		load("/logback.xml");

		Logger logger = LoggerFactory.getLogger(LogbackConfigLoader.class);
		logger.debug("现在的时间是 {}", new Date().toString());
		logger.info(" This time is {}", new Date().toString());
		logger.warn(" This time is {}", new Date().toString());
		logger.error(" This time is {}", new Date().toString());
		System.out.println(1);

	}

	public static void load(String configFile) throws IOException, JoranException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		String s = LogbackConfigLoader.class.getResource(configFile).getPath();
		System.out.println(s);
		File externalConfigFile = new File(s);
		if (!externalConfigFile.exists()) {
			throw new IOException("Logback External Config File Parameter does not reference a file that exists");
		} else {

			if (!externalConfigFile.isFile()) {
				throw new IOException("Logback External Config File Parameter exists, but does not reference a file");
			} else {
				if (!externalConfigFile.canRead()) {
					throw new IOException("Logback External Config File exists and is a file, but cannot be read.");

				} else {
					JoranConfigurator configurator = new JoranConfigurator();
					configurator.setContext(lc);
					lc.reset();
					configurator.doConfigure(s);
					StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
				}
			}
		}
	}
}
