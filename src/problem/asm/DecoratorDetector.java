package problem.asm;

import java.io.IOException;
import java.util.List;

public class DecoratorDetector implements IPatternDetector {
	private boolean pattern = false;

	@Override
	public boolean findPattern(IClassData d) throws IOException {
		IClassData sup = VisitorManager.visitClass(d.getSuperClass()).getClassData();
		boolean assocField = false;
		for (IFieldData field : d.getFields()) {
			if (field.getType().equals(d.getSuperClass())) {
				assocField = true;
			}
		}
		if (sup.hasPattern() && sup.getPattern().contains("decorator"))
			return true;
		return false;
	}

	@Override
	public boolean getPattern() {
		return pattern;
	}

	@Override
	public void findPattern(List<IClassData> datas) {
	}

}
