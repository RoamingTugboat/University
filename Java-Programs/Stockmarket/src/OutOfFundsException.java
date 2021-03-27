public class OutOfFundsException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public OutOfFundsException(Player p) {
		super(p.getName() + " doesn't have enough money to buy this many shares.");
	}
}
