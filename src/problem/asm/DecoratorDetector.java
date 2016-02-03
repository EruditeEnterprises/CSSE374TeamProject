package problem.asm;

import java.util.List;

public class DecoratorDetector implements IPatternDetector {
	private boolean pattern = false;

	@Override
	public void findPattern(IClassData d, IPackageModel m) {
		for (IMethodData meth : d.getMethods()) {
			if (meth.getName().equals("<init>")) {
				for(String asc : d.getAssociatedClasses()) {
					if (meth.getArgs().contains(asc) && (d.getImplementedClasses().contains(asc) 
							|| d.getSuperClass().equalsIgnoreCase(asc))) {
						IClassData in = m.getClassDataFromName(asc);
						if (in != null && !in.hasPattern()) {
							in.setHasPattern(true);
							in.setFill("fillcolor = green\n");
							in.setPattern("\n\\<\\<component\\>\\>\n");
						}
						d.setHasPattern(true);
						d.setFill("fillcolor = green\n");
						d.setPattern("\n\\<\\<decorator\\>\\>\n");
					} 
				}
			}
		}
		IClassData sup = m.getClassDataFromName(d.getSuperClass());
		if (sup != null && sup.hasPattern() && sup.getPattern().contains("decorator")) {
			d.setHasPattern(true);
			d.setFill("fillcolor = green\n");
			d.setPattern("\n\\<\\<decorator\\>\\>\n");
		}			
	}

	@Override
	public boolean getPattern() {
		return pattern;
	}

	@Override
	public void findPattern(List<IClassData> datas) {
	}

	@Override
	public void findPattern(IClassData d) {
		// TODO Auto-generated method stub
		
	}

}
