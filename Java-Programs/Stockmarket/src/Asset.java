
public abstract class Asset {

	protected String name;

	
	public Asset() {
		
	}
	
	public Asset(String name) {
		this.name = name;
	}
	
	
	public abstract long getValue();
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	//public abstract boolean equals();
		
	
	public String toString(){
		return "Asset \"" + name + "\" is worth " + getValue() + " WAEHRUNG";
	}

}
