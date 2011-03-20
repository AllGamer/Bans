package com.craftrepo.Bans;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

@SuppressWarnings("unused")
public class sqliteConnection 
{

	static Logger             log = Logger.getLogger("Minecraft");

	private final Bans plugin;

	public sqliteConnection(Bans plugin)
	{
		this.plugin = plugin;
	}

	public static String        DATABASE       = "jdbc:sqlite:plugins\\Bans\\Bans.db";

	private final static String PLAYER_TABLE     = "CREATE TABLE `player_bans` "
		+ "("
		+ "`id`       	INT PRIMARY KEY, "
		+ "`name`     	VARCHAR(32) NOT NULL DEFAULT 'Player', "
		+ ")";

	private final static String IP_TABLE    = "CREATE TABLE `ip_bans` "
		+ "("
		+ "`id`       	INT PRIMARY KEY, "
		+ "'ip'    		INT NOT NULL DEFAULT '0', "
		+ ")";

	private final static String EXEMPT_TABLE = "CREATE TABLE `exempt` "
		+ "("
		+ "`id`         INT PRIMARY KEY, "
		+ "`name`     	VARCHAR(32) NOT NULL DEFAULT 'Player', "
		+ ")";

	public void initialize() 
	{
		Logger log = Logger.getLogger("Minecraft");
		log.info(Bans.logPrefix + " Loading SQLite");

		if (!tableExists("player_bans")) 
		{
			log.info(Bans.logPrefix + " 'player_bans' table doesn't exist, creating...");
			if (!createTable(PLAYER_TABLE)) 
			{
				log.info(Bans.logPrefix + " Cannot make table 'player_bans', disabling plugin.");
				this.plugin.getPluginLoader().disablePlugin((Plugin) this);
			}
		}

		if (!tableExists("ip_bans")) 
		{
			log.info(Bans.logPrefix + " 'ip_bans' table doesn't exist, creating...");
			if (!createTable(IP_TABLE)) 
			{
				log.info(Bans.logPrefix + " Cannot make table 'ip_banns', disabling plugin.");
				this.plugin.getPluginLoader().disablePlugin((Plugin) this);
			}
		}

		if (!tableExists("exempt")) 
		{
			log.info(Bans.logPrefix + " 'exempt' table doesn't exist, creating now.");
			if (!createTable(EXEMPT_TABLE)) 
			{
				log.info(Bans.logPrefix + " Cannot make table 'exempt', disabling plugin.");
				this.plugin.getPluginLoader().disablePlugin((Plugin) this);
			}
		}
	}

	public static boolean sql(String sql) 
	{
		Connection conn = null;
		Statement st = null;
		try 
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			st = conn.createStatement();
			st.executeUpdate(sql);
			return true;
		}
		catch (SQLException e) 
		{
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(Bans.logPrefix + " Error loading org.sqlite.JDBC");
			return false;
		}
		finally 
		{
			try 
			{
				if (conn != null) conn.close();
				if (st != null) st.close();
			}
			catch (SQLException e) 
			{
				log.info(Bans.logPrefix + " Could not close DB Connections.");
				return false;
			}
		}
	}

	private static boolean tableExists(String table) 
	{
		Connection conn = null;
		ResultSet rs = null;
		try 
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, table, null);
			if (!rs.next()) return false;
			return true;
		}
		catch (SQLException ex) 
		{
			log.info(Bans.logPrefix + " Table Check Exception");
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(Bans.logPrefix + " Error loading org.sqlite.JDBC");
			return false;
		}
		finally 
		{
			try 
			{
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			}
			catch (SQLException ex) 
			{
				log.info(Bans.logPrefix + " Table Check SQL Exception (on closing)");
			}
		}
	}

	private static boolean createTable(String sql) 
	{
		Connection conn = null;
		Statement st = null;
		try 
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			st = conn.createStatement();
			st.executeUpdate(sql);
			return true;
		}
		catch (SQLException e) 
		{
			log.info(Bans.logPrefix + " Create Table Exception");
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(Bans.logPrefix + " Error loading org.sqlite.JDBC");
			return false;
		}
		finally 
		{
			try 
			{
				if (conn != null) conn.close();
				if (st != null) st.close();
			}
			catch (SQLException e) 
			{
				log.info(Bans.logPrefix + " Could not create the table (on close)");
				return false;
			}
		}
	}

}
