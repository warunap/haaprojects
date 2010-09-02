/**
 * Stock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.stock.schemas.services.stock;

public class Stock  implements java.io.Serializable {
    private java.lang.String ticker;

    private com.stock.schemas.services.stock.Quote[] quotes;

    public Stock() {
    }

    public Stock(
           java.lang.String ticker,
           com.stock.schemas.services.stock.Quote[] quotes) {
           this.ticker = ticker;
           this.quotes = quotes;
    }


    /**
     * Gets the ticker value for this Stock.
     * 
     * @return ticker
     */
    public java.lang.String getTicker() {
        return ticker;
    }


    /**
     * Sets the ticker value for this Stock.
     * 
     * @param ticker
     */
    public void setTicker(java.lang.String ticker) {
        this.ticker = ticker;
    }


    /**
     * Gets the quotes value for this Stock.
     * 
     * @return quotes
     */
    public com.stock.schemas.services.stock.Quote[] getQuotes() {
        return quotes;
    }


    /**
     * Sets the quotes value for this Stock.
     * 
     * @param quotes
     */
    public void setQuotes(com.stock.schemas.services.stock.Quote[] quotes) {
        this.quotes = quotes;
    }

    public com.stock.schemas.services.stock.Quote getQuotes(int i) {
        return this.quotes[i];
    }

    public void setQuotes(int i, com.stock.schemas.services.stock.Quote _value) {
        this.quotes[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Stock)) return false;
        Stock other = (Stock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ticker==null && other.getTicker()==null) || 
             (this.ticker!=null &&
              this.ticker.equals(other.getTicker()))) &&
            ((this.quotes==null && other.getQuotes()==null) || 
             (this.quotes!=null &&
              java.util.Arrays.equals(this.quotes, other.getQuotes())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTicker() != null) {
            _hashCode += getTicker().hashCode();
        }
        if (getQuotes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQuotes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQuotes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Stock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", ">Stock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ticker");
        elemField.setXmlName(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "ticker"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quotes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "quotes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "Quote"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
