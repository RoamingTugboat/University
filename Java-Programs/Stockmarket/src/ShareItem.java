/**
 * Aktienpaket einer Firma. Wird mit einem Share aufgerufen und repraesentiert dann eine Menge von Aktien dieses Typen.
 *
 */

public class ShareItem extends Asset {

	
	/**
	 * ShareItem holds a share type and a share amount. This class is used to represent X Shares of a company
	 * so they can be moved around uniformly in an array.
	 */
	
	private Share share;
	private int shareCount = 0;

	
	public ShareItem(Share share) {
		this.share = share;
	}
	
	public ShareItem(Share share, int shareCount) {
		this.share = share;
		this.shareCount = shareCount;
	}
	
	

	public Share getShare() {
		return share;
	}
	
	
	public int getShareCount() {
		return shareCount;
	}
	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}
	
		
	public long getValue() {
		return getShare().getRate() * getShareCount();
	}


	public String toString() {
		return getClass() + ": " + getShare().getCompany() + ", count: " + shareCount;
	}
	
	public boolean equals(ShareItem shareItem) {
		return this.getValue()==shareItem.getValue() && this.getShare() == shareItem.getShare();
	}
	
}
