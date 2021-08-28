package com.tommket.plugins;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Test class for the AVDL to AVSC conversion
 *
 * @author sameerbhadouria
 */
@Log
@MavenJupiterExtension
@MavenGoal("avdl2avsc:genschema")
public class Avdl2AvscIT {
	private static final List<Path> AVSC_PATHS = Arrays.asList(
			Paths.get("Type.avsc"),
			Paths.get("Car.avsc"),
			Paths.get("nested", "Type.avsc"),
			Paths.get("nested", "Car.avsc"),
			Paths.get("nested", "Dealership.avsc")
	);

	@MavenTest
	public void testBasicSchemaGeneration(final MavenExecutionResult result) throws Exception {
		assertThat(result).isSuccessful();

		//assert files
		AVSC_PATHS.forEach(avscPath -> assertAvscContents(result, avscPath));
	}

	@SneakyThrows
	private void assertAvscContents(@NonNull final MavenExecutionResult result, @NonNull final Path avscPath) {
		JSONAssert.assertEquals("Comparing expected and generated AVSC files: " + avscPath,
				loadExpectedAvscResource(avscPath),
				loadGeneratedAvsc(result, avscPath),
				true
		);
	}

	private String loadGeneratedAvsc(@NonNull final MavenExecutionResult result,
	                                 @NonNull final Path generatedAvscPath) throws IOException {
		final Path generatedFilePath = Paths.get(
				result.getMavenProjectResult().getTargetProjectDirectory().toString()
				, "target"
				, "avsc"
				, generatedAvscPath.toString()
		);
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
