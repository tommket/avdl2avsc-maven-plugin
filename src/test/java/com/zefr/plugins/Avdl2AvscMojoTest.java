package com.zefr.plugins;

import java.io.File;

import org.apache.avro.Schema;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import com.zefr.plugins.Avdl2AvscMojo;

/**
 * Test class for the AVDL to AVSC conversion
 * 
 * @author sameerbhadouria
 *
 */
public class Avdl2AvscMojoTest extends AbstractMojoTestCase {

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
		
		assertNotNull(pomFile);
		assertTrue(pomFile.exists());
		
		Avdl2AvscMojo mojo = (Avdl2AvscMojo) lookupMojo("genschema", pomFile);
		
		assertNotNull(mojo);
		
		mojo.execute();
		//Verify all the types were created and parse successfully 

		//base directory should exist
        assertEquals("com.zefr.plugins.test.Type",new Schema.Parser().parse(new File("target/generated-sources/avsc/Type.avsc")).getFullName());
        assertEquals("com.zefr.plugins.test.Car",new Schema.Parser().parse(new File("target/generated-sources/avsc/Car.avsc")).getFullName());
		// nested should exist
        Schema.Parser parser = new Schema.Parser();
        assertEquals("com.zefr.plugins.test.Type",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Type.avsc")).getFullName());
        assertEquals("com.zefr.plugins.test.Car",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Car.avsc")).getFullName());
        assertEquals("com.zefr.plugins.test.nested.Dealership",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Dealership.avsc")).getFullName());
	}
	
}
