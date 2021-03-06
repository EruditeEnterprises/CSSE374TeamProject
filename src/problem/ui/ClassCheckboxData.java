package problem.ui;

import java.io.IOException;

import problem.util.DesignParser;

public class ClassCheckboxData {
	private String text;
	private boolean checked;
	private DesignParser parser;
	private boolean threadIsRunning = false;

	public ClassCheckboxData(final String text, final boolean checked, DesignParser parser) {
		this.text = text;
		this.checked = checked;
		this.parser = parser;
	}

	public boolean isChecked() throws IOException {
		if(this.checked) {
			if (this.parser.getModel().getInactiveClasses().contains(this.parser.getModel().getClassDataFromName(this.text)))
				this.parser.getModel().removeInactiveClass(this.parser.getModel().getClassDataFromName(this.text));
		} else {
			if (this.parser.getModel().getClassNames().contains(this.text))
				this.parser.getModel().addInactiveClass(this.parser.getModel().getClassDataFromName(this.text));
		}
		
//		if (!this.threadIsRunning) {
//			this.threadIsRunning = true;
//			Thread t = new Thread(new Runnable() { @Override
//			public void run() {
//				try {
					parser.createOutputFiles();
//					threadIsRunning = false;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			});
//			t.start();
//		}
		return checked;
	}

	public void setChecked(final boolean checked) throws IOException {
		this.checked = checked;
		
//		if(!this.checked) {
//			System.out.println("Checked");
//			if (this.parser.getModel().getClassNames().contains(this.text))
//				this.parser.getModel().addInactiveClass(this.parser.getModel().getClassDataFromName(this.text));
//		} else {
//			System.out.println("UnChecked");
//			if (this.parser.getModel().getInactiveClasses().contains(this.parser.getModel().getClassDataFromName(this.text)))
//				this.parser.getModel().removeInactiveClass(this.parser.getModel().getClassDataFromName(this.text));
//		}
//		this.parser.createOutputFiles();
	}
	
	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
