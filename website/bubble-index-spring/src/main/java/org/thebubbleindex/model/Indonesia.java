package org.thebubbleindex.model;

public class Indonesia extends BubbleIndexTimeseries {
	    
	protected Indonesia() {}
	
	public Indonesia(final String name, final String symbol, final String dtype, final String keywords) {
		this.symbol = symbol;
		this.dtype = dtype;		
		this.name = name;
		this.keywords = keywords;
	}
}
