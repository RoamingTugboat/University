
/**
 * Provider fuer Aktienkurse. Dummyklasse, aendert also die Aktienkurse nicht.
 * @author Jakob
 */

public class ConstStockPriceProvider extends StockPriceProvider {

	public void updateShareRate(Share share) {
		share.setRate(share.getRate());
	}
}
