package it.nicola_amatucci.android.game_progress_backup.scripts;

public class ScriptDescriptor {
	String title;
	String description;
	int root;
	String script;
	
	public ScriptDescriptor() { }
	
	public ScriptDescriptor(String title, String description, int root,
			String script) {
		super();
		this.title = title;
		this.description = description;
		this.root = root;
		this.script = script;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRoot() {
		return root;
	}

	public void setRoot(int root) {
		this.root = root;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
