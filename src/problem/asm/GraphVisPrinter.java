package problem.asm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphVisPrinter implements IClassStructurePrinter{

	private Map<String, List<String>> classToInterfaces = new HashMap<>();
	private Map<String, String> classToSuperclass = new HashMap<>();
	private Map<String, List<String>> classToAssociatedClasses = new HashMap<>();
	private Map<String, List<IMethodData>> classToMethods = new HashMap<>();
	private List<String> classNames;
	private List<AbstractClassDataVisitor> classes;
	
	public GraphVisPrinter(List<AbstractClassDataVisitor> classes){
		this.classNames = StringParser.getClassNames(classes);
		this.classes = classes;
	}
	
	@Override
	public void printToFile(OutputStream out){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("digraph G {\n");
			sb.append("fontname = \" Bitstream Vera San\"\n");
			sb.append("fontsize =8\n");
			
			sb.append("node [\n");
			sb.append("fontname = \"Bitstream Vera Sans\"\n");
			sb.append("fontsize =8\n");
			sb.append("shape = \"record\"");
			sb.append("]\n");
			
			sb.append("edge [\n");
			sb.append("fontname = \"Bitstream Vera Sans\"\n");
			sb.append("fontsize =8\n");
			sb.append("]\n");
			
			for(AbstractClassDataVisitor currentData: this.classes){
				sb.append(currentData.getUMLString());
				this.classToSuperclass.put(currentData.getName(), currentData.getSuperClass());
				this.classToInterfaces.put(currentData.getName(), currentData.getImplementedClasses());
				this.classToAssociatedClasses.put(currentData.getName(), currentData.getAssociatedClasses());
				this.classToMethods.put(currentData.getName(), currentData.getMethods());
			}
			sb.append(this.createArrows());
			sb.append("}\n");
			out.write(sb.toString().getBytes());
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public Map<String, List<String>> getClassToInterfaces() {
		return this.classToInterfaces;
	}

	public void setClassToInterfaces(Map<String, List<String>> classToInterfaces) {
		this.classToInterfaces = classToInterfaces;
	}

	public Map<String, String> getClassToSuperclass() {
		return this.classToSuperclass;
	}

	public void setClassToSuperclass(Map<String, String> classToSuperclass) {
		this.classToSuperclass = classToSuperclass;
	}

	public Map<String, List<String>> getClassToAssociatedClasses() {
		return this.classToAssociatedClasses;
	}

	public void setClassToAssociatedClasses(
			Map<String, List<String>> classToAssociatedClasses) {
		this.classToAssociatedClasses = classToAssociatedClasses;
	}

	public Map<String, List<IMethodData>> getClassToMethods() {
		return this.classToMethods;
	}

	public void setClassToMethods(Map<String, List<IMethodData>> classToMethods) {
		this.classToMethods = classToMethods;
	}

	public List<String> getClassNames() {
		return this.classNames;
	}

	public void setClassNames(List<String> classNames) {
		this.classNames = classNames;
	}

	public List<AbstractClassDataVisitor> getClasses() {
		return this.classes;
	}

	public void setClasses(List<AbstractClassDataVisitor> classes) {
		this.classes = classes;
	}

	public String createArrows(){
		StringBuilder sb = new StringBuilder();
		sb.append("edge [ \n");
		sb.append("arrowhead = \"empty\"\n");
		sb.append("style = \"solid\"\n]\n");
		for(String className: this.classToSuperclass.keySet()){
			if(this.classNames.contains(this.classToSuperclass.get(className))){
				sb.append(className+" -> "+ this.classToSuperclass.get(className)+ "\n");
			}
		}
		sb.append("edge [ \n");
		sb.append("arrowhead = \"empty\"\n");
		sb.append("style = \"dashed\"\n]\n");
		for(String className: this.classToInterfaces.keySet()){
			for(String curInterface : this.classToInterfaces.get(className)){
				if(this.classNames.contains(curInterface)
						&&!this.classToSuperclass.get(className).equals(curInterface))
				{
					sb.append(className+" -> "+ curInterface+ "\n");
				}
			}
		}
		sb.append("edge [ \n");
		sb.append("arrowhead = \"vee\"\n");
		sb.append("style = \"solid\"\n]\n");
		for(String className: this.classToAssociatedClasses.keySet()){
			for(String assocClass: this.classToAssociatedClasses.get(className)){
				if(this.classNames.contains(assocClass)
						&&!this.classToInterfaces.get(className).contains(assocClass))
				{
					sb.append(className+" -> "+assocClass+"\n");
				}
			}
		}

		sb.append("edge [ \n");
		sb.append("arrowhead = \"vee\"\n");
		sb.append("style = \"dashed\"\n]\n");
		sb.append(getUsedClassesArrows());
		
		return sb.toString();
	}
	
	private String getUsedClassesArrows(){
		StringBuilder sb = new StringBuilder();
		List<String> usedClasses = new ArrayList<>();
		for(String className: this.classToMethods.keySet()){
			for(IMethodData method : this.classToMethods.get(className)){
				
				String paramType;
				for(String param: method.getArgs()){
					if(param.contains("\\<")){
						paramType = param.substring(param.indexOf("<")+1, param.lastIndexOf("\\"));
					}
					else{
						paramType = param;
					}
					if(!usedClasses.contains(paramType)&&this.classNames.contains(paramType)
							&&!paramType.equals(className)
							&&!this.classToAssociatedClasses.get(className).contains(paramType))
					{
						usedClasses.add(paramType);
						sb.append(className+" -> "+paramType+"\n");
					}
				}
				
				boolean returnIsConcrete = true;
				for(String usedClass: method.getUsedClasses()){
					if(usedClass.contains("\\<")){
						usedClass = usedClass.substring(usedClass.indexOf("<")+1, usedClass.lastIndexOf("\\"));
					}
					if((this.classToInterfaces.get(usedClass)!=null
						&&this.classToInterfaces.get(usedClass).contains(method.getType()))
						||(this.classToSuperclass.get(usedClass)!=null
						&&this.classToSuperclass.get(usedClass).equals(method.getType())))
					{
						returnIsConcrete = false;
					}
					if(!usedClasses.contains(usedClass)&&this.classNames.contains(usedClass)
						&&!usedClass.equals(className)
						&&!this.classToAssociatedClasses.get(className).contains(usedClass))
					{
						usedClasses.add(usedClass);
						sb.append(className+" -> "+usedClass+"\n");
					}
				}
				if(returnIsConcrete){
					String returnType;
					if(method.getType().contains("\\<")){
						returnType = method.getType().substring(method.getType().indexOf("<")+1, method.getType().lastIndexOf("\\"));
					}
					else{
						returnType = method.getType();
					}
					if(!usedClasses.contains(returnType)&&this.classNames.contains(returnType)
							&&!returnType.equals(className)
							&&!this.classToAssociatedClasses.get(className).contains(returnType)){
						usedClasses.add(returnType);
						sb.append(className+" -> "+returnType+"\n");
					}
				}
				
			}
		}
		return sb.toString();
	}


}
