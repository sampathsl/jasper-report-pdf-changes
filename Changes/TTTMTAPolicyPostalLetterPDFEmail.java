package src.com.intervest.hercules;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;

public class TTTMTAPolicyPostalLetterPDFEmail {
	
	SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
	private final static String FILE_SEPARATOR = "/";
	
	public static void main(String[] args) {
		TTTMTAPolicyPostalLetterPDFEmail generator = new TTTMTAPolicyPostalLetterPDFEmail();
		// load all properties
		CommonUtil commonUtil = new CommonUtil();
		try {
			generator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute() {

		System.out.println("=========execute Job TTTMTAPolicyPostalLetterPDFEmail ====");
		String uploadTotalPdf = ProgramProperties.UPLOAD_TOTAL_PDF.trim();
		DBAdmin dbAdmin = new DBAdmin();
		String policyDocsProjectPath = "resources";
		String frontPageTemplatePath = policyDocsProjectPath+FILE_SEPARATOR+"jasper-templates/front-page/StartPage.jasper";
		String coverLetterTemplatePath = policyDocsProjectPath+FILE_SEPARATOR+"jasper-templates/cover-letter/";
		String validationCertificateTemplatePath = policyDocsProjectPath+FILE_SEPARATOR+"jasper-templates/validation-certificate/";
		String medicalCertificateTemplatePath = policyDocsProjectPath+FILE_SEPARATOR+"jasper-templates/medical-certificate/";
		String endPageTemplatePath = policyDocsProjectPath+FILE_SEPARATOR+"jasper-templates/end-page/EndPage.jasper";
		String fileOutPutPath = policyDocsProjectPath+FILE_SEPARATOR+"daily-pdf/";
		
		ByteArrayOutputStream outputStream = null;

		// First get the list of insurance ids
		try {

			List<Long> insuraceIdList = dbAdmin.selectTimTravelInsuranceMTARecords();
			List<PdfReader> pdfReaderList = new ArrayList<PdfReader>();

			for (Iterator iterator = insuraceIdList.iterator(); iterator.hasNext();) {

				Long insuranceId = (Long) iterator.next();
				StsTravelInsurance stsTravelInsurance = dbAdmin.selectStsTravelInsurance(insuranceId, null, null);
				List<StsTravelPersonScreening> personScreenings = dbAdmin.selectStsTravelPersonWithScreening(insuranceId, null, null);
				HashMap<String, Object> parametersTable = new HashMap<String, Object>();
				ByteArrayOutputStream firstPage = new ByteArrayOutputStream();
				ByteArrayOutputStream outCoverPage = new ByteArrayOutputStream();
				ByteArrayOutputStream outLastPage = new ByteArrayOutputStream();

				Long countTravellerWithScreening = 0l;
				for (Iterator iterator2 = personScreenings.iterator(); iterator2.hasNext();) {
					StsTravelPersonScreening stsTravelPersonScreening = (StsTravelPersonScreening) iterator2.next();
					if (stsTravelPersonScreening.getFreeMedicalCondition() != null
							|| stsTravelPersonScreening.getStatus() != null) {
						countTravellerWithScreening++;
					}
				}

				StringBuffer diliveryTypeSb = new StringBuffer();
				if (stsTravelInsurance.getPriorityMail() != null
						&& stsTravelInsurance.getPriorityMail().equals(BigDecimal.ZERO)) {
					diliveryTypeSb.append("Standard Postage");
				} else if (stsTravelInsurance.getPriorityMail() != null
						&& stsTravelInsurance.getPriorityMail().equals(BigDecimal.ONE)) {
					diliveryTypeSb.append("First Class Postage");
				} else if (stsTravelInsurance.getPriorityMail() != null
						&& stsTravelInsurance.getPriorityMail().equals(new BigDecimal(2))) {
					diliveryTypeSb.append("Special Delivery Postage");
				} else {
					if (stsTravelInsurance.getPostage().compareTo(BigDecimal.ZERO) != 0) {
						diliveryTypeSb.append("Standard Postage");
					} else if (stsTravelInsurance.getPostage().compareTo(BigDecimal.ZERO) == 0
							&& stsTravelInsurance.getWaivePostage() != null && stsTravelInsurance.getWaivePostage().equalsIgnoreCase("Y")) {
						diliveryTypeSb.append("Standard Postage");
					} else if (stsTravelInsurance.getPostage().compareTo(BigDecimal.ZERO) != 0
							&& stsTravelInsurance.getWaivePostage() != null && stsTravelInsurance.getWaivePostage().equalsIgnoreCase("Y")) {
						diliveryTypeSb.append("Standard Postage");
					}
				}

				StringBuffer cruiseCoverSb = new StringBuffer();
				if (stsTravelInsurance.getCruiseCover() != null
						&& stsTravelInsurance.getCruiseCover().equalsIgnoreCase("Y")) {
					cruiseCoverSb.append("Included");
				} else {
					cruiseCoverSb.append("Excluded");
				}

				StringBuffer gadgetCoverSb = new StringBuffer();
				if (stsTravelInsurance.getGadgetCover() != null
						&& stsTravelInsurance.getGadgetCover().equalsIgnoreCase("Y")) {
					gadgetCoverSb.append("Included");
				} else {
					gadgetCoverSb.append("Excluded");
				}

				TotalPolicyLetter totalPolicyLetter = new TotalPolicyLetter(
						UploadFile.getFilteredPolicyNumber(stsTravelInsurance.getPolicyNumber()), 1l, countTravellerWithScreening,
						1l, diliveryTypeSb.toString(), cruiseCoverSb.toString(), gadgetCoverSb.toString());

				try {

					JRBeanCollectionDataSource beanCollectionDataSourceDyanmicFrontPage = new JRBeanCollectionDataSource(
							Arrays.asList(totalPolicyLetter));
					JasperPrint jasperPrintTableROI = JasperFillManager.fillReport(frontPageTemplatePath,
							parametersTable, beanCollectionDataSourceDyanmicFrontPage);
					JasperExportManager.exportReportToPdfStream(jasperPrintTableROI, firstPage);

					PdfReader reder = new PdfReader(firstPage.toByteArray());
					FileBufferedOutputStream fileBufferedOutputStream = new FileBufferedOutputStream();
					PdfStamper stamper = new PdfStamper(reder, fileBufferedOutputStream);
					try {
						stamper.insertPage(reder.getNumberOfPages() + 1, reder.getPageSizeWithRotation(1));
						pdfReaderList.add(reder);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (stamper != null) {
								stamper.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (fileBufferedOutputStream != null) {
								fileBufferedOutputStream.flush();
								fileBufferedOutputStream.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					try {
						firstPage.flush();
						firstPage.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {

					UploadFile.writeCoverLetterDocumentPDF(outCoverPage, insuranceId, coverLetterTemplatePath,
							true);
					PdfReader reder = new PdfReader(outCoverPage.toByteArray());

					if (reder.getNumberOfPages() % 2 != 0) {
						FileBufferedOutputStream fileBufferedOutputStream = new FileBufferedOutputStream();
						PdfStamper stamper = new PdfStamper(reder, fileBufferedOutputStream);
						try {
							stamper.insertPage(reder.getNumberOfPages() + 1, reder.getPageSizeWithRotation(1));
							pdfReaderList.add(reder);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (stamper != null) {
									stamper.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								if (fileBufferedOutputStream != null) {
									fileBufferedOutputStream.flush();
									fileBufferedOutputStream.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						pdfReaderList.add(new PdfReader(outCoverPage.toByteArray()));
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (outCoverPage != null) {
							outCoverPage.flush();
							outCoverPage.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				ByteArrayOutputStream outValidationCertificate = new ByteArrayOutputStream();
				try {

					UploadFile.generateValidationCertificatePDF(outValidationCertificate,
							stsTravelInsurance.getInsuranceId(), validationCertificateTemplatePath, true);
					PdfReader reder = new PdfReader(outValidationCertificate.toByteArray());

					if (reder.getNumberOfPages() % 2 != 0) {
						FileBufferedOutputStream fileBufferedOutputStream = new FileBufferedOutputStream();
						PdfStamper stamper = new PdfStamper(reder, fileBufferedOutputStream);
						try {
							stamper.insertPage(reder.getNumberOfPages() + 1, reder.getPageSizeWithRotation(1));
							pdfReaderList.add(reder);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (stamper != null) {
									stamper.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								if (fileBufferedOutputStream != null) {
									fileBufferedOutputStream.flush();
									fileBufferedOutputStream.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						pdfReaderList.add(new PdfReader(outValidationCertificate.toByteArray()));
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (outValidationCertificate != null) {
							outValidationCertificate.flush();
							outValidationCertificate.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				for (Iterator iterator2 = personScreenings.iterator(); iterator2.hasNext();) {

					StsTravelPersonScreening stsTravelPersonScreening = (StsTravelPersonScreening) iterator2.next();

					if (stsTravelPersonScreening.getFreeMedicalCondition() != null
							|| stsTravelPersonScreening.getStatus() != null) {
						ByteArrayOutputStream outPersonScreening = new ByteArrayOutputStream();
						try {
							UploadFile.writeScreeningLetterPDF(outPersonScreening,
									stsTravelInsurance.getInsuranceId(), stsTravelPersonScreening.getPersonId(),
									medicalCertificateTemplatePath, true);
							PdfReader reder = new PdfReader(outPersonScreening.toByteArray());

							if (reder.getNumberOfPages() % 2 != 0) {
								FileBufferedOutputStream fileBufferedOutputStream = new FileBufferedOutputStream();
								PdfStamper stamper = new PdfStamper(reder, fileBufferedOutputStream);
								try {
									stamper.insertPage(reder.getNumberOfPages() + 1, reder.getPageSizeWithRotation(1));
									pdfReaderList.add(reder);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									try {
										if (stamper != null) {
											stamper.close();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									try {
										if (fileBufferedOutputStream != null) {
											fileBufferedOutputStream.flush();
											fileBufferedOutputStream.close();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								pdfReaderList.add(new PdfReader(outPersonScreening.toByteArray()));
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (outPersonScreening != null) {
									outPersonScreening.flush();
									outPersonScreening.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				}

				try {

					JRBeanCollectionDataSource beanCollectionDataSourceDyanmicEndPage = new JRBeanCollectionDataSource(
							Arrays.asList(totalPolicyLetter));
					JasperPrint jasperPrintTableROI4 = JasperFillManager.fillReport(endPageTemplatePath,
							parametersTable, beanCollectionDataSourceDyanmicEndPage);
					JasperExportManager.exportReportToPdfStream(jasperPrintTableROI4, outLastPage);

					PdfReader reder = new PdfReader(outLastPage.toByteArray());
					FileBufferedOutputStream fileBufferedOutputStream = new FileBufferedOutputStream();
					PdfStamper stamper = new PdfStamper(reder, fileBufferedOutputStream);
					try {
						stamper.insertPage(reder.getNumberOfPages() + 1, reder.getPageSizeWithRotation(1));
						pdfReaderList.add(reder);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {

						try {
							if (stamper != null) {
								stamper.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (fileBufferedOutputStream != null) {
								fileBufferedOutputStream.flush();
								fileBufferedOutputStream.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (outLastPage != null) {
							outLastPage.flush();
							outLastPage.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			
			// TTT_Repost_yyyymmdd
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String yyyyMMdd = sdf.format(new Date());
			String tempPdfPath = fileOutPutPath+FILE_SEPARATOR+"TTT_Repost_"+yyyyMMdd+".pdf";

			if (insuraceIdList.size() > 0) {
				
				outputStream = new ByteArrayOutputStream();
				UploadFile.doMerge(pdfReaderList, outputStream);
				
				//write the file to location
				try {
					
					System.out.println("DONE :: " + tempPdfPath);
					FileOutputStream output = new FileOutputStream(tempPdfPath);
					output.write(outputStream.toByteArray());
					output.close();
					
					Thread.sleep(1000);
					
					File f = new File(tempPdfPath);
					if(f.exists() && !f.isDirectory()) { 
						
						if (uploadTotalPdf != null && uploadTotalPdf.equalsIgnoreCase("Y") ) {
							// start upload file
							//UploadFile.uploadFile(tempPdfPath,"TTT_Repost_"+yyyyMMdd+".pdf");
							Thread.sleep(1000);
						}
						
						//delete file after upload
						/*File file = new File(tempPdfPath);
						if (file.delete()) {
							System.out.println(file.getName() + " is deleted!");
						} else {
							System.out.println("Delete operation is failed.");
						}*/
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				
				try {
					// send an empty page pdf
					Document document = new Document();
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempPdfPath));
					document.open();
					writer.setPageEmpty(false);
					document.newPage();
					document.close();
					
					Thread.sleep(1000);
					
					File f = new File(tempPdfPath);
					if(f.exists() && !f.isDirectory()) {
						
						if (uploadTotalPdf != null && uploadTotalPdf.equalsIgnoreCase("Y") ) {
							// start upload file
							//UploadFile.uploadFile(tempPdfPath,"TTT_Repost_"+yyyyMMdd+".pdf");
							Thread.sleep(1000);
						}
						
						//delete file after upload
						/*File file = new File(tempPdfPath);
						if (file.delete()) {
							System.out.println(file.getName() + " is deleted!");
						} else {
							System.out.println("Delete operation is failed.");
						}*/
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
