/**
 * StockQuoteServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.stock.schemas.services.stock.wsdl;

public class StockQuoteServiceLocator extends org.apache.axis.client.Service implements com.stock.schemas.services.stock.wsdl.StockQuoteService {

    public StockQuoteServiceLocator() {
    }


    public StockQuoteServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public StockQuoteServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for StockQuoteService
    private java.lang.String StockQuoteService_address = "http://localhost:9080/axis/services/StockQuoteSOAPBinding";

    public java.lang.String getStockQuoteServiceAddress() {
        return StockQuoteService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String StockQuoteServiceWSDDServiceName = "StockQuoteService";

    public java.lang.String getStockQuoteServiceWSDDServiceName() {
        return StockQuoteServiceWSDDServiceName;
    }

    public void setStockQuoteServiceWSDDServiceName(java.lang.String name) {
        StockQuoteServiceWSDDServiceName = name;
    }

    public com.stock.schemas.services.stock.wsdl.StockQuotePortType getStockQuoteService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(StockQuoteService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getStockQuoteService(endpoint);
    }

    public com.stock.schemas.services.stock.wsdl.StockQuotePortType getStockQuoteService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.stock.schemas.services.stock.wsdl.StockQuoteSOAPBindingStub _stub = new com.stock.schemas.services.stock.wsdl.StockQuoteSOAPBindingStub(portAddress, this);
            _stub.setPortName(getStockQuoteServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setStockQuoteServiceEndpointAddress(java.lang.String address) {
        StockQuoteService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.stock.schemas.services.stock.wsdl.StockQuotePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.stock.schemas.services.stock.wsdl.StockQuoteSOAPBindingStub _stub = new com.stock.schemas.services.stock.wsdl.StockQuoteSOAPBindingStub(new java.net.URL(StockQuoteService_address), this);
                _stub.setPortName(getStockQuoteServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("StockQuoteService".equals(inputPortName)) {
            return getStockQuoteService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://stock.com/schemas/services/stock/wsdl", "StockQuoteService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock/wsdl", "StockQuoteService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("StockQuoteService".equals(portName)) {
            setStockQuoteServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
