package problem.main;

import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problem.asm.VisitorManager;
import problem.detector.AdapterDetector;
import problem.detector.CompositeDetector;
import problem.detector.DecoratorDetector;
import problem.detector.IPatternDetector;
import problem.detector.SingletonDetector;
import problem.model.data.IMethodCallData;
import problem.model.data.MethodCallData;
import problem.model.visit.IVisitor;
import problem.model.visit.SDEditVisitor;
import problem.ui.MainWindow;

public class Client {
	public static final String SD_OUTPUT = "./input_output/sDiagram.sd";
	
	/**
	 * Reads in a list of Java Classes and reverse engineers their design.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length<1){
			System.out.println("NO ARGUMENTS");
			System.exit(0);
		}
		if(args[0].toLowerCase().equals("uml")){
			Map<String, IPatternDetector> detectors = new HashMap<>();
			detectors.put("Adapter", new AdapterDetector(2));
			detectors.put("Composite", new CompositeDetector());
			detectors.put("Decorator", new DecoratorDetector(1));
			detectors.put("Singleton", new SingletonDetector(false));
		    MainWindow window = new MainWindow(detectors);
		    window.show();
		}
		else if(args[0].toLowerCase().equals("sd")){
			if(args.length<2){
				System.out.println("NO ARGUMENTS");
				System.exit(0);
			}
			String methodSignature = args[1];
			int depth = 5;
			if(args.length>=3){
				depth = Integer.parseInt(args[2]);
			}
			
			List<IMethodCallData> methodCalls = new ArrayList<>();
			String firstClass = methodSignature.substring(0, methodSignature.lastIndexOf("."));
			
			IMethodCallData startingMethod = new MethodCallData();
			startingMethod.setMethodClass(firstClass);
			startingMethod.setCallingClass("");
			startingMethod.setDepth(depth + 1);
			startingMethod.setName(methodSignature.substring(methodSignature.lastIndexOf(".")+1, 
					methodSignature.lastIndexOf("(")));
		
			methodCalls = VisitorManager.getMethodCalls(startingMethod);
			
			IVisitor visitor = new SDEditVisitor(firstClass);
			for(IMethodCallData data: methodCalls){
				data.accept(visitor);
			}
			OutputStream out = new FilterOutputStream(new FileOutputStream(SD_OUTPUT));
			visitor.printToOutput(out);
			out.close();

		}
		else{
			System.out.println("INVALID COMMAND");
			System.exit(0);
		}
	}
}