package dashnetwork.protocolsupportpotions.main;

import com.comphenix.protocol.PacketType;
import dashnetwork.protocolsupportpotions.listeners.AreaEffectCloudListener;
import dashnetwork.protocolsupportpotions.listeners.PacketListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProtocolSupportPotions extends JavaPlugin {

    private static ProtocolSupportPotions instance;
    private PacketListener packetListener;

    public static ProtocolSupportPotions getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        packetListener = new PacketListener();
        packetListener.start();

        if (getServer().getPluginManager().getPlugin("ViaRewind-Legacy-Support") == null)
            new AreaEffectCloudListener();
    }

    @Override
    public void onDisable() {
        packetListener.stop();
        packetListener = null;

        instance = null;
    }

    public List<PacketType> getAllPackets() {
        return StreamSupport.stream(PacketType.values().spliterator(), false).filter(type -> type.isSupported()).collect(Collectors.toList());
    }

}
