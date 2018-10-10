import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * 窗体操作工具类
 * @author 1182749253 燃
 */
public class FrameUtils {

	/**
	 * 获取窗体的位置
	 * @param frame
	 * @return 返回包含窗体x,y坐标的数组:new double[]{x,y}
	 */
	public static double[] getLocation(Frame frame) {
		Point location = frame.getLocation();
		double x = location.getX();
		double y = location.getY();
		return new double[] { x, y };
	}

	/**
	 * 获取窗体的大小
	 * @param frame
	 * @return 返回包含窗体大小的数组:new double[]{width,height}
	 */
	public static double[] getSize(Frame frame) {
		Dimension size = frame.getSize();
		double width = size.getWidth();
		double height = size.getHeight();
		return new double[] { width, height };
	}

	/**
	 * 获取窗体的大小及位置
	 * @param frame
	 * @return 返回包含窗体的大小及位置的数组:new double[]{width,height,x,y}
	 */
	public static double[] getSizeAndLocation(Frame frame) {
		double[] size = getSize(frame);
		double[] location = getLocation(frame);
		return new double[] { size[0], size[1], location[0], location[1] };
	}

	/**
	 * 获取屏幕的大小
	 * @return 返回包含屏幕大小的数组:new double[]{width,height}
	 */
	public static double[] getScreenSize(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		return new double[]{screenSize.getWidth(),screenSize.getHeight()};
	}
	
	/**
	 * 以动画的效果变换窗体的大小
	 * @param frame
	 * @param width 最终的窗体的宽度
	 * @param height 最终的窗体的高度
	 * @param time 运行时间
	 * @warn 步长(step)稍大时最终窗体大小会产生误差,在运行时间方面控制的不是很好,对非等比扩大(缩放)的数据所产生的动画效果还不是很平滑
	 */
	public static Thread setSizeWithAnimate(Frame frame, int finalWidth, int finalHeight, double time) {
		double[] size = getSize(frame);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 获取需要向水平(x)方向或向竖直(y)方向需要改变大小的最大距离
				double lengthWidth = Math.abs(finalWidth - size[0]);
				double lengthHeight = Math.abs(finalHeight - size[1]);
				
				double maxLength = lengthWidth > lengthHeight ? lengthWidth : lengthHeight;
				double minLength = lengthWidth < lengthHeight ? lengthWidth : lengthHeight;
				
				// 定义每次移动的距离(最小距离为1)
				int step = 1;
				// 计算需要移动的次数
				int times = (int) Math.round(maxLength / step); 
				
				// 获取移动之前的位置
				int currentWidth = (int) size[0];
				int currentHeight = (int) size[1];
				
				//定义变量用于保存插入的循环次数以及剩余的插入次数
				int repeat,surplusTimes=0; 
				
				if (minLength == 0 || minLength== 1) {
					repeat = (int) (maxLength - minLength); // 计算对数据进行插空时需要的循环次数
				} else {
					repeat = (int) ((maxLength - minLength) / (minLength - 1));
					surplusTimes = (int) ((maxLength - minLength) % (minLength - 1)); // 计算除完整的循环次数外多余的插入次数
				}

				//使用StringBuilder保存标记
				StringBuilder sb = new StringBuilder();
				//生成标记
				if (minLength != 0) {
					outerFor: for (int i = 0; i <= minLength; i++) {
						if (minLength != 0 && minLength != i) {
							sb.append(1); // 1表示要变换坐标
						}
						for (int j = 0; j < repeat; j++) {
							if (sb.length() >= maxLength) {
								break outerFor;
							}
							sb.append(0); // 0表示不变换坐标
						}
						if (i < surplusTimes) {
							sb.append(0);
						}
					}
				}
				
				//生成标记字符串
				String flag=sb.toString();
				
				for (int i = 0; i < times; i++) {
					if (i == times - 1) { // 计算最后一次变换时窗体的大小
						if (lengthWidth != 0) {
							currentWidth = (int) (finalWidth > currentWidth ? currentWidth + (lengthWidth % step == 0 ? step : lengthWidth % step) : currentWidth - (lengthWidth % step == 0 ? step : lengthWidth % step));
						}
						if (lengthHeight != 0) {
							currentHeight = (int) (finalHeight > currentHeight ? currentHeight + (lengthHeight % step == 0 ? step : lengthHeight % step) : currentHeight - (lengthHeight % step == 0 ? step : lengthHeight % step));
						}
					} else {
						if (lengthWidth > lengthHeight) { //向水平方向拉长的距离大于向竖直方向拉长的距离
							currentWidth = finalWidth > currentWidth ? currentWidth + step : currentWidth - step; // 计算下次的窗体的宽度
							if (lengthHeight != 0 && "1".equals(flag.charAt(i) + "")) {
								currentHeight = finalHeight > currentHeight ? currentHeight + step : currentHeight - step; // 计算下次的窗体的高度
							}
						} else {
							if (lengthWidth != 0 && "1".equals(flag.charAt(i) + "")) {
								currentWidth = finalWidth > currentWidth ? currentWidth + step : currentWidth - step; // 计算下次的窗体的宽度
							}
							currentHeight = finalHeight > currentHeight ? currentHeight + step : currentHeight - step; // 计算下次的窗体的高度
						}
					}
					frame.setSize(currentWidth, currentHeight); //重新设置窗体的大小
					try {
						long sleepTime = Math.round(time * 1000 / times);
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
		return thread;
	}

	/**
	 * 以动画的效果变换窗体的位置
	 * @param frame
	 * @param finalX 目标x坐标
	 * @param finalY 目标y坐标
	 * @param time 设置要在多长时间内移动到指定的位置(单位:秒)
	 * @warn 步长(step)稍大时最终窗体坐标会产生误差,在运行时间方面控制的不是很好,窗体对于在非坐标轴上移动的效果还不是很平滑
	 */
	public static Thread setLocationWithAnimate(Frame frame, int finalX, int finalY, double time) {
		double[] screenSize=getScreenSize();
		double[] location = getLocation(frame);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//对最终的x,y的坐标值进行修正
				int final_X = (int) (finalX > screenSize[0] ? screenSize[0] : (finalX < 0 ? 0 : finalX));
				int final_Y = (int) (finalY > screenSize[1] ? screenSize[1] : (finalY < 0 ? 0 : finalY));
				
				// 获取需要向水平(x)方向或向竖直(y)方向需要移动的最大距离
				double lengthX = Math.abs(final_X - location[0]);
				double lengthY = Math.abs(final_Y - location[1]);
				
				double maxLength = lengthX > lengthY ? lengthX : lengthY;
				double minLength = lengthX < lengthY ? lengthX : lengthY;
				
				// 定义每次移动的距离(最小距离为1)
				int step = 1;
				// 计算需要移动的次数
				int times = (int) Math.round(maxLength / step); 
				
				// 获取移动之前的位置
				int currentX = (int) location[0];
				int currentY = (int) location[1];
				
				//定义变量用于保存插入的循环次数以及剩余的插入次数
				int repeat,surplusTimes=0; 
				
				if (minLength == 0 || minLength== 1) {
					repeat = (int) (maxLength - minLength); // 计算对数据进行插空时需要的循环次数
				} else {
					repeat = (int) ((maxLength - minLength) / (minLength - 1));
					surplusTimes = (int) ((maxLength - minLength) % (minLength - 1)); // 计算除完整的循环次数外多余的插入次数
				}
				
				//使用StringBuilder保存标记
				StringBuilder sb = new StringBuilder();
				//生成标记
				if (minLength != 0) {
					outerFor: for (int i = 0; i <= minLength; i++) {
						if (minLength != 0 && minLength != i) {
							sb.append(1); // 1表示要变换坐标
						}
						for (int j = 0; j < repeat; j++) {
							if (sb.length() >= maxLength) {
								break outerFor;
							}
							sb.append(0); // 0表示不变换坐标
						}
						if (i < surplusTimes) {
							sb.append(0);
						}
					}
				}
				
				//生成标记字符串
				String flag=sb.toString();
				
				for (int i = 0; i < times; i++) {
					if (i == times - 1) { // 计算最后一次移动的距离
						if (lengthX != 0) {
							currentX = (int) (final_X > currentX ? currentX + (lengthX % step == 0 ? step : lengthX % step) : currentX - (lengthX % step == 0 ? step : lengthX % step));
						}
						if (lengthY != 0) {
							currentY = (int) (final_Y > currentY ? currentY + (lengthY % step == 0 ? step : lengthY % step) : currentY - (lengthY % step == 0 ? step : lengthY % step));
						}
					} else {
						if (lengthX > lengthY) { //向水平方向移动的距离大于向竖直方向移动的距离
							currentX = final_X > currentX ? currentX + step : currentX - step; // 计算要移动到的下一个x的坐标
							if (lengthY != 0 && "1".equals(flag.charAt(i) + "")) {
								currentY = final_Y > currentY ? currentY + step : currentY - step; // 计算要移动到的下一个y的坐标
							}
						} else {
							if (lengthX != 0 && "1".equals(flag.charAt(i) + "")) {
								currentX = final_X > currentX ? currentX + step : currentX - step; // 计算要移动到的下一个x的坐标
							}
							currentY = final_Y > currentY ? currentY + step : currentY - step; // 计算要移动到的下一个y的坐标
						}
					}
					frame.setLocation(currentX, currentY); // 重新设置窗体的位置
					try {
						long sleepTime = Math.round(time * 1000 / times);
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
		return thread;
	}
}
