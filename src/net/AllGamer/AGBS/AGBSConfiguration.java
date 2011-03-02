package net.AllGamer.AGBS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

public class AGBSConfiguration 
{

	private AGBS plugin;
	private File folder;
	private final Logger log = Logger.getLogger("Minecraft");
	private String logPrefix;

	public AGBSConfiguration(File folder, AGBS instance) 
	{
		this.folder = folder;
		this.plugin = instance;
		this.logPrefix = instance.logPrefix;
	}

	public void setupConfigs() 
	{
		File config = new File(this.folder, "config.yml");
		if (!config.exists()) 
		{
			try {
				log.info(logPrefix + "- Creating config directory... ");
				log.info(logPrefix + "- Creating config files... ");
				config.createNewFile();
				FileWriter fstream = new FileWriter(config);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("#AGBS configuration");
				out.write("");
				out.write("\n");
				out.write("#only select one datasource any more could cause problems... you have been warned...");
				out.write("#Use mysql?\n");
				out.write("mysql: false\n");
				out.write("");
				out.write("mysqldb: jdbc:mysql://localhost:3306/minecraft\n");
				out.write("mysqluser: root\n");
				out.write("mysqlpass: root\n");
				out.write("#use sqlite\n");
				out.write("sqlite: false\n");
				out.write("\n");
				out.write("#use flatfiles (default)\n");
				out.write("flatfiles: true\n");
				
				out.close();
				fstream.close();
				
			}
			catch (IOException ex) 
			{
				log.info(logPrefix
						+ "Error creating default Configuration File");
				this.plugin.getServer().getPluginManager()
						.disablePlugin((Plugin) this);
			}
		File banned = new File(this.folder, "banned.yml");
		if (!banned.exists()) 
			{
			try {
				log.info(logPrefix + "- Creating ban file... ");
				banned.createNewFile();
				FileWriter fstream = new FileWriter(banned);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("#AGBS local banned user file\n");
				out.write("banned:\n");
				out.write("    - badguy1\n");
				
				out.close();
				fstream.close();
				
				}
			catch (IOException ex) 
				{
				log.info(logPrefix
						+ "Error creating ban File");
				this.plugin.getServer().getPluginManager()
						.disablePlugin((Plugin) this);
				}	
			}
		File exempt = new File(this.folder, "exempt.yml");
		if (!exempt.exists()) 
			{
			try {
				log.info(logPrefix + "- Creating exempt file... ");
				exempt.createNewFile();
				FileWriter fstream = new FileWriter(exempt);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("#AGBS local exempt user file\n");
				out.write("#server owners, add yourself to this file!\n");
				out.write("exempt:\n");
				out.write("    - aetaric\n");
				out.close();
				fstream.close();
				
				}
			catch (IOException ex) 
				{
				log.info(logPrefix
						+ "Error creating exempt File");
				this.plugin.getServer().getPluginManager()
						.disablePlugin((Plugin) this);
				}	
			}
		}
	}
}