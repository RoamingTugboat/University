/**
 * Main. Ablauf siehe Kommentare.
 *
 */

public class StockGameLauncher {
	
	public static void main(String[] args) {
		
		
		// **** INITIALIZE ACCOUNTMANAGERIMPL AND THE PREFERRED KIND OF STOCKPRICEPROVIDER
		AccountManagerImpl ami = new AccountManagerImpl();
		StockPriceProvider stockPriceProvider = new RandomStockPriceProvider();	//wieso wurde AMI mit einem SPP als Parameter konstuiert? ich habs rausgenommen und instanziiere den SPP alleine
		stockPriceProvider.startUpdate(); //StockPriceViewer: aktuelle Anzeige der Aktienkurse, nach Timer-Timings; koennte auch im Konstruktor automatisch gestartet werden. Pause-Methode sollte evtl eingebaut werden
		
		// **** CREATE STOCKS
		stockPriceProvider.globalShareAdd(new Share("EON", 6212L));
		stockPriceProvider.globalShareAdd(new Share("BMW", 4000L));
		stockPriceProvider.globalShareAdd(new Share("ITL", 5551L));
		stockPriceProvider.globalShareAdd(new Share("MCZ", 6980L));
		stockPriceProvider.globalShareAdd(new Share("HSA", 8004L));
		stockPriceProvider.globalShareAdd(new Share("VHO", 8230L));

			
		// **** CREATE PLAYERS
		ami.globalPlayerAdd(new Player("acid"));
		ami.globalPlayerAdd(new Player("void"));
		Player player1 = ami.globalPlayerFind("acid");
		player1.getCashAccount().setName("ACID_CASH01");
		player1.getShareDeposit().setName("ACID_SHARES01");
		Player player2 = ami.globalPlayerFind("void");
		player2.getCashAccount().setName("voidGeld");
		player2.getShareDeposit().setName("voidAktien");
		
		
		// **** SET CASH ACC VALUES
		player1.setBalance(1000000L);
		player2.setBalance(1000000L);

		
		
		// **** TRY BUYING SHARES
		try {
			// player1.buyShares(stockPriceProvider.globalShareFind("Nonexistant Shares"), 2);// NullPointerEx
			// player1.buyShares(stockPriceProvider.globalShareFind("MCZ"), (int)10e16); // OutOfFundsEx
			player1.buyShares(stockPriceProvider.globalShareFind("ITL"), 5);
			player1.buyShares(stockPriceProvider.globalShareFind("ITL"), 5);
			player1.buyShares(stockPriceProvider.globalShareFind("EON"), 10);
			
			player2.buyShares(stockPriceProvider.globalShareFind("MCZ"), 10);
			player2.buyShares(stockPriceProvider.globalShareFind("EON"), 10);
		} catch (OutOfFundsException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		System.out.println(player1.getCashAccount().toString() + "\n" + player1.getShareDeposit().toString() + "\n" + player1.getShareDeposit().findShareItemByCompany(stockPriceProvider.globalShareFind("ITL")).toString());
		
		player1.printStatus();
	}

}