package ug.co.yo.payments;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class YoPaymentsResponse {
    private String status;
    private int statusCode;
    private String statusMessage;
    private String errorMessage;
    private String transactionStatus;
    private String transactionReference = null;
      
    YoPaymentsResponse(String xmlResponse) {
        parseXmlResponse(xmlResponse);
    }

    /**
     * If there is an error, this parameter is set to “ERROR”. In this case,
     * check the Message parameter for a description of the error which
     * occurred.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * This is an integral value which uniquely identifies the status of the
     * transaction. To obtain the possible values of this field and their
     * respective descriptions, refer to Section 7: API Status Codes
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Textual description of the status code above.
     */
    public String getStatusMessage() {
        return this.statusMessage;
    }

    /**
     * This field may or may not be present in the response XML. If present, it
     * provides you with additional information about any error which may have
     * occurred. The information contained in this field is highly useful in
     * resolving any internal errors with the Yo! Payments service. If this
     * information is provided in the response XML, provide it to your Yo!
     * Payments contact, should you opt to submit an error report.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * This field is present for transaction oriented API requests such as
     * withdrawals, deposits and internal transfers. This field may take any of
     * two possible values namely FAILED and INDETERMINATE. Below is the meaning
     * of the respective values: • FAILED. This means that your request was not
     * successful. You may re-submit your request for processing if there was an
     * error on your part. • INDETERMINATE. This means that your request is
     * pending resolution by the Yo! Payments team. This normally happens if
     * there was a delay in processing of mobile money transactions. Typicially
     * requests which result in this status are resolved within 24 hours of your
     * initiating the request. Contact your Yo! Payments representative for
     * earlier resolution.
     */
    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * This value may or may not be present. If present, this value uniquely
     * identifies your transaction in your Yo! Payments account. If you submit a
     * transaction-oriented API request, such as withdrawal, deposit or internal
     * transfer and you get a status code less than zero, then the
     * TransactionReference field shall not be present in the XML Response and
     * you shall not be able to view the transaction details in the “View
     * Statement” section of your web account
     */
    public String getTransactionReference() {
        return transactionReference;
    }

    /*<?xml version="1.0" encoding="UTF-8"?> 
    <AutoCreate> 
     <Response> 
      <Status>ERROR</Status> 
      <StatusCode></StatusCode> 
      <StatusMessage></StatusMessage> 
      <ErrorMessage></ErrorMessage> 
      <TransactionStatus></TransactionStatus> 
      <TransactionReference></TransactionReference> 
     </Response> 
    </AutoCreate>*/
    
    private void parseXmlResponse(String xmlResponse) {
        Element line;
        // parse the xmlResponse and populate the member variables
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlResponse));

            Document doc = db.parse(is);

            NodeList status = doc.getElementsByTagName("Status");
            if (status.getLength()>0) {
                line = (Element)status.item(0);
                System.out.println("Status: " + getCharacterDataFromElement(line));
                this.status = getCharacterDataFromElement(line);
            }

            NodeList statusCode = doc.getElementsByTagName("StatusCode");
            if (statusCode.getLength()>0) {
                line = (Element)statusCode.item(0);
                System.out.println("StatusCode: " + getCharacterDataFromElement(line));
                this.statusCode = Integer.parseInt(getCharacterDataFromElement(line));
            }

            NodeList statusMessage = doc.getElementsByTagName("StatusMessage");
            if (statusMessage.getLength()>0) {
                line = (Element)statusMessage.item(0);
                System.out.println("StatusMessage: " + getCharacterDataFromElement(line));
                this.statusMessage = getCharacterDataFromElement(line);
            }

            NodeList errorMessage = doc.getElementsByTagName("ErrorMessage");
            if (errorMessage.getLength()>0) {
                line = (Element)errorMessage.item(0);
                System.out.println("ErrorMessage: " + getCharacterDataFromElement(line));
                this.errorMessage = getCharacterDataFromElement(line);
            }

            NodeList transactionStatus = doc.getElementsByTagName("TransactionStatus");
            if (transactionStatus.getLength()>0) {
                line = (Element)transactionStatus.item(0);
                System.out.println("TransactionStatus: " + getCharacterDataFromElement(line));
                this.transactionStatus = getCharacterDataFromElement(line);
            }

            NodeList transactionReference = doc.getElementsByTagName("TransactionReference");
            if (transactionReference.getLength()>0) {
                line = (Element)transactionReference.item(0);
                System.out.println("TransactionReference: " + getCharacterDataFromElement(line));
                this.transactionReference = getCharacterDataFromElement(line);
            }

            NodeList currency = doc.getElementsByTagName("Currency");
            if (currency.getLength()>0) {
                for (int i = 0; i < currency.getLength(); i++) {
                    Element element = (Element)currency.item(i);
                    NodeList code = element.getElementsByTagName("Code");
                    line = (Element)code.item(0);
                    System.out.println("Name: " + getCharacterDataFromElement(line));

                    NodeList balance = element.getElementsByTagName("Balance");
                    line = (Element)balance.item(0);
                    System.out.println("Balance: " + getCharacterDataFromElement(line));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData)child;
            return cd.getData();
        }
        return null;
    }

    // String toString()
}
    


