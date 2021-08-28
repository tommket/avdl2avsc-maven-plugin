avdl2avsc-maven-plugin
======================

Converts Avro IDL files (.avdl) to Avro schema files (.avsc).

Motivation:
============
The avro-maven-plugin allows you to code generate Java classes from .avdl, .avpr or .apvsc files. Writing Avro schema
files can be quite tedious with the repeated json schema syntax. It also has limited support for importing other avsc
files. You have to define an import directory as a configuration parameter in the build plugin.

Avro IDL provides a concise way to describe records and rpc messages. It convenient import functionality.

This plugin leverages the power of IDL to make your life easier.

Note:
=====
Defining RPC messages in the avdl will be of no use in this case as schema files do not contain this information.

Release versioning:
===================
The versioning will use the version number of the underlying avro-compiler dependency, that uses the version of its AVRO
specification.

Usage:
======

Add the following to your maven repositories plugin:

TODO: Get to Maven Central, via a release pipeline job.

Add the following to the maven build plugins:

```xml

<build>
	<plugins>
		<plugin>
			<groupId>com.tommket.plugins</groupId>
			<artifactId>avdl2avsc-maven-plugin</artifactId>
			<version>1.10.2</version>
			<executions>
				<execution>
					<phase>generate-sources</phase>
					<goals>
						<goal>genschema</goal>
					</goals>
					<configuration>
						<inputAvdlDirectory>${basedir}/src/main/resources/avro/idl</inputAvdlDirectory>
						<outputSchemaDirectory>${basedir}/target/generated-sources/avro/schema</outputSchemaDirectory>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```
