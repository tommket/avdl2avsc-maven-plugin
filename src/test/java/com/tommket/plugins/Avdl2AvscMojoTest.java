package com.tommket.plugins;

import java.io.File;

import org.apache.avro.Schema;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Test;

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
		
		Assert.assertNotNull(pomFile);
		Assert.assertTrue(pomFile.exists());
		
		Avdl2AvscMojo mojo = (Avdl2AvscMojo) lookupMojo("genschema", pomFile);
		
		Assert.assertNotNull(mojo);
		
		mojo.execute();
		//Verify all the types were created and parse successfully 

		//base directory should exist
        Assert.assertEquals("com.tommket.plugins.test.Type",new Schema.Parser().parse(new File("target/generated-sources/avsc/Type.avsc")).getFullName());
        Assert.assertEquals("com.tommket.plugins.test.Car",new Schema.Parser().parse(new File("target/generated-sources/avsc/Car.avsc")).getFullName());
		// nested should exist
        Schema.Parser parser = new Schema.Parser();
        Assert.assertEquals("com.tommket.plugins.test.Type",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Type.avsc")).getFullName());
        Assert.assertEquals("com.tommket.plugins.test.Car",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Car.avsc")).getFullName());
        Assert.assertEquals("com.tommket.plugins.test.nested.Dealership",new Schema.Parser().parse(new File("target/generated-sources/avsc/nested/Dealership.avsc")).getFullName());
	}
	
}
