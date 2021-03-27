/**
 * Enthaelt Array mit allen Player-Objekten sowie Methoden zu deren Verwaltung.
 * @author void
 *
 */

public class AccountManagerImpl implements AccountManager {

		private Player[] globalPlayerArray = new Player[1]; 
		
		public AccountManagerImpl() {

		}
		
			
		
		public void newPlayer(String playerName) {
			globalPlayerAdd(new Player(playerName));
		}

		
		
		public void globalPlayerAdd(Player player) {

			for(int i=0;i<globalPlayerArray.length;i++) {
				if(globalPlayerArray[i] == null) {
					globalPlayerArray[i] = player;
					break;
				} else if(globalPlayerArray[i].getName().equals(player.getName())) {
					System.out.println("Trying to add " + player.getName() + ", but it already exists!");
					break;
				} else if(i == globalPlayerArray.length - 1) {
					Player[] newArray = new Player[globalPlayerArray.length + 1];
					for(int j=0;j<globalPlayerArray.length;j++) {
						newArray[j] = globalPlayerArray[j];
					}
					globalPlayerArray = newArray;
					globalPlayerArray[globalPlayerArray.length-1] = player;
					break;
				}
			}
		}
		
		
		public Player globalPlayerFind(String playerName) throws NullPointerException {
			for(int i=0;i<globalPlayerArray.length;i++) {
				if(globalPlayerArray[i].getName().equals(playerName)) {
					return globalPlayerArray[i];
				}
			}
			throw new NullPointerException("Player not found.");
		}
			
		
		
		
		
		
		public void buyStock(String playerName, Share share, int buyAmount) throws OutOfFundsException, NullPointerException {
			globalPlayerFind(playerName).buyShares(share, buyAmount);
		}
		
		public void sellStock(String playerName, Share share, int sellAmount) {
			globalPlayerFind(playerName).sellShares(share, sellAmount);
		}
		/*
		public long getValue(Asset asset) { 
			return asset.getValue();
		}
		
		
		public long getValueAllAssets(String playerName){
			Player player = globalPlayerFind(playerName);
			return player.getCashAccount().getValue() + player.getShareDeposit().getValue();
		}
		
		
		public long getRate(String shareName){
			return globalShareFind(shareName).getRate();
			
		}
		*/
	
		
}
