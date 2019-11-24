package com.sgg.zookeeper.demo_practice;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

public class Zookeeper_Test {
	
	private static String connectString = "mles01:2181,mles02:2181,mles03:2181";
	private int sessionTimeout = 2000;
	private ZooKeeper zkClient = null;

	@Test
	// 创建Zookeeper客户端
	public void init() throws IOException{
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher(){

			public void process(WatchedEvent event) {
				
				System.out.println(event.getType()+"--"+event.getPath());
				try{
					zkClient.getChildren("/servers", true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		});
	}
	//创建子节点
	@Test
	public void create() throws Exception{
		System.out.println("******start******");
		
		zkClient.create("/servers", "servers".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("******end*****");

	}
	//获取子节点并监听节点变化
	public void getChildren() throws KeeperException, InterruptedException{
		List<String> children = zkClient.getChildren("/servers", true);
		for (String child : children) {
			System.out.println(child);
		}
		Thread.sleep(Long.MAX_VALUE);
	}
	//判断子节点是否存在
	public void isExist() throws KeeperException, InterruptedException{
		Stat stat = zkClient.exists("/servers", false);
		System.out.println(stat==null?"not exist":"exist");
	}
	

}
