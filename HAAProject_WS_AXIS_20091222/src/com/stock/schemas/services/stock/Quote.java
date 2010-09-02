/**
 * Quote.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.stock.schemas.services.stock;

public class Quote  implements java.io.Serializable {
    private int quotePrice;

    private java.util.Date dtQuote;

    public Quote() {
    }

    public Quote(
           int quotePrice,
           java.util.Date dtQuote) {
           this.quotePrice = quotePrice;
           this.dtQuote = dtQuote;
    }


    /**
     * Gets the quotePrice value for this Quote.
     * 
     * @return quotePrice
     */
    public int getQuotePrice() {
        return quotePrice;
    }


    /**
     * Sets the quotePrice value for this Quote.
     * 
     * @param quotePrice
     */
    public void setQuotePrice(int quotePrice) {
        this.quotePrice = quotePrice;
    }


    /**
     * Gets the dtQuote value for this Quote.
     * 
     * @return dtQuote
     */
    public java.util.Date getDtQuote() {
        return dtQuote;
    }


    /**
     * Sets the dtQuote value for this Quote.
     * 
     * @param dtQuote
     */
    public void setDtQuote(java.util.Date dtQuote) {
        this.dtQuote = dtQuote;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Quote)) return false;
        Quote other = (Quote) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.quotePrice == other.getQuotePrice() &&
            ((this.dtQuote==null && other.getDtQuote()==null) || 
             (this.dtQuote!=null &&
              this.dtQuote.equals(other.getDtQuote())));
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
        _hashCode += getQuotePrice();
        if (getDtQuote() != null) {
            _hashCode += getDtQuote().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Quote.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "Quote"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quotePrice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "quotePrice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dtQuote");
        elemField.setXmlName(new javax.xml.namespace.QName("http://stock.com/schemas/services/stock", "dtQuote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
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
