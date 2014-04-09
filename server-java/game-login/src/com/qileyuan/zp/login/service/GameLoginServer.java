package com.qileyuan.zp.login.service;

import org.apache.log4j.Logger;

import com.qileyuan.tatala.socket.server.AioSocketServer;
import com.qileyuan.tatala.util.Configuration;

public class GameLoginServer {
	static Logger log = Logger.getLogger(GameLoginServer.class);
	
	public static void initialize(){
		log.info("Game Login Server initialize...");
	}
	
	public static void startup(){
		log.info("Game Login Server starting...");
		
		int listenPort = Configuration.getIntProperty("Server.Socket.listenPort");
		int poolSize = Configuration.getIntProperty("Server.Socket.poolSize");
		
		AioSocketServer server = new AioSocketServer(listenPort, poolSize);

		server.start();
	}
	
	public static void main(String args[]) {
		log.info("*** Game Login Server ***");
		initialize();
		startup();
	}
}
