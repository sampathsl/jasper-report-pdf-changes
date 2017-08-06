package src.com.intervest.hercules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class DBAdmin {
	
	private Connection connection = null;
	private Connection mysqlconnection = null;
	private Connection sqlserverconnection = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	CommonUtil commonUtil = new CommonUtil();
	
	private Map<String,Country> cachedCountryHash = null;
	private List<Country> cachedCountryList = null;

	private void makeConnection() throws Exception{
		Class.forName(ProgramProperties.JDBCDRIVER).newInstance();
		connection = DriverManager.getConnection(ProgramProperties.JDBCURL, ProgramProperties.USERNAME,
				ProgramProperties.PASSWORD);
	}
	
	private void makeMysqlConnection() throws Exception{
		Class.forName(ProgramProperties.MYSQLDRIVER).newInstance();
		mysqlconnection = DriverManager.getConnection(ProgramProperties.MYSQLURL+ProgramProperties.MYSQLDBNAME,
                ProgramProperties.MYSQLUSERNAME, ProgramProperties.MYSQLPASSWORD);
	}
	
	private void makesqlserverConnection() throws Exception{
		Class.forName(ProgramProperties.SQLSERVERDRIVER).newInstance();
		sqlserverconnection = DriverManager.getConnection(ProgramProperties.SQLSERVERURL, ProgramProperties.SQLSERVERUSERNAME,
				ProgramProperties.SQLSERVERPASSWORD);
	}

	//...
		
	public List<Long> selectTimTravelInsuranceNewPolicyRecords() {
		
		List<Long> policyList = new ArrayList<Long>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			makeConnection();
			prepStmt = connection.prepareStatement("SELECT \r\n" +
					"        TI.INSURANCE_ID \r\n" + 
					"        FROM \r\n" + 
					"        TIM_TRAVEL_INSURANCE TI, TIM_TRAVEL_ORGANISER TORG \r\n" + 
					"        WHERE \r\n" + 
					"        TI.INSURANCE_ID = TORG.INSURANCE_ID AND TI.INSURANCE_STATUS ='Approved' \r\n" + 
					"        AND TI.POSTAGE  > 0 \r\n" + 
					"        AND (TI.ACTION IN ('EMAILED') OR TI.ACTION IS NULL) \r\n" + 
					"        AND TRIM(LOWER(TORG.ORGANISER_SURNAME)) != 'test' \r\n" + 
					"        AND TO_DATE(TI.CREATION_DATE,'dd/MM/YYYY') >= TO_DATE((SYSDATE - 1),'dd/MM/YYYY') \r\n" +
					"		 AND TO_DATE(TI.CREATION_DATE,'dd/MM/YYYY') < (TO_DATE((SYSDATE),'dd/MM/YYYY')) \r\n" + 
					"        ORDER BY DECODE(TI.POLICY_UNDERWRITER, 'ASTRENSKA', 1, 'URV', 2, 'UKG', 3, 'ERV', " +
                    "       4,'CIGNA', 5,'AXA', 6, 7) , TI.POLICY_NUMBER");
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				policyList.add(rs.getLong(1));	
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return policyList;
	}
	
	public List<Long> selectTimTravelInsuranceMTARecords() {
		
		List<Long> policyList = new ArrayList<Long>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			makeConnection();
			prepStmt = connection.prepareStatement("SELECT \r\n" +
					" TI.INSURANCE_ID \r\n" + 
					" FROM \r\n" + 
					" TIM_TRAVEL_INSURANCE TI, TIM_TRAVEL_ORGANISER TORG \r\n" + 
					" WHERE \r\n" + 
					" TI.INSURANCE_ID = TORG.INSURANCE_ID AND TI.INSURANCE_STATUS ='Approved' \r\n" + 
					" AND (TI.ACTION IN ('POSTED') ) \r\n" + 
					" AND TRIM(LOWER(TORG.ORGANISER_SURNAME)) != 'test' \r\n" + 
					" AND TO_DATE(TI.POSTAGE_MODIFIED_WHEN,'dd/MM/YYYY') >= TO_DATE((SYSDATE - 1),'dd/MM/YYYY') \r\n" +
					" AND TO_DATE(TI.POSTAGE_MODIFIED_WHEN,'dd/MM/YYYY') < (TO_DATE((SYSDATE),'dd/MM/YYYY')) \r\n" + 
					" ORDER BY DECODE(TI.POLICY_UNDERWRITER, 'ASTRENSKA', 1, 'URV', 2, 'UKG', 3, 'ERV', 4," +
                    "'CIGNA', 5,'AXA', 6, 7) , TI.POLICY_NUMBER ");
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				policyList.add(rs.getLong(1));	
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return policyList;
	}
	
	public StsTravelInsurance selectStsTravelInsurance(Long insuranceId, String policyNumber, Long bookingId ) {
		
		StsTravelInsurance stsTravelInsurance = new StsTravelInsurance();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			makeConnection();
			StringBuffer sb = new StringBuffer("SELECT \r\n" + 
					" \r\n" + 
					"TI.INSURANCE_ID,TI.INSURANCE_STATUS,TI.CREATION_DATE,TI.START_DATE,TI.END_DATE,TI.SINGLE_TRIP, \r\n" + 
					"TI.AREA,TI.DURATION,TI.DEPART,TI.RETURN,TI.LONG_STAY,TI.PARTY,TI.PEOPLE,TI.UNDER2,TI.AGED2TO17," +
                    "TI.AGED18TO65,TI.AGED66TO70, \r\n" +
					"TI.AGED71TO75,TI.AGED76TO80,TI.PREMIUM,TI.ST_WINTER_SPORTS,TI.PRINT_POLICY,TI.BUY_PRICE," +
                    "TI.SELL_PRICE,TI.SELL_PRICE_IPT,TI.PAYMENT_SUCCESS, \r\n" +
					"TI.POLICY_NUMBER,TI.UPGRADES,TI.PROMO_NAME,TI.PROMO_CODE,TI.PROMO_VALUE,TI.TAX_FREE,TI.USER_ID," +
                    "TI.POSTAGE,TI.FINAL_PRICE,TI.BRAND,TI.WINTER_SPORTS, \r\n" +
					"TI.GOLF_EQUIPMENT,TI.GOLF_EQUIPMENT_PPL,TI.DOMAIN,TI.HOW_HEAR,TI.CALL_CENTRE_SURCHARGE," +
                    "TI.AGED81TO85,TI.SCREENING_COST,TI.SCREENING_BASE_PRICE, \r\n" +
					"TI.SCREENING_MARGIN_PRICE,TI.TRANSACTION_FEE,TI.TXN_NUMBER,TI.PAYMENT_TYPE,TI.COUNTRIES," +
                    "TI.EA_STATUS,TI.FINANCE_PRODUCT_CODE,TI.AMT_AUTO_RENEWAL, \r\n" +
					"TI.DOB,TI.ADMIN_FEE,TI.AUTO_RENEWAL_STATUS,TI.POLICY_TYPE,TI.AGED18TO30,TI.AGED31TO55," +
                    "TI.AGED56TO65,TI.PRICE_MATCHING_DISCOUNT,TI.PRICE_MATCHING_DISCOUNT_AMOUNT,TI.SALE_DOMAIN_CODE, \r\n" +
					"TI.POLICY_UNDERWRITER,TI.IPT,TI.BASE_SELL_PRICE,TI.BASE_SELL_PRICE_IPT,TI.EXTRAS," +
                    "TI.EXTRAS_BUY_PRICE,TI.EXTRAS_SELL_PRICE,TI.EXTRAS_SELL_PRICE_IPT, \r\n" +
					"TI.SCREENING_TOTAL_PRICE,TI.DIRECT_DISCOUNT,TI.DIRECT_DISCOUNT_AMOUNT,TI.WAIVE_TRANSACTION_FEE," +
                    "TI.PAYMENT_METHOD,TI.CONFIRMATION_EMAIL_SENT, \r\n" +
					"TI.SILVERPOP_SYNCED,TI.TO_LOCATION,TI.POLICYPACKAGE,TI.MEDICALZONE,TI.Q_HOLIDAY_ALREADY_BOOKED," +
                    "TI.PARTY_TYPE,TI.HOW_HEAR_SUB_OPTION,TI.BASE_BUY_PRICE, \r\n" +
					"TI.TEST_VALUE_SELECT_DOMAIN,TI.IS_ADVERTISED,TI.PRICE_OVERRIDE_ID,TI.PRODUCT_F_SALES_CHANNEL_ID," +
                    "TI.PRODUCT_F_NO_OF_TRAVELLER_ID,TI.PRODUCT_F_COUNTRY_ID, \r\n" +
					"TI.PRODUCT_F_DEPATUREMONTH_ID,TI.PRODUCT_F_ISSUEMONTH_ID,TI.PRICE_F_SALES_CHANNEL_ID,TI.PRICE_F_NO_OF_TRAVELLER_ID," +
                    "TI.PRICE_F_COUNTRY_ID,TI.PRICE_F_DEPATUREMONTH_ID, \r\n" +
					"TI.PRICE_F_ISSUEMONTH_ID,TI.PRICE_F_DURATION_AREA_ID,TI.POLICY_UPGRADE_NOTE,TI.CANCELLATION_COVER," +
                    "TI.MEDICAL_EXCESS,TI.PERSONAL_BAGGAGE,TI.CRUISE_COVER, \r\n" +
					"TI.FINANCIAL_DATE,TI.FINANCIAL_VALUE,TI.FINANCIAL_TXN_FEE,TI.FINANCIAL_ADMIN_FEE,TI.FINANCIAL_POSTAGE_FEE," +
                    "TI.FINANCIAL_REFUND_AMOUNT,TI.FINANCIAL_PAYMENT, \r\n" +
					"TI.F_REFUND_AMOUNT_COMPLETE,TI.PAYMENT_GATEWAY,TI.CHASE_PROFILE_ID,TI.GOLF_SELL_PRICE," +
                    "TI.CRUISE_SELL_PRICE,TI.POSTAGE_MODIFIED_WHEN,TI.FINANCIAL_REFUND_POSTAGE_FEE, \r\n" +
					"TI.F_REFUND_POSTAGE_COMPLETE,TI.FINANCIAL_REFUND_PAYMENT,TI.DOWNGRADE_TOTAL_REFUND_AMOUNT," +
                    "TI.AMENDED_DISCOUNT_AMOUNT,TI.MEDICAL_DECLARATION_QUESTION1, \r\n" +
					"TI.MEDICAL_DECLARATION_QUESTION2,TI.MEDICAL_DECLARATION_QUESTION3,TI.MEDICAL_EXPENSES," +
                    "TI.EMAIL,TI.PRODUCT_F_CRUISE_COVER_AREA_ID,TI.PRICE_F_CRUISE_COVER_AREA_ID, \r\n" +
					"TI.PRICE_F_CRUISE_AREA_MAPPING_ID,TI.PRICE_F_CRUISE_LOADING_ID,TI.PRODUCT_F_CRUISE_LOADING_ID," +
                    "TI.CRUISE_LOADING,TI.NEW_FORMULA,TI.NO_SUBSCRIBE,TI.WAIVE_POSTAGE, \r\n" +
					"TI.GADGET_COVER,TI.GADGET_BUY_PRICE,TI.GADGET_SELL_PRICE,TI.PRIORITY_MAIL \r\n" + 
					" \r\n" + 
					"FROM \r\n" + 
					"TIM_TRAVEL_INSURANCE TI, \r\n" + 
					"TIM_TRAVEL_ORGANISER TORG \r\n" + 
					"WHERE \r\n" + 
					" \r\n" + 
					"TI.INSURANCE_ID = TORG.INSURANCE_ID ");
			
			if(insuranceId != null) {
				sb.append(" AND TI.INSURANCE_ID = " + insuranceId);
			}
			
			if(policyNumber != null) {
				sb.append(" AND TI.POLICY_NUMBER = " + policyNumber);
			}
			
			if(bookingId != null) {
				sb.append(" AND TORG.BOOKING_ID = " + bookingId);
			}
				
			prepStmt = connection.prepareStatement(sb.toString());
					
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				
				try {

					stsTravelInsurance.setInsuranceId(rs.getLong("INSURANCE_ID"));
					stsTravelInsurance.setInsuranceStatus(rs.getString("INSURANCE_STATUS"));
					stsTravelInsurance.setCreationDate(rs.getDate("CREATION_DATE"));
					stsTravelInsurance.setStartDate(rs.getDate("START_DATE"));
					stsTravelInsurance.setEndDate(rs.getDate("END_DATE"));
					stsTravelInsurance.setSingleTrip(rs.getString("SINGLE_TRIP"));
					stsTravelInsurance.setArea(rs.getString("AREA"));
					stsTravelInsurance.setDuration(rs.getInt("DURATION"));
					stsTravelInsurance.setDepartLocation(rs.getString("DEPART"));
					stsTravelInsurance.setReturnLocation(rs.getString("RETURN"));
					stsTravelInsurance.setLongStay(rs.getString("LONG_STAY"));
					stsTravelInsurance.setParty(rs.getString("PARTY"));
					stsTravelInsurance.setPeople(rs.getInt("PEOPLE"));
					stsTravelInsurance.setUnder2(rs.getInt("UNDER2"));
					stsTravelInsurance.setAge2To17(rs.getInt("AGED2TO17"));
					stsTravelInsurance.setAge18To65(rs.getInt("AGED18TO65"));
					stsTravelInsurance.setAge66To70(rs.getInt("AGED66TO70"));
					stsTravelInsurance.setAge71To75(rs.getInt("AGED71TO75"));
					stsTravelInsurance.setAge76To80(rs.getInt("AGED76TO80"));
					stsTravelInsurance.setPremium(rs.getString("PREMIUM"));
					stsTravelInsurance.setStWinterSports(rs.getString("ST_WINTER_SPORTS"));
					stsTravelInsurance.setPrintPolicy(rs.getString("PRINT_POLICY"));
					stsTravelInsurance.setBuyPrice(rs.getBigDecimal("BUY_PRICE"));
					stsTravelInsurance.setSellPrice(rs.getBigDecimal("SELL_PRICE"));
					stsTravelInsurance.setSellPriceIpt(rs.getBigDecimal("SELL_PRICE_IPT"));
					stsTravelInsurance.setPaymentSuccess(rs.getString("PAYMENT_SUCCESS"));
					stsTravelInsurance.setPolicyNumber(rs.getString("POLICY_NUMBER"));
					stsTravelInsurance.setUpgrades(rs.getString("UPGRADES"));
					stsTravelInsurance.setPromoName(rs.getString("PROMO_NAME"));
					stsTravelInsurance.setPromoCode(rs.getString("PROMO_CODE"));
					stsTravelInsurance.setPromoValue(rs.getBigDecimal("PROMO_VALUE"));
					stsTravelInsurance.setTaxFree(rs.getString("TAX_FREE"));
					stsTravelInsurance.setUserId(rs.getInt("USER_ID"));
					stsTravelInsurance.setPostage(rs.getBigDecimal("POSTAGE"));
					stsTravelInsurance.setFinalPrice(rs.getBigDecimal("FINAL_PRICE"));
					stsTravelInsurance.setBrand(rs.getString("BRAND"));
					stsTravelInsurance.setWinterSports(rs.getString("WINTER_SPORTS"));
					stsTravelInsurance.setGolfEquipment(rs.getString("GOLF_EQUIPMENT"));
					stsTravelInsurance.setGolfEquipmentPpl(rs.getInt("GOLF_EQUIPMENT_PPL"));
					stsTravelInsurance.setDomain(rs.getString("DOMAIN"));
					stsTravelInsurance.setHowHear(rs.getString("HOW_HEAR"));
					stsTravelInsurance.setCallCenterSurcharge(rs.getString("CALL_CENTRE_SURCHARGE"));
					stsTravelInsurance.setAged81To85(rs.getInt("AGED81TO85"));
					stsTravelInsurance.setScreeningCost(rs.getBigDecimal("SCREENING_COST"));
					stsTravelInsurance.setScreeningBasePrice(rs.getBigDecimal("SCREENING_BASE_PRICE"));
					stsTravelInsurance.setScreeningMargingPrice(rs.getBigDecimal("SCREENING_MARGIN_PRICE"));
					stsTravelInsurance.setTransactionFee(rs.getBigDecimal("TRANSACTION_FEE"));
					stsTravelInsurance.setTxnNumber(rs.getString("TXN_NUMBER"));
					stsTravelInsurance.setPaymentType(rs.getString("PAYMENT_TYPE"));
					stsTravelInsurance.setCountries(rs.getString("COUNTRIES"));
					stsTravelInsurance.setEaStatus(rs.getString("EA_STATUS"));
					stsTravelInsurance.setFinanceProductCode(rs.getString("FINANCE_PRODUCT_CODE"));
					stsTravelInsurance.setAmtAutoRenewal(rs.getString("AMT_AUTO_RENEWAL"));
					stsTravelInsurance.setDob(rs.getDate("DOB"));
					stsTravelInsurance.setAdminFee(rs.getBigDecimal("ADMIN_FEE"));
					stsTravelInsurance.setAutoRenewalStatus(rs.getString("AUTO_RENEWAL_STATUS"));
					stsTravelInsurance.setPolicyType(rs.getString("SINGLE_TRIP").equalsIgnoreCase("Y") ?
                            PolicyType.SINGLE_TRIP : PolicyType.ANNUAL_MULTI_TRIP);
					stsTravelInsurance.setAge18To30(rs.getInt("AGED18TO30"));
					stsTravelInsurance.setAge31To55(rs.getInt("AGED31TO55"));
					stsTravelInsurance.setAge56To65(rs.getInt("AGED56TO65"));
					stsTravelInsurance.setPriceMatchingDiscount(rs.getString("PRICE_MATCHING_DISCOUNT"));
					stsTravelInsurance.setPricematchingDiscountAmount(rs.getBigDecimal("PRICE_MATCHING_DISCOUNT_AMOUNT"));
					stsTravelInsurance.setSaleDomainCode(rs.getString("SALE_DOMAIN_CODE"));
					stsTravelInsurance.setPolicyUnderwriter(Underwriter.valueOf(rs.getString("POLICY_UNDERWRITER")));
					stsTravelInsurance.setIpt(rs.getBigDecimal("IPT"));
					stsTravelInsurance.setBaseSellPrice(rs.getBigDecimal("BASE_SELL_PRICE"));
					stsTravelInsurance.setBaseSellPriceIpt(rs.getBigDecimal("BASE_SELL_PRICE_IPT"));
					stsTravelInsurance.setExtras(rs.getString("EXTRAS"));
					stsTravelInsurance.setExtrasBuyPrice(rs.getBigDecimal("EXTRAS_BUY_PRICE"));
					stsTravelInsurance.setExtrasSellPrice(rs.getBigDecimal("EXTRAS_SELL_PRICE"));
					stsTravelInsurance.setExtrasSellPriceIpt(rs.getBigDecimal("EXTRAS_SELL_PRICE_IPT"));
					stsTravelInsurance.setScreeningTotalPrice(rs.getBigDecimal("SCREENING_TOTAL_PRICE"));
					stsTravelInsurance.setDirectDiscount(rs.getString("DIRECT_DISCOUNT"));
					stsTravelInsurance.setDirectDiscountAmount(rs.getBigDecimal("DIRECT_DISCOUNT_AMOUNT"));
					stsTravelInsurance.setWaiveTransactionFee(rs.getString("WAIVE_TRANSACTION_FEE"));
					stsTravelInsurance.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
					stsTravelInsurance.setConfirmationEmailSent(rs.getString("CONFIRMATION_EMAIL_SENT"));
					stsTravelInsurance.setSilverPopSynced(rs.getString("SILVERPOP_SYNCED"));
					stsTravelInsurance.setToLocation(rs.getString("TO_LOCATION"));
					stsTravelInsurance.setPolicyPackage(rs.getString("POLICYPACKAGE"));
					stsTravelInsurance.setMedicalZone(rs.getString("MEDICALZONE"));
					stsTravelInsurance.setqHolidayAlreadyBooked(rs.getString("Q_HOLIDAY_ALREADY_BOOKED"));
					stsTravelInsurance.setPartyType(rs.getString("PARTY_TYPE"));
					stsTravelInsurance.setHowHear(rs.getString("HOW_HEAR_SUB_OPTION"));
					stsTravelInsurance.setBaseBuyPrice(rs.getBigDecimal("BASE_BUY_PRICE"));
					stsTravelInsurance.setTestValueTestDomain(rs.getString("TEST_VALUE_SELECT_DOMAIN"));
					stsTravelInsurance.setIsAdvertised(rs.getString("IS_ADVERTISED"));
					stsTravelInsurance.setPriceOverrideId(rs.getLong("PRICE_OVERRIDE_ID"));
					stsTravelInsurance.setProductFSalesChannelId(rs.getLong("PRODUCT_F_SALES_CHANNEL_ID"));
					stsTravelInsurance.setProductFNoOfTravellerId(rs.getLong("PRODUCT_F_NO_OF_TRAVELLER_ID"));
					stsTravelInsurance.setProductFCountryId(rs.getLong("PRODUCT_F_COUNTRY_ID"));
					stsTravelInsurance.setProductFDepaturemonthId(rs.getLong("PRODUCT_F_DEPATUREMONTH_ID"));
					stsTravelInsurance.setProductFIssuemonthId(rs.getLong("PRODUCT_F_ISSUEMONTH_ID"));
					stsTravelInsurance.setPriceFSalesChannelId(rs.getLong("PRICE_F_SALES_CHANNEL_ID"));
					stsTravelInsurance.setPriceFNoOfTravellerId(rs.getLong("PRICE_F_NO_OF_TRAVELLER_ID"));
					stsTravelInsurance.setPriceFCountryId(rs.getLong("PRICE_F_COUNTRY_ID"));
					stsTravelInsurance.setPriceFDepaturemonthId(rs.getLong("PRICE_F_DEPATUREMONTH_ID"));
					stsTravelInsurance.setPriceFIssuemonthId(rs.getLong("PRICE_F_ISSUEMONTH_ID"));
					stsTravelInsurance.setPriceFDurationAreaId(rs.getLong("PRICE_F_DURATION_AREA_ID"));
					stsTravelInsurance.setPolicyUpgradeNote(rs.getString("POLICY_UPGRADE_NOTE"));
					stsTravelInsurance.setCancellationCover(rs.getLong("CANCELLATION_COVER"));
					stsTravelInsurance.setMedicalExcess(rs.getLong("MEDICAL_EXCESS"));
					stsTravelInsurance.setPersonalBaggage(rs.getLong("PERSONAL_BAGGAGE"));
					stsTravelInsurance.setCruiseCover(rs.getString("CRUISE_COVER"));
					stsTravelInsurance.setFinancialDate(rs.getDate("FINANCIAL_DATE"));
					stsTravelInsurance.setFinancialValue(rs.getBigDecimal("FINANCIAL_VALUE"));
					stsTravelInsurance.setFinancialTxnFee(rs.getBigDecimal("FINANCIAL_TXN_FEE"));
					stsTravelInsurance.setFinancialAdminFee(rs.getBigDecimal("FINANCIAL_ADMIN_FEE"));
					stsTravelInsurance.setFinancialPostageFee(rs.getBigDecimal("FINANCIAL_POSTAGE_FEE"));
					stsTravelInsurance.setFinancialRefundAmount(rs.getBigDecimal("FINANCIAL_REFUND_AMOUNT"));
					stsTravelInsurance.setFinancialPayment(rs.getBigDecimal("FINANCIAL_PAYMENT"));
					stsTravelInsurance.setfRefundAmountComplete(rs.getString("F_REFUND_AMOUNT_COMPLETE"));
					stsTravelInsurance.setFinancialRefundPayment(rs.getBigDecimal("FINANCIAL_REFUND_PAYMENT"));
					stsTravelInsurance.setDowngradeTotalRefundAmount(rs.getBigDecimal("DOWNGRADE_TOTAL_REFUND_AMOUNT"));
					stsTravelInsurance.setAmendedDiscountAmount(rs.getBigDecimal("AMENDED_DISCOUNT_AMOUNT"));
					stsTravelInsurance.setMedicalDeclarationQuestion1(rs.getString("MEDICAL_DECLARATION_QUESTION1"));
					stsTravelInsurance.setMedicalDeclarationQuestion2(rs.getString("MEDICAL_DECLARATION_QUESTION2"));
					stsTravelInsurance.setMedicalDeclarationQuestion3(rs.getString("MEDICAL_DECLARATION_QUESTION3"));
					stsTravelInsurance.setMedicalExpenses(rs.getBigDecimal("MEDICAL_EXPENSES"));
					stsTravelInsurance.setEmail(rs.getString("EMAIL"));
					stsTravelInsurance.setProductFCruiseLoadingId(rs.getLong("PRODUCT_F_CRUISE_COVER_AREA_ID"));
					stsTravelInsurance.setPriceFCruiseCoverAreaId(rs.getLong("PRICE_F_CRUISE_COVER_AREA_ID"));
					stsTravelInsurance.setPriceFCruiseAreaMappingId(rs.getLong("PRICE_F_CRUISE_AREA_MAPPING_ID"));
					stsTravelInsurance.setPriceFCruiseLoadingId(rs.getLong("PRICE_F_CRUISE_LOADING_ID"));
					stsTravelInsurance.setProductFCruiseLoadingId(rs.getLong("PRODUCT_F_CRUISE_LOADING_ID"));
					stsTravelInsurance.setCruiseLoading(rs.getString("CRUISE_LOADING"));
					stsTravelInsurance.setNewFormula(rs.getString("NEW_FORMULA"));
					stsTravelInsurance.setNoSubscribe(rs.getString("NO_SUBSCRIBE"));
					stsTravelInsurance.setWaivePostage(rs.getString("WAIVE_POSTAGE"));
					stsTravelInsurance.setGadgetCover(rs.getString("GADGET_COVER"));
					stsTravelInsurance.setGadgetBuyPrice(rs.getBigDecimal("GADGET_BUY_PRICE"));
					stsTravelInsurance.setGadgetSellPrice(rs.getBigDecimal("GADGET_SELL_PRICE"));
					stsTravelInsurance.setPriorityMail(rs.getInt("PRIORITY_MAIL"));

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stsTravelInsurance;
		
	}
	
	public StsTravelOrganizer selectStsTravelOrganiser(Long insuranceId, String policyNumber, Long bookingId ) {
		
		StsTravelOrganizer stsTravelOrganizer = new StsTravelOrganizer();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			makeConnection();
			StringBuffer sb = new StringBuffer("SELECT \r\n" + 
					"ORGANISER_ID,TORG.INSURANCE_ID,ADDRESS1,ADDRESS2,CUST_ADDRESS3,TOWN,POSTCODE," +
                    "COUNTY,COUNTRY,ORGANISER_TITLE,ORGANISER_FORENAME, \r\n" +
					"ORGANISER_MIDDLENAME,ORGANISER_SURNAME,ORGANISER_DAYPHONE,ORGANISER_EVENINGPHONE," +
                    "ORGANISER_EMAIL,CARD_TYPE,CARD_HOLDER,TORG.PAYMENT_SUCCESS, \r\n" +
					"BOOKING_ID,COUNTRY_CODE,SOURCE_CODE \r\n" + 
					"FROM \r\n" + 
					"TIM_TRAVEL_ORGANISER TORG, TIM_TRAVEL_INSURANCE TI \r\n" + 
					"WHERE \r\n" + 
					"TI.INSURANCE_ID = TORG.INSURANCE_ID \r\n");
			
			if(insuranceId != null) {
				sb.append(" AND TI.INSURANCE_ID = " + insuranceId);
			}
			
			if(policyNumber != null) {
				sb.append(" AND TI.POLICY_NUMBER = " + policyNumber);
			}
			
			if(bookingId != null) {
				sb.append(" AND TORG.BOOKING_ID = " + bookingId);
			}
				
			prepStmt = connection.prepareStatement(sb.toString());
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				
				try {

					stsTravelOrganizer.setOrganiserId(rs.getLong("ORGANISER_ID"));
					stsTravelOrganizer.setInsuranceId(rs.getLong("INSURANCE_ID"));
					stsTravelOrganizer.setAddress1(rs.getString("ADDRESS1"));
					stsTravelOrganizer.setAddress2(rs.getString("ADDRESS2"));
					stsTravelOrganizer.setTown(rs.getString("TOWN"));
					stsTravelOrganizer.setPostcode(rs.getString("POSTCODE"));
					stsTravelOrganizer.setCounty(rs.getString("COUNTY"));
					stsTravelOrganizer.setCountryCode(rs.getString("COUNTRY"));
					stsTravelOrganizer.setOrganiserTitle(rs.getString("ORGANISER_TITLE"));
					stsTravelOrganizer.setOrganiserForename(rs.getString("ORGANISER_FORENAME"));
					stsTravelOrganizer.setOrganiserMiddlename(rs.getString("ORGANISER_MIDDLENAME"));
					stsTravelOrganizer.setOrganiserSurname(rs.getString("ORGANISER_SURNAME"));
					stsTravelOrganizer.setOrganiserDayphone(rs.getString("ORGANISER_DAYPHONE"));
					stsTravelOrganizer.setOrganiserEveningphone(rs.getString("ORGANISER_EVENINGPHONE"));
					stsTravelOrganizer.setOrganiserEmail(rs.getString("ORGANISER_EMAIL"));
					stsTravelOrganizer.setCardType(rs.getString("CARD_TYPE"));
					stsTravelOrganizer.setCardHolder(rs.getString("CARD_HOLDER"));
					stsTravelOrganizer.setPaymentSuccess(rs.getString("PAYMENT_SUCCESS"));

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stsTravelOrganizer;
		
	}
	
	public List<StsTravelPersonScreening> selectStsTravelPersonWithScreening(Long insuranceId, String policyNumber, Long personId ) {
		
		
		List<StsTravelPersonScreening> stsTravelScreeningResultsList = new ArrayList<StsTravelPersonScreening>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			makeConnection();
			StringBuffer sb = new StringBuffer("SELECT \r\n" + 
					"SR.ID AS ID,P.PERSON_ID,P.INSURANCE_ID,P.TITLE,P.FORENAME,P.SURNAME,P.AGE,P.MEDICAL_REF," +
                    "P.NOTE_FLAG,P.NOTE,SR.HASH AS HASH,SR.CONDITIONS AS \r\n" +
					"CONDITIONS,SR.SCORE AS SCORE,SR.PRICE AS PRICE,SR.EXCLUDECOST AS EXCLUDECOST," +
                    "SR.LINKED_CONDITIONS AS LINKED_CONDITIONS,SR.STATUS AS STATUS, \r\n" +
					"SR.SCR_COST AS SCR_COST,SR.SCR_BASE_PRICE AS SCR_BASE_PRICE,SR.SCR_MARGIN AS SCR_MARGIN," +
                    "SR.FREE_MEDICAL_CONDITION AS FREE_MEDICAL_CONDITION, \r\n" +
					"SR.SCR_AMT_ALLOWED as SCR_AMT_ALLOWED,SR.MEDICAL_FREE as MEDICAL_FREE \r\n" + 
					"FROM \r\n" + 
					"TIM_TRAVEL_INSURANCE TI, TIM_TRAVEL_PERSON P,TIM_SCREENING_RESULTS SR \r\n" + 
					"WHERE \r\n" + 
					"TI.INSURANCE_ID = P.INSURANCE_ID AND \r\n" + 
					"P.PERSON_ID = SR.ID(+)");
			
			if(insuranceId != null) {
				sb.append(" AND TI.INSURANCE_ID = " + insuranceId);
			}
			
			if(policyNumber != null) {
				sb.append(" AND TI.POLICY_NUMBER = " + policyNumber);
			}
			
			if(personId != null) {
				sb.append(" AND TORG.BOOKING_ID = " + personId);
			}
				
			prepStmt = connection.prepareStatement(sb.toString());
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				
				try {
					StsTravelPersonScreening stsTravelScreeningResults = new StsTravelPersonScreening();
					stsTravelScreeningResults.setId(rs.getLong("ID"));
					stsTravelScreeningResults.setPersonId(rs.getLong("PERSON_ID"));
					stsTravelScreeningResults.setInsuranceId(rs.getLong("INSURANCE_ID"));
					stsTravelScreeningResults.setTitle(rs.getString("TITLE"));
					stsTravelScreeningResults.setForename(rs.getString("FORENAME"));
					stsTravelScreeningResults.setSurname(rs.getString("SURNAME"));
					stsTravelScreeningResults.setAge(rs.getInt("AGE"));
					stsTravelScreeningResults.setMedicalRef(rs.getString("MEDICAL_REF"));
					stsTravelScreeningResults.setNoteFlag(rs.getString("NOTE_FLAG"));
					stsTravelScreeningResults.setNote(rs.getString("NOTE"));
					stsTravelScreeningResults.setHash(rs.getString("HASH"));
					try {
						stsTravelScreeningResults.setXml(""/*getHealixSRTIMPerPerson(rs.getLong("PERSON_ID"))*/);
					} catch (Exception e) {
						e.printStackTrace();
					}
					stsTravelScreeningResults.setConditions(rs.getString("CONDITIONS"));
					stsTravelScreeningResults.setScore(rs.getBigDecimal("SCORE"));
					stsTravelScreeningResults.setPrice(rs.getBigDecimal("PRICE"));
					stsTravelScreeningResults.setExcludeCost(rs.getString("EXCLUDECOST"));
					stsTravelScreeningResults.setLinkedConditions(rs.getString("LINKED_CONDITIONS"));
					stsTravelScreeningResults.setStatus(rs.getString("STATUS"));
					stsTravelScreeningResults.setScrCost(rs.getBigDecimal("SCR_COST"));
					stsTravelScreeningResults.setScrBasePrice(rs.getBigDecimal("SCR_BASE_PRICE"));
					stsTravelScreeningResults.setScrMargin(rs.getBigDecimal("SCR_MARGIN"));
					try {
						stsTravelScreeningResults.setFreeMedicalCondition( rs.getString("FREE_MEDICAL_CONDITION") != null ? 
								FreeMedicalCondition.fromString(rs.getString("FREE_MEDICAL_CONDITION")) : null);
					} catch (Exception e) {
						e.printStackTrace();
						stsTravelScreeningResults.setFreeMedicalCondition(null);
					}
					stsTravelScreeningResults.setScrAmtAllowed(rs.getString("SCR_AMT_ALLOWED"));
					stsTravelScreeningResults.setMedicalFree(rs.getString("MEDICAL_FREE"));
					stsTravelScreeningResultsList.add(stsTravelScreeningResults);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stsTravelScreeningResultsList;
		
	}
	
	private String getHealixSRTIMPerPerson(long personId) {

		StringBuffer healixSR = new StringBuffer("");
		String strSql = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		strSql = new String(
				"SELECT TP.PERSON_ID,TP.INSURANCE_ID,SR.Hash as SRDOC FROM TIM_SCREENING_RESULTS SR INNER " +
                        " JOIN TIM_TRAVEL_PERSON TP ON TP.PERSON_ID=SR.ID WHERE TP.PERSON_ID= "
						+ String.valueOf(personId));
		prepStmt = null;
		
		try {
			
			prepStmt = connection.prepareStatement(strSql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {

				try {
					if (rs.getString("SRDOC") != null) {
						String fileName = String.valueOf(rs.getLong("INSURANCE_ID")) + "/"
								+ String.valueOf(rs.getLong("PERSON_ID")) + "/" + String.valueOf(rs.getLong("PERSON_ID"))
								+ ".xml";
						S3AwsClient s3client = new S3AwsClient();
						String file = s3client.readFile(fileName);
						healixSR.append(CipherUtils.decrypt(file, new Long(
								Long.toString(rs.getLong("INSURANCE_ID")) + Long.toString(rs.getLong("PERSON_ID")))) );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		} catch (SQLException e) {
			commonUtil.writeErrorLog("getHealixSRTIMPerPerson-", new Date(), e);
			e.printStackTrace();
		} catch (Exception e) {
			commonUtil.writeErrorLog("getHealixSRTIMPerPerson-", new Date(), e);
			e.printStackTrace();
		} finally {
			
			try {
				if(rs != null)
					rs.close();
			} catch (Throwable t) {
			}
			
			try {
				if(prepStmt != null)
					prepStmt.close();
			} catch (Throwable t) {
			}
			
		}
		
		return healixSR.toString();

	}
	
	public List<String> destinationListToSortedCountryNames( List<String> destinationCodes) {
		List<String> sortList = new ArrayList<String>();
		for(String countryCode : destinationListToCountryCodes(destinationCodes))
			sortList.add( getCountryByCode(countryCode).getDisplayName() );
		Collections.sort(sortList);
		return sortList;
	}
	
	private Country getCountryByCode( String code ) {
		if( cachedCountryHash == null ) {
			cachedCountryHash = new HashMap<String, Country>();
			for( Country c: getCountryList() )
				cachedCountryHash.put(c.getCode(), c);
			cachedCountryHash = Collections.unmodifiableMap( cachedCountryHash );
		}
		return cachedCountryHash.get(code);
	}
	
	private List<Country> getCountryList() {
		if( cachedCountryList == null)
			cachedCountryList = Collections.unmodifiableList(getAllCountryList());
		return cachedCountryList;	
	}
	
	public List<Country> getAllCountryList() {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		List<Country> countries = new ArrayList<Country>();
		
		try {

			String strSql = null;
			makeConnection();
			strSql = new String(
					" SELECT c.CODE,  c.CONTINENT_CODE,  c.STANDARDIZED_NAME,  c.DISPLAY_NAME,  c.IS_ALIAS," +
                            "  c.ALIAS_COUNTRY_CODE \r\n" +
					" FROM \r\n" + 
					" TIM_COUNTRIES c \r\n" + 
					" ORDER BY c.display_name ");
			
			prepStmt = null;
			
			prepStmt = connection.prepareStatement(strSql);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				try {
					Country country = new Country();
					country.setCode(rs.getString("CODE"));
					country.setContinentCode(rs.getString("CONTINENT_CODE"));
					country.setStandardizedName(rs.getString("STANDARDIZED_NAME"));
					country.setDisplayName(rs.getString("DISPLAY_NAME"));
					country.setAlias(rs.getString("IS_ALIAS") != null && rs.getString("IS_ALIAS").
                            equalsIgnoreCase("Y") ? true : false);
					country.setAliasCountryCode(rs.getString("ALIAS_COUNTRY_CODE"));
					countries.add(country);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return countries;

	}
	
	private List<String> destinationListToCountryCodes( List<String> destinationCodes ) {
		
		List<String> destinationCodeList = new ArrayList<String>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try {
			
			StringBuffer sbDestinationCodesList = new StringBuffer("");
			
			for (int i = 0; i < destinationCodes.size(); i++) {
				String destinationCode = destinationCodes.get(i);
				sbDestinationCodesList.append( "'" + destinationCode + "'," );
			}
			
			String finalAppendedList = sbDestinationCodesList.toString().substring(0,
                    sbDestinationCodesList.toString().length() - 1);
			
			makeConnection();
			StringBuffer sb = new StringBuffer(" SELECT distinct c.CODE \r\n" + 
					" FROM TIM_COUNTRIES c \r\n" + 
					" WHERE c.code IN ("+finalAppendedList+") \r\n" + 
					" UNION ALL \r\n" + 
					" SELECT distinct c.CODE \r\n" + 
					" FROM \r\n" + 
					" TIM_COUNTRIES c, \r\n" + 
					" TIM_DEST_COUNTRY_MAPPINGS m \r\n" + 
					" WHERE \r\n" + 
					" c.code = m.country_code AND \r\n" + 
					" m.dest_group_code IN ("+finalAppendedList+") \r\n" + 
					" ORDER BY CODE ");
				
			prepStmt = connection.prepareStatement(sb.toString());
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				destinationCodeList.add(rs.getString("CODE"));
			}
			
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			try {
				
				if(rs != null)
					rs.close();
				
				if(prepStmt != null)
					prepStmt.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return destinationCodeList;
		
	}
		
}