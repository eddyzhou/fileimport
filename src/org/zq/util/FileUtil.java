package org.zq.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件操作工具类
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class FileUtil {
	/** 下载文件的编码格式 */
	private static final String FILE_ENCODING = "UTF-8";

	/** 日志实例 */
	private static final Log logger = LogFactory.getLog(FileUtil.class);

	private static final String CONTENT_TYPE = "application/octet-stream";

	/** 缓存大小 */
	private static final int BUFFER_SIZE = 8096;

	private FileUtil() {
	}

	/**
	 * download file with this fileName
	 * 
	 * @param fileName
	 *            the name of file to download
	 * @param response
	 * @throws IOException
	 */
	public static void downLoad(String fileName, HttpServletResponse response)
			throws IOException {
		if (StringUtil.isEmpty(fileName) || response == null)
			throw new IllegalArgumentException("Argument not valid");

		String downFileName = null;
		try {
			downFileName = URLEncoder.encode(fileName, FILE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			downFileName = fileName;
		}
		response.setHeader("Content-disposition", "fileName=" + downFileName);
		response.setContentType(CONTENT_TYPE);

		try {
			InputStream is = new FileInputStream(fileName);
			BufferedOutputStream out = new BufferedOutputStream(response
					.getOutputStream());

			byte[] b = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = is.read(b)) > 0) {
				out.write(b, 0, len);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			final String message = "Caught exception while download file:"
					+ downFileName;
			logger.error(message, e);
			IOException newExp = new IOException(message);
			newExp.initCause(e);
			throw newExp;
		}
	}

	/**
	 * get type of the file
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName) {
		if (StringUtil.isEmpty(fileName)) {
			return null;
		}

		int len = fileName.length();
		int beginPos = fileName.lastIndexOf(".") + 1;
		if (beginPos > 0) {
			return fileName.substring(beginPos, len);
		}

		return null;
	}

	/**
	 * write the content to the file
	 * 
	 * @param content
	 *            the content to write
	 * @param fileName
	 *            dest file name
	 * @param append
	 *            to append or cover
	 * @throws IOException
	 */
	public static void writeToFile(String content, String fileName,
			boolean append) throws IOException {

		// create file path
		createFilePath(fileName);

		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, append);
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			bw.close();
		} finally {
			bw.close();
			fw.close();
		}
	}

	/**
	 * create file path
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean createFilePath(String fileName) {
		return new File(fileName).getParentFile().mkdirs();
	}

	/**
	 * Recursively copy all files from one directory to another.
	 * 
	 * @param src
	 *            file or directory to copy from.
	 * @param dest
	 *            file or directory to copy to.
	 * @throws IOException
	 */
	public static void copyFiles(File src, File dest) throws IOException {
		if (!src.exists()) {
			return;
		}

		if (src.isDirectory()) {
			// Create destination directory
			dest.mkdirs();

			// Go trough the contents of the directory
			String list[] = src.list();
			for (int i = 0; i < list.length; i++) {
				copyFiles(new File(src, list[i]), new File(dest, list[i]));
			}

		} else {
			copyFile(src, dest);
		}
	}

	/**
	 * Copy the src file to the destination.
	 * 
	 * @param src
	 * @param dest
	 * @return True if the extent was greater than actual bytes copied.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean copyFile(File src, File dest)
			throws FileNotFoundException, IOException {
		return copyFile(src, dest, -1);
	}

	/**
	 * Copy up to extent bytes of the source file to the destination
	 * 
	 * @param src
	 * @param dest
	 * @param extent
	 *            Maximum number of bytes to copy
	 * @return True if the extent was greater than actual bytes copied.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean copyFile(File src, File dest, long extent)
			throws FileNotFoundException, IOException {
		boolean result = false;
		if (dest.exists()) {
			dest.delete();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// Get channels
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			fcin = fis.getChannel();
			fcout = fos.getChannel();
			if (extent < 0) {
				extent = fcin.size();
			}

			// do the file copy
			long trans = fcin.transferTo(0, extent, fcout);
			if (trans < extent) {
				result = false;
			}
			result = true;
		} catch (IOException e) {
			// Add more info to the exception. Preserve old stacktrace.
			IOException newE = new IOException("Copying "
					+ src.getAbsolutePath() + " to " + dest.getAbsolutePath()
					+ " with extent " + extent + " got IOE: " + e.getMessage());
			newE.setStackTrace(e.getStackTrace());
			throw newE;
		} finally {
			// finish up
			if (fcin != null) {
				fcin.close();
			}
			if (fcout != null) {
				fcout.close();
			}
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return result;
	}

	/**
	 * Utility method to read an entire file as a String.
	 * 
	 * @param fileName
	 *            the name of file to read
	 * @return the file content as String
	 * @throws IOException
	 */
	public static String readFromFile(String fileName) throws IOException {
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder buf = new StringBuilder();
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String text = null;
			while ((text = br.readLine()) != null) {
				buf.append(text);
			}
		} finally {
			br.close();
			fr.close();
		}
		return buf.toString();
	}

	/**
	 * Utility method to read an entire file as a String.
	 * 
	 * @param file
	 * @return File as String.
	 * @throws IOException
	 */
	public static String readFileAsString(File file) throws IOException {
		StringBuffer sb = new StringBuffer((int) file.length());
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		try {
			line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

	/**
	 * Deletes all files and subdirectories under dir.
	 * 
	 * @param dir
	 * @return true if all deletions were successful. If a deletion fails, the
	 *         method stops attempting to delete and returns false.
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * Get a list of all files in directory that have passed prefix.
	 * 
	 * @param dir
	 *            Dir to look in.
	 * @param prefix
	 *            Basename of files to look for. Compare is case insensitive.
	 * 
	 * @return List of files in dir that start w/ passed basename.
	 */
	public static File[] getFilesWithPrefix(File dir, final String prefix) {
		FileFilter prefixFilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().startsWith(
						prefix.toLowerCase());
			}
		};
		return dir.listFiles(prefixFilter);
	}

	/**
	 * Get a
	 * 
	 * @link java.io.FileFilter that filters files based on a regular
	 *       expression.
	 * 
	 * @param regexp
	 *            the regular expression the files must match.
	 * @return the newly created filter.
	 */
	public static FileFilter getRegexpFileFilter(String regexp) {
		// Inner class defining the RegexpFileFilter
		class RegexpFileFilter implements FileFilter {
			Pattern pattern;

			protected RegexpFileFilter(String re) {
				pattern = Pattern.compile(re);
			}

			public boolean accept(File pathname) {
				return pattern.matcher(pathname.getName()).matches();
			}
		}

		return new RegexpFileFilter(regexp);
	}
}
