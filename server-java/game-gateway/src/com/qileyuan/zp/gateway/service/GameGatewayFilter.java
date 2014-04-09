package com.qileyuan.zp.gateway.service;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.qileyuan.tatala.socket.server.ServerSession;
import com.qileyuan.tatala.socket.server.SessionFilter;
import com.qileyuan.tatala.socket.util.NetworkUtil;
import com.qileyuan.tatala.util.Configuration;

public class GameGatewayFilter implements SessionFilter {
	static Logger log = Logger.getLogger(GameGatewayFilter.class);
	
	@Override
	public void onClose(ServerSession session) {
		try {
			long clientId = NetworkUtil.getClientIdBySocketChannel(session.getSocketChannel());
			GameGatewayServerLogic.getInstance().logout(clientId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onReceive(ServerSession session, byte[] receiveData) {
		//check and send Unity3D Policy response xml.
		if(Configuration.getProperty("Unity.PolicyRequest.xml") != null
				&& Configuration.getProperty("Unity.PolicyRequest.xml").equals(new String(receiveData).trim())){
			
			String unityPolicyResponseXml = Configuration.getProperty("Unity.PolicyResponse.xml");
			try {
				//send Unity Policy File response
				ByteBuffer byteBuffer = ByteBuffer.wrap(unityPolicyResponseXml.getBytes());
				session.getSocketChannel().write(byteBuffer).get();
				return false;
			} catch (Exception e) {
				log.error("Unity Policy Response Xml: " + unityPolicyResponseXml);
				log.error("Send Unity Policy Response error: " + e);
			}
		}
		return true;
	}
}
