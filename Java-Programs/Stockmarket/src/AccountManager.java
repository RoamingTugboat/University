public interface AccountManager {

	public void newPlayer(String playerName);
	
	public void buyStock(String playerName, Share share, int buyAmount) throws OutOfFundsException, NullPointerException;
	
	public void sellStock(String playerName, Share share, int sellAmount);
	
}
