import java.util.Random;

/**
 * Zaehlt eine Zufallszahl zwischen -100 und 100 zu jedem Kurs hinzu.
 */

public class RandomStockPriceProvider extends StockPriceProvider {

	public void updateShareRate(Share share) {
		Random rand = new Random();
		long n = rand.nextInt(200)-100;
				
		share.setRate(share.getRate() +  n);
			if(share.getRate() < 0) {
				share.setRate(0);
			}
	}
}
