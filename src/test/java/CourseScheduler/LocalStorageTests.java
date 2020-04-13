package CourseScheduler;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalStorageTests {
	
	@Test
	public void createsFiles() throws IOException {
		File file = LocalStorage.get("test_file.txt");
		assertTrue(file.exists());
		assertEquals(file.getName(), "test_file.txt");
		assertEquals("CourseScheduler", file.getParentFile().getName());
		file.deleteOnExit();
	}
	
	@Test
	public void listFiles() throws IOException {
		File file = LocalStorage.get("test_file.txt");
		List<File> list = LocalStorage.list();
		assertTrue(list.contains(file));
	}
	
}
