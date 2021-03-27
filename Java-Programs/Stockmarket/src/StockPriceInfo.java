
public interface StockPriceInfo {

	public long getValue(Asset asset);
	
	public long getValueAllAssets(Player player);
	
	public long getRate(Share share);

	public boolean isShareListed(String shareName);
	
	public Share[] getAllSharesAsSnapshot();
	
	public void printAllShareData();
}
