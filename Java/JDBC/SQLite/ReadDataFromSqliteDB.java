import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 工具类：用于操作sqlite数据库
 * @author 1182749253 燃
 *
 */
public class ReadDataFromSqliteDB extends SQLiteJDBC{
	private static ReadDataFromSqliteDB sqlite;
	private static final String dbName = "cmdStart.db";

	private ReadDataFromSqliteDB(String dbName) {
		super(dbName);
	}
	
	static {
		init();
	}

	private static void init() {
		String dbName = null;
		try {
			// ReadDataFromFile.setReadFile(ReadDataFromSqliteDB.class.getClassLoader().getResourceAsStream("cmdStart.conf")); //从jar包内部的配置文件中读取信息
			String configFileName = "cmdStart.conf"; // 设置配置文件名称
			String confFilePath = System.getProperty("user.dir") + "\\" + configFileName; // 获取当前可执行jar包所在目录
			File file = new File(confFilePath); // 设置文件为当前可执行jar包所在目录下的文件
			if (!file.exists()) {
				// 如果文件不存在,则从jar包内部将配置文件复制一份到可执行jar包所在目录
				FileUtils.putContents(FileUtils.getContents(ReadDataFromSqliteDB.class.getClassLoader().getResourceAsStream(configFileName), "utf-8"),file);
				dbName = System.getProperty("user.dir") + "\\" +ReadDataFromSqliteDB.dbName; //设置db文件所在路径
				ReadDataFromFile.setHandleFile(file); // 从可执行jar包所在目录读取配置文件
				ReadDataFromFile.setProperty("dbname", dbName);
				ReadDataFromFile.saveToFile();
			} else {
				ReadDataFromFile.setHandleFile(file); // 从可执行jar包所在目录读取配置文件
				dbName = ReadDataFromFile.getProperty("dbname"); // 读取db文件信息
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		String trueDBName = dbName.endsWith(".db") ? dbName : ReadDataFromSqliteDB.dbName;
		File temp = new File(trueDBName);
		// 判断db文件是否存在
		boolean fileExists = temp.exists() ? true : false;
		sqlite = new ReadDataFromSqliteDB(trueDBName);
		if (!fileExists) {
			createTable(); // 如果db文件不存在,则创建新db文件后要进行创建表操作
		}
	}

	/**
	 * 初始化db文件(建表)
	 */
	private static void createTable() {
		// 读取可执行jar包内部的sql文件并执行
		InputStream is = ReadDataFromSqliteDB.class.getClassLoader().getResourceAsStream("InitSQLite.sql");
		String content = null;
		try {
			content = FileUtils.getContents(is, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		execSql(content);
	}


	/**
	 * 插入(普通),删除,修改操作
	 * 
	 * @param sql
	 * @return 受影响的行数
	 */
	public static int execSql(String sql) {
		try {
			return sqlite.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 插入操作
	 * @param sql sql语句
	 * @param table 从哪个表中查询rowid值
	 * @return 指定的表中最后一条数据的rowid值
	 */
	public static int add(String sql, String table){
		try {
			return sqlite.insert(sql, table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	/**
	 * 查询操作,返回ResultSet
	 * @param sql
	 * @return
	 */
	public static ResultSet query(String sql) {
		return sqlite.select(sql);
	}
	
	/**
	 * 查询操作,返回ArrayList<HashMap<String, String>>
	 * @param preSql
	 * @param selectFields
	 * @return
	 * @use 调用格式:select("select %s,%s from user",new String[]{"name","age"})
	 */
	public static ArrayList<HashMap<String, String>> query(String preSql, String[] selectFields) {
		return sqlite.select(preSql, selectFields);
	}
}
