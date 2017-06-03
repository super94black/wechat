package com.wechat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class ServerThread implements Runnable {
	// 定义一个socket变量
	Socket client = null;
	// 定义一个Datainputstream变量
	DataInputStream di = null;
	// 定义一个Dataoutputstream变量
	DataOutputStream dos = null;
	// 定义一个变量保存连接当前线程的用户名
	String name = null;
	// 创建一个hashtable对象用来保存所有的为客户端开辟的线程对象
	static Hashtable<String, ServerThread> clientlist = new Hashtable<String, ServerThread>();

	public ServerThread() {
		
	}

	public ServerThread(Socket client, String name) {
		try {
			// 将传入的client赋值给成员变量的client
			this.client = client;
			// 将传入的name赋值给成员变量的name
			this.name = name;
			// 将服务器的输出流封装到DataInputStream中
			di = new DataInputStream(client.getInputStream());
			// 将服务器的输出流封装到DataOutputStream中
			dos = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			
		}
	}

	public void run() {
		try {
			// 添加当前对象到hashtable
			clientlist.put(name, this);
			// 发送新用户进入的消息给所有客户端
			sendallClient(name + "进入聊天室");

			while (true) {

				// 定义一个string对象接受从流中读取到的信息
				String mess = di.readUTF();
				// 创建一个stringtokenizer对象分析接收到的消息
				StringTokenizer str = new StringTokenizer(name, "/");
				// 判断截取到的信息有没分隔符
				if (mess.equalsIgnoreCase("/B")) {
					// 匹配到调用getlist方法
					getList();
					// 判断信息是否与-change匹配
				} else if (mess.equalsIgnoreCase("/0")) {
					System.out.println("name" + "退出聊天室");
					break;
				} else if (str.countTokens() == 1 || str.countTokens() >= 3) {
					// 调用sendallclient发送公聊信息
					sendallClient(name + "说：" + mess);
				} 
			}
			client.close();
		} catch (Exception e) {

		} finally {
			// 清除客户端信息
			clientlist.remove(name);
			File file = new File(name + ".txt");
			// 文件删除
			file.delete();
			// 有人退出时，给所有人发送退出信息
			sendallClient(name + "退出聊天室");
			System.out.println(getDate() + " " + name + "退出聊天室");
		}
	}

	// 公聊
	public void sendallClient(String mess) {
		// 获得clientlist中值得ServerThread放入枚举集合中
		Enumeration<ServerThread> allclients = clientlist.elements();
		// 遍历所有客户
		while (allclients.hasMoreElements()) {
			// 枚举中还有元素是，返回此举的下一个元素
			ServerThread st = (ServerThread) allclients.nextElement();
			try {
				// 将信息写入流中
				st.dos.writeUTF(getDate() + "\t" + mess);
				// 刷新流
				st.dos.flush();
			} catch (IOException e) {
				Thread th = new Thread(st);
				// 产生异常中断此线程
				th.interrupt();
			}
		}
	}

	// 私聊
	public void sendClient(String name1, String mess) {
		ServerThread st = clientlist.get(name1);
		ServerThread st1 = clientlist.get(name);
		try {
			// 将要发送的信息保存到流中
			st.dos.writeUTF(getDate() + "\t" + name + "对你说：\t" + mess);
			// 刷新流
			st.dos.flush();

			// 把信息发给原客户端
			st1.dos.writeUTF(getDate() + "\t你对" + name1 + "说：\t" + mess);
			// 刷新流
			st1.dos.flush();
		} catch (IOException e) {

			System.out.println("你发送的信息有误，请重新发送！");
			//sendClient(name1, mess);
		}
	}

	public boolean checkp(String str) {
		boolean flag = true;
		// 得到所有的用户名
		Enumeration<String> checkname = clientlist.keys();
		// 循环检查用户名
		while (checkname.hasMoreElements()) {
			// 枚举中还有元素是，返回此举的下一个元素
			String sn = (String) checkname.nextElement();
			// 判断用户名是否重复，重复的话返回false
			if (str.equalsIgnoreCase(sn)) {
				// 返回false
				flag = false;
				// 判断用户名是否为空
			} else if (str.equalsIgnoreCase("")) {
				// 返回false
				flag = false;
			}
		}
		return flag;
	}

	// 关闭服务器
	public void guanBi() {
		if (!(client == null)) {
			try {
				client.close();
				System.out.println("客户端退出此系统");
			} catch (IOException e) {

			}
		}
	}

	// 显示用户在线
	public void getList() {
		// 获得所有键的枚举
		Enumeration<String> checkname = clientlist.keys();
		// 通过用户名获得线程对象
		ServerThread st = clientlist.get(name);
		try {
			// 写入信息
			st.dos.writeUTF("在线用户列表");
			// 写入用户在线人数
			st.dos.writeUTF(clientlist.size() + ":人在线");
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// 便利枚举
		while (checkname.hasMoreElements()) {
			// 枚举中还有元素是，返回此举的下一个元素
			String sn = (String) checkname.nextElement();
			try {
				// 将用户信息写入流中
				st.dos.writeUTF("用户名称：" + sn);
				// 清空输出流
				st.dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 时间
	public String getDate() {
		// 获得data对象
		Date nowTime = new Date();
		// 创建格式化参数
		String pattern = "HH:mm:ss";
		// 创建SimpleDateFormat对象
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		// 定义一个变量接收时间
		String timePattern = sdf.format(nowTime);
		// 返回当前时间
		return timePattern;
	}
}
