import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FolderManager {

	public static void main(String[] args) throws IOException {

		Map<String, String> prefixes = Map.of("test", "/Users/glodimendes/Desktop/Test/", "sim",
				"/Users/glodimendes/Desktop/Sim/", "algdat", "/Users/glodimendes/Documents/Pessoal/ETH/AlgDat/",
				"linalg", "/Users/glodimendes/Documents/Pessoal/ETH/LinAlg/", "eprog",
				"/Users/glodimendes/Documents/Pessoal/ETH/EProg/", "diskmat",
				"/Users/glodimendes/Documents/Pessoal/ETH/DiskMat/");

		List<String> ignored = Arrays.asList(".ds_store");
		Set<String> ignore = new HashSet<String>(ignored);

		String origin = "/Users/glodimendes/Desktop/Origin";

		System.out.println(distribute(origin, prefixes, ignore));

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

		if (!isEmpty(originPath, ignore)) {
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