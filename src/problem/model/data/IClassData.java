package problem.model.data;

import java.util.List;

import problem.model.visit.ITraverser;

public interface IClassData extends ITraverser{

	public String getName();

	public void setName(String name);

	public String getSuperClass();

	public void setSuperClass(String superClass);

	public List<String> getImplementedClasses();

	public void setImplementedClasses(List<String> interfaces);

	public void addField(IFieldData f);
	
	public List<IFieldData> getFields();
	
	public void addMethod(IMethodData m);
    
	public List<IMethodData> getMethods();
	
	public String getUMLString();

	public List<String> getAssociatedClasses();
	
	public boolean hasPattern();
	
	public String getPattern();
	
	public String getFill();

	public void setPattern(String string);

	public void setFill(String string);

	public void setHasPattern(boolean b);
	
	public boolean isInterface();
	
	public void setIsInterface(boolean b);
}
