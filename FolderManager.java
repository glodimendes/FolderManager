import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FolderManager {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		//testing
		File prefixesFile = new File("/Users/glodimendes/Desktop/Origin/.prefixes.txt");
		
		File ignoreFile = new File("/Users/glodimendes/Desktop/Origin/.ignore.txt");
		
		String origin = "/Users/glodimendes/Desktop/Origin";
		
		
		Map<String, String> prefixes = readPrefixes(prefixesFile);
		
		Set<String> ignore = readIgnored(ignoreFile);

		System.out.println(distribute(origin, prefixes, ignore));
		
		

	}
	
	public static Map<String, String> readPrefixes(File prefixes) throws FileNotFoundException {
		Scanner sc = new Scanner(prefixes);
		
		Map<String, String> out = new HashMap<String, String>();
		
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.equals("")) continue;
			
			//splits line, first part is the prefix
			String key = line.split(" ")[0];
			
			//rest of the line is the path that needs to be addressed to
			int length = key.length();	 //gets at which point of the line the prefix goes.
			
			String value = line.substring(length + 1);
			
			out.put(key, value);
			
		}
		
		
		return out;
	}
	
	public static Set readIgnored(File ignored) throws FileNotFoundException {
		Scanner sc = new Scanner(ignored);
		
		Set<String> ignore = new HashSet<String>();
		
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			ignore.add(line);
		}
		
		return ignore;
	}

	public static boolean isEmpty(String originPath, Set ignore) {
		File origin = new File(originPath);

		if (origin.listFiles().length == 0 || checkIgnored(ignore, origin)) {
			return true; // true when there are no files to distribute
		}

		return false;
	}

	public static boolean checkIgnored(Set ignore, File origin) {
		for (File file : origin.listFiles()) {

			String str = file.getPath();

			str = str.split("/")[str.split("/").length - 1];

			if (!ignore.contains(str.toLowerCase()))
				return false; // false when at least 1 file is not ignored
		}

		return true; // true when all files are ignored
	}

	public static String distribute(String originPath, Map prefixes, Set ignore) throws IOException {

		File origin = new File(originPath);

		ArrayList<String> errors = new ArrayList<String>();

		if (!isEmpty(originPath, ignore)) { //checks if the directory is not empty
			
			for (String file : origin.list()) {
				String pathFrom = originPath + "/" + file; // defines origin

				Path pFrom = Paths.get(pathFrom);

				String prefix = file.split(" ")[0].toLowerCase(); // gets prefix

				if (prefixes.containsKey(prefix)) {
					String pathTo = (String) prefixes.get(prefix); // uses prefix to get target
					pathTo = pathTo + file; // defines path to

					Path pTo = Paths.get(pathTo);

					Files.move(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);

				} else {
					if (!ignore.contains(prefix))
						errors.add(prefix);
				}
			}

		} else { // folder is empty
			return "No files to distribute";
		}

		if (errors.size() == 0) {
			return "Files distributed successfully";
			
		} else { // indicate error in prefix naming
			String out = "The following prefixes don't exist:";
			for (String prefix : errors) {
				out = out + "\n" + prefix;
			}

			return out;
		}

	}
}