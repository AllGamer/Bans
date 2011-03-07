package net.AllGamer.AGBS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.bukkit.util.config.ConfigurationNode;

public class subscription extends Thread 
{

	String key = AGBS.getAPIKEY();
	List<ConfigurationNode> sub = null;
	List<ConfigurationNode> subs = AGBS.config.getNodeList("subscriptions", sub );


	public void run()
	{
		while (true)
		{
			for (ConfigurationNode s : subs)
			{
				try
				{

					String data = URLEncoder.encode("subscribe", "UTF-8") + "=" + URLEncoder.encode(s.toString(), "UTF-8");
					data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");


					// Send data
					URL url = new URL("http://209.236.124.35/api/subscribe.json");
					java.net.URLConnection conn = url.openConnection();
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();

					// Get the response
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line;
					while ((line = rd.readLine()) != null) 
					{
						if (line.contains("ok")) {

						}
						// we need to figure out how we will handle this asap...
					}
					wr.close();
					rd.close();
					Thread.sleep(900000);
				} 
				catch (Exception e) 
				{
					AGBS.log.severe(AGBS.logPrefix + " An error has occured while obtaining the subscriptions");
					AGBS.log.severe(AGBS.logPrefix + " " + e);
					try 
					{
						Thread.sleep(900000);
					} 
					catch (InterruptedException e1) 
					{
					}
				}
			}
		}
	}
}