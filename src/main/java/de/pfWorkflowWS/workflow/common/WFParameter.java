package de.pfWorkflowWS.workflow.common;

public class WFParameter {
	
	private String key;
	private ParameterType payloadClazz;
	private Object value;

	public WFParameter(String key, Object o) throws Exception{
		setKey(key);
		setValue(o);
		
		if(o instanceof Integer){
			setPayloadClazz(ParameterType.INTEGER);

		}else if(o instanceof String){
			setPayloadClazz(ParameterType.STRING);

		}else if (o instanceof Long){
			setPayloadClazz(ParameterType.LONG);

		}else if (o instanceof Double){
			setPayloadClazz(ParameterType.DOUBLE);
		}else{
			throw new Exception("Object has to be complex type");
		}
	}

	public Object getValue() {
		return value;
	}
	
	private void setValue(Object value) {
		this.value = value;
	}
	
	public ParameterType getPayloadClazz() {
		return payloadClazz;
	}
	
	private void setPayloadClazz(ParameterType payloadClazz) {
		this.payloadClazz = payloadClazz;
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	

}
