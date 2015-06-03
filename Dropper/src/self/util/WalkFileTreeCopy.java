package self.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.EnumSet;
import java.nio.file.StandardCopyOption;

import org.apache.commons.codec.digest.DigestUtils;

public class WalkFileTreeCopy {

	protected MetadataReader mdr = null;

	public void WalkTheDirTree() throws IOException {
		final Path baseDir = Paths.get("C:\\MyTemp");
		final Path sourceDir = baseDir.resolve("samsung_note");

		// Create reader
		mdr = new MetadataReader();

		final Path targetDir = Paths.get("C:\\MyTemp\\samgsung_note_bckp\\Photos");
		Files.walkFileTree(sourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path target = targetDir.resolve(sourceDir.relativize(dir));
				/*
				 * try { Files.copy(dir, target); } catch
				 * (FileAlreadyExistsException e) { if
				 * (!Files.isDirectory(target)) throw e; }
				 */
				System.out.println(" Dir =" + dir.getFileName());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// Files.copy(file,
				// targetDir.resolve(sourceDir.relativize(file)));

				// System.out.println("Source Absolute Parent =" +
				// file.getParent());
				// System.out.println(" Source Relative Path =" +
				// sourceDir.relativize(file.getParent()));
				System.out.println(" File =" + file.getFileName());
				String fname = file.getFileName().toString();
				String secondaryFileNameExt = fname.substring(fname.lastIndexOf(".") + 1, fname.length());
				if ((secondaryFileNameExt.compareToIgnoreCase("jpg") == 0 || secondaryFileNameExt.compareToIgnoreCase("jpeg") == 0)) {

					String exifTimeStamp = getEXIFTimeStampOfFile(file.toFile());
					System.out.println("exifTimeStamp=" + exifTimeStamp);
					if (!Files.exists(targetDir.resolve(exifTimeStamp))) {

						Files.createDirectory(targetDir.resolve(exifTimeStamp));
						System.out.println(exifTimeStamp + " Dir Created.");

					}

					Path targetFile = targetDir.resolve(exifTimeStamp).resolve(file.getFileName());
					if (!Files.exists(targetFile)) {

						Files.move(file, targetFile, StandardCopyOption.ATOMIC_MOVE);
						System.out.println(targetFile.getFileName() + " File  Moved.");

					} else {

						String exifCreateTimeOfTarget = getEXIFTimeStampOfFile(targetFile.toFile());
						System.out.println("exifCreateTimeOfTarget=" + exifCreateTimeOfTarget);
						String exifCreateTimeOfSource = getEXIFTimeStampOfFile(file.toFile());
						System.out.println("exifCreateTimeOfSource" + exifCreateTimeOfSource);
						if (exifCreateTimeOfTarget.compareTo(exifCreateTimeOfSource) == 0) {

							String targetMD5 = checkMD5HashCode(targetFile.toFile());
							System.out.println("targetMD5=" + targetMD5 + " File=" + targetFile.toFile().getAbsolutePath());
							String sourceMD5 = checkMD5HashCode(file.toFile());
							System.out.println("sourceMD5=" + sourceMD5 + " File=" + file.toFile().getAbsolutePath());
							if (sourceMD5.compareTo(targetMD5) == 0) {
								System.out.println(targetFile.getFileName() + " File  exists! Skipping the move.");
							} else {
								String conflictResolvedFileName = filterAndGetConflictResolvedFileNames(targetFile);
								System.out.println("conflictResolvedFileName=" + conflictResolvedFileName);

								// targetFile =
								// targetDir.resolve(exifTimeStamp).resolve(conflictResolvedFileName);
								// Files.move(file, targetFile,
								// StandardCopyOption.ATOMIC_MOVE );
								System.out.println(targetFile.getFileName() + " File  Moved with New Name. --TODO.1");

							}
						}

						else {
							String conflictResolvedFileName = filterAndGetConflictResolvedFileNames(targetFile);
							System.out.println("conflictResolvedFileName=" + conflictResolvedFileName);
							System.out.println(targetFile.getFileName() + " File  Moved with New Name. --TODO.2");
						}
					}
				}
				/*
				 * Get the time stamp of the file Move to targetDir/TIMESTAMP
				 */
				return FileVisitResult.CONTINUE;
			}
		});

	}

	// Creating a DirectoryStream which accepts only filenames ending with
	// '.jpg'
	public String filterAndGetConflictResolvedFileNames(Path targetFile) throws IOException {

		/*
		 * //Path p =
		 * FileSystems.getDefault().getPath("C:\\MyWork\\JPEGTestData\\D1");
		 * 
		 * try (DirectoryStream<Path> ds = Files.newDirectoryStream(p, "*.jpg"))
		 * { for (Path p1 : ds) { // Iterate over the paths in the directory and
		 * print filenames System.out.println(p1.getFileName()); } } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
		String resolvedFileName = null;
		/*
		 * Path p = Paths.get("C:\\MyWork\\JPEGTestData\\S1"); String
		 * currentFileName = "IMG_0469*";
		 */
		Path p = targetFile.getParent();
		String currentFileName = targetFile.getFileName().toString();
		String baseFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
		System.out.println("baseName=" + baseFileName);
		// DirectoryStream<Path> ds = Files.newDirectoryStream(p, "*.jpg");
		DirectoryStream<Path> ds = Files.newDirectoryStream(p, baseFileName + "*");
		for (Path p1 : ds) {
			// Iterate over the paths in the directory and print filenames
			System.out.println(p1.getFileName().toString());
			// Pattern uName =
			// Pattern.compile("(.+)(\\s-\\sCopy(\\s\\((\\d?)\\))).*");
			Pattern uName = Pattern.compile(baseFileName + "(\\s-\\sCopy(\\s\\((\\d?)\\))?.*)?(.JPG)");
			Matcher mUname = uName.matcher(p1.getFileName().toString());

			if (mUname.find()) {
				System.out.println("Found[0] value: " + mUname.group(0));
				System.out.println("Found[1] value: " + mUname.group(1));
				System.out.println("Found[2] value: " + mUname.group(2));
				System.out.println("Found[3] value: " + mUname.group(3));
				System.out.println("Found[4] value: " + mUname.group(4));

			} else {
				System.out.println("NO MATCH");
			}

		}
		return resolvedFileName;

	}

	public String getEXIFTimeStampOfFile(File filename) throws IOException {
		// might need to change the format
		Date exifDate = null;
		try {
			exifDate = mdr.getCreateDateAsFormatedString(filename);

		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new IOException(pe.getMessage());
		}

		if (exifDate != null) {
			DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
			String exifCreateDate = formatter2.format(exifDate);
			return exifCreateDate;
		}
		return "1800-01-01";

	}

	public String checkMD5HashCode(File filename) throws IOException {

		FileInputStream fis = new FileInputStream(filename);
		String md5 = DigestUtils.md5Hex(fis);

		fis.close();
		return md5;

	}
}
