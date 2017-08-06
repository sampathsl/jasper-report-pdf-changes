package src.com.intervest.hercules;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class UploadFile {
	
	private final static SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
	private final static String FILE_SEPARATOR = "/";

	public static void uploadFile(String outputFileName, String fileName) {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		FTPSClient ftpClient = null;

		try {

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			ftpClient = new FTPSClient(sc);

			ftpClient.connect(ProgramProperties.FTP_SERVER.trim(),
					Integer.parseInt(ProgramProperties.FTP_PORT.trim()));

			int reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				throw new Exception("Exception in connecting to FTPs Server");
			}

			ftpClient.login(ProgramProperties.FTP_USER.trim(), PasswordEncoder.
					base64decode(ProgramProperties.FTP_PASSWORD.trim()));
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// Set protection buffer size
			ftpClient.execPBSZ(0);
			// Set data channel protection to private
			ftpClient.execPROT("P");

			File uploadedFile1 = new File(outputFileName);
			InputStream inputStream1 = new BufferedInputStream(new FileInputStream(uploadedFile1));

			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String folderName = format2.format(Calendar.getInstance().getTime());

			ftpClient.makeDirectory("/Daily Data Feeds/" + folderName);

			boolean success = ftpClient.storeFile("/Daily Data Feeds/" + folderName +
                    FILE_SEPARATOR + fileName, inputStream1);
			inputStream1.close();

			if (success) {
				System.out.println("File #1 has been Uploaded successfully.");
			} else {
				throw new RuntimeException("unable to uploaded file");
			}

		} catch (Exception ex) {

			try {
				// emailFile(outputFileNameZip,fileName);
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	/**
	 * Filter policy number and convert policy number into short format E.g:
	 * ERV0000000345 -> ERV345
	 * 
	 * @param policyNumber
	 * @return
	 */

	public static String getFilteredPolicyNumber(String policyNumber) {

		StringBuffer filteredPolicyNumber = new StringBuffer();

		if (policyNumber.contains(FILE_SEPARATOR)) {

			String[] amendmentPolicyNumber = policyNumber.split(FILE_SEPARATOR);
			String firstPart = amendmentPolicyNumber[0];
			String lastPart = amendmentPolicyNumber[1];

			String value = firstPart.substring(0, 3);
			StringBuffer policyShortNumber = new StringBuffer(firstPart.substring(4, firstPart.length()));
			String filteredZero = policyShortNumber.toString().replaceFirst("^0+(?!$)", "");
			
			if (value != null) {
				filteredPolicyNumber.append(value + filteredZero + FILE_SEPARATOR + lastPart);
			} else {
				throw new IllegalArgumentException("Invalid Policy Number : " + policyNumber);
			}

		} else {

			String value = policyNumber.substring(0, 3);
			StringBuffer policyShortNumber = new StringBuffer(policyNumber.substring(4, policyNumber.length()));
			String filteredZero = policyShortNumber.toString().replaceFirst("^0+(?!$)", "");
	
			if (value != null) {
				filteredPolicyNumber.append(value + filteredZero);
			} else {
				throw new IllegalArgumentException("Invalid Policy Number : " + policyNumber);
			}

		}

		return filteredPolicyNumber.toString();

	}
	
	/**
     * Merge multiple pdf into one pdf
     * 
     * @param list
     *            of PdfReaders
     * @param outputStream
     *            output file output stream
     * @throws DocumentException
     * @throws IOException
     */
    public static void doMerge(List<PdfReader> list, ByteArrayOutputStream outputStream)
            throws DocumentException, IOException {
    	
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        
        for (PdfReader reader : list) {
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
        }
        
        outputStream.flush();
        document.close();
        outputStream.close();
        
    }
    
    
    public static void generateValidationCertificatePDF( java.io.OutputStream out , Long insuranceId ,
                                                         String templatePath , boolean isPrintVersion) {
		
		try {
		
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			DBAdmin dbAdmin = new DBAdmin();
			
			ValidationCertificate validationCertificate = new ValidationCertificate();
			validationCertificate.setIsPrintVersion(isPrintVersion);
			List<TableOne> tableOneList = new ArrayList<TableOne>();
			List<TableTwo> tableTwoList = new ArrayList<TableTwo>();
			List<TableThree> TableThreeList = new ArrayList<TableThree>();
			List<TableFour> tableFourList = new ArrayList<TableFour>();
			TableOne tableOne = new TableOne();
			TableThree tableThree = new TableThree();
			
			StsTravelInsurance t = dbAdmin.selectStsTravelInsurance(insuranceId,null,null);
			StsTravelOrganizer org = dbAdmin.selectStsTravelOrganiser(insuranceId,null,null);
			StringBuffer sbNameAndAddress = new StringBuffer();
			StringBuffer sbContactDetails = new StringBuffer();
			
			
			if(t.getCreationDate().compareTo(getDateFromPropertyFile()) < 0)
				t.setCruiseLoading(null);
			
			if(org != null && org.getOrganiserTitle() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getOrganiserTitle())+" ");
			}
			
			if(org != null && org.getOrganiserForename() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getOrganiserForename())+" ");
			}
			
			if(org != null && org.getOrganiserSurname() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getOrganiserSurname())+"\n");
			}
			
			if(org != null && org.getAddress1() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getAddress1())+"\n");
			}
			
			if(org != null && org.getAddress2() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getAddress2())+"\n");
			}
			
			if(org != null && org.getTown() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getTown())+"\n");
			}
			
			if(org != null && org.getCounty() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(org.getCounty())+"\n");
			}
			
			if(org != null && org.getCountry() != null) {
				sbNameAndAddress.append(org.getCountry()+"\n");
			}
			
			if(org != null && org.getPostcode() != null) {
				sbNameAndAddress.append(org.getPostcode().toUpperCase());
			}
			
			if(org != null && org.getOrganiserDayphone() != null){
				sbContactDetails.append("Home Tel no: " + org.getOrganiserDayphone() + "\n");
			}
			
			if(org != null && org.getOrganiserEveningphone() != null){
				sbContactDetails.append("Mobile Tel no: " + org.getOrganiserEveningphone());
			}
			
			tableOne.setNameAndAddress(sbNameAndAddress.toString());
			tableOne.setContactDetails(sbContactDetails.toString());
			tableOneList.add(tableOne);
			validationCertificate.setTableOneList(tableOneList);
			
			//set insurer
			if(t.getPolicyNumber().equals(Underwriter.ASTRENSKA)) {
				validationCertificate.setInsurer("Astrenska");
			} else if(t.getPolicyNumber().equals(Underwriter.UKG)) {
				validationCertificate.setInsurer("UK General Insurance Ltd");
			} else if(t.getPolicyNumber().equals(Underwriter.URV)) {
				validationCertificate.setInsurer("Union Reiseversicherung AG");
			} else if(t.getPolicyNumber().equals(Underwriter.ERV)) {
				validationCertificate.setInsurer("ERV Insurance");
			} else if(t.getPolicyNumber().equals(Underwriter.AXA)) {
				validationCertificate.setInsurer("AXA Insurance UK plc");
			} else if(t.getPolicyNumber().equals(Underwriter.CIGNA)) {
				validationCertificate.setInsurer("Cigna Insurance Services (Europe) Limited");
			} else {
				validationCertificate.setInsurer("ERV Insurance");
			}
			
			validationCertificate.setPolicyRefNumber(t.getPolicyNumber());
			String toDay = df.format(new Date());
			validationCertificate.setDate(toDay);
			
			if(t == null || org == null)
				throw new Exception("Policy not found!");
	
			List <StsTravelPersonScreening> p = null;
			
			try{
			  p = dbAdmin.selectStsTravelPersonWithScreening(insuranceId,null,null);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			if(t.getAdminFee() == null)
				t.setAdminFee(new BigDecimal("0.00"));
		    
		   /*Keep the traveller screenings for validation certificate*/
		    List<TravellerScreeningsDTO> travellerScreenings = new ArrayList<TravellerScreeningsDTO>();
	  		for(StsTravelPersonScreening stps : p){
	  			if(stps.getXml() != null){
	  				TravellerScreeningsDTO tsd = new TravellerScreeningsDTO();
	  				tsd.setTitle(stps.getTitle());
	  				tsd.setForeName(stps.getForename());
	  				tsd.setSurName(stps.getSurname());
	  				tsd.setConditions(stps.getConditions());
	  				tsd.setQandAnswers(getScreeningQnA(stps, insuranceId));
	  				tsd.setLinkedConditions(stps.getLinkedConditions());
	  				travellerScreenings.add(tsd);
	  			}
	  		}
	  		
	  		for(StsTravelPersonScreening stps : p) {
	  			
	  			TableTwo tableTwo = new TableTwo();
	  			tableTwo.setTitle(stps.getTitle());
	  			tableTwo.setFirstName(stps.getForename());
	  			tableTwo.setSurname(stps.getSurname());
	  			tableTwo.setAge(new Long(stps.getAge()));
	
	  			StringBuffer sb = new StringBuffer();
	  			if(stps.getFreeMedicalCondition() != null){
	  				sb.append(stps.getFreeMedicalCondition().getDisplayName());
	  				tableTwo.setMedicalRef(sb.toString());
	  			} else if(stps.getConditions() != null && stps.getStatus() != null) {
	  				if(stps.getStatus().equalsIgnoreCase("decline")){
	  					sb.append("EXCLUDE PRE-EXISTING (Declined)");
					}
	  				sb.append(stps.getConditions().replaceAll("\\|", "\\ \\|\\ "));
	  				if(stps.getStatus().equalsIgnoreCase("exclude")){
	  					sb.append("- EXCLUDE PRE-EXISTING");
					}
	  				tableTwo.setMedicalRef(sb.toString());
	  			} else {
	  				if(stps.getStatus() != null && stps.getStatus().equalsIgnoreCase("exclude")){
	  					sb.append("EXCLUDE PRE-EXISTING");
	  				} else {
	  					sb.append("none");
	  				}
	  				tableTwo.setMedicalRef(sb.toString());
				}
	  			
	  			tableTwoList.add(tableTwo);
	  			
	  		}
	  		
	  		validationCertificate.setTableTwoList(tableTwoList);
	  		
	  		StringBuffer countryList = new StringBuffer();
			if(t.getCountries() != null) {
				String[] countries = t.getCountries().split(",");
				int count = 0;
				for(String country : countries){
					countryList = countryList.append(WordUtils.capitalizeFully(country));
					count++;
					if(!(countries.length <= count)){
						countryList = countryList.append(",");
					}
				}
			}
			
			//UK , USA UPPER CASE FIX - 30-10-2014
			if(countryList != null){
				countryList = countryList.toString().contains("Uk") ? new StringBuffer( countryList.toString().
                        replace("Uk","UK") ) : countryList ;
				countryList = countryList.toString().contains("Usa") ? new StringBuffer( countryList.toString().
                        replace("Usa","USA")) : countryList ;
			}
	  		
	  		tableThree.setDateOfIssue(df.format(t.getCreationDate()));
	  		tableThree.setPolicyType(t.getSingleTrip().equals("Y") ? "Single Trip" : "Annual Multi Trip");
	  		String destination = t.getLongStay().equals("A") ? t.getCountries() : countryList.toString();
	  		destination = destination != null ? destination.replaceAll(",", ", ") : "";
	  		tableThree.setDestination(destination != null ? destination.trim().replaceAll(" +", " ") : "");
	  		
	  		tableThree.setDepartureDate(df.format(t.getStartDate()));
	  		tableThree.setReturnDate(df.format(t.getEndDate()));
	  		
	  		StringBuffer sbPeriodOfInsurance = new StringBuffer();
	  		if(t.getLongStay().equalsIgnoreCase("S")) {
	  			if(t.getDuration() == 1){
	  				sbPeriodOfInsurance.append(t.getDuration() + " day");
	  			} else {
	  				sbPeriodOfInsurance.append(t.getDuration() + " days");
	  			}
	  		} else {
	  			sbPeriodOfInsurance.append("365 days");
	  		}
	  		
	  		tableThree.setPeriodOfInsurance(sbPeriodOfInsurance.toString());
	  		
	  		StringBuffer sbCancellationCover = new StringBuffer();
	  		
	  		if(t.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA)) {
				
	  			if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000 || t.getCancellationCover() == 1500){
		  				sbCancellationCover.append("�1500");
		  			} else if(t.getCancellationCover() == 2000 || t.getCancellationCover() == 3000){
		  				sbCancellationCover.append("�3000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
	  			
			} else if(t.getPolicyUnderwriter().equals(Underwriter.UKG)) {
				
				if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000){
		  				sbCancellationCover.append("�1000");
		  			} else if(t.getCancellationCover() == 1500 || t.getCancellationCover() == 2000 ||
                            t.getCancellationCover() == 3000){
		  				sbCancellationCover.append("�3000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
			} else if(t.getPolicyUnderwriter().equals(Underwriter.URV)) {
				
				if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000) {
		  				sbCancellationCover.append("�1000");
		  			} else if(t.getCancellationCover() == 1500 || t.getCancellationCover() == 2000 ||
                            t.getCancellationCover() == 3000){
		  				sbCancellationCover.append("�3000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
				
			} else if(t.getPolicyUnderwriter().equals(Underwriter.ERV)) {
				
				if(t.getCancellationCover() != 0) {
		  			sbCancellationCover.append("�"+t.getCancellationCover());
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
			} else if(t.getPolicyUnderwriter().equals(Underwriter.AXA)){
				
				if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000 ||
                            t.getCancellationCover() == 1500 || t.getCancellationCover() == 2000){
		  				sbCancellationCover.append("�2000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
			} else if(t.getPolicyUnderwriter().equals(Underwriter.CIGNA)){
				
				if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000 ||
                            t.getCancellationCover() == 1500){
		  				sbCancellationCover.append("�1500");
		  			} else if(t.getCancellationCover() == 2000){
		  				sbCancellationCover.append("�3000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
			} else {
				
				if(t.getCancellationCover() != 0) {
		  			if(t.getCancellationCover() == 500 || t.getCancellationCover() == 1000 ||
                            t.getCancellationCover() == 1500){
		  				sbCancellationCover.append("�1500");
		  			} else if(t.getCancellationCover() == 2000 || t.getCancellationCover() == 3000){
		  				sbCancellationCover.append("�3000");
		  			} else {
		  				sbCancellationCover.append("�"+t.getCancellationCover());
		  			}
		  		} else {
		  			sbCancellationCover.append("�0");
		  		}
				
			}
	  		
	  		tableThree.setCanellationLimit( sbCancellationCover.toString() );
	  		
	  		tableThree.setMedicalExcess("�"+String.valueOf(t.getMedicalExcess()));
	  		
	  		StringBuffer sbPersonalBaggageLimit = new StringBuffer();
	  		if(t.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA) || t.getPolicyUnderwriter().equals(Underwriter.URV) ||
                    t.getPolicyUnderwriter().equals(Underwriter.UKG)){
		  		if(t.getPersonalBaggage() != 0){
		  			sbPersonalBaggageLimit.append("�2000");
		  		} else {
		  			sbPersonalBaggageLimit.append("Excluded");
		  		}
	  		} else {
		  		if(t.getPersonalBaggage() != 0){
		  			sbPersonalBaggageLimit.append("�"+t.getPersonalBaggage());
		  		} else {
		  			sbPersonalBaggageLimit.append("Excluded");
		  		}
	  		}
	  		
	  		tableThree.setPersonalBaggageLimit(sbPersonalBaggageLimit.toString());
	  		
	  		StringBuffer sbWinterSportsCover = new StringBuffer();
	  		if(t.getWinterSports() != null){
	  			if(t.getWinterSports().equalsIgnoreCase("Y")){
	  				sbWinterSportsCover.append("Included");
	  			} else {
	  				sbWinterSportsCover.append("Excluded");
	  			}
	  		} else {
	  			sbWinterSportsCover.append("Not Permitted");
	  		}
	  		
	  		tableThree.setWinterSportsCover(sbWinterSportsCover.toString());
	  		
	  		StringBuffer sbGolfCover1 = new StringBuffer();
	  		StringBuffer sbGolfCover2 = new StringBuffer();
	  		if(t.getGolfEquipment() != null){
	  			if(t.getGolfEquipment().equalsIgnoreCase("Y")){
	  				sbGolfCover1.append("Included " + t.getGolfEquipment() + " Person(s)");
	  				sbGolfCover2.append("Included");
	  			} else {
	  				sbGolfCover1.append("Excluded");
	  				sbGolfCover2.append("Excluded");
	  			}
	  		} else {
	  			sbGolfCover1.append("Not Permitted");
	  			sbGolfCover2.append("Not Permitted");
	  		}
	  		
	  		tableThree.setGolfCover(sbGolfCover2.toString());
	  		
	  		if (!(t.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA) || t.getPolicyUnderwriter().equals(Underwriter.UKG) ||
                    (t.getPolicyUnderwriter().equals(Underwriter.URV) &&
	  				(t.getPolicyType().equals(PolicyType.ANNUAL_MULTI_TRIP) || t.getPolicyType().equals(PolicyType.LONGSTAY)) )) ){
	  			
	  			if(t.getCruiseLoading() != null){
		  			StringBuffer sbOceanCruiseCover = new StringBuffer();
			  		if(t.getCruiseLoading().equalsIgnoreCase("Y")){
			  			//sbOceanCruiseCover.append("You are covered for ocean going cruises");
			  			sbOceanCruiseCover.append("Included");
			  		} else {
			  			if(t.getCruiseLoading() != null){
			  				//sbOceanCruiseCover.append("You are NOT covered for ocean going cruises");
			  				sbOceanCruiseCover.append("Excluded");
			  			}
			  		}
			  		tableThree.setOceanCruiseCover(sbOceanCruiseCover.toString());
		  		}
	  			
	  		}
	  		
	  		if( !(t.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA) || t.getPolicyUnderwriter().equals(Underwriter.UKG) || 
	  				t.getPolicyUnderwriter().equals(Underwriter.ERV)) ){
		  		
		  		if(t.getCruiseCover() != null){
		  			StringBuffer sbOceanOptionalCover = new StringBuffer();
			  		if(t.getCruiseCover().equalsIgnoreCase("Y")){
			  			//sbOceanOptionalCover.append("Included");
			  			sbOceanOptionalCover.append("Yes");
			  		} else {
			  			//sbOceanOptionalCover.append("Excluded");
			  			sbOceanOptionalCover.append("No");
			  		}
			  		
			  		tableThree.setCruiseOptionalCoverOption(sbOceanOptionalCover.toString());
		  		}
	  			
	  		}
	  		
	  		StringBuffer sbGadgetCover = new StringBuffer();
	  		if(t.getGadgetCover() != null){
	  			if(t.getGadgetCover().equalsIgnoreCase("Y")){
	  				sbGadgetCover.append("Included");
	  			} else {
	  				sbGadgetCover.append("Excluded");
	  			}
	  		} else {
	  			sbGadgetCover.append("Not Permitted");
	  		}
	  		
	  		tableThree.setGadgetCover(sbGadgetCover.toString());
	  		
	  		TableThreeList.add(tableThree);
	  		validationCertificate.setTableThreeList(TableThreeList);
			
			MathContext mc = new MathContext(6,RoundingMode.HALF_UP);
			
			BigDecimal baseSellPrice = t.getBaseSellPrice() != null ? t.getBaseSellPrice() : new BigDecimal("0.00");
			BigDecimal screeningTotalPrice = t.getScreeningTotalPrice() != null ? t.getScreeningTotalPrice() : new BigDecimal("0.00");
			BigDecimal promoCodeValue = t.getPromoValue() != null ? t.getPromoValue() : new BigDecimal("0.00");
//			BigDecimal basePremium = (baseSellPrice.add(screeningTotalPrice).add(t.getWINTER_SELL_PRICE() != null ?
// t.getWINTER_SELL_PRICE():new BigDecimal(0)).add(t.getGOLF_SELL_PRICE() != null ? t.getGOLF_SELL_PRICE():new BigDecimal(0)).
// add(t.getCRUISE_SELL_PRICE() != null ? t.getCRUISE_SELL_PRICE():new BigDecimal(0)).add(t.getGADGET_SELL_PRICE() != null ?
// t.getGADGET_SELL_PRICE():new BigDecimal(0))).setScale(2,RoundingMode.HALF_UP);
			BigDecimal basePremium = (baseSellPrice.add(screeningTotalPrice)).setScale(2,RoundingMode.HALF_UP);
			
			BigDecimal directDiscountAmount = t.getDirectDiscountAmount() != null ? t.getDirectDiscountAmount() :
                    new BigDecimal("0.00");
			BigDecimal priceMatchingDiscountAmount = t.getPricematchingDiscountAmount() != null ? t.getPricematchingDiscountAmount() :
                    new BigDecimal("0.00");
			BigDecimal amendedDiscountAmount=t.getAmendedDiscountAmount() != null ? t.getAmendedDiscountAmount() :
                    new BigDecimal("0.00");
			BigDecimal discounts = ((directDiscountAmount.add(priceMatchingDiscountAmount)).add(promoCodeValue).
                    add(amendedDiscountAmount)).setScale(2,RoundingMode.HALF_UP);
			
			BigDecimal extrasSellPrice = t.getExtrasSellPrice() != null ? t.getExtrasSellPrice() : new BigDecimal("0.00");
			BigDecimal screeingTotalPrice = t.getScreeningTotalPrice() != null ? t.getScreeningTotalPrice() : new BigDecimal("0.00");
			BigDecimal sellPriceIpt = ((baseSellPrice.add(extrasSellPrice).add(screeingTotalPrice)).
                    subtract(promoCodeValue.add(directDiscountAmount).add(priceMatchingDiscountAmount).
                            add(amendedDiscountAmount))).setScale(2,RoundingMode.HALF_UP);
			
			TableFour tableFour = new TableFour();
	  		tableFour.setBasePremium("�"+String.format("%,.2f", basePremium.setScale(2)));
	  		tableFour.setAdditionalCoverPremium("�"+String.format("%,.2f", extrasSellPrice.setScale(2,RoundingMode.HALF_UP)));
	  		String discount = discounts.compareTo(new BigDecimal(0)) > 0 ? String.format("%,.2f", discounts.setScale(2)) :
                    String.format("%,.2f", BigDecimal.ZERO.setScale(2)) ;
	  		if( discounts.compareTo(new BigDecimal(0)) > 0 ){
	  			tableFour.setDiscount("�"+discount);
	  		}
	  		
	  		BigDecimal insurancePremiumTax = sellPriceIpt.multiply((t.getIpt().subtract(BigDecimal.ONE)));
	  		/*https://stackoverflow.com/questions/24272849/how-to-truncate-a-bigdecimal-without-rounding
*/	  		tableFour.setInsurancePremiumTax("�"+String.format("%,.2f", insurancePremiumTax.setScale(2,RoundingMode.DOWN)));
	  		tableFour.setAdminFee("�" + String.format("%,.2f", t.getAdminFee().setScale(2,RoundingMode.DOWN)));
	  		tableFour.setTransactionFee("�"+ String.format("%,.2f", t.getTransactionFee().setScale(2,RoundingMode.DOWN)));
	  		tableFour.setpAndP("�"+String.format("%,.2f", t.getPostage().setScale(2,RoundingMode.DOWN)));
	  		tableFour.setTotalPaid("�"+String.format("%,.2f", t.getFinalPrice().setScale(2,RoundingMode.DOWN)));
	  		tableFour.setIptPercent((t.getIpt().subtract(new BigDecimal(1)).multiply(new BigDecimal(100),mc)).intValue()+"%");
	  		
	  		tableFourList.add(tableFour);
	  		validationCertificate.setTableFourList(tableFourList);
		
			Map<String, Object> parametersTable = new HashMap<String, Object>();
			parametersTable.put("SUBREPORT_DIR",templatePath + File.separator );
			String pathToDynamicPdfJasperFile = templatePath + File.separator + "MainReport.jasper";
			
		  	JRBeanCollectionDataSource beanCollectionDataSourceDyanmicPage = new JRBeanCollectionDataSource(Arrays.
                    asList(validationCertificate));
		  	JasperPrint jasperPrintTableROI = JasperFillManager.fillReport(pathToDynamicPdfJasperFile,parametersTable,
                    beanCollectionDataSourceDyanmicPage);
            JasperExportManager.exportReportToPdfStream(jasperPrintTableROI,out);
            
            System.out.println("VALIDATION CERTIFICATE DONE!");
            
		} catch(Exception e) {
			  e.printStackTrace();
			  System.out.println("jasper exe error is at dynamic validation certificate" + e);
		}
		
	}
    
    public static void writeCoverLetterDocumentPDF(  java.io.OutputStream out , Long insuranceId,String templatePath,
                                                     boolean coverLetterPrint) {
		
		try {
			
			DBAdmin dbAdmin = new DBAdmin();
			StsTravelInsurance t = dbAdmin.selectStsTravelInsurance(insuranceId,null,null);
			StsTravelOrganizer p = dbAdmin.selectStsTravelOrganiser(insuranceId,null,null);
			Map<String, Object> parametersTable = new HashMap<String, Object>();
			parametersTable.put("SUBREPORT_DIR",templatePath + File.separator );
			StringBuffer sbNameAndAddress = new StringBuffer();
			StringBuffer sbOrganiserName = new StringBuffer();
			StringBuffer pathToDynamicPdfJasperFile = new StringBuffer();
			StringBuffer dateStr = new StringBuffer();
			StringBuffer underWriterName = new StringBuffer();
			
			if(coverLetterPrint){
				pathToDynamicPdfJasperFile.append(templatePath + File.separator + "CoverLetterPrint.jasper");
			} else {
				pathToDynamicPdfJasperFile.append(templatePath + File.separator + "CoverLetterMain.jasper");
			}
			
			if(p.getOrganiserTitle() != null){
				sbOrganiserName.append(p.getOrganiserTitle() + " ");
			}
			
			if(p.getOrganiserSurname() != null){
				sbOrganiserName.append(p.getOrganiserSurname());
			}
			
			//--------------------------------------------------
			if(p != null && p.getOrganiserTitle() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getOrganiserTitle())+" ");
			}
			
			if(p != null && p.getOrganiserForename() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getOrganiserForename())+" ");
			}
			
			if(p != null && p.getOrganiserSurname() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getOrganiserSurname())+"\n");
			}
			
			if(p != null && p.getAddress1() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getAddress1())+"\n");
			}
			
			if(p != null && p.getAddress2() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getAddress2())+"\n");
			}
			
			if(p != null && p.getTown() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getTown())+"\n");
			}
			
			if(p != null && p.getCounty() != null) {
				sbNameAndAddress.append(WordUtils.capitalize(p.getCounty())+"\n");
			}
			
			if(p != null && p.getCountry() != null) {
				sbNameAndAddress.append(p.getCountry()+"\n");
			}
			
			if(p != null && p.getPostcode() != null) {
				sbNameAndAddress.append(p.getPostcode().toUpperCase());
			}
			
			String imageURLTrustPilot = null;
			if(p.getOrganiserEmail() != null){
				imageURLTrustPilot = TrustPilot.getUniqueLink(Long.toString(insuranceId),p.getOrganiserEmail(),
						p.getOrganiserForename() +" "+ p.getOrganiserSurname());
			}
			
			dateStr.append(df.format(new Date()));
			
			if(t.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA)) {
				underWriterName.append("Astrenska");
			} else if(t.getPolicyUnderwriter().equals(Underwriter.UKG)) {
				underWriterName.append("UK General Insurance Ltd");
			} else if(t.getPolicyUnderwriter().equals(Underwriter.URV)) {
				underWriterName.append("Union Reiseversicherung AG");
			} else if(t.getPolicyUnderwriter().equals(Underwriter.ERV)) {
				underWriterName.append("ERV Insurance");
			} else if(t.getPolicyUnderwriter().equals(Underwriter.AXA)) {
				underWriterName.append("AXA Insurance UK plc");
			} else if(t.getPolicyUnderwriter().equals(Underwriter.CIGNA)) {
				underWriterName.append("Cigna Insurance Services (Europe) Limited");
			} else {
				underWriterName.append("ERV Insurance");
			}
			
			PolicyLetterMain policyLetterMain = new PolicyLetterMain(sbNameAndAddress.toString(),t.getPolicyNumber(),
					sbOrganiserName.toString(),dateStr.toString(),imageURLTrustPilot);
			
		  	JRBeanCollectionDataSource beanCollectionDataSourceDyanmicPage = new JRBeanCollectionDataSource(Arrays.
                    asList(policyLetterMain));
		  	JasperPrint jasperPrintTableROI = JasperFillManager.fillReport(pathToDynamicPdfJasperFile.toString(),
                    parametersTable,beanCollectionDataSourceDyanmicPage);
            JasperExportManager.exportReportToPdfStream(jasperPrintTableROI,out);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
    public static Date getDateFromPropertyFile()
	{
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date cutOffDate = new Date();
		try{
			String axaCruiseChangedDate = ProgramProperties.AXA_CRUISE_CHANGED_DATE.trim();
			cutOffDate = formatter.parse(axaCruiseChangedDate);
		}catch(Exception e){
			e.printStackTrace();
		}
		return cutOffDate;
	}
    
	public static String getScreeningQnA(StsTravelPersonScreening t, long insuranceId) {
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		StringBuilder sb = new StringBuilder();
		
		try {
			
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(new String(CipherUtils.decrypt(t.getXml(),
					new Long(Long.toString(insuranceId) + Long.toString(t.getPersonId())))))));

			XPathFactory xpFactory = XPathFactory.newInstance();
			XPath xpath = xpFactory.newXPath();
			XPathExpression conditionExpr = xpath
					.compile("/Screening/ScreeningPath/ScreeningHistory/conditions/Condition");
			XPathExpression conditionNameExpr = xpath.compile("name/text()");
			XPathExpression conditionQAndAExpr = xpath.compile("questions/QandA");
			// XPathExpression conditionQuestionsExpr =
			// xpath.compile("questions/QandA/question");
			// XPathExpression conditionAnswersExpr =
			// xpath.compile("questions/QandA/answer");
			// XPathExpression conditionNegativeAnswerExpr =
			// xpath.compile("questions/QandA/negativeanswer/text()");

			NodeList conditionList = (NodeList) conditionExpr.evaluate(doc, XPathConstants.NODESET);
			for (int nCond = 0; nCond < conditionList.getLength(); nCond++) {
				Node conditionNode = conditionList.item(nCond);
				Node conditionNameNode = (Node) conditionNameExpr.evaluate(conditionNode, XPathConstants.NODE);
				String conditionName = conditionNameNode.getNodeValue();
				sb.append("<b>" + conditionName + "</b>");
				NodeList conditionQAndAList = (NodeList) conditionQAndAExpr.evaluate(conditionNode,
						XPathConstants.NODESET);

				sb.append("<ul>");
				for (int nQuest = 0; nQuest < conditionQAndAList.getLength(); nQuest++) {
					// Node conditionQAndANode = (Node)
					// conditionQAndAList.item(nQuest);

					Element question = (Element) conditionQAndAList.item(nQuest);
					NodeList txts = question.getElementsByTagName("question");
					NodeList answer = question.getElementsByTagName("answer");

					// Node conditionQuestionNode = (Node)
					// conditionQuestionsExpr.evaluate(conditionQAndANode,
					// XPathConstants.NODE);
					String conditionQuestion = txts.item(0).getFirstChild().getNodeValue();

					// NodeList conditionAnswerList = (NodeList)
					// conditionAnswersExpr.evaluate(conditionQAndANode,
					// XPathConstants.NODESET);
					// NodeList conditionNegitiveAnswerList = (NodeList)
					// conditionNegativeAnswerExpr.evaluate(conditionQAndANode,
					// XPathConstants.NODESET);

					// Node conditionAnswerNode = (Node)
					// conditionAnswerList.item(nQuest);
					// String conditionQuestion =
					// conditionQuestionNode.getNodeValue();
					String conditionAnswer = getAnswer(answer);
					sb.append("<li>" + conditionQuestion + " =&gt; <i>" + conditionAnswer + "</i></li>");
				}
				sb.append("</ul><br/>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	public static String getAnswer(NodeList conditionAnswerList) {
		
		StringBuilder answer = new StringBuilder();
		
		for (int nQuest = 0; nQuest < conditionAnswerList.getLength(); nQuest++) {
			Node conditionNode = conditionAnswerList.item(nQuest);
			if (nQuest >= 1)
				answer.append(",");
			answer.append(conditionNode.getFirstChild().getNodeValue());
		}
		
		return answer.toString();
		
	}
	
	public static void writeScreeningLetterPDF(  java.io.OutputStream out, Long insuranceId, Long policyTravellerId, 
			String templatePath, boolean isPrintVersion) {
		
		try {
		
			DBAdmin dbAdmin = new DBAdmin();
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			StsTravelInsurance s = dbAdmin.selectStsTravelInsurance(insuranceId,null,null);
			List <StsTravelPersonScreening> tss = dbAdmin.selectStsTravelPersonWithScreening(insuranceId,null,null);
			
			MedicalCertificateMain medicalCertificateMain = new MedicalCertificateMain();
			medicalCertificateMain.setIsPrintVersion(isPrintVersion);
			String toDay = df.format(new Date());
			medicalCertificateMain.setDate(toDay);
			
			//set insurer
			//set insurer company name
			StringBuffer sbCompanyName = new StringBuffer();
			if(s.getPolicyUnderwriter().equals(Underwriter.ASTRENSKA)) {
				medicalCertificateMain.setUnderwriter("Astrenska");
				sbCompanyName.append("Astrenska");
			} else if(s.getPolicyUnderwriter().equals(Underwriter.UKG)) {
				medicalCertificateMain.setUnderwriter("UK General Insurance Ltd");
				sbCompanyName.append("UK General Insurance Ltd");
			} else if(s.getPolicyUnderwriter().equals(Underwriter.URV)) {
				medicalCertificateMain.setUnderwriter("Union Reiseversicherung AG");
				sbCompanyName.append("Union Reiseversicherung AG");
			} else if(s.getPolicyUnderwriter().equals(Underwriter.ERV)) {
				medicalCertificateMain.setUnderwriter("ERV Insurance");
				sbCompanyName.append("ERV Insurance plc");
			} else {
				medicalCertificateMain.setUnderwriter("ERV Insurance");
				sbCompanyName.append("ERV Insurance plc");
			}
			
			medicalCertificateMain.setPolicyNumber(s.getPolicyNumber());
			
			List<MedicalDeclarationDetails> medicalDeclarationDetailsList = new ArrayList<MedicalDeclarationDetails>();
			MedicalDeclarationDetails medicalDeclarationDetails = new MedicalDeclarationDetails();
			medicalDeclarationDetails.setAgentRefPolicyNumber(s.getPolicyNumber());
			medicalDeclarationDetails.setPeriodOfInsurance(s.getSingleTrip().equals("Y") ? "Single Trip" : "Annual Multi Trip");
			medicalDeclarationDetails.setExpiryDate(df.format(s.getEndDate()));
			medicalDeclarationDetails.setDateOfDeparture(df.format(s.getStartDate()));
			medicalDeclarationDetails.setDateOfReturn(df.format(s.getEndDate()));
			
			StringBuffer returnStr = new StringBuffer();
			if(s.getPolicyType().equals(PolicyType.ANNUAL_MULTI_TRIP)) {
				returnStr.append(s.getCountries());
			}
			
			List<String> TO_LOCATION_AREA = new ArrayList<String>();
			
			if( s.getToLocation() !=null){
				String[] l = s.getToLocation().split("\\|");
				for( String el: l )
					TO_LOCATION_AREA.add( el);
			}
				
			if ( s.getPolicyType().equals(PolicyType.SINGLE_TRIP) || s.getPolicyType().equals(PolicyType.LONGSTAY) ) {
				returnStr = new StringBuffer();
				System.out.println("TO_LOCATION_AREA :: " + TO_LOCATION_AREA);
				for( String c : dbAdmin.destinationListToSortedCountryNames(TO_LOCATION_AREA) ) {
					returnStr = returnStr.append(c + ", ");
				}
				returnStr = new StringBuffer(returnStr.toString().substring(0, returnStr.length() - 2));
			}
			
			medicalDeclarationDetails.setGeographicalLimit(returnStr.toString());
			medicalDeclarationDetailsList.add(medicalDeclarationDetails);
			medicalCertificateMain.setMedicalDeclarationDetailsList(medicalDeclarationDetailsList);
			
			StsTravelPersonScreening t = null;
			for( StsTravelPersonScreening traveller : tss )
				if( traveller.getPersonId() == policyTravellerId )
					t = traveller;
			
			if(t == null) 
				System.out.println(String.format("Could not find traveller #%d in policy #%d", policyTravellerId, insuranceId));
			
			if(t != null) {
				
				if( t.getStatus() == null && t.getFreeMedicalCondition() == null )
					System.out.println(String.format("Traveller #%d does not have a FMC or a screening", t.getPersonId()));
				
				List<MedicalCondition> medicalConditionList = new ArrayList<MedicalCondition>();
				MedicalCondition medicalCondition = new MedicalCondition();
				StringBuffer sbPersonName = new StringBuffer();
				
				if(t.getTitle() != null){
					sbPersonName.append(WordUtils.capitalize(t.getTitle()) + " ");
				}
				
				if(t.getForename() != null){
					sbPersonName.append(WordUtils.capitalize(t.getForename()) + " ");
				}
				
				if(t.getSurname() != null){
					sbPersonName.append(WordUtils.capitalize(t.getSurname()));
				}
				
				medicalCondition.setPersonName(sbPersonName.toString());
				medicalCondition.setPersonAge(String.valueOf(t.getAge()));
				
				if( t.getXml() != null ) {
					medicalCondition.setScreeningQnA(getScreeningQnA( t ,insuranceId));
				} else {
					medicalCondition.setScreeningQnA("");
				}
				
				medicalConditionList.add(medicalCondition);
				medicalCertificateMain.setMedicalConditionsList(medicalConditionList);
				medicalCertificateMain.setUnderwriterCompanyName(sbCompanyName.toString());
				medicalCertificateMain.setTripType(s.getSingleTrip().equals("Y") ? "Single Trip" : "Annual Multi Trip");
			
				Map<String, Object> parametersTable = new HashMap<String, Object>();
				parametersTable.put("SUBREPORT_DIR",templatePath + File.separator);
				String pathToDynamicPdfJasperFile = templatePath + File.separator + "MedicalMain.jasper";
				
			  	JRBeanCollectionDataSource beanCollectionDataSourceDyanmicPage = new JRBeanCollectionDataSource(Arrays.
                        asList(medicalCertificateMain));
			  	JasperPrint jasperPrintTableROI = JasperFillManager.fillReport(pathToDynamicPdfJasperFile,parametersTable,
                        beanCollectionDataSourceDyanmicPage);
	            JasperExportManager.exportReportToPdfStream(jasperPrintTableROI,out);
	            
			}
            
		} catch(Exception e) {
			  e.printStackTrace();
			  System.out.println("jasper exe error is at dynamic medical certificate" + e);
		}
		
	}

}
