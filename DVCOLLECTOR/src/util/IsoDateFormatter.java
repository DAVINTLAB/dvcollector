package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IsoDateFormatter {
	
	private static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
																	//2016-12-07T16:41:26Z
																	//EEE MMM dd HH:mm:ss Z yyyy
																	//Wed Aug 27 13:08:45 +0000 2008" <- Twitter format
	
	public static String format(Date date){
		return isoFormat.format(date);
	}
}
