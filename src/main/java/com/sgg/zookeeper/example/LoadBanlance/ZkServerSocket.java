package com.sgg.zookeeper.example.LoadBanlance;

import java.net.ServerSocket;
import java.net.Socket;

import org.I0Itec.zkclient.ZkClient;

//ServerSocket 服务端

public class ZkServerSocket implements Runnable {
	private static int port = 18083;
	private static String zkServers = "mles01:2181,mles02:2181,mles03:2181";
	private static int sessionTimeout = 5000;
	public ZkServerSocket(int port) {
		this.port = port;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(port);
			System.out.println("Server start port:"+port);
			Socket socket = null;
			while(true){
				socket = serverSocket.accept();
				new Thread(new ServerHandler(socket)).start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(serverSocket!=null){
					serverSocket.close();
				}
			}catch(Exception e2){
				e2.printStackTrace();
			}
		}
		
	}
	//注册服务
	private static void regserver() {
		//1 建立zk连接
		ZkClient zkClient = new ZkClient(zkServers , sessionTimeout , 10000);
		//2 创建父节点
		String root = "/toov5";
		if(!zkClient.exists(root)){
			zkClient.createPersistent(root);
		}
		//3 创建子节点
		String nodename = root+"/service_"+port;
		String nodeValue = "127.0.0.1:"+port;
		
		if(zkClient.exists(nodename)){
			zkClient.delete(nodename);
		}
		zkClient.createEphemeral(nodename,nodeValue );
//		zkClient.createEphemeral(nodename);
		System.out.println("服务注册成功:"+nodename);
	}
	public static void main(String[] args) {
		ZkServerSocket server = new ZkServerSocket(port);
		regserver();
		Thread thread = new Thread(server);
		thread.start();
	}

	
	

}
