import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用于操作SQLiteDB的工具类
 * @author 1182749253 燃
 */
class SQLiteJDBC {
	protected Connection conn;
	protected Statement stmt;
	private String dbName;
	private boolean autoCommit=false;

	/**
	 * 初始化操作
	 */
	private void init() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			stmt = conn.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	/**
	 * 私有化无参构造,不能通过此种方式创建对象
	 */
	private SQLiteJDBC(){}
	
	/**
	 * 带参构造方法
	 * @param dbName 要连接的数据库文件名称,如果为空则会使用默认的名称(数据库不存在则会自动创建)
	 */
	public SQLiteJDBC(String dbName) {
		if (dbName == null || "".equals(dbName)) {
			dbName = "default.db";
		}
		this.dbName = dbName;
		init();
	}

	/**
	 * 设置是否自动提交
	 * @param cmt boolean:true|false
	 * @throws SQLException
	 */
	public void setAutoCommit(boolean cmt) throws SQLException {
		conn.setAutoCommit(cmt);
		autoCommit=cmt;
	}
	
	/**
	 * 执行sql语句的方法
	 * @param sql 要执行sql语句
	 * @return 受影响的行数
	 * @throws SQLException 
	 */
	private int exec(String sql) throws SQLException {
		return stmt.executeUpdate(sql);
	}

	/**
	 * 插入操作
	 * @param sql 完整的sql语句
	 * @return 返回受影响的行数
	 * @throws SQLException 
	 */
	public int insert(String sql) throws SQLException {
		 return this.exec(sql);
	}
	
	/**
	 * 插入操作
	 * @param sql 完整的sql语句
	 * @param table 从哪个表中查询rowid值
	 * @return 指定的表中最后一条数据的rowid值
	 * @throws SQLException
	 */
	public int insert(String sql, String table) throws SQLException {
		this.exec(sql); //执行插入操作
		String maxRowId = "select max(rowid) rowid from " + table;
		ResultSet results = this.select(maxRowId); //查询rowid值
		while (results.next()) {
			return results.getInt("rowid");
		}
		return -1;
	}

	/**
	 * 删除操作
	 * @param sql 完整的sql语句
	 * @return 返回受影响的行数
	 * @throws SQLException 
	 */
	public int delete(String sql) throws SQLException {
		return this.exec(sql);
	}

	/**
	 * 更新操作
	 * @param sql 完整的sql语句
	 * @return 返回受影响的行数
	 * @throws SQLException 
	 */
	public int update(String sql) throws SQLException {
		return this.exec(sql);
	}
	
	/**
	 * 提交数据
	 * @throws SQLException 
	 */
	public void commit() throws SQLException {
		if (!autoCommit) {
			conn.commit();
		}
	}

	/**
	 * 查询操作
	 * @param sql 完整的sql语句
	 * @return 返回结果集
	 */
	public ResultSet select(String sql) {
		ResultSet rs = null;
		try {
			rs=this.stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 查询操作
	 * @param preSql 待格式化的sql语句
	 * @param selectFields 要查询的字段数组
	 * @return
	 * 调用格式:select("select %s,%s from user",new String[]{"name","age"})
	 */
	public ArrayList<HashMap<String, String>> select(String preSql,String[] selectFields){
		preSql=preSql.replaceAll("%s", "`%s`"); //将"%s"替换为"`%s`"
		preSql=String.format(preSql,selectFields); //格式化字符串
		ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>(); //定义ArrayList用于存储结果
		ResultSet rs=null;
		try {
			rs=this.stmt.executeQuery(preSql); //执行查询操作
			while(rs.next()){
				HashMap<String, String> hm=new HashMap<String, String>(); //创建HashMap对象
				for(String field:selectFields){
					hm.put(field, rs.getString(field)); //根据要查询的字段将数据根据键值对类型存入HashMap中,一条记录对应一个HashMap对象
				}
				list.add(hm);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 关闭资源
	 * @throws SQLException
	 */
	public void closeAll() throws SQLException {
		stmt.close();
		conn.close();
	}
}
