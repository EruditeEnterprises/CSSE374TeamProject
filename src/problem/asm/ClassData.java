package problem.asm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassData implements IClassData {
	protected String className;
	protected String superClass;
	protected List<String> implementedClasses;
	protected List<IFieldData> fields = new ArrayList<>();
	protected List<IMethodData> methods = new ArrayList<>();
	protected List<String> associatedClasses = new ArrayList<>();
	protected boolean hasPattern = false;
	protected String pattern = "";
	protected String fill = "";
	
	public void setHasPattern(boolean hasPattern) {
		this.hasPattern = hasPattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}
	
	public boolean hasPattern() {
		return hasPattern;
	}

	public String getPattern() {
		return pattern;
	}

	public String getFill() {
		return fill;
	}

	@Override
	public String getName() {
		return this.className;
	}

	@Override
	public void setName(String name) {
		this.className = name;
	}

	@Override
	public String getSuperClass() {
		return this.superClass;
	}

	@Override
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	@Override
	public List<String> getImplementedClasses() {
		return this.implementedClasses;
	}

	@Override
	public void setImplementedClasses(List<String> interfaces) {
		List<String> list = new ArrayList<>();
		for(String inter: interfaces){
			list.add(StringParser.parseClassName(inter));
		}
		this.implementedClasses = list;
	}

	@Override
	public void addField(IFieldData f) {
		this.fields.add(f);
		String fieldType = f.getType();
		if(fieldType.contains("\\<")){
			fieldType = fieldType.substring(fieldType.indexOf("<"), fieldType.lastIndexOf("\\"));
		}		
		
		if(!this.associatedClasses.contains(fieldType)&&this.className!=fieldType){
			this.associatedClasses.add(fieldType);
		}
	}
	
	@Override
	public List<IFieldData> getFields() {
		return this.fields;
	}
	
	@Override
	public void addMethod(IMethodData m) {
		this.methods.add(m);
	}
    
	@Override
	public List<IMethodData> getMethods() {
		return this.methods;
	}
	
	@Override
	public String getUMLString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.className);
		sb.append(" [\n");
		if(hasPattern)
			sb.append(fill);
		sb.append("label = \"{"+this.className);
		if(hasPattern)
			sb.append(pattern);
		sb.append("|");
		for(IFieldData fd : this.fields) {
			sb.append(fd.toString());
		}
		sb.append("|");
		for(IMethodData md : this.methods) {
			sb.append(md.toString());
		}
		sb.append("}\"\n]\n");
		return sb.toString();
	}

	@Override
	public List<String> getAssociatedClasses() {
		return this.associatedClasses;
	}
	
	@Override
	public void accept(IVisitor v) throws IOException {
//		this.scanForPatterns();
		v.preVisit(this);
		for(IFieldData field : this.fields){
			field.accept(v);
		}
		v.visit(this);
		for(IMethodData method : this.methods){
			method.accept(v);
		}
		v.postVisit(this);
	}
}
