package zoo.asynweb.comet;

public enum HttpAction {
	GET("get"),
	POST("post"),
	PUT("put"),
	DELETE("delete");
	
	private String v;
	
	HttpAction(String value)
	{
		this.v = value;
	}
	
	public static HttpAction getAction(String value)
	{
		if (value.endsWith("get"))
			return GET;
		
		if (value.endsWith("post"))
			return POST;
		
		if (value.endsWith("put"))
			return PUT;
		
		if (value.endsWith("delete"))
			return DELETE;
		
		throw new java.lang.RuntimeException("No Such HttpAction.");
	}
	
	@Override
	public String toString()
	{
		return v;
	}
}
