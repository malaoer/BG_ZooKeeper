package com.sgg.zookeeper.example.serviceDynamicUpAndDown;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class DistributeServer {
	//创建到zk的客户端连接
	private String connectString = "mles01:2181,mles02:2181,mles03:2181";
	private int sessionTimeout = 2000;
	private ZooKeeper zkClient = null;
	private String parentNode = "/servers";

	public void getConnect() throws IOException{
		zkClient  = new ZooKeeper(connectString, sessionTimeout, new Watcher(){

			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void registServer(String hostname) throws Exception{
		String create = zkClient.create(parentNode+"/server", hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname+" is online"+create);
	}
	
	public void bussiness(String hostname) throws InterruptedException{
		System.out.println(hostname+" is working....");
		Thread.sleep(Long.MAX_VALUE);
		
	}
	public static void main(String[] args) throws Exception{
		DistributeServer ds = new DistributeServer();
		//1.获取zk连接
		ds.getConnect();
		//2.利用zk连接注册服务器信息
		ds.registServer(args[0]);
		//3. 启动业务功能
		ds.bussiness(args[0]);
		
	}

}
