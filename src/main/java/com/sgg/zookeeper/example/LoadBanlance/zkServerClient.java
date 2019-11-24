package com.sgg.zookeeper.example.LoadBanlance;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;

public class zkServerClient {
	public static List<String> listServer = new ArrayList<String>();
	private static String zkServers ="mles01:2181,mles02:2181,mles03:2181";
	private static int sessionTimeout =  5000;
	private static int connectionTimeout = 10000;
	//请求总数
	private static int reqCount = 1;
	//服务个数
	private static int serverCount = 0;
	
	public static void main(String[] args) {
		initServer();
		zkServerClient client = new zkServerClient();
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String name;
			try{
				name = console.readLine();
				if("exit".equals(name)){
					System.exit(0);
				}
				client.send(name);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	
	//注册所有server	
	private static void initServer() {
		listServer.clear();
//		listServer.add("127.0.0.1:18080");
		//建立zk连接
		ZkClient zkClient = new ZkClient(zkServers, sessionTimeout, connectionTimeout);
		String root = "/toov5";
		List<String> children = zkClient.getChildren(root);
		for (String child : children) {
			String path = root+"/"+child;
			String nodeValue = zkClient.readData(path);
			listServer.add(nodeValue);
			
		}
		System.out.println("服务发现:"+listServer.toString());
		serverCount  = listServer.size();
	}
	public static String getServer(){
		
//		return listServer.get(0);
		//本地负载均衡轮训算法
		String serverName = listServer.get(reqCount%serverCount);
		System.out.println("客户端请求次数:"+reqCount+"对应服务器"+serverName);
		reqCount++;
		return serverName;
		
	}
	private void send(String name) {
		String server = zkServerClient.getServer();
		String[] cfg = server.split(":");
		
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out =null;
		try{
			socket = new Socket(cfg[0],Integer.parseInt(cfg[1]));
			in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out= new PrintWriter(socket.getOutputStream(),true);
			out.println(name);
			
			while(true){
				String resp = in.readLine();
				if(resp==null){
					break;
				}else if(resp.length()>0){
					System.out.println("Receive:"+resp);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(out!=null){
				out.close();
			}
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	}

}
