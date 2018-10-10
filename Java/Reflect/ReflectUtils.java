import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {
	/**
	 * 使用反射创建实例
	 */
	public static Object newInstance(String className, Class[] parameterTypes, Object[] initargs) throws Exception {
		Class c = Class.forName(className); // 获取字节码文件对象
		Constructor constructor = c.getDeclaredConstructor(parameterTypes); // 获取构造方法对象
		constructor.setAccessible(true); // 取消访问检查
		Object resObj = constructor.newInstance(initargs); // 创建声明类的新实例
		return resObj; // 返回该实例
	}

	/**
	 * 创建成员变量对象
	 */
	private static Field getField(Object obj, String propertyName) throws Exception {
		Class c = obj.getClass(); // 根据对象获取字节码文件对象
		Field field = c.getDeclaredField(propertyName); // 获取该对象的propertyName成员变量,返回成员变量对象
		field.setAccessible(true); // 取消访问检查
		return field; // 返回属性对象
	}

	/**
	 * 使用反射设置指定对象的属性值
	 */
	public static void setProperty(Object obj, String propertyName, Object value) throws Exception {
		Field field = getField(obj, propertyName);
		field.set(obj, value); // 给对象的成员变量赋值为指定的值
	}

	/**
	 * 使用反射获取指定对象的属性值
	 */
	public static Object getProperty(Object obj, String propertyName) throws Exception {
		Field field = getField(obj, propertyName);
		return field.get(obj); // 获取指定成员变量的值
	}

	/**
	 * 使用反射调用指定对象的方法
	 */
	public static Object callMethod(Object obj, String methodName, Class[] parameterTypes, Object[] args)throws Exception {
		Class c = obj.getClass(); //获取字节码文件对象
		Method m = c.getDeclaredMethod(methodName, parameterTypes); //获取成员方法对象(方法存在重载,需要根据参数才能确定具体的方法)
		m.setAccessible(true); // 取消访问检查
		Object resObj = m.invoke(obj, args); //调用指定的方法
		return resObj; //返回方法中返回的数据
	}
}
