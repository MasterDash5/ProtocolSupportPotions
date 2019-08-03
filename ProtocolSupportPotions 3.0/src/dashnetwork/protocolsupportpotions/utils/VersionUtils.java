package dashnetwork.protocolsupportpotions.utils;

import dashnetwork.protocolsupportpotions.ProtocolSupportPotions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import us.myles.ViaVersion.api.Via;

public class VersionUtils {

    private static ProtocolSupportPotions plugin = ProtocolSupportPotions.getInstance();
    private static String serverVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static String getServerVersion() {
        String[] split = serverVersion.replace("v", "").replace("R", "").split("_");
        String version = split[0] + "." + split[1];

        return version;
    }

    public static int getVersion(Player player) {
        Plugin protocolSupport = plugin.getServer().getPluginManager().getPlugin("ProtocolSupport");
        Plugin viaVersion = plugin.getServer().getPluginManager().getPlugin("ViaVersion");

        if (protocolSupport != null) {
            int protocolSupportVersion = getProtocolSupportVersion(player);
            int latest = protocolsupport.api.ProtocolVersion.getLatest(ProtocolType.PC).getId();

            if (protocolSupportVersion == latest && viaVersion != null) {
                int viaVersionVersion = getViaVersionVersion(player);

                if (viaVersionVersion != latest)
                    return viaVersionVersion;
            }

            return protocolSupportVersion;
        } else if (viaVersion != null)
            return getViaVersionVersion(player);

        return -1;
    }

    private static int getProtocolSupportVersion(Player player) {
        return ProtocolSupportAPI.getConnection(player).getVersion().getId();
    }

    private static int getViaVersionVersion(Player player) {
        return Via.getAPI().getPlayerVersion(player);
    }

}
