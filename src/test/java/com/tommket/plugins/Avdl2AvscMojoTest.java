package com.tommket.plugins;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for the AVDL to AVSC conversion
 *
 * @author sameerbhadouria
 */
@Log
public class Avdl2AvscMojoTest extends AbstractMojoTestCase {
	private static final List<Path> AVSC_PATHS = Arrays.asList(
			Paths.get("Type.avsc"),
			Paths.get("Car.avsc"),
			Paths.get("nested", "Type.avsc"),
			Paths.get("nested", "Car.avsc"),
			Paths.get("nested", "Dealership.avsc")
	);

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.AbstractMojoTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.plexus.PlexusTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testBasicSchemaGeneration() throws Exception {
		File pomFile = getTestFile("src/test/resources/unit/avdl2avsc/pom.xml");

		Assert.assertNotNull(pomFile);
		Assert.assertTrue(pomFile.exists());

		Avdl2AvscMojo mojo = (Avdl2AvscMojo) lookupMojo("genschema", pomFile);

		Assert.assertNotNull(mojo);

		mojo.execute();
		//Verify all the types were created and parse successfully

		//base directory should exist
		AVSC_PATHS.forEach(this::assertAvscContents);
	}

	@SneakyThrows
	private void assertAvscContents(@NonNull final Path avscPath) {
		JSONAssert.assertEquals("Comparing expected and generated AVSC files: " + avscPath,
				loadExpectedAvscResource(avscPath),
				loadGeneratedAvsc(avscPath),
				true
		);
	}

	private String loadGeneratedAvsc(@NonNull final Path generatedAvscPath) throws IOException {
		final Path generatedFilePath = Paths.get("target", "generated-sources", "avsc", generatedAvscPath.toString());
		log.info("Loading generated AVSC file: " + generatedFilePath);
		return new String(Files.readAllBytes(generatedFilePath), StandardCharsets.UTF_8);
	}

	private String loadExpectedAvscResource(@NonNull final Path expectedResourcePath) throws URISyntaxException,
			IOException {
		return loadTestResource(Paths.get("/unit", "avdl2avsc", "expected", expectedResourcePath.toString()));
	}

	private String loadTestResource(@NonNull final Path resourcePath) throws URISyntaxException, IOException {
		log.info("Loading test resource: " + resourcePath);
		final Path resPath = Paths.get(this.getClass().getResource(resourcePath.toString()).toURI());
		return new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);
	}

}
