
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{

	HashMap<String, String> urls = new HashMap<String, String>();
	
	public void onEnable(){
		this.saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public String locToStr(Location l){
		String s = l.getWorld().getName();
		s+="," + l.getBlockX();
		s+="," + l.getBlockY();
		s+="," + l.getBlockZ();
		return s;
	}
	
	public Location strToLoc(String s){
		String[] split = s.split(",");
		Location l = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		return l;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		String uuid = e.getPlayer().getUniqueId().toString();
		if(urls.containsKey(uuid))
			urls.remove(uuid);
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e){
		if((e.getBlock().getType() == Material.SIGN || e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST)){
			Location l = e.getBlock().getLocation();
			String loc = locToStr(l);
			if(this.getConfig().contains(loc) && this.getConfig().getString(loc).length() > 2){
				this.getConfig().set(loc, null);
				saveConfig();
				e.getPlayer().sendMessage(ChatColor.RED + "Removed url sign");
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		String uuid = e.getPlayer().getUniqueId().toString();
		if(e.getClickedBlock() != null){
			Location l = e.getClickedBlock().getLocation();
			String loc = locToStr(l);
			if(urls.containsKey(uuid)){
				String url = urls.get(uuid);
				if(e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST)){
					this.getConfig().set(loc, urls.get(uuid));
					saveConfig();
					e.getPlayer().sendMessage(ChatColor.GREEN + "Set sign " + loc + " to url " + urls.get(uuid));
					urls.remove(uuid);
				}else{
					urls.remove(uuid);
					e.getPlayer().sendMessage(ChatColor.RED + "You interacted with something other than a sign, cancelling");
				}
			}else if(this.getConfig().contains(loc) && this.getConfig().getString(loc).length() > 2){
				String url = this.getConfig().getString(loc);
				e.getPlayer().sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "" + ChatColor.UNDERLINE + url);
			}
		}
	}

	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args){
		if(sender instanceof Player && sender.hasPermission("urlsign.create")){
			Player p = (Player) sender;
			if(args.length > 0){
				urls.put(p.getUniqueId().toString(), args[0]);
				p.sendMessage(ChatColor.GREEN + "Right click a sign to set it's URL!");
			}else{
				p.sendMessage(ChatColor.RED + "/urlsign (url)");
			}
		}
		return true;
		 
	}
	
	
}
