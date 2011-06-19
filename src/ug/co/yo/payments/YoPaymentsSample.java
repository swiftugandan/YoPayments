
package ug.co.yo.payments;


/**
 * @author Munaawa Philip (swiftugandan@gmail.com)
 * This class is a sample application of the Yo! Payments API (www.yo.co.ug/payments)
 */
public class YoPaymentsSample {

    /**
     * @param args
     */
    public final static void main(String[] args) throws Exception {

        YoPaymentsAPIClient yoPaymentsClient = new YoPaymentsAPIClient("90004135493", "1203036617");

        String inputXML =yoPaymentsClient.createWithdrawalXml(1000,"25677123456","Narrative 6");
        // String inputXML = yoPaymentsClient.createWithdrawalXml(1000,"25677123456", "", "This is a Narrative", "Readme.txt", "", "2");
        // String inputXML = yoPaymentsClient.createDepositXml(1000000,"25677123456", "Narrative 7");
        // String inputXML = yoPaymentsClient.createBalanceCheckXml();

        String serviceUrl = "http://41.220.12.206/services/yopaymentsdev/task.php";

        YoPaymentsResponse yoPaymentsResponse = (YoPaymentsResponse)yoPaymentsClient.executeYoPaymentsRequest(inputXML, serviceUrl);
        
        System.out.println(yoPaymentsResponse.toString());

    }
}
