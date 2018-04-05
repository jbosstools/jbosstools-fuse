package org.fusesource.ide.camel.model.service.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GrapeEnvironmentConfigurator {
	
	public Path getGrapeFolderInsideTempFolder() throws IOException {
		Path grapeFolder = Paths.get(System.getProperty("java.io.tmpdir"),"grape");
		if(!grapeFolder.toFile().exists()){
			Files.createDirectory(grapeFolder);
		}
		Files.createTempDirectory(grapeFolder,"m2repo");
		return grapeFolder;
	}
}
