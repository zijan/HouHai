package com.qileyuan.zp.lobby.proxy;

import java.util.List;

import com.qileyuan.tatala.executor.ServerExecutor;
import com.qileyuan.tatala.socket.client.SocketConnection;
import com.qileyuan.tatala.socket.client.SocketController;
import com.qileyuan.tatala.socket.to.NewTransferObject;
import com.qileyuan.tatala.socket.to.TransferObject;
import com.qileyuan.tatala.socket.to.TransferObjectFactory;
import com.qileyuan.zp.lobby.service.GameLobbyServerLogic;

public class GameLobbyServerProxy {

	private String GatewayServerNamePrefix = "game-gateway";
	
	private GameLobbyServerLogic serverLogic = GameLobbyServerLogic.getInstance();

	public void enterLobby(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		String userName = to.getString();
		int userLevel = to.getInt();
		int equipedWeaponTypeId = to.getInt();
		float[] position = to.getFloatArray();
		serverLogic.enterLobby(userId, userName, userLevel, equipedWeaponTypeId, position);
	}

	public void receiveMovement(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		float[] targetPosition = to.getFloatArray();
		serverLogic.receiveMovement(userId, targetPosition);
	}
	
	public void exitLobby(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		int userId = to.getInt();
		serverLogic.exitLobby(userId);
	}
	
	public void addPlayer(int[] receiverIds, int userId, String userName, int userLevel, int equipedWeaponTypeId, float[] position) {
		//go through all gateway server
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if (connection.getName().startsWith(GatewayServerNamePrefix)) {
				TransferObjectFactory transferObjectFactoryGateway = new TransferObjectFactory(connection.getName(), true);
				
				NewTransferObject to = transferObjectFactoryGateway.createNewTransferObject();
				to.setCalleeMethod("executeServerCall");
				to.putString("addPlayer");
				to.putIntArray(receiverIds);
				to.putInt(userId);
				to.putString(userName);
				to.putInt(userLevel);
				to.putInt(equipedWeaponTypeId);
				to.putFloatArray(position);
				to.registerReturnType(TransferObject.DATATYPE_VOID);

				ServerExecutor.execute(to);
			}
		}
	}
	
	public void sendMovement(int[] receiverIds, int userId, float[] targetPosition) {
		//go through all gateway server
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if (connection.getName().startsWith(GatewayServerNamePrefix)) {
				TransferObjectFactory transferObjectFactoryGateway = new TransferObjectFactory(connection.getName(), true);
				
				NewTransferObject to = transferObjectFactoryGateway.createNewTransferObject();
				to.setCalleeMethod("executeServerCall");
				to.putString("receiveMovement");
				to.putIntArray(receiverIds);
				to.putInt(userId);
				to.putFloatArray(targetPosition);
				to.registerReturnType(TransferObject.DATATYPE_VOID);

				ServerExecutor.execute(to);
			}
		}
	}
	
	public void sendPlayerInfos(int[] receiverIds, int userId, int[] playerIds, String[] playerNames, int[] playerLevels, int[] equipedWeaponTypeIds, float[] playerPositions) {
		//go through all gateway server
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if (connection.getName().startsWith(GatewayServerNamePrefix)) {
				TransferObjectFactory transferObjectFactoryGateway = new TransferObjectFactory(connection.getName(), true);
				
				NewTransferObject to = transferObjectFactoryGateway.createNewTransferObject();
				to.setCalleeMethod("executeServerCall");
				to.putString("receivePlayerInfos");
				to.putIntArray(receiverIds);
				to.putInt(userId);
				to.putIntArray(playerIds);
				to.putStringArray(playerNames);
				to.putIntArray(playerLevels);
				to.putIntArray(equipedWeaponTypeIds);
				to.putFloatArray(playerPositions);
				to.registerReturnType(TransferObject.DATATYPE_VOID);

				ServerExecutor.execute(to);
			}
		}
	}
	
	public void removePlayer(int[] receiverIds, int userId) {
		//go through all gateway server
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if (connection.getName().startsWith(GatewayServerNamePrefix)) {
				TransferObjectFactory transferObjectFactoryGateway = new TransferObjectFactory(connection.getName(), true);
				
				NewTransferObject to = transferObjectFactoryGateway.createNewTransferObject();
				to.setCalleeMethod("executeServerCall");
				to.putString("removePlayer");
				to.putIntArray(receiverIds);
				to.putInt(userId);
				to.registerReturnType(TransferObject.DATATYPE_VOID);

				ServerExecutor.execute(to);
			}
		}
	}
	
	public String getLobbyStatus(TransferObject to){
		String result = serverLogic.getLobbyStatus();
		return result;
	}
	
	public boolean isPlayerLogin(int userId){
		TransferObjectFactory loginToFactory = new TransferObjectFactory("game-login", true);
		loginToFactory.setCalleeClass("com.qileyuan.zp.login.proxy.GameLoginServerProxy");
		NewTransferObject to = loginToFactory.createNewTransferObject();
		to.setCalleeMethod("isPlayerLogin");
		to.putInt(userId);
		to.registerReturnType(TransferObject.DATATYPE_BOOLEAN);
		return (boolean)ServerExecutor.execute(to);
	}
}
