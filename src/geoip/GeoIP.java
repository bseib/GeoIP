package geoip;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class GeoIP {
	
	final static private Logger log = LoggerFactory.getLogger(GeoIP.class);
	
	private String dbPath;
	
	/**
	 * <p>Construct a GeoIP object, which can be used to lookup Geo information based on an IP address. It
	 * needs to be given a MaxMind database file to supply the data.</p>
	 * 
	 * <p>There should be a singleton of this object for your application.</p>
	 *  
	 * @param dbFilename The dbFilename is a real path to a MaxMind database file. This filepath will be used to open the
	 * file by utilizing {@link File}. If you are using GeoIP in a web application, the database file could live somewhere
	 * on the classpath, and you can get the real path by calling {@link ServletContext#getRealPath(String)}, e.g.
	 * context.getRealPath("/WEB-INF/geoip/GeoLiteCity-20120403.dat");
	 */
	public GeoIP(String dbFilename) {
		this.dbPath = dbFilename;
		log.debug("geoIP database file path: "+dbPath);
	}

	/**
	 * <p>Given an IP address in dotted decimal format, provide any known geo data for that location.</p> 
	 * 
	 * @param ipDottedDecimal pass the IP address as a dotted decimal string, e.g., "169.10.11.12".
	 * @return Returns a {@link Location} object if there is any known geo information for the given IP address.
	 * Otherwise null is returned if no information is available.
	 */
	public Location getLocationByIp(String ipDottedDecimal) {
		long ipLong = ipDottedToLong(ipDottedDecimal);
		File geoipFile = new File(dbPath);
		try {
			long start = System.currentTimeMillis();
			LookupService service = new LookupService(geoipFile);
			Location loc = service.getLocation(ipLong);
			long delta = System.currentTimeMillis() - start;
			log.debug("geoip lookup took "+delta+"ms. ("+ipDottedDecimal+"->"+ipLong+").");
			return loc;
		}
		catch (IOException e) {
			// fall thru to return null
		}
		return null;
	}
	
	private long ipDottedToLong(String ipDottedDecimal) throws IllegalArgumentException {
		if ( ipDottedDecimal == null ) {
			throw new IllegalArgumentException("ipDottedDecimal cannot be null.");
		}
//		log.debug("ipDottedDecimal="+ipDottedDecimal);
		String[] octets = ipDottedDecimal.split("\\.");
		if ( octets.length != 4 ) {
			log.error("The IP address does not have four octets. Only got:");
			for (String o : octets) {
				log.error("["+o+"] ");
			}
			throw new IllegalArgumentException("ipDottedDecimal must be an IP address with four octets.");
		}
		try {
			long ipLong =	Long.parseLong(octets[0]) * 16777216 +
							Long.parseLong(octets[1]) * 65536 +
							Long.parseLong(octets[2]) * 256 +
							Long.parseLong(octets[3]);
//			log.debug("ipLong="+ipLong);
			return ipLong;
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("ipDottedDecimal must be an IP address with four numerical octets, base 10.");
		}
	}

}
