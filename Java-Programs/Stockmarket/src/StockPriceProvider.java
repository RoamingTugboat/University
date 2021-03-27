import java.util.Timer;
import java.util.TimerTask;


/**
 * Enthaelt Array mit allen Shares sowie Methoden zu deren Verwaltung. Seit 13-04-13 auch
 * mit startUpdate eine Methode zur Erstellung eines GUIelements zum Ausprinten der Shares
 * und eine Methode zum regulaeren Aktualisieren aller Shares. Art der Share-Aenderung wird
 * in Subklassen durch ueberladung von updateShareRate(Share share) festgelegt.
 */

public abstract class StockPriceProvider implements StockPriceInfo {

	protected Share[] globalShareArray = new Share[1];
	protected StockPriceViewer sViewer; //GUI element
	//protected StockPriceViewer sViewer2;
	
	
	public StockPriceProvider() {
		sViewer = new StockPriceViewer(this);
		//sViewer2 = new StockPriceViewer(this);
	}
	
	
	public void startUpdate() {

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateShareRates();
				sViewer.run(); //updates JPanel
				//sViewer2.run();
				}
		}, 500, 1000); //was 2000, 1000);
	}
	

	public long getValue(Asset asset) {
		return asset.getValue();
	}

	public long getValueAllAssets(Player player) { //Was macht die Methode hier im SPP? Besser im AccountManagerImpl aufgehoben?
		return player.getCashAccount().getValue() + player.getShareDeposit().getValue();
	}

	public long getRate(Share share) {
		return share.getRate();
	}

	public boolean isShareListed(String shareName) {
		return (globalShareFind(shareName) != null);
	}

	public long getCurrentShareRate(String shareName) {
		return globalShareFind(shareName).getRate();
	}

	public Share[] getAllSharesAsSnapshot() {
		Share[] newArray = new Share[globalShareArray.length];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = globalShareArray[i];
		}
		return newArray;
	}

	public void globalShareAdd(Share share) {

		for (int i = 0; i < globalShareArray.length; i++) {
			if (globalShareArray[i] == null) {
				globalShareArray[i] = share;
				break;
			} else if (globalShareArray[i].getCompany().equals(
					share.getCompany())) {
				System.out.println("Trying to add " + share.getCompany()
						+ "share as new kind, but it already exists!");
				break;
			} else if (i == globalShareArray.length - 1) {
				Share[] newArray = new Share[globalShareArray.length + 1];
				for (int j = 0; j < globalShareArray.length; j++) {
					newArray[j] = globalShareArray[j];
				}
				globalShareArray = newArray;
				globalShareArray[globalShareArray.length - 1] = share;
				break;
			}
		}
	}

	public Share globalShareFind(String shareName) throws NullPointerException {
		for (int i = 0; i < globalShareArray.length; i++) {
			if (globalShareArray[i].getCompany().equals(shareName)) {
				return globalShareArray[i];
			}
		}
		throw new NullPointerException(shareName
				+ " is not registered in the stock market.");
	}

	
	
	public void updateShareRate(Share share) {
		//stub; overwritten by subclasses
	}

	public void updateShareRates() {
		for (Share s : globalShareArray) {
			updateShareRate(s);
		}
	}
	
	
	
	public void printAllShareData() {
		System.out.println("----");
		for (int i = 0; i < globalShareArray.length; i++) {
			System.out.println(" -" + globalShareArray[i].getCompany() + ": "
					+ globalShareArray[i].getRate() + " Cents/Share");
		}
	}

	
	public String returnAllShareData() {				 // specifies JPanel output of this.sViewer
		String result = "";
		for (int i = 0; i < globalShareArray.length; i++) {
			result = result + "" + globalShareArray[i].getCompany() + ": "
					+ globalShareArray[i].getRate() + " +++ ";
		}
		return result;
	}



}
