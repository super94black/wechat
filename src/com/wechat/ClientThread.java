package com.wechat;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientThread implements Runnable {
	// 定义一个DataInputStream变量
	DataInputStream di;
	// 定义一个string对象接收服务器发送的消息
	String str;
	boolean ff=true;

	public void setDataInputStream(DataInputStream di) {
		this.di = di;
	}

	public void run() {
		while (true) {
			try {
				// 从流中读取信息
				str = di.readUTF();
				// 调用writeFile将信息写入文件中
				writeFile(str);
				// 显示信息
				System.out.println(str);
			} catch (IOException e) {
				// 服务器断开后客户端显示提示信息
				System.out.println("服务断开~~~~~~~");
				// 终止客户端
				System.exit(0);
			}
		}
	}
 
	public void writeFile(String str2) {
		try {
			// 创建一个文件
			File file = new File(ChatClient.name + ".txt");
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str2);
			// 写入一个行分隔符
			bw.newLine();
			// 关闭缓冲流
			bw.close();
			// 关闭字符输出流
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
 