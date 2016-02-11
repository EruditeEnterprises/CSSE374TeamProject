package problem.composite;

import java.util.List;

public class Composite extends AbstractComponent{
	private List<IComponent> comps;
	
	public void add(IComponent comp){
		this.comps.add(comp);
	}
	public void remove(IComponent comp){
		this.comps.remove(comp);
	}
	@Override
	public void method() {
		if(this.comps==null)
			System.out.println("this does nothing");
	}


}
