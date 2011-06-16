
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This example demonstrates how to create secure connections with a custom SSL
 * context.
 */
public class YoPaymentsClient {
    
    public void YoPaymentsClient(){
        
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
    
    private static String encodeBase64WithApache() throws IOException {

        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream input = new FileInputStream("Readme.txt");
        OutputStream output = new Base64OutputStream(new FileOutputStream("Readme64.txt"));
        int n = input.read(buffer, 0, BUFFER_SIZE);
        while (n >= 0) {
            output.write(buffer, 0, n);
            n = input.read(buffer, 0, BUFFER_SIZE);
        }
        input.close();
        output.close();
        return null;
    }
    
    //Withdrawfunds XML Builder method No Narrative file, No Internal Reference
    
    /**
     * This method creates the xml format that is accepted by the Yo Payments server.
     * 
     *  @return String          
     */
    
    public static String createWithdrawalXml(String APIUsername, String APIPassword,
            float Amount, String AccountPhoneNo,String Narrative) {
        String base64String = "VGhpcyBpcyBhIHRlc3QgZmlsZQ==";
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+AccountPhoneNo+"</Account>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+
                "<NarrativeFileName>invoice.txt</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    //Withdrawfunds XML Builder method No Narrative file, No Internal Reference
    
    public static String createWithdrawalXml(String APIUsername, String APIPassword,
            float Amount, String AccountPhoneNo, String AccountProviderCode,
            String Narrative, String NarrativeFileName, String NarrativeFileBase64,
            String InternalReference, String ExternalReference) {
        String base64String = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+AccountPhoneNo+"</Account>"+ 
                "<AccountProviderCode></AccountProviderCode>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+ 
                "<NarrativeFileName>invoice.txt</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+ 
                "<InternalReference></InternalReference>"+ 
                "<ExternalReference>2</ExternalReference>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    public static String executeYoPaymentsRequest (String inputXML, String serviceUrl)throws Exception{
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
        
        //String inputXML = createWithdrawalXml("90004135493","1203036617", 1000,"25677123456","Narrative 3" );
        
        //String base64String = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
               
        //String serviceUrl = "https://41.220.12.206/services/yopaymentsdev/task.php";
        
        //System.out.println(executeYoPaymentsRequest (inputXML, serviceUrl));
        
        encodeBase64WithApache();

    }

}
