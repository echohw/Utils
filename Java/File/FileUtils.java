import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageTypeSpecifier;

/**
 * 文件工具类：用于操作文件
 * @author 1182749253 燃
 */
public class FileUtils {
	private FileUtils(){}
	/**
	 * 向文件写入内容
	 * @param content 要写入的内容
	 * @param filePath 文件路径
	 * @throws IOException
	 */
	public static void putContents(String content,String filePath) throws IOException {
		putContents(content, new File(filePath));
	}
	
	/**
	 * 向文件写入内容
	 * @param content 要写入的内容
	 * @param file 文件对象
	 * @throws IOException
	 */
	public static void putContents(String content,File file) throws IOException{
		putContents(content, file, false);
	}
	
	/**
	 * 向文件写入内容
	 * @param content 要写入的内容
	 * @param file 文件对象
	 * @param append 是否使用追加模式
	 * @throws IOException
	 */
	public static void putContents(String content,File file,boolean append) throws IOException{
		putContents(content, file, append, "utf-8");
	}
	
	/**
	 * 向文件写入内容
	 * @param content 要写入的内容
	 * @param file 文件对象
	 * @param append 是否使用追加模式
	 * @param charset 文件编码
	 * @throws IOException
	 */
	public static void putContents(String content,File file,boolean append,String charset) throws IOException{
		OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(file, append), charset);
		osw.write(content);
		osw.close();
	}
	
	

	/**
	 * 读取文件,返回文件内容
	 * @param filePath 文件路径
	 * @return
	 * @throws IOException
	 */
	public static String getContents(String filePath) throws IOException {
		return getContents(new File(filePath));
	}

	/**
	 * 读取文件,返回文件内容
	 * @param file 文件对象
	 * @return
	 * @throws IOException
	 */
	public static String getContents(File file) throws IOException {
		return getContents(file, "utf-8");
	}
	
	/**
	 * 读取文件,返回文件内容
	 * @param file 文件对象
	 * @param charset 编码格式
	 * @return
	 * @throws IOException
	 */
	public static String getContents(File file, String charset) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在");
		} else {
			return getContents(new FileInputStream(file), charset);
		}
	}

	/**
	 * 根据输入流读取文件,返回文件内容
	 * @param is 输入流
	 * @param charset 文件编码格式
	 * @return
	 * @throws IOException
	 */
	public static String getContents(InputStream is, String charset) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(is, charset);
		char[] chs = new char[1024];
		int len = 0;
		while ((len = isr.read(chs)) != -1) {
			sb.append(chs, 0, len);
		}
		isr.close();
		return sb.toString();
	}
	
	
	/**
	 * 复制单个文件(非目录)
	 * @param srcFile 要复制的文件对象
	 * @param destFile 目标文件对象
	 * @throws IOException
	 */
	public static boolean copyFile(File srcFile, File destFile) throws IOException {
		if (!srcFile.exists()) {
			throw new FileNotFoundException("文件不存在");
		}
		if (!destFile.getParentFile().exists()) { // 判断文件所在的目录是否存在,如果不存在则创建
			destFile.getParentFile().mkdirs();
		}
		return copyFile(new FileInputStream(srcFile), new FileOutputStream(destFile));
	}

	/**
	 * 复制单个文件(非目录)
	 * @param is 输入流
	 * @param os 输出流
	 * @return 是否复制成功
	 * @throws IOException
	 */
	public static boolean copyFile(InputStream is, OutputStream os) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		byte[] bys = new byte[1024];
		int len = 0;
		while ((len = bis.read(bys)) != -1) {
			bos.write(bys, 0, len);
		}
		bos.close(); // 遵循先开后关原则
		bis.close();
		return true;
	}
	
	/**
	 * 复制目录
	 * @param srcFolder 要复制的目录文件对象
	 * @param destFolder 目标目录文件对象(复制到该目录下)
	 * @throws IOException
	 */
	public static boolean copyFolder(File srcFolder, File destFolder) throws IOException {
		if (!srcFolder.exists()) {
			throw new FileNotFoundException("目录不存在");
		}
		File newFolder = new File(destFolder, srcFolder.getName()); // 根据要复制的目录创建新目录对象
		if (!newFolder.exists()) {
			newFolder.mkdirs(); // 在指定的目录下创建新目录
		}
		File[] fileArr = srcFolder.listFiles();
		if (fileArr != null) {
			for (File file : fileArr) {
				if (file.isDirectory()) { // 判断文件对象是否是目录
					copyFolder(file, newFolder); // 根据目录在指定目录下创建新目录
				} else {
					copyFile(file, new File(newFolder, file.getName()));
				}
			}
		}
		return true;
	}
	
	/**
	 * 删除目录对象下的指定类型的文件(递归删除)
	 * @param file
	 * @param endWithType new String[]{"txt","png"}; 如果为null或空数组则删除所有文件
	 */
	public static void deleteFiles(File file,String[] endWithType) {
		if (!file.exists()) {
			return;
		}
		File[] fileArr = file.listFiles(); //列出当前路径下的所有文件
		List<String> asList = null;
		if (endWithType != null && endWithType.length != 0) {
			asList = Arrays.asList(endWithType);
		}
		for (File f : fileArr) { //使用增强for遍历文件数组
			if (f.isDirectory()) {
				deleteFiles(f, endWithType); //如果文件是目录,则进行递归删除
			} else {
				if (endWithType != null && endWithType.length != 0) {
					if (asList.contains(getExtension(f))) { //如果是指定类型文件,则进行删除
						f.delete();
					}
				} else {
					f.delete();
				}
			}
		}
		file.delete();
	}
	
	/**
	 * 获取不包含"."的文件后缀名(扩展名),如果没有后缀名则返回空串
	 * @param file
	 * @return
	 */
	public static String getExtension(File file) {
		String fileName = file.getName();
		int lastIndex = fileName.lastIndexOf(".");
		return lastIndex == -1 ? "" : fileName.substring(lastIndex + 1);
	}
	
	/**
	 * 获取快捷方式所指向程序的真实路径
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static String getLnkFileRealPath(File file) throws IOException {
		String content = getContents(file);
		String pattern = "([A-Za-z]:\\\\\\w+.*?\\.exe)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		if (m.find()) {
			return m.group();
		}
		return null;
	}
	
}
