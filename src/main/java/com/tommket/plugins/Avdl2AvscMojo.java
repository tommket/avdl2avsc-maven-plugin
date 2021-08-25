package com.tommket.plugins;

import lombok.NonNull;
import org.apache.avro.Schema;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.idl.ParseException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

/**
 * Generates Avro Schema from Avro IDL files.
 *
 * @author sameerbhadouria
 */
@Mojo(name = "genschema")
public class Avdl2AvscMojo extends org.apache.maven.plugin.AbstractMojo {

	public static final String AVDL_SUFFIX = ".avdl";

	@Parameter(defaultValue = "${basedir}/src/main/resources/avro/idl/")
	private File inputAvdlDirectory;

	@Parameter(defaultValue = "${basedir}/src/main/resources/avro/schema/")
	private File outputSchemaDirectory;

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.AbstractMojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		getLog().info("Finding .avdl files in directory: " + inputAvdlDirectory.toString());
		generateSchemas(inputAvdlDirectory, outputSchemaDirectory);

	}

	private void checkInputAndOutputDirectories(@NonNull final File inputDir, @NonNull final File outputDir) throws MojoExecutionException {
		if (!inputDir.exists()) {
			throw new MojoExecutionException("Cannot find inputAvdlDirectory: "
					+ inputDir);
		}
		if (!inputDir.isDirectory()) {
			throw new MojoExecutionException("inputAvdlDirectory: "
					+ inputDir + " is not a directory");
		}
		if (!outputDir.exists()) {
			if (!outputDir.mkdirs()) {
				throw new MojoExecutionException("outputSchemaDirectory: "
						+ outputDir + " cannot be created");
			}
		} else if (!outputDir.isDirectory()) {
			throw new MojoExecutionException("outputSchemaDirectory: "
					+ outputDir + " is not a directory");
		}
	}

	private void generateSchemas(@NonNull final File inputDir, @NonNull final File outputDir) throws MojoExecutionException {
		checkInputAndOutputDirectories(inputDir, outputDir);

		final File[] avdlFiles =
				inputDir.listFiles((dir, name) ->
						name.endsWith(AVDL_SUFFIX) || (!name.startsWith(".") && (new File(dir, name)).isDirectory()));

		if (avdlFiles != null) {
			for (final File avdlFile : avdlFiles) {
				if (avdlFile.getName().endsWith(AVDL_SUFFIX)) {
					generateSchema(avdlFile, outputDir);
				}
				if (avdlFile.isDirectory() && !avdlFile.getName().startsWith(".")) {
					generateSchemas(avdlFile, new File(outputDir, avdlFile.getName()));
				}
			}
		}
	}

	/**
	 * Generates the Avro schema avsc file from the specified avdl file into the specif.
	 *
	 * @param avdlFile        - {@link File} input file
	 * @param outputSchemaDir - {@link File} output d directory
	 * @throws MojoExecutionException - any exception that happens during execution
	 */
	private void generateSchema(@NonNull final File avdlFile, @NonNull final File outputSchemaDir) throws MojoExecutionException {
		getLog().info("Found avdl file: " + avdlFile.getPath());
		try (final Idl idlParser = new Idl(avdlFile)) {
			for (Schema schema : idlParser.CompilationUnit().getTypes()) {
				final String filename =
						Paths.get(outputSchemaDir.getAbsolutePath(), schema.getName() + ".avsc").toString();
				getLog().info("Creating schema file: " + filename);
				try (final FileOutputStream fileOutputStream = new FileOutputStream(filename);
				     final PrintStream printStream = new PrintStream(fileOutputStream)) {
					printStream.println(schema.toString(true));
				}
			}
		} catch (IOException | ParseException e) {
			getLog().error("Failed to generate Avro schema for file: " + avdlFile.getName(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
