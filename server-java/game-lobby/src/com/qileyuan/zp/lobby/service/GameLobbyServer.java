package com.qileyuan.zp.lobby.service;

import org.apache.log4j.Logger;

import com.qileyuan.tatala.socket.server.AioSocketServer;
import com.qileyuan.tatala.util.Configuration;

public class GameLobbyServer {
	static Logger log = Logger.getLogger(GameLobbyServer.class);
	
	public static void initialize(){
		log.info("Game Lobby Server initialize...");
	}
	
	public static void startup(){
		log.info("Game Lobby Server starting...");
		
		int listenPort = Configuration.getIntProperty("Server.Socket.listenPort");
		int poolSize = Configuration.getIntProperty("Server.Socket.poolSize");
		
		AioSocketServer server = new AioSocketServer(listenPort, poolSize);

		server.start();
	}
	
	public static void main(String args[]) {
		log.info("*** Game Lobby Server ***");
		initialize();
		startup();
	}
}
