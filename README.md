How to use:

	import java.net.URL;
	import com.maxmind.geoip.Location;
	
	public class DemoGeoIP {
		
		static public void main(String[] args) {
			new DemoGeoIP().demo1();
		}
	
		public void demo1() {
			URL url = this.getClass().getResource("GeoLiteCity-20121109.dat");
			String filePath = url.getFile();
			GeoIP geo = new GeoIP(filePath);
			Location loc = geo.getLocationByIp("74.125.225.78");  // one of google.com addresses
			System.out.println("loc="+loc);
		}
	
	}

You should probably use a singleton pattern with GeoIP.
