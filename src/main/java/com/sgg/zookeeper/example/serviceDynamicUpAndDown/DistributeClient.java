package com.sgg.zookeeper.example.serviceDynamicUpAndDown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DistributeClient {
	public static void main(String[] args) throws Exception {
		DistributeClient dc = new DistributeClient();
		dc.getConnect();
		dc.getServerList();
		dc.bussiness();
	}

	

	private String connectString = "mles01:2181,mles02:2181,mles03:2181";
	private int sessionTimeout = 2000;
	private ZooKeeper zkClient = null;
	private String parentNode = "/servers";

	private void getConnect() throws IOException {
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher(){

			public void process(WatchedEvent event) {
				// 再次启动监听
				try{
					getServerList();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		});
		
	}
	private void getServerList() throws Exception {
		List<String> children = zkClient.getChildren(parentNode, true);
		ArrayList<String> servers = new ArrayList<String>();
		for (String child : children) {
			byte[] data = zkClient.getData(parentNode+"/"+child, false, null);
			servers.add(new String(data));
		}
		System.out.println(servers);
		
		
	}
	
	public void bussiness() throws InterruptedException{
		System.out.println("client is working....");
		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	

}
