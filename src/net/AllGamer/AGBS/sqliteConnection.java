package net.AllGamer.AGBS;

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

@SuppressWarnings("unused")
public class sqliteConnection 
{

    static Logger             log = Logger.getLogger("Minecraft");

    private final AGBS plugin;

    public sqliteConnection(AGBS plugin)
    {
        this.plugin = plugin;
    }

    public static String        DATABASE       = "jdbc:sqlite:plugins\\AGBS\\AGBS.db";

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
                                                       + "'trap_id'    INT NOT NULL DEFAULT '0', "
                                                       + ")";

    public void initialize() 
    {
        Logger log = Logger.getLogger("Minecraft");
        log.info(AGBS.logPrefix + " Loading SQLite");

        Boolean success = true;

        if (!tableExists("trap")) 
        {
            log.info(AGBS.logPrefix + " 'player_bans' table doesn't exist, creating...");
            if (!createTable(PLAYER_TABLE)) 
            {
                success = false;
            }
        }

        if (!tableExists("trap_blocks")) 
        {
            log.info(AGBS.logPrefix + " 'ip_bans' table doesn't exist, creating...");
            if (!createTable(IP_TABLE)) 
            {
                success = false;
            }
        }

        if (!tableExists("trap_triggers")) 
        {
            log.info(AGBS.logPrefix + " 'exempt' table doesn't exist, creating now.");
            if (!createTable(EXEMPT_TABLE)) 
            {
                success = false;
            }
        }

        if (success) 
        {
            log.info(AGBS.logPrefix + " Database Loaded!");
        }
        else 
        {
            log.info(AGBS.logPrefix + " Error Loading Database!");
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
            log.info(AGBS.logPrefix + " Error loading org.sqlite.JDBC");
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
                log.info(AGBS.logPrefix + " Could not close DB Connections.");
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
            log.info(AGBS.logPrefix + " Table Check Exception");
            return false;
        }
        catch (ClassNotFoundException e) 
        {
            log.info(AGBS.logPrefix + " Error loading org.sqlite.JDBC");
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
                log.info(AGBS.logPrefix + " Table Check SQL Exception (on closing)");
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
            log.info(AGBS.logPrefix + " Create Table Exception");
            return false;
        }
        catch (ClassNotFoundException e) 
        {
            log.info(AGBS.logPrefix + " Error loading org.sqlite.JDBC");
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
                log.info(AGBS.logPrefix + " Could not create the table (on close)");
                return false;
            }
        }
    }

}
