
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
public class ClientCustomSSL {

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
    
    //Withdrawfunds XML Builder method No Narrative file, No Internal Reference
    
    /**
     * @param String APIUsername, String APIPassword, float Amount, String
     *            BeneficiaryPhone, String Narrative
     */
    
    private static String createWithdrawFundsXML(String APIUsername, String APIPassword, float Amount,
            String BeneficiaryPhone, String Narrative) {
        return "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<AutoCreate>" + 
            "<Request>" + 
                "<APIUsername>"+APIUsername+"</APIUsername>" + 
                "<APIPassword>"+APIPassword+"</APIPassword>"+ 
                "<Method>acwithdrawfunds</Method>"+
                "<Amount>"+String.valueOf(Amount)+"</Amount>"+ 
                "<Account>"+BeneficiaryPhone+"</Account>"+ 
                //"<AccountProviderCode></AccountProviderCode>"+ 
                "<Narrative>"+Narrative+"</Narrative>"+ 
                //"<NarrativeFileName></NarrativeFileName>"+
                //"<NarrativeFileBase64></NarrativeFileBase64>"+ 
                //"<InternalReference></InternalReference>"+ 
                //"<ExternalReference>1</ExternalReference>"+
             "</Request>"+ 
        "</AutoCreate>";
    }
    
    private static String executePostRequest (String inputXML, String serviceUrl)throws Exception{
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
        
        String inputXML = createWithdrawFundsXML("90004135493","1203036617", 1000,"25677123456","Narrative 3" );
       
        String serviceUrl = "https://41.220.12.206/services/yopaymentsdev/task.php";
        
        System.out.println(executePostRequest (inputXML, serviceUrl));

    }

}
