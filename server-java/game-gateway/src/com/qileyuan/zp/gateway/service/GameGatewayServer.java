package com.qileyuan.zp.gateway.service;

import org.apache.log4j.Logger;

import com.qileyuan.tatala.proxy.DefaultProxy;
import com.qileyuan.tatala.socket.server.AioSocketServer;
import com.qileyuan.tatala.util.Configuration;
import com.qileyuan.zp.gateway.proxy.GameGatewayProxy;

public class GameGatewayServer {
	static Logger log = Logger.getLogger(GameGatewayServer.class);

	public static void initialize() {
		//log.info("Game Gateway Server initialize...");
	}

	public static void startup() {
		log.info("Game Gateway Server starting...");

		int listenPort = Configuration.getIntProperty("Server.Socket.listenPort");
		int poolSize = Configuration.getIntProperty("Server.Socket.poolSize");

		AioSocketServer server = new AioSocketServer(listenPort, poolSize);

		GameGatewayFilter filter = new GameGatewayFilter();
		server.registerSessionFilter(filter);

		DefaultProxy defaultProxy = new GameGatewayProxy();
		server.registerProxy(defaultProxy);
		
		server.start();

		GameGatewayServerLogic serverLogic = GameGatewayServerLogic.getInstance();
		serverLogic.setSessionMap(AioSocketServer.getSessionMap());
	}

	public static void main(String args[]) {
		log.info("*** Game Gateway Server ***");
		initialize();
		startup();
	}
}
