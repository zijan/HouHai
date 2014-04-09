package com.qileyuan.zp.gateway.proxy;
	
import com.qileyuan.tatala.proxy.DefaultProxy;
import com.qileyuan.tatala.socket.to.TransferObject;
import com.qileyuan.zp.gateway.service.GameGatewayServerLogic;

public class GameGatewayProxy extends DefaultProxy {
	
	private GameGatewayServerLogic serverLogic = GameGatewayServerLogic.getInstance();
	
	public Object execute(TransferObject to){
		String calleeMethod = to.getCalleeMethod();
		
		if(calleeMethod.equals("executeClientCall")){
			executeClientCall(to);
		} else if(calleeMethod.equals("executeServerCall")){
			executeServerCall(to);
		} else if(calleeMethod.equals("showServerStatus")){
			showServerStatus(to);
		}
		return null;
	}
	
	public void executeClientCall(TransferObject to){
		serverLogic.executeClientCall(to);
	}

	public void executeServerCall(TransferObject to){
		serverLogic.executeServerCall(to);
	}
	
	public void showServerStatus(TransferObject to){
		serverLogic.showServerStatus();
	}
}
