package ug.co.yo.payments;

/**
 * @author munaawa
 *
 */
public class YoPaymentsSample {

    /**
     * @param args
     */
    public final static void main(String[] args) throws Exception {
        
        YoPaymentsAPIClient yoPaymentsClient= new YoPaymentsAPIClient("90004135493","1203036617");
        
        //String inputXML = yoPaymentsClient.createWithdrawalXml(1000,"25677123456","Narrative 6" );
        //String inputXML = yoPaymentsClient.createWithdrawalXml(1000, "25677123456", "", "This is a Narrative", "Readme.txt", "", "2");
        //String inputXML = yoPaymentsClient.createDepositXml(1000000, "25677123456", "Narrative 6");
        String inputXML = yoPaymentsClient.createBalanceCheckXml();
                     
        String serviceUrl = "https://41.220.12.206/services/yopaymentsdev/task.php";
        
        System.out.println(yoPaymentsClient.executeYoPaymentsRequest (inputXML, serviceUrl));

    }

}
