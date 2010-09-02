/**
 * StockQuoteService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.stock.schemas.services.stock.wsdl;

public interface StockQuoteService extends javax.xml.rpc.Service {
    public java.lang.String getStockQuoteServiceAddress();

    public com.stock.schemas.services.stock.wsdl.StockQuotePortType getStockQuoteService() throws javax.xml.rpc.ServiceException;

    public com.stock.schemas.services.stock.wsdl.StockQuotePortType getStockQuoteService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
