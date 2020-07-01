package coveotest;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.*;
import com.couchbase.client.java.json.*;
import com.couchbase.client.java.query.*;
import org.reactivestreams.Subscription;
import reactor.core.publisher.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;

import coveotest.TextFileFix;

/**
 * This class can query a couchbase clustery, then parse a highcharts template
 * and replace ##markers## by specific content
 * and generate a html file
 *
 * java -cp coveotest.jar coveotest.QueryCountryDeviceToHighCharts <user> <password> 
 * example : java coveotest.QueryCountryDeviceToHighCharts Admin mypass
 *  
 */
 
public final class QueryCountryDeviceToHighCharts
{
	public static String countryDeviceQuery = "SELECT country, deviceCategory, count(*) as nb FROM searchdata r WHERE deviceCategory is not null and country is not null group by country, deviceCategory order by country, nb desc";
	
	public static String countryQuery = "SELECT COUNT(deviceCategory) AS nb , country FROM searchdata r WHERE deviceCategory is not null and country is not null GROUP BY country order by nb desc";
	
	public static void main(final String[] args) {
		try {
			if( args.length != 2 )
			{
				System.err.println("args.length="+args.length);
				usage();
			}
			
			String user = args[0];
			String password = args[1];

			Cluster cluster = Cluster.connect("127.0.0.1", user, password);
			// First we map this kind of data
			// { name: "Item1",  y: 5.58, drilldown: "Item1"  }
			StringBuffer sbFirst = new StringBuffer();
			QueryResult result = cluster.query(countryQuery, queryOptions().metrics(true));
			for (JsonObject row : result.rowsAsObject()) {
				String country = row.getString("country");
				if (country.length()>0)
				{
					sbFirst.append("{ name: \"" + country + "\", y:" + row.get("nb") + ", drilldown: \"" + country + "\"},");
				}				
			}	
			if (sbFirst.length() > 0) {
				sbFirst.setLength(sbFirst.length() - 1);
			}			

			// Then the second part
			// { name: "Item1",id: "Item1",data: [["v11.0",3.39],["v10.1",0.96],["v10.0",0.36],["v9.1",0.54],["v9.0",0.13],["v5.1",0.2]]},
			StringBuffer sbSecond = new StringBuffer();
			result = cluster.query(countryDeviceQuery, queryOptions().metrics(true));

			String currentCountry = "";
			for (JsonObject row : result.rowsAsObject()) {
				String country = row.getString("country");
				if (country.length()>0)
				{
					if (!currentCountry.equals(country))
					{
						if (currentCountry.length()>0)
						{
							sbSecond.setLength(sbSecond.length() - 1);
							sbSecond.append("]},");
						}
						sbSecond.append("{ name: \"" + country + "\", id:\"" + country + "\",data:[");
						currentCountry = country;
					}					
					sbSecond.append("[\"" + row.getString("deviceCategory") + "\","+row.get("nb") + "],");
					
				}
			}	
			if (sbSecond.length() > 0) {
				sbSecond.setLength(sbSecond.length() - 1);
				sbSecond.append("]}");
			}	
			System.out.print(sbSecond+"\t");

			
			File source = new File("./highcharts/country_device_template.html");
			File dest = new File("./highcharts/country_device.html");
			dest.delete();
			Files.copy(source.toPath(), dest.toPath());
			
			TextFileFix.modifyFile("./highcharts/country_device.html","##data##",sbFirst.toString());
			TextFileFix.modifyFile("./highcharts/country_device.html","##series##",sbSecond.toString());
						
			System.out.println("Reported execution time: "    + result.metaData().metrics().get().executionTime());
		} catch (CouchbaseException | IOException ex) {
		ex.printStackTrace();
		}
    }
	
	private static void usage()
    {
        System.err.println("Must pass three arguments for 1)login , 2) password of couchbase server and 3) template html file");
        System.exit(1);
    }
}

