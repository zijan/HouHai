package com.qileyuan.zp.gateway.service;

public class RelayCall {
	private String method;
	private String connectionName;
	private String calleeClass;
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getCalleeClass() {
		return calleeClass;
	}

	public void setCalleeClass(String calleeClass) {
		this.calleeClass = calleeClass;
	}
}
