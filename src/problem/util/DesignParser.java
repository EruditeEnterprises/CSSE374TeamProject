package problem.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import problem.asm.AbstractASMVisitor;
import problem.asm.VisitorManager;
import problem.detector.IPatternDetector;
import problem.detector.InterfaceDetector;
import problem.model.data.IClassData;
import problem.model.data.PackageModel;
import problem.model.visit.IVisitor;
import problem.model.visit.UMLVisitor;

public class DesignParser extends Observable{
	
	private File configFile;
	private ConfigReader reader;
	private PackageModel model;
	private String imagePath;
	private boolean threadIsRunning = false;
	private Map<String, IPatternDetector> detectorMap;

	public DesignParser(File configFile, Map<String, IPatternDetector> detectors){
		this.configFile = configFile;
		this.detectorMap = detectors;
	}
	
	public void createThread(){
		if(!this.threadIsRunning){
			this.threadIsRunning = true;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						DesignParser.this.readClassesFromConfig();
						DesignParser.this.createModel();
						DesignParser.this.createOutputFiles();
					} 
					catch (IOException exception) {
						exception.printStackTrace();
					}
					DesignParser.this.setThreadIsRunning(false);
				}
			});
			t.start();
		}
		
	}
	
	public void readClassesFromConfig() throws IOException{
		this.reader = new ConfigReader();
		this.reader.configProject(this.configFile);
		this.notifyObservers();
	}
	
	public void createModel() throws IOException{
		List<IClassData> classDatas = new ArrayList<>();
		for (String className : this.reader.getClasses()) {
			System.out.println(className);
			AbstractASMVisitor visitor = VisitorManager.visitClass(className);
			classDatas.add(visitor.getClassData());
		}
		
		List<IPatternDetector> detectors = phaseSelector(this.reader.getPhases());
		updateDetectors(detectors);
		
		this.model = new PackageModel(detectors);
		this.model.setClasses(classDatas);
		this.notifyObservers();
	}
	
	public void createOutputFiles() throws IOException{
		OutputStream out = new FilterOutputStream(new FileOutputStream(this.reader.getOutputFile()+"\\Output.dot"));
		IVisitor visitor = new UMLVisitor();
		this.model.accept(visitor);
		visitor.printToOutput(out);
		out.close();
		Runtime rt = Runtime.getRuntime();
		String outputString= this.reader.getOutputFile()+"\\\\Output.";
		this.imagePath = outputString+"png";
		String command = "\""+this.reader.getDotPath()+"\" -Tpng " +outputString +"dot -o "+this.imagePath;
		rt.exec(command);

		this.notifyObservers();
	}
	
	private List<IPatternDetector> phaseSelector(List<String> phases) {
		List<IPatternDetector> detectors = new ArrayList<>();
		detectors.add(new InterfaceDetector());
		for(String phase : phases){
			if(this.detectorMap.containsKey(phase)&&!detectors.contains(phase)){
				detectors.add(this.detectorMap.get(phase));
			}
		}
		return detectors;
	}
	private void updateDetectors(List<IPatternDetector> detectors){
		for(String phaseInstr : this.reader.getPhaseInstructions()){
			String phaseName = phaseInstr.substring(0, phaseInstr.indexOf("-"));
			if(this.detectorMap.containsKey(phaseName)){
				for(IPatternDetector detector : detectors){
					if(detector.getPatternName().equalsIgnoreCase(phaseName)){
						String valueName = phaseInstr.substring(phaseInstr.indexOf("-")+1, phaseInstr.indexOf(":"));
						String value = phaseInstr.substring(phaseInstr.indexOf(": ")+2);
						detector.update(valueName, value);
					}
				}
			}
		}
	}

	public ConfigReader getReader() {
		return this.reader;
	}

	public void setReader(ConfigReader reader) {
		this.reader = reader;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public PackageModel getModel() {
		return this.model;
	}

	public void setModel(PackageModel model) {
		this.model = model;
	}

	public boolean isThreadIsRunning() {
		return this.threadIsRunning;
	}

	public void setThreadIsRunning(boolean threadIsRunning) {
		this.threadIsRunning = threadIsRunning;
	}

}
