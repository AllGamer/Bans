package net.AllGamer.AGBS;

import java.io.*;
import java.*;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.plugin.java.JavaPlugin;

public class heartbeat extends Thread 
{
	public static Configuration config;
	{
	}
	public void configInit()
	{
		config = new Configuration(new File("./plugins/AGBS", "config.yml"));
	}
	public void run()
	{
		int count = 0;
		while (count < 9)
		{
		
		try
		{
			String key = AGBS.getAPIKEY();
			String data = URLEncoder.encode("playerlist", "UTF-8") + "=" + URLEncoder.encode("aetaric", "UTF-8");
			data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
			
			
			// Send data
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
					Thread.sleep(300000);
				}
				else 
				{
					AGBS.log.severe(AGBS.logPrefix + " Error while trying to heartbeat");
					AGBS.log.severe(AGBS.logPrefix + " Check your apikey in config.yml");
					Thread.sleep(300000);
				}
			}
			wr.close();
			rd.close();
			
			// do it again in 5 min...
			
			} 
			catch (Exception e) 
			{
				AGBS.log.severe(AGBS.logPrefix + " An error has occured while trying to heartbeat");
				AGBS.log.severe(AGBS.logPrefix + " " + e);
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e1) {
					AGBS.log.severe(AGBS.logPrefix + " Something stupid happened while trying to sleep the thread...");
				}
			}
		}
	}	
}