package ug.co.yo.payments;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Munaawa Philip (swiftugandan@gmail.com)
 * This class implements the Yo Payments API (www.yo.co.ug/payments)
 */
public class YoPaymentsClient {
    
    private String APIUsername;
    private String APIPassword;
    
    /**
     * 
     * @param APIUsername - This is the API Username which, together with the
     *            API Password below, maps your API request to your Yo! Payments
     *            account. Obtain this parameter from the web interface. Note
     *            that if you do not have a Business Account, you cannot use the
     *            API <p></p>
     * @param APIPassword - This is the API Password which, together with the
     *            API Username above, maps your API request to your Yo! Payments
     *            account. Obtain this parameter from the web interface. Note
     *            that if you do not have a Business Account, you cannot use the
     *            API.
     */
    public YoPaymentsClient(String APIUsername, String APIPassword){
                this.APIUsername = APIUsername;
                this.APIPassword = APIPassword;
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    private static String base64Encode(String inputFile) throws IOException {
        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream input = new FileInputStream(inputFile);
        ByteArrayOutputStream byte1 = new ByteArrayOutputStream();
        OutputStream output = new Base64OutputStream(byte1); // System.out
        int n = input.read(buffer, 0, BUFFER_SIZE);
        while (n >= 0) {
            output.write(buffer, 0, n);
            n = input.read(buffer, 0, BUFFER_SIZE);
        }
        String result = byte1.toString();
        input.close();
        output.close();
        return result;
    }
    
    /**
     * Withdrawfunds XML Builder Minimal method No Narrative file, No Internal Reference
     * 
     * This method creates the xml format that is accepted by the Yo Payments
     * server.
     * 
     * @param Amount This is the amount to be withdrawn. Must be set to a value
     *            greater than zero. Fractional amounts may not be supported by
     *            certain mobile money providers. - <b>Mandatory</b>
     * @param BeneficiaryPhone - This is a numerical value representing the
     *            account number of the mobile money account wher you wish to
     *            transfer the funds to. This is typically the telephone number
     *            of the mobile phone receiving the amount. Telephone numbers
     *            MUST have the international code prepended, without the “+”
     *            sign. An example of a mobile money account number which would
     *            be valid for the MTN Uganda network is 256771234567 - <b>Mandatory</b>
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @return String
     */
    public String createWithdrawalXml(float Amount, String BeneficiaryPhone,String Narrative){
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+BeneficiaryPhone+"</Account>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    
    /**
     * Withdrawfunds XML Builder method All Parameters (incl optional arguments)
     * This method creates the xml format that is accepted by the Yo Payments
     * server.
     * 
     * @param Amount This is the amount to be withdrawn. Must be set to a value
     *            greater than zero. Fractional amounts may not be supported by
     *            certain mobile money providers. - <b>Mandatory</b>
     * @param BeneficiaryPhone - This is a numerical value representing the
     *            account number of the mobile money account wher you wish to
     *            transfer the funds to. This is typically the telephone number
     *            of the mobile phone receiving the amount. Telephone numbers
     *            MUST have the international code prepended, without the “+”
     *            sign. An example of a mobile money account number which would
     *            be valid for the MTN Uganda network is 256771234567 -
     *            <b>Mandatory</b>
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @param AccountProviderCode - Provide here the account provider code of
     *            the institution holding the account indicated in the Account
     *            parameter. See section 8 for a list of all supported account
     *            provider code - <b>Optional</b>
     * @param NarrativeFileName - This parameter enables you to attach a file to
     *            the transaction. This is useful, for example, in the case
     *            where you may want to attach a scanned receipt, or a scanned
     *            payment authorization, depending on your internally
     *            established business rules. This parameter requires you to
     *            provide the name of the file you are attaching, as a string,
     *            for example “receipt.doc” or “receipt.pdf”. Note that the
     *            contents of this parameter are ignored if you have not
     *            provided the contents of the file using NarrativeFileBase64
     *            below. <b>Optional</b>
     * @param InternalReference - In this field, provide an internal transaction
     *            reference. If this transfer is related to another system
     *            transaction, enter its reference code in this field. If you
     *            are unsure about the meaning of this field, do not include it
     *            in your request. This field is useful in linking this request
     *            to another existing transaction which is already in the system
     *            <b>Optional</b>
     * @param ExternalReference - In this field, enter an external transaction
     *            reference. An external transaction reference is something
     *            which yourself and the benecifiary agree upon. For example,
     *            this may be an invoice number, or a phrase describing the
     *            purpose of this transaction in a way that the beneficiary
     *            would understand. This field is optional and you may omit it
     *            in your request <b>Optional</b>
     * @return String
     */
    public String createWithdrawalXml(float Amount, String BeneficiaryPhone,
            String AccountProviderCode, String Narrative, String NarrativeFileName,
            String InternalReference, String ExternalReference) throws IOException {
        String base64String = null;
        if (!NarrativeFileName.isEmpty()) {
            base64String = base64Encode(NarrativeFileName);
        } else {
            base64String="";
        }
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+BeneficiaryPhone+"</Account>"+ 
                "<AccountProviderCode></AccountProviderCode>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+ 
                "<NarrativeFileName>"+NarrativeFileName+"</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+ 
                "<InternalReference>"+InternalReference+"</InternalReference>"+ 
                "<ExternalReference>"+ExternalReference+"</ExternalReference>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    /**
     * DepositFunds XML Builder Minimal method No Narrative file, No Internal
     * Reference This method creates the xml format that is accepted by the Yo
     * Payments server.
     * 
     * @param Amount - This is the amount to be deducted from the mobile money
     *            account and deposited into your Payment Account. Must be set
     *            to a value greater than zero. Fractional amounts may not be
     *            supported by certain mobile money providers. -
     *            <b>Mandatory</b>
     * @param FundsSourcePhone - This is a numerical value representing the
     *            account number of the mobile money account from where you are
     *            deducting the funds to be deposited into your Payment Account.
     *            This is typically the telephone number of the mobile phone
     *            receiving the amount. Telephone numbers MUST have the
     *            international code prepended, without the “+” sign. An example
     *            of a mobile money account number which would be valid for the
     *            MTN Uganda network is 256771234567. - <b>Mandatory</b>
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @return String
     */
    public String createDepositXml(float Amount, String FundsSourcePhone,String Narrative){
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acdepositfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+FundsSourcePhone+"</Account>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    
    /**
     * DepositFunds XML Builder method All Parameters (incl optional arguments)
     * This method creates the xml format that is accepted by the Yo Payments
     * server.
     * 
     * @param Amount - This is the amount to be deducted from the mobile money
     *            account and deposited into your Payment Account. Must be set
     *            to a value greater than zero. Fractional amounts may not be
     *            supported by certain mobile money providers. -
     *            <b>Mandatory</b>
     * @param FundsSourcePhone - This is a numerical value representing the
     *            account number of the mobile money account from where you are
     *            deducting the funds to be deposited into your Payment Account.
     *            This is typically the telephone number of the mobile phone
     *            receiving the amount. Telephone numbers MUST have the
     *            international code prepended, without the “+” sign. An example
     *            of a mobile money account number which would be valid for the
     *            MTN Uganda network is 256771234567. - <b>Mandatory</b>
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @param AccountProviderCode - Provide here the account provider code of
     *            the institution holding the account indicated in the Account
     *            parameter. See section 8 for a list of all supported account
     *            provider code - <b>Optional</b>
     * @param NarrativeFileName - This parameter enables you to attach a file to
     *            the transaction. This is useful, for example, in the case
     *            where you may want to attach a scanned receipt, or a scanned
     *            payment authorization, depending on your internally
     *            established business rules. This parameter requires you to
     *            provide the name of the file you are attaching, as a string,
     *            for example “receipt.doc” or “receipt.pdf”. Note that the
     *            contents of this parameter are ignored if you have not
     *            provided the contents of the file using NarrativeFileBase64
     *            below. <b>Optional</b>
     * @param InternalReference - In this field, provide an internal transaction
     *            reference. If this transfer is related to another system
     *            transaction, enter its reference code in this field. If you
     *            are unsure about the meaning of this field, do not include it
     *            in your request. This field is useful in linking this request
     *            to another existing transaction which is already in the
     *            system. <b>Optional</b>
     * @param ExternalReference - In this field, enter an external transaction
     *            reference. An external transaction reference is something
     *            which yourself and the benecifiary agree upon. For example,
     *            this may be an invoice number, or a phrase describing the
     *            purpose of this transaction in a way that the beneficiary
     *            would understand. This field is optional and you may omit it
     *            in your request. <b>Optional</b>
     * @return String
     */
    public String createDepositXml(float Amount, String FundsSourcePhone,
            String AccountProviderCode, String Narrative, String NarrativeFileName,
            String InternalReference, String ExternalReference) throws IOException {
        String base64String = null;
        if (!NarrativeFileName.isEmpty()) {
            base64String = base64Encode(NarrativeFileName);
        } else {
            base64String="";
        }
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acdepositfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+FundsSourcePhone+"</Account>"+ 
                "<AccountProviderCode></AccountProviderCode>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+ 
                "<NarrativeFileName>"+NarrativeFileName+"</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+ 
                "<InternalReference>"+InternalReference+"</InternalReference>"+ 
                "<ExternalReference>"+ExternalReference+"</ExternalReference>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    /**
     * DepositFunds XML Builder Minimal method No Narrative file, No Internal
     * Reference This method creates the xml format that is accepted by the Yo
     * Payments server.
     * 
     * @param CurrencyCode - Specify here the standard code of the currency in
     *            which you wish to perform the transfer. Note that (a) you must
     *            have a positive balance in the currency you specify; and (b)
     *            you must specify a valid currency code. Examples of valid
     *            currency codes are UGX for “Uganda Shillings”, KES for “Kenyan
     *            Shillings”, USD for United States Dollars.
     * @param Amount - This is the amount to be transferred. This amount will be
     *            debited from your Payment Account (plus applicable fees) and
     *            credited to the Payment Account of the beneficiary. Fractional
     *            amounts are supported. - <b>Mandatory</b>
     * @param BeneficiaryAccount - This is a numerical value representing the
     *            account number of the Payment Account to which you are
     *            transferring the funds, i.e the “beneficiary” account number.
     *            Obtain this account number from the user to whom you are
     *            transferring the funds. You must provide a valid account
     *            number for the transaction to succeed - <b>Mandatory</b>
     * @param BeneficiaryEmail - Provide here the email address of the recipient
     *            of the funds. You must provide a valid email address for the
     *            transaction to succeed. The Yo! Payments transaction processor
     *            will attempt to match the values you provide in the
     *            BeneficiaryAccount and BeneficiaryEmail with the values stored
     *            in the database. If they do not match, the transaction will
     *            not succeed.
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @return String
     */
    public String createInternalTransferXml(String CurrencyCode, float Amount,
            String BeneficiaryAccount, String BeneficiaryEmail, String Narrative){
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acinternaltransfer</Method>"+
                "<CurrencyCode>"+CurrencyCode+"</CurrencyCode>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<BeneficiaryAccount>"+BeneficiaryAccount+"</BeneficiaryAccount>"+ 
                "<BeneficiaryEmail>"+BeneficiaryEmail+"</BeneficiaryEmail>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    
    /**
     * DepositFunds XML Builder method All Parameters (incl optional arguments)
     * This method creates the xml format that is accepted by the Yo Payments
     * server.
     * 
     * @param CurrencyCode - Specify here the standard code of the currency in
     *            which you wish to perform the transfer. Note that (a) you must
     *            have a positive balance in the currency you specify; and (b)
     *            you must specify a valid currency code. Examples of valid
     *            currency codes are UGX for “Uganda Shillings”, KES for “Kenyan
     *            Shillings”, USD for United States Dollars.
     * @param Amount - This is the amount to be transferred. This amount will be
     *            debited from your Payment Account (plus applicable fees) and
     *            credited to the Payment Account of the beneficiary. Fractional
     *            amounts are supported. - <b>Mandatory</b>
     * @param BeneficiaryAccount - This is a numerical value representing the
     *            account number of the Payment Account to which you are
     *            transferring the funds, i.e the “beneficiary” account number.
     *            Obtain this account number from the user to whom you are
     *            transferring the funds. You must provide a valid account
     *            number for the transaction to succeed - <b>Mandatory</b>
     * @param BeneficiaryEmail - Provide here the email address of the recipient
     *            of the funds. You must provide a valid email address for the
     *            transaction to succeed. The Yo! Payments transaction processor
     *            will attempt to match the values you provide in the
     *            BeneficiaryAccount and BeneficiaryEmail with the values stored
     *            in the database. If they do not match, the transaction will
     *            not succeed.
     * @param Narrative - Textual narrative about the transaction. Enter here a
     *            sentence describing the transaction. Provide a maximum of 4096
     *            characters here. If you wish to provide more information,
     *            consider using the Extended parameter method with
     *            NarrativeFileBase6. - <b>Mandatory</b>
     * @param AccountProviderCode - Provide here the account provider code of
     *            the institution holding the account indicated in the Account
     *            parameter. See section 8 for a list of all supported account
     *            provider code - <b>Optional</b>
     * @param NarrativeFileName - This parameter enables you to attach a file to
     *            the transaction. This is useful, for example, in the case
     *            where you may want to attach a scanned receipt, or a scanned
     *            payment authorization, depending on your internally
     *            established business rules. This parameter requires you to
     *            provide the name of the file you are attaching, as a string,
     *            for example “receipt.doc” or “receipt.pdf”. Note that the
     *            contents of this parameter are ignored if you have not
     *            provided the contents of the file using NarrativeFileBase64
     *            below. <b>Optional</b>
     * @param InternalReference - In this field, provide an internal transaction
     *            reference. If this transfer is related to another system
     *            transaction, enter its reference code in this field. If you
     *            are unsure about the meaning of this field, do not include it
     *            in your request. This field is useful in linking this request
     *            to another existing transaction which is already in the
     *            system. <b>Optional</b>
     * @param ExternalReference - In this field, enter an external transaction
     *            reference. An external transaction reference is something
     *            which yourself and the benecifiary agree upon. For example,
     *            this may be an invoice number, or a phrase describing the
     *            purpose of this transaction in a way that the beneficiary
     *            would understand. This field is optional and you may omit it
     *            in your request. <b>Optional</b>
     * @return String
     */
    public String createInternalTransferXml(String CurrencyCode, float Amount,
            String BeneficiaryAccount, String BeneficiaryEmail, String Narrative,
            String NarrativeFileName, String InternalReference, String ExternalReference)
            throws IOException {
        String base64String = null;
        if (!NarrativeFileName.isEmpty()) {
            base64String = base64Encode(NarrativeFileName);
        } else {
            base64String="";
        }
        
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acinternaltransfer</Method>"+
                "<CurrencyCode>"+CurrencyCode+"</CurrencyCode>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<BeneficiaryAccount>"+BeneficiaryAccount+"</BeneficiaryAccount>"+ 
                "<BeneficiaryEmail>"+BeneficiaryEmail+"</BeneficiaryEmail>"+  
                "<Narrative>"+Narrative+"</Narrative>"+ 
                "<NarrativeFileName>"+NarrativeFileName+"</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+ 
                "<InternalReference>"+InternalReference+"</InternalReference>"+ 
                "<ExternalReference>"+ExternalReference+"</ExternalReference>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    public String executeYoPaymentsRequest (String inputXML, String serviceUrl)throws Exception{
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        String result = null;

        // Work around self signed certificate issues
        // http://javaskeleton.blogspot.com/2010/07/avoiding-peer-not-authenticated-with.html

        httpclient = (DefaultHttpClient)WebClientDevWrapper.wrapClient(httpclient);
        
        System.out.println(inputXML);
        
        StringEntity entity = new StringEntity(inputXML,"UTF-8");
        
        HttpPost httppost = new HttpPost(serviceUrl);
        httppost.setEntity(entity);
        httppost.setHeader("Content-type", "text/xml");
        httppost.setHeader("Content-transfer-encoding","text");

        try {
            System.out.println("executing request" + httppost.getRequestLine());

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                if (responseEntity != null) {
                    InputStream instream = responseEntity.getContent();
                    result = convertStreamToString(instream);
                    //Do something with the result ...
                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    if (responseEntity != null) {
                        System.out.println("Response content length: " + responseEntity.getContentLength());
                        System.out.println("Response content: " + result);
                    }
                    EntityUtils.consume(responseEntity);
                }
            }
        } catch (ClientProtocolException e) {
            httpclient.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            httpclient.getConnectionManager().shutdown();
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        return result; 
    }

    public final static void main(String[] args) throws Exception {
        
        YoPaymentsClient yoPaymentsClient= new YoPaymentsClient("90004135493","1203036617");
        
        //String inputXML = yoPaymentsClient.createWithdrawalXml(1000,"25677123456","Narrative 6" );
        String inputXML = yoPaymentsClient.createWithdrawalXml(1000, "25677123456", "", "This is a Narrative", "Readme.txt", "", "1");
        //String inputXML = yoPaymentsClient.createDepositXml(1000000, "25677123456", "Narrative 6");
                     
        String serviceUrl = "https://41.220.12.206/services/yopaymentsdev/task.php";
        
        System.out.println(yoPaymentsClient.executeYoPaymentsRequest (inputXML, serviceUrl));
        
        //encodeBase64WithApache(); 

    }

}
