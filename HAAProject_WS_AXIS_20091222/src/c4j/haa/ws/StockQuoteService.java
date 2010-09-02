/**
 * StockQuoteSOAPBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package c4j.haa.ws;

import java.util.Date;

import com.stock.schemas.services.stock.Quote;
import com.stock.schemas.services.stock.Stock;

public class StockQuoteService {
	public com.stock.schemas.services.stock.Stock getStockQuote(java.lang.String ticker)
			throws java.rmi.RemoteException {
		Stock stock = new Stock();
		stock.setTicker(ticker);
		Quote[] quotes = new Quote[2];
		quotes[0] = createQuote(111);
		quotes[1] = createQuote(22);
		stock.setQuotes(quotes);
		return stock;
	}

	private Quote createQuote(int i) {
		Quote quote = new Quote();
		quote.setDtQuote(new Date());
		quote.setQuotePrice(i);
		return quote;
	}

}
