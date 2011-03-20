package net.AllGamer.AGBS;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import net.AllGamer.AGBS.AGBS;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class heartbeat extends Thread
{
	private AGBS AGBSPlugin = null;

	public heartbeat(AGBS AGBSin)
	{
		this.AGBSPlugin = AGBSin;
	}
	
	public void run()
	{
		BufferedReader rd;
		while (true)
		{
			try
			{
				String key = AGBS.getAPIKEY();
				String playerList = this.AGBSPlugin.getPlayers();

				if (playerList == "")
				{
					playerList = "Server-Empty";
				}
				String data = URLEncoder.encode("playerlist", "UTF-8") + "=" + URLEncoder.encode(playerList, "UTF-8");
				data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
			
				// Send data
				URL url = new URL("http://209.236.124.35/api/heartbeat.json");
				java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.close();

				// Get the response
				if (conn.getResponseCode() == 200)
				{
					rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				}
				else
				{
					rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}
				String line = "";
				try 
				{
					line = rd.readLine();	
				}
				catch (Exception e) 
				{
					line = "fail";	
				}
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
				wr.close();
				rd.close();
			
				// do it again in 5 min...
			
				} 
			catch (Exception e) 
			{
				try 
				{
					Thread.sleep(300000);
				}
				catch (InterruptedException e1) 
				{
				}
			}
		}
	}	
}
