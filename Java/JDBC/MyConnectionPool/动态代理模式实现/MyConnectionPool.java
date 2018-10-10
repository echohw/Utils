import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * 动态代理模式实现自定义连接池
 * @author 燃
 */
public class MyConnectionPool extends DataSourceAdapter {
	private String driverClass;
	private String url;
	private String user;
	private String password;

	private int initCount = 3; // 定义默认的初始化连接数
	private int maxCount = 10; // 定义默认的最大连接数
	private int currentCount = 0; // 定义当前连接数

	private LinkedList<Connection> pool = new LinkedList<>(); // 定义集合用于存储连接对象

	public MyConnectionPool(Properties prop) {
		readConfig(prop);
		registerDriver();
		for (int i = 0; i < initCount; i++) { // 初始化连接对象并添加到池中
			pool.addLast(createConnection());
		}
	}
	
	/**
	 * 读取配置信息
	 * @param prop
	 */
	private void readConfig(Properties prop) {
		driverClass = prop.getProperty("driverClass");
		url = prop.getProperty("url");
		user = prop.getProperty("user");
		password = prop.getProperty("password");
		String initCountStr = prop.getProperty("initCount");
		if (initCountStr != null && !"".equals(initCountStr.trim())) {
			initCount = Integer.parseInt(initCountStr);
		}
		String maxCountStr = prop.getProperty("maxCount");
		if (maxCountStr != null && !"".equals(maxCountStr.trim())) {
			maxCount = Integer.parseInt(maxCountStr);
		}
	}

	/**
	 * 注册驱动
	 * @param prop
	 */
	private void registerDriver() {
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取初始化连接数
	 * @return
	 */
	public int getInitCount() {
		return initCount;
	}

	/**
	 * 获取最大连接数
	 * @return
	 */
	public int getMaxCount() {
		return maxCount;
	}

	/**
	 * 获取当前连接数
	 * @return
	 */
	public int getCurrentCount() {
		return currentCount;
	}

	/**
	 * 创建连接
	 * @return
	 */
	private Connection createConnection() {
		try {
			currentCount++;
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取连接
	 */
	@Override
	public Connection getConnection() throws SQLException { // 实际上给的是代理对象
		Connection conn = null;
		if (pool.size() > 0) { // 如果池中有连接对象,则从池中获取
			conn = pool.removeFirst();
		} else if (currentCount < maxCount) { // 如果池中没有连接对象且当前连接数小于最大连接数则创建连接对象
			conn = createConnection();
		}
		if (conn != null) {
			// 创建代理对象
			Connection proxy = (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(),
					new Class[] { Connection.class }, new MyInvocationHandler(conn));
			return proxy;
		}
		throw new RuntimeException("当前连接已经达到最大连接数目!");
	}

	/**
	 * 释放资源
	 * @param conn
	 */
	public void release(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放资源,销毁池子中的连接
	 */
	public void releasePool() {
		try {
			for (int i = 0; i < pool.size(); i++) {
				pool.removeFirst().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 调用代理对象的相关方法时的处理类
	 */
	private class MyInvocationHandler implements InvocationHandler {
		private Connection conn;

		public MyInvocationHandler(Connection conn) {
			this.conn = conn;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// proxy是代理对象而不是被代理对象
			Object result = null;
			if ("close".equals(method.getName())) { // 如果调用了代理对象的close方法,则尝试将连接对象放入连接池
				if (conn != null) {
					if (pool.size() < initCount) {
						pool.addLast(conn); // 将代理对象添加到连接池
					} else {
						currentCount--;
						conn.close(); // 释放连接对象
					}
					conn = null; // 将conn设置为null,断开代理对象对连接对象的引用
				}
			} else {
				if (conn == null) {
					throw new RuntimeException("连接已被释放！");
				}
				result = method.invoke(conn, args); // 调用连接对象的相关方法
			}
			return result;
		}
	}
}
