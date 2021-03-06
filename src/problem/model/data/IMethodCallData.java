package problem.model.data;

import problem.model.visit.ITraverser;

public interface IMethodCallData extends ITraverser{
	
	public void setCallingClass(String callingClass);
	public String getCallingClass();
	public void setMethod(IMethodData method);
	public IMethodData getMethod();
	public void setMethodClass(String methodClass);
	public String getMethodClass();
	public void setDepth(int depth);
	public int getDepth();
	public void setName(String name);
	public String getName();
	
}
