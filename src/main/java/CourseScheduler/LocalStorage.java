package CourseScheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalStorage {
	
	/**
	 * Retrieves a storage file in the user's appdata/CourseScheduler folder.
	 *
	 * @param names The path names of the folders and file name.
	 * @return The File at the given path. Creates it if necessary.
	 */
	public static File get(String... names) throws IOException {
		// Use the local %APPDATA% variable
		String appdata = System.getenv("APPDATA");
		// Create a path within under %APPDATA%/CourseScheduler/
		Path path = Paths.get(appdata + "/CourseScheduler", names);
		// Create the parent folders.
		path.toFile().getParentFile().mkdir();
		// Create the file itself.
		path.toFile().createNewFile();
		
		return path.toFile();
	}
	
	/**
	 * Lists all the files in the LocalStorage directory.
	 */
	public static List<File> list() {
		// Use the local %APPDATA% variable
		String appdata = System.getenv("APPDATA");
		// Create a path within under %APPDATA%/CourseScheduler/
		Path path = Paths.get(appdata + "/CourseScheduler");
		
		if (!path.toFile().exists())
			return Collections.emptyList();
		else
			return Arrays.asList(path.toFile().listFiles());
	}
	
}
