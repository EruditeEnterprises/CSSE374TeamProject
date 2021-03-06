package problem.asm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import problem.model.data.IMethodCallData;

public class VisitorManager {
	
	public static AbstractASMVisitor visitClass(String className) throws IOException{
		ClassReader reader = new ClassReader(className);
		ClassMethodVisitor methodVisitor = buildVisitor(className);
		
		// Tell the Reader to use our (heavily decorated) ClassVisitor to visit the class
		reader.accept(methodVisitor, ClassReader.EXPAND_FRAMES);
		return methodVisitor;
	}
	
	public static ClassMethodVisitor visitMethods(String className, IMethodCallData callData) throws IOException{
		ClassReader reader = new ClassReader(className);
		ClassMethodVisitor methodVisitor = buildVisitor(className);
		methodVisitor.setCallData(callData);
		
		// Tell the Reader to use our (heavily decorated) ClassVisitor to visit the class
		reader.accept(methodVisitor, ClassReader.EXPAND_FRAMES);
		return methodVisitor;
	}
	
	private static ClassMethodVisitor buildVisitor(String className) throws IOException{
		// make class declaration visitor to get superclass and interfaces
		AbstractASMVisitor decVisitor = new ClassDeclarationVisitor(Opcodes.ASM5, null);
		// DECORATE declaration visitor with field visitor
		AbstractASMVisitor fieldVisitor = new ClassFieldVisitor(Opcodes.ASM5,
				decVisitor);
		// DECORATE field visitor with method visitor
		ClassMethodVisitor methodVisitor = new ClassMethodVisitor(Opcodes.ASM5,
				fieldVisitor);
		return methodVisitor;
	}
	
	public static List<IMethodCallData> getMethodCalls(IMethodCallData data) throws IOException{
		List<IMethodCallData> calls = new ArrayList<>();
		ClassMethodVisitor methodVisitor = visitMethods(data.getMethodClass(), data);
		for(IMethodCallData callData: methodVisitor.getMethodCalls()){
			callData.setCallingClass(data.getMethodClass());
			if(callData.getDepth()!=0){
				callData.setDepth(data.getDepth()-1);
			}
			if(callData.getDepth()>0){
				calls.add(callData);
				calls.addAll(getMethodCalls(callData));
			}
		}
		return calls;
	}

}
