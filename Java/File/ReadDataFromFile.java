import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

/**
 * 工具类：用于读取键值类型的文件数据，例如：name=Tom
 * @author 1182749253 燃
 */
public class ReadDataFromFile {

	private static Properties prop;

	private static File file;

	/**
	 * 私有化构造方法,不让外界创建对象
	 */
	private ReadDataFromFile() {}

	/**
	 * 设置要读取的配置文件
	 * @param filePath 文件路径字符串
	 * @throws FileNotFoundException
	 */
	public static void setHandleFile(String filePath) throws FileNotFoundException {
		setHandleFile(new File(filePath));
	}

	/**
	 * 设置要读取的配置文件
	 * @param file 文件对象
	 * @throws FileNotFoundException
	 */
	public static void setHandleFile(File file) throws FileNotFoundException {
		if (file.exists()) {
			ReadDataFromFile.file = file;
			prop = null;
		} else {
			throw new FileNotFoundException("文件不存在");
		}
	}
	
	/**
	 * 根据输入流设置要读取的配置文件
	 * @param is
	 * @throws IOException
	 */
	public static void setHandleFile(InputStream is) throws IOException{
		if(prop==null){
			prop = new Properties();
		}
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		prop.load(isr);
		isr.close();
	}

	/**
	 * 初始化方法
	 * @throws IOException
	 */
	private static void init() throws IOException {
		if (file == null) {
			throw new FileNotFoundException("请先设置配置文件路径");
		} else {
			prop = new Properties();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			prop.load(isr);
			isr.close();
		}
	}

	/**
	 * 根据key获取值
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getProperty(String key) throws IOException {
		if (prop == null) {
			init();
		}
		return prop.getProperty(key);
	}

	/**
	 * 获取配置文件中所有的key
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getKeys() throws IOException {
		if (prop == null) {
			init();
		}
		return prop.stringPropertyNames();
	}

	/**
	 * 设置属性
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public static void setProperty(String key, String value) throws IOException {
		if (prop == null) {
			init();
		}
		prop.setProperty(key, value);
	}

	/**
	 * 保存到文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static boolean saveToFile() throws IOException {
		if (prop != null) {
			return saveToFile(new FileOutputStream(ReadDataFromFile.file));
		}
		return false;
	}
	
	/**
	 * 保存到文件
	 * 
	 * @param os
	 * @throws IOException
	 */
	public static boolean saveToFile(OutputStream os) throws IOException {
		if (prop != null) {
			prop.store(os, "cmdStart.conf");
			os.close();
			return true;
		}
		return false;
	}
}
