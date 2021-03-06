package net.craftrepo.Bans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
//import java.util.List;

//import org.bukkit.util.config.ConfigurationNode;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class subscription extends Thread
{
	String key = Bans.getAPIKEY();
	String[] x = Bans.config.getString("subscriptions").split(",");
	{
		if ((Object)x == null)
		{
			Bans.log.severe(Bans.logPrefix + " An error has occured while obtaining the subscriptions");
			Bans.log.severe(Bans.logPrefix + " You haven't defined any subscriptions! Please edit the log file.");
			this.interrupt();
		}
	}

	String subs = strip(Bans.makesubs(x, 0));
	
	public static String strip(String s) 
	{
		String good = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String result = "";
		for ( int i = 0; i < s.length(); i++ ) 
		{
			if ( good.indexOf(s.charAt(i)) >= 0 )
				result += s.charAt(i);
		}
		return result;
	}

	public void run()
	{
		while (true)
		{
			try
			{
				String data = URLEncoder.encode("subscribe", "UTF-8") + "=" + URLEncoder.encode(subs, "UTF-8");
				data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");

				// Send data
				URL url = new URL("http://209.236.124.35/api/fetch_subscriptions.json");
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

					}
						// we need to figure out how we will handle this asap...
				}
				wr.close();
				rd.close();
				Thread.sleep(900000);
			}
			catch (Exception e) 
			{
				Bans.log.severe(Bans.logPrefix + " An error has occured while obtaining the subscriptions");
				Bans.log.severe(Bans.logPrefix + " " + e);
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