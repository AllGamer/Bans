package net.craftrepo.Bans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class BansConfiguration 
{

	private Bans plugin;
	private File folder;
	private final Logger log = Logger.getLogger("Minecraft");
	private String logPrefix;

	public BansConfiguration(File folder, Bans instance) 
	{
		this.folder = folder;
		this.plugin = instance;
		this.logPrefix = Bans.logPrefix;
	}

	public void setupConfigs() 
	{
		File config = new File(this.folder, "config.yml");
		if (!config.exists()) 
		{
			try 
				{
					log.info(logPrefix + " - Creating config directory... ");
					log.info(logPrefix + " - Creating config files... ");
					config.createNewFile();
					FileWriter fstream = new FileWriter(config);
					BufferedWriter out = new BufferedWriter(fstream);

					out.write("#Bans Configuration");
					out.write("");
					out.write("\n");
					out.write("#IMPORTANT!!!\n");
					out.write("apikey:\n");
					out.write("\n");
					out.write("#Only select one datasource, any more could cause problems... You have been warned!\n");
					out.write("#Use mysql?\n");
					out.write("mysql: false\n");
					out.write("");
					out.write("mysqldb: jdbc:mysql://localhost:3306/minecraft\n");
					out.write("mysqluser: root\n");
					out.write("mysqlpass: root\n");
					out.write("mysqlport: 3306\n");
					out.write("\n");
					out.write("#Use sqlite?\n");
					out.write("sqlite: false\n");
					out.write("\n");
					out.write("#Use flatfiles? (default)\n");
					out.write("flatfiles: true\n");
					out.write("\n");
					out.write("\n");
					out.write("subscriptions:\n");
					out.write("#These will all be commented out by default...uncomment the ones you want...\n");
					out.write("\n");
					out.write("#Prevent known hackers\n");
					out.write("#    - hax\n");
					out.write("#Prevent known griefers\n");
					out.write("#    - grf\n");
					out.write("#Prevent known theives\n");
					out.write("#    - thf\n");
					out.write("#Prevent people known for making Discriminatory Comments\n");
					out.write("#    - dis\n");
					out.write("#Prevent people known for using foul language\n");
					out.write("#    - lan\n");
					out.write("#Prevent people known for making inappropriate buildings\n");
					out.write("#    - bld\n");
					out.write("#Bans issued by my server\n");
					out.write("#   - mys\n");
				
				
					out.close();
					fstream.close();
					log.info(logPrefix + " Make sure to edit your config file!");
				
				}
			catch (IOException ex) 
				{
					log.severe(logPrefix + " Error creating default Configuration File");
					log.severe(logPrefix + " " + ex);
					this.plugin.getServer().getPluginManager().disablePlugin((Plugin) this);
				}
			}
		File banned = new File(this.folder, "bans.yml");
		if (!banned.exists()) 
			{
				try 
					{
						log.info(logPrefix + " - Creating ban file... ");
						banned.createNewFile();
						FileWriter fstream = new FileWriter(banned);
						BufferedWriter out = new BufferedWriter(fstream);

						out.write("#Bans local banned user file\n");
						out.write("banned:\n");
						out.write("    - badguy1\n");
				
						out.close();
						fstream.close();
				
					}
				catch (IOException ex) 
					{
						log.severe(logPrefix + " Error creating ban File");
						log.severe(logPrefix + " " + ex);
						this.plugin.getServer().getPluginManager().disablePlugin((Plugin) this);
					}	
			}
		File bannedIP = new File(this.folder, "banIP.yml");
		if (!bannedIP.exists()) 
			{
				try 
					{
						log.info(logPrefix + " - Creating IP ban file... ");
						banned.createNewFile();
						FileWriter fstream = new FileWriter(bannedIP);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write("#Bans local banned IP file\n");
						out.write("banned:\n");
						out.write("    - 69.69.69.69\n");
				
						out.close();
						fstream.close();
				
					}
				catch (IOException ex) 
					{
						log.severe(logPrefix + " Error creating IP ban File");
						log.severe(logPrefix + " " + ex);
						this.plugin.getServer().getPluginManager().disablePlugin((Plugin) this);
					}	
			}
		File exempt = new File(this.folder, "exempt.yml");
		if (!exempt.exists()) 
			{
				try 
					{
						log.info(logPrefix + " - Creating exempt file... ");
						exempt.createNewFile();
						FileWriter fstream = new FileWriter(exempt);
						BufferedWriter out = new BufferedWriter(fstream);

						out.write("#Bans local exempt user file\n");
						out.write("#Server owners, add yourself to this file!\n");
						out.write("exempt:\n");
						out.write("    - serverstaff\n");
						out.close();
						fstream.close();
				
					}
				catch (IOException ex) 
					{
					log.severe(logPrefix + " Error creating exempt File");
					log.severe(logPrefix + " " + ex);
					this.plugin.getServer().getPluginManager().disablePlugin((Plugin) this);
				}	
			}
		}
	}
