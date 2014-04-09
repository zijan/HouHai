package com.qileyuan.zp.gateway.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import com.qileyuan.tatala.executor.ServerExecutor;
import com.qileyuan.tatala.socket.client.SocketConnection;
import com.qileyuan.tatala.socket.client.SocketController;
import com.qileyuan.tatala.socket.server.ServerSession;
import com.qileyuan.tatala.socket.to.NewTransferObject;
import com.qileyuan.tatala.socket.to.TransferObject;
import com.qileyuan.tatala.util.Configuration;
import com.thoughtworks.xstream.XStream;

public class GameGatewayServerLogic {
	static Logger log = Logger.getLogger(GameGatewayServerLogic.class);

	private static GameGatewayServerLogic serverLogic = new GameGatewayServerLogic();

	private Map<Long, ServerSession> sessionMap; // <clientId, session>
	private BidiMap clientBidiMap; // <clientId, userId>
	private Map<Long, Boolean> aliveMap; // <clientId, aliveflag>

	private List<RelayCall> relayCallList;
	private List<String> trustIPList = new ArrayList<String>();
	public static String MethodNameLogin = "login";
	public static String MethodNameLogout = "logout";
	public static String MethodNameEcho = "echo";

	private GameGatewayServerLogic() {
		clientBidiMap = new DualHashBidiMap();
		aliveMap = new HashMap<Long, Boolean>();

		loadRelayCallMapping();

		loadTrustIPList();
		
		startAliveCheck();
	}

	public static GameGatewayServerLogic getInstance() {
		return serverLogic;
	}

	public Map<Long, ServerSession> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<Long, ServerSession> sessionMap) {
		this.sessionMap = sessionMap;
	}

	@SuppressWarnings("unchecked")
	private void loadRelayCallMapping() {
		XStream xstream = new XStream();
		xstream.alias("RelayCalls", List.class);
		xstream.alias("RelayCall", RelayCall.class);

		InputStream is = SocketController.class.getClassLoader().getResourceAsStream("relaycall.xml");
		if (is == null) {
			throw new RuntimeException("Can't find relaycall.xml");
		}
		relayCallList = (List<RelayCall>) xstream.fromXML(is);
	}

	private void loadTrustIPList(){
		List<SocketConnection> connectionList = SocketController.getConnectionList();
		for (SocketConnection connection : connectionList) {
			if(!trustIPList.contains(connection.getHostIp())){
				trustIPList.add(connection.getHostIp());
			}
		}
	}
	
	public void executeClientCall(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		
		if (relayCallList == null) {
			loadRelayCallMapping();
		}

		String methodName = to.getString();
		
		//log.debug("ClientCall.MethodName: " + methodName);

		if (methodName.equals(MethodNameLogin)) {
			// if user is already login return
			if (!login(to)) {
				return;
			}
		} else if (methodName.equals(MethodNameLogout)) {
			logout(to);

			// echo call, set alive flag is true, return don't relay client call
		} else if (methodName.equals(MethodNameEcho)) {
			echo(to);
			return;
		}

		// relay client call to target connection
		for (RelayCall relayCall : relayCallList) {
			if (relayCall.getMethod().equals(methodName)) {
				to.setConnectionName(relayCall.getConnectionName());
				to.setCalleeClass(relayCall.getCalleeClass());
				to.setCalleeMethod(methodName);
				ServerExecutor.execute(to);
			}
		}
	}

	// the first call, when user login
	public boolean login(NewTransferObject to) {
		long clientId = to.getClientId();
		int userId = (int)to.peek(0);

		if (clientBidiMap.containsKey(clientId)) {
			log.debug("[" + clientBidiMap.get(clientId) + "] already longin");
			return false;
		}
		log.debug("[" + userId + "] Longin");

		// add new player into map
		clientBidiMap.put(clientId, userId);
		aliveMap.put(clientId, true);

		//put gateway id into TransferObject, and pass to login server
		int gatewayId = Configuration.getIntProperty("Server.GatewayId");
		to.putInt(gatewayId);
		
		return true;
	}

	// when user call logout for disconnect, remove user
	public void logout(NewTransferObject to) {
		long clientId = to.getClientId();
		if (clientBidiMap.containsKey(clientId)) {
			clientBidiMap.remove(clientId);
			aliveMap.remove(clientId);
		}
	}

	// when detected user disconnected, remove user and relay call
	public void logout(long clientId) {
		if (clientBidiMap.containsKey(clientId)) {
			int userId = (int) clientBidiMap.get(clientId);

			NewTransferObject to = new NewTransferObject();
			to.setNewVersion(true);
			to.setClientId(clientId);
			to.putString(MethodNameLogout);
			to.putInt(userId);
			to.registerReturnType(TransferObject.DATATYPE_VOID);
			to.setLongConnection(true);
			executeClientCall(to);
		}
	}

	private void echo(NewTransferObject to) {
		// echo call, set alive is true
		long clientId = to.getClientId();
		if (aliveMap.containsKey(clientId)) {
			aliveMap.put(clientId, true);
		}
	}

	public void executeServerCall(TransferObject baseto) {
		NewTransferObject to = (NewTransferObject)baseto;
		
		String methodName = to.getString();
		int[] receverIds = to.getIntArray();

		for(int receiverId : receverIds){
			if(clientBidiMap.containsValue(receiverId)){
				long clientId = (long) clientBidiMap.getKey(receiverId);
				ServerSession session = sessionMap.get(clientId);
				if(session != null){
					//log.debug("ServerCall.MethodName: " + methodName);
					to.setCalleeClass(TransferObject.DEFAULT_PROXY);
					to.setServerCall(true);
					to.setCalleeMethod(methodName);
					session.executeServerCall(to);
				}
			}
		}
	}

	private void startAliveCheck() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					if(sessionMap == null || sessionMap.size() == 0){
						return;
					}
					
					Long[] clientIds = sessionMap.keySet().toArray(new Long[0]);
					for(long clientId : clientIds){
						//if contain clientId
						if(aliveMap.containsKey(clientId)){
							if(aliveMap.get(clientId)){
								// true means alive, set false for next check
								aliveMap.put(clientId, false);
							}else{
								// false means client disconnected, logout
								logout(clientId);
								
								//close session
								if(sessionMap.containsKey(clientId)){
									sessionMap.get(clientId).close();
								}
							}
						}else{
							//if don't contain clientId, and not in trust list, logout
							ServerSession session = sessionMap.get(clientId);
							String IPstr = session.getClientIP();
							if(!trustIPList.contains(IPstr)){
								// false means client disconnected, logout
								logout(clientId);
								
								//close session
								if(sessionMap.containsKey(clientId)){
									sessionMap.get(clientId).close();
								}
							}
						}
					}
					
					//clear dirty data
					clearMap();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 120 * 1000);
	}
	
	private void clearMap(){
		Object[] objs = clientBidiMap.keySet().toArray();
		for(Object key : objs){
			long clientId = (long)key;
			if(!sessionMap.containsKey(clientId)){
				clientBidiMap.remove(clientId);
			}
		}
		
		Long[] clientIds = aliveMap.keySet().toArray(new Long[0]);
		for(long clientId : clientIds){
			if(!sessionMap.containsKey(clientId)){
				aliveMap.remove(clientId);
			}
		}
	}
	
	public void showServerStatus(){
		log.info("clientBidiMap:"+clientBidiMap);
		log.info("aliveMap:"+aliveMap);
		log.info("sessionMap:"+sessionMap);
	}
}
