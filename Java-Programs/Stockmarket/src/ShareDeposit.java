/**
 * Enthaelt Array mit allen ShareItems eines Spielers sowie Methoden zu deren Verwaltung.
 */

public class ShareDeposit extends Asset {

	private ShareItem[] shareItemArray = new ShareItem[1];

	
	public void addShareItem(Share share, int shareCount) { //sollte share uebergeben, keine shareitem
		for(int i=0;i<shareItemArray.length;i++) {
			if(shareItemArray[i] == null) {
				shareItemArray[i] = new ShareItem(share);
				shareItemArray[i].setShareCount(shareCount);
				break;
			} else if(shareItemArray[i].getShare().getCompany().equals(share.getCompany())) {
				shareItemArray[i].setShareCount(shareItemArray[i].getShareCount()+shareCount);
				break;
			} else if(i == shareItemArray.length - 1) {
				ShareItem[] newArray = new ShareItem[shareItemArray.length + 1];
				for(int k=0;k<shareItemArray.length;k++) {
					newArray[k] = shareItemArray[k];
				}
				shareItemArray = newArray;
				shareItemArray[shareItemArray.length-1] = new ShareItem(share, shareCount);
				break;
			}
		}
	}
	
	

	public int getShareAmountOfCompany(Share share) {
		return findShareItemByCompany(share).getShareCount();
	}
	
	public ShareItem findShareItemByCompany(Share share) throws NullPointerException {
		for(int i = 0;i<shareItemArray.length;i++) {
			if(shareItemArray[i] != null) {
				if(shareItemArray[i].getShare().getCompany().equals(share.getCompany())) {
					return shareItemArray[i];
				}
			}
		} 
		throw new NullPointerException("No shares with that name.");
	}
	

	
	public long getValue() {
		long value = 0;
		for(int i = 0; i<shareItemArray.length; i++) {
			if(shareItemArray[i] != null) {
				value += shareItemArray[i].getValue();
			}
		}
		return value;
	}
	
	
	
	public void buyShares(Share share, int buyAmount) throws NullPointerException {
		if(share != null) {	
			addShareItem(share, buyAmount);
		} else {
			throw new NullPointerException("Trying to buy stock of non-existing company.");
		}
	}
	public void sellShares(Share share, int sellAmount) {
		ShareItem shareItem = findShareItemByCompany(share);
		if(shareItem != null) {
				shareItem.setShareCount(shareItem.getShareCount() - sellAmount);
		}
		 else {
			System.err.println("Trying to sell " + share.getCompany() + " stock but player has no shares of that company");
		}
	}
	
	
	
	public void printSharesArray() {
		for(int i=0;i<shareItemArray.length;i++) {
			if(shareItemArray[i]!=null) {
				System.out.println("   " + shareItemArray[i].getShare().getCompany() + ":\t"+ shareItemArray[i].getShareCount() +" shares \tcurrent total worth " + shareItemArray[i].getValue() + " Cents" );
			} else {
				System.out.println("None.");
			}
		}
	}

	public String toString() {
		return getClass() + " named " + getName() + " is worth " + getValue();
	}
	
	public boolean equals(ShareDeposit shareDeposit) {
		return (this.getValue() == shareDeposit.getValue());
	}
}
