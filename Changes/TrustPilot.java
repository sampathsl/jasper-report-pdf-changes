package src.com.intervest.hercules;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class TrustPilot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String quoteRef="1";
		String email="";
		String name="";
		String uniqueURL=getUniqueLink(quoteRef,email,name);
		//System.out.println("uniqueURL "+uniqueURL);
	}

	/***
	 * <p>
	 * http://{1}/evaluate/{2}?a={3}&b={4}&c={5}&e={6}
	 * http://www.trustpilot.com/evaluate/embed/talktotim.co.uk?a=tyu9ytyui&b=bGdoQHRydXN0cGlsb3QuY29t&c=john&e=13d1ca785caccf7d6c0cb7cc4e33fa71e812b9d9
	 * The basic elements of the unique link
	 * Unique Links - Short User Guide
	 * http://{1}/evaluate/{2}?a={3}&b={4}&c={5}&e={6}
	 * </p>
	 * <li>
	 * 1.The Trustpilot site that your customer will land on.
	 * 2.Your domain name on Trustpilot.
	 * 3.Your customer�s unique order ID (reference number).
	 * 4.Is a BASE64(1) representation of the customer�s email.
	 * 5.Is the customer�s name URL(2)	encoded form.
	 * 6.Is a calculated hash(3) value that veries the correctness of the link.
	 * </li>
	 * @return
	 * @author sam
	 */
	public static String getUniqueLink(String quoteRef,String email,String name)
	{
		String urlTrustPilot="";
		String urlTalktotim="";
		//String quoteRef="1";
		//String email="";
		String emailBase64=getCustomerEmailBase64Encode(email);
		//String name="sam";
		String nameBase64=getCustomerNameURLEncode(name);
		String key="";
		String hash=getCustomerDetailSHA1(key,email,quoteRef);
		String uniqueURL="http://"+urlTrustPilot+"/evaluate/"+urlTalktotim+
				"?a="+quoteRef+"&b="+emailBase64+"&c="+nameBase64+"&e="+hash;
		return uniqueURL;
	}
	
	/***
	 * 
	 * encode data on your side using BASE64
	 * @param email
	 * @return
	 * @author sam
	 */
	private static String getCustomerEmailBase64Encode(String email)
	{
		byte[]   bytesEncoded = Base64.encodeBase64(email .getBytes());
		//System.out.println("ecncoded value is :" + new String(bytesEncoded ));
		return new String(bytesEncoded );
	}
	
	/***
	 * function return URL encoded value of customer name
	 * @param name
	 * @return
	 * @author sam
	 */
	private static String getCustomerNameURLEncode(String name)
	{
		String url = "";
		try {
			url = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	/*public static String getCustomerEmailBase64Decode(byte[] bytesEncoded)
	{
		byte[] valueDecoded= Base64.decodeBase64(bytesEncoded);
		System.out.println("Decoded value is :" + new String(valueDecoded));
		return new String(valueDecoded );
	}*/
	
	/***
	 * 
	 * function return 
	 * @param key
	 * @param email
	 * @param quoteRef 1
	 * @return
	 * @author sam
	 */
	private static String getCustomerDetailSHA1(String key,String email,String quoteRef )
	{
		//Secret key = mykey12, CustomerEmail 0 email@email.com, OrderRef=1234
		String uniqueKey = key+email+quoteRef;
		try {
			uniqueKey= SHA1(uniqueKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("uniqueKey :"+uniqueKey);
		return uniqueKey;
	}
	
	/***
	 * 
	 * support function for getCustomerDetailSHA1
	 * @param data
	 * @return
	 * @author sam
	 */
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/***
	 * 
	 * support function for getCustomerDetailSHA1
	 * @param data
	 * @return
	 * @author sam
	 */
	private static String SHA1(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}
	
}
