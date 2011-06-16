
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
 * This example demonstrates how to create secure connections with a custom SSL
 * context.
 */
public class YoPaymentsClient {
    
    public YoPaymentsClient(){
        
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
        ByteArrayOutputStream byte1=new ByteArrayOutputStream();
        OutputStream output = new Base64OutputStream(byte1); //System.out
        int n = input.read(buffer, 0, BUFFER_SIZE);;
        while (n >= 0) {
            output.write(buffer, 0, n);
            n = input.read(buffer, 0, BUFFER_SIZE);
        }
        String result=byte1.toString();
        input.close();
        output.close();
        return result;
    }
    
    //Withdrawfunds XML Builder method No Narrative file, No Internal Reference
    
    /**
     * This method creates the xml format that is accepted by the Yo Payments server.
     * 
     *  @return String          
     * @throws IOException 
     */
    
    public static String createWithdrawalXml(String APIUsername, String APIPassword,
            float Amount, String AccountPhoneNo,String Narrative) throws IOException {
        String narrativeFile = "Desert.jpg";
        String base64String = base64Encode(narrativeFile);
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+AccountPhoneNo+"</Account>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+
                "<NarrativeFileName>"+narrativeFile+"</NarrativeFileName>"+
                "<NarrativeFileBase64>"+base64String+"</NarrativeFileBase64>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    //Withdrawfunds XML Builder method No Narrative file, No Internal Reference
    
    public static String createWithdrawalXml(String APIUsername, String APIPassword,
            float Amount, String AccountPhoneNo, String AccountProviderCode,
            String Narrative, String NarrativeFileName, String NarrativeFileBase64,
            String InternalReference, String ExternalReference) throws IOException {
        String base64String = base64Encode(NarrativeFileName);
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
                "<NarrativeFileName>"+NarrativeFileName+"</NarrativeFileName>"+
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
        
        String inputXML = createWithdrawalXml("90004135493","1203036617", 1000,"25677123456","Narrative 5" );
                     
        String serviceUrl = "https://41.220.12.206/services/yopaymentsdev/task.php";
        
        System.out.println(executeYoPaymentsRequest (inputXML, serviceUrl));
        
        //encodeBase64WithApache();

    }

}
