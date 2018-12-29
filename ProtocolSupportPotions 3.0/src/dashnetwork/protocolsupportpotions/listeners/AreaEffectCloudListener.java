package dashnetwork.protocolsupportpotions.listeners;

import dashnetwork.protocolsupportpotions.main.ProtocolSupportPotions;
import dashnetwork.protocolsupportpotions.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * From ViaRewind Legacy Support
 * github.com/Gerrygames/ViaRewind-Legacy-Support
 */
public class AreaEffectCloudListener implements Listener {
	
	private ProtocolSupportPotions plugin;
	private List<AreaEffectCloud> effectClouds;
	
	public AreaEffectCloudListener() {
		plugin = ProtocolSupportPotions.getInstance();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		effectClouds = new CopyOnWriteArrayList<>();
		
		new BukkitRunnable() {
			public void run() {
				for (AreaEffectCloud cloud : effectClouds) {
					if (cloud == null || cloud.isDead() || !cloud.isValid())
						effectClouds.remove(cloud);
					else {
						Location location = cloud.getLocation();
						float radius = cloud.getRadius();
						float area = (float) Math.PI * radius * radius;
						
						int color = cloud.getColor().asRGB();
						int red = color >> 16 & 255;
						int green = color >> 8 & 255;
						int blue = color & 255;
						
						for (int i = 0; i < area; i++) {
							float f1 = (float) Math.random() * 6.2831855F;
							float f2 = (float) Math.sqrt(Math.random()) * radius;
							float f3 = (float) Math.cos(f1) * f2;
							float f4 = (float) Math.sin(f1) * f2;
							
							for (Player player : cloud.getWorld().getPlayers()) {
								if (VersionUtils.getVersion(player) <= 106)
									player.spawnParticle(cloud.getParticle(), location.getX() + f3, location.getY(), location.getZ() + f4, 0, red / 255f, green / 255f, blue / 255f);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 1L);
	}
	
	@EventHandler
	public void onSplash(LingeringPotionSplashEvent event) {
		effectClouds.add(event.getAreaEffectCloud());
	}

}
