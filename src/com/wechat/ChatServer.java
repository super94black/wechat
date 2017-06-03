package com.wechat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	// 定义一个serversocket对象
	private static ServerSocket server = null;
	// 定义一个socket对象
	private static Socket client = null;
	// 定义一个变量用来保存客户端的用户名
	private static String name;
	// 定义一个布尔值变量
	private static boolean falg = true;
	// 定义输入流变量
	private static DataInputStream di = null;
	// 定义输出流变量
	private static DataOutputStream dos = null;

	public static void main(String args[]) {
	// 创建ServerThread对象
	ServerThread st = new ServerThread();
	while (true) {
		try {
			// 创建serversocket对象指定端口号为8000
			server = new ServerSocket(8000);
		} catch (IOException e) {
			System.out.println("正在监听！！");
		}
		try {
			System.out.println("等待客户端连接....");
			// 将客户端的套接字与服务器的套接字连接起来
			client = server.accept();
			System.out.println("连接成功！！");
			// 将服务器的输入流封装到DataInputStream中
			di = new DataInputStream(client.getInputStream());
			// 将服务器的输出流封装到DataInputStream中
			dos = new DataOutputStream(client.getOutputStream());
			// 从流中读取用户名
			while (falg) {
				name = di.readUTF();
				if (st.checkp(name)) {
					System.out.println("客户的地址：" + client.getInetAddress()+ "\t" + name + ":进入聊天室");
					// 将falg变为false
					falg = false;
				} else {
					// 清空输出流
					dos.flush();
				}
			}
		} catch (IOException e) {
			System.out.println("正在等待客户端呼叫......");
		}

		// 判断是否有客户端连接到服务器
		if (client.isConnected()) {
			// 创建服务器端的收发信息线程对象
			ServerThread sth = new ServerThread(client, name);
			Thread th = new Thread(sth);
			// 启动线程
			th.start();
			falg = true;
			}
		}
	}
}
