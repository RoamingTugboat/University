/**
 * Verwaltet Kontostand eines Spielers. 
 *
 */

public class CashAccount extends Asset {

	private long value;
	
	
	public CashAccount(String name, int value) {
		super(name);
		this.value = value;
	}
	
	
	public CashAccount(String name) {
		super(name);
	}
	
	public CashAccount(long value) {
		this.value = value;
	}
		
		
	
	public void setValue(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return this.value;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String toString() {
		return getClass() + " named " + getName() + " is worth " + getValue();
	}
	
	public boolean equals(CashAccount cashAccount){
		return (cashAccount.getValue()==this.value &&
				cashAccount.getName()==this.name);
	}
				
}
