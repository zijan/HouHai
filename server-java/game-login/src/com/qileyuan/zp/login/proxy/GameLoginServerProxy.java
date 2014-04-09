package com.qileyuan.zp.login.proxy;

import java.util.List;

import com.qileyuan.tatala.executor.ServerExecutor;
import com.qileyuan.tatala.socket.client.SocketConnection;
import com.qileyuan.tatala.socket.client.SocketController;
import com.qileyuan.tatala.socket.to.NewTransferObject;
import com.qileyuan.tatala.socket.to.TransferObject;
import com.qileyuan.tatala.socket.to.TransferObjectFactory;
import com.qileyuan.zp.login.service.GameLoginServerLogic;

public class GameLoginServerProxy {

	private String GatewayServerNamePrefix = "game-gateway";
	
	private GameLoginServerLogic serverLogic = GameLoginServerLogic.getInstance();

	public void login(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		String userName = to.getString();
		int gatewayId = to.getInt();
		serverLogic.login(gatewayId, userId, userName);
	}

	public void receiveMessage(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		String message = to.getString();
		serverLogic.receiveMessage(userId, message);
	}
	
	public void logout(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		serverLogic.removePlayer(userId);
	}
	
	public void sendMessage(int[] receiverIds, int userId, String message) {
		//go through all gateway server
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if (connection.getName().startsWith(GatewayServerNamePrefix)) {
				TransferObjectFactory transferObjectFactoryGateway = new TransferObjectFactory(connection.getName(), true);
				NewTransferObject to = transferObjectFactoryGateway.createNewTransferObject();
				to.setCalleeMethod("executeServerCall");
				to.putString("receiveMessage");
				to.putIntArray(receiverIds);
				to.putInt(userId);
				to.putString(message);
				to.registerReturnType(TransferObject.DATATYPE_VOID);

				ServerExecutor.execute(to);
			}
		}
	}
	
	public void exitLobby(int userId){
		TransferObjectFactory lobbyToFactory = new TransferObjectFactory("game-lobby", true);
		lobbyToFactory.setCalleeClass("com.qileyuan.zp.lobby.proxy.GameLobbyServerProxy");
		NewTransferObject to = lobbyToFactory.createNewTransferObject();
		to.setCalleeMethod("exitLobby");
		to.putInt(userId);
		to.registerReturnType(TransferObject.DATATYPE_VOID);
		ServerExecutor.execute(to);
	}
	
	public void exitArena(int userId){
		TransferObjectFactory arenaToFactory = new TransferObjectFactory("game-arena", true);
		arenaToFactory.setCalleeClass("com.qileyuan.zp.arena.proxy.GameArenaServerProxy");
		NewTransferObject to = arenaToFactory.createNewTransferObject();
		to.setCalleeMethod("exitArena");
		to.putInt(userId);
		to.registerReturnType(TransferObject.DATATYPE_VOID);
		ServerExecutor.execute(to);
	}
	
	public String getLoginStatus(TransferObject to){
		String result = serverLogic.getLoginStatus();
		return result;
	}
	
	public boolean isPlayerLogin(TransferObject baseto){
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		return serverLogic.isPlayerLogin(userId);
	}
}
