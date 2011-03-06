package net.AllGamer.AGBS;

import java.io.*;
import java.*;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.util.config.ConfigurationNode;

public class heartbeat extends Thread 
{
	
	
	Object apikey = AGBS.config.getProperty("apikey");
	
	public void run()
	{
		int count = 0;
		while (count < 9)
		{
		
		try
		{
			
			String data = URLEncoder.encode("playerlist", "UTF-8") + "=" + URLEncoder.encode("aetaric", "UTF-8");
			data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode((String) apikey, "UTF-8");
			
			
			// Send data
			//URL url = new URL("http://hostname:80/api");
			URL url = new URL("http://209.236.124.35/api/heartbeat.json");
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
				if (line.contains("ok"))
				{
					continue;
				}
				else 
				{
					AGBS.log.severe(AGBS.logPrefix + "Error while trying to heartbeat");
					AGBS.log.severe(AGBS.logPrefix + "Check your apikey in config.yml");
				}
			}
			wr.close();
			rd.close();
			
			// do it again in 5 min...
			Thread.sleep(300000);
			} 
			catch (Exception e) 
			{
				AGBS.log.severe(AGBS.logPrefix + "An error has occured while trying to heartbeat");
				AGBS.log.severe(AGBS.logPrefix + "Do we have active internet?");
				AGBS.log.severe(AGBS.logPrefix + " " + e);
			}
		}
	}	
}