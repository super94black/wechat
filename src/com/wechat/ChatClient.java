package com.wechat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ChatClient {
	// 创建一个Socket
	private static Socket client = null;
	// 定义输入流变量
	private static DataInputStream di = null;
	// 定义输出流变量
	private static DataOutputStream dos = null;
	// 创建scanner对象接收数据
	Scanner sc = new Scanner(System.in);
	// 定义一个String变量保存用户名
	static String name;
	// 定义一个布尔值来判断是否循环接受用户名
	boolean falg = true;

	public static void main(String args[]) {
		// 创建对象
		ChatClient cc = new ChatClient();
		// 创建客户端的socket对象
		client = new Socket();
		// 调用方法
		cc.connect();
		// 调用方法
		cc.send();
	}

	// 连接
	public void connect() {

		// 创建客户端的socket对象
		client = new Socket();
		// 定义一个String接收IP地址
		String IP = null;
		// 定义一个int端口号
		int port = 0;
		System.out.println("***网络聊天室***");
		try {
			System.out.println("输入服务器的默认地址是0");
			// 接收一个IP保存到string对象中
			IP = sc.nextLine();
			// 匹配接收到的IP，接收到后将IP赋值为默认
			if (IP.equalsIgnoreCase("0")) {
				IP = "";
			}
			// 设置端口号
			port = 8000;
		} catch (Exception e) {
		}
		try {
			// 从给定的主机名得到ip存入inetaddress对象中
			InetAddress address = InetAddress.getByName(IP);
			// 根据得到的ip和端口号创建套接字地址
			InetSocketAddress socketaddress = new InetSocketAddress(address,
					port);
			// 将客户端的套接字链接到服务器
			try {
				// 连接服务器与客户端
				client.connect(socketaddress);
				// 判断是否有连接
				if (client.isConnected()) {
					// 调用方法
					runn();
				} else {
					client.connect(socketaddress);
					// 调用方法
					connect();
				}
			} catch (SocketException e) {
				System.out.println("不能连接到服务器，请重新输入");
				// 调用connect()重新连接
				connect();
			}
		} catch (Exception e) {
			System.out.println("不能连接到服务器，请重新输入");
			// 调用connect()重新连接
			connect();
		}
	}

	public void runn() {
		try {
			// 定义read对象
			ClientThread read = null;
			// 创建read对象
			read = new ClientThread();
			// 创建readdata线程对象
			Thread readData = new Thread(read);
			// 封装一个DataInputStream对象得到输入流
			di = new DataInputStream(client.getInputStream());
			// 封装一个DataOutputStream对象得到输出流
			dos = new DataOutputStream(client.getOutputStream());
			// 接受用户名
			while (falg) {
				System.out.println("请输入用户名：");
				name = sc.next();
				System.out.println(name + "上线了");
				System.out.println("欢迎进入聊天室，需要帮助请输入/A");
				dos.writeUTF(name);
				dos.flush();
				read.setDataInputStream(di);
				// 启动线程
				readData.start();
				// 改变flag中断循环
				falg = false;
			}
		} catch (IOException e) {

		}
	}

	// 写入信息
	@SuppressWarnings("deprecation")
	public void send() {
		// 循环接收发送的消息
		System.out.println("请输入内容：");
		while (sc.hasNext()) {
			String mess;
			mess = sc.nextLine();
			if (mess.equalsIgnoreCase("/0")) {
				System.exit(0);
			} else if (mess.equalsIgnoreCase("/D")) {
				System.out.println("这功能我撸不出来");
			}
			if (mess.equals("/D")) {
				System.out.println("请选择表情：");
			} else {
				select(mess);
			}
		}
	}

	// 将消息发送给服务器
	public void select(String mess) {
		// 判断输入的信息
		if (mess.equalsIgnoreCase("/A")) {
			// 匹配上调用helpList()方法
			helpList();
		} else if (mess.equalsIgnoreCase("/C")) {
			try {
				System.out.println("请输入你要查看的聊天记录的名字");
				String str1 = sc.next();
				File file = new File(str1 + ".txt");
				BufferedReader bf = new BufferedReader(new FileReader(file));
				String str = null;
				while ((str = bf.readLine()) != null) {
					System.out.println(str);
				}
				bf.close();
			} catch (IOException e) {

			}

		} else {
			try {
				// 将消息发送给服务器
				dos.writeUTF(mess);
				// 清空输出流
				dos.flush();
			} catch (IOException e) {

			}
		}
	}

	public void helpList() {
		System.out.println("提示：进入聊天室，默认公聊!!");
		System.out.println("/B 用户在线列表，用户/信息 私聊，/C 查看聊天记录，/D 发送表情，/0 退出系统");
	}
}
