package dashnetwork.protocolsupportpotions.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import dashnetwork.protocolsupportpotions.main.ProtocolSupportPotions;
import dashnetwork.protocolsupportpotions.utils.PotionTranslator;
import dashnetwork.protocolsupportpotions.utils.SplashTranslator;
import dashnetwork.protocolsupportpotions.utils.TranslationData;
import dashnetwork.protocolsupportpotions.utils.VersionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;

public class PacketListener extends PacketAdapter {

    public PacketListener() {
        super(new AdapterParameteters().gamePhase(GamePhase.BOTH).plugin(ProtocolSupportPotions.getInstance()).types(new HashSet<>(ProtocolSupportPotions.getInstance().getAllPackets())).listenerPriority(ListenerPriority.HIGHEST));
    }

    public void start() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    public void stop() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {}

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        PacketType type = packet.getType();

        if (type.equals(PacketType.Play.Server.SPAWN_ENTITY)) {
            if (packet.getIntegers().read(6) == 73) {
                Player player = event.getPlayer();
                int version = VersionUtils.getVersion(player);
                PacketContainer edited = packet.deepClone();
                Entity entity = packet.getEntityModifier(event).read(0);

                if (entity != null && entity instanceof ThrownPotion) {
                    ThrownPotion potion = (ThrownPotion) entity;

                    for (PotionEffect effect : potion.getEffects()) {
                        for (PotionTranslator translator : PotionTranslator.values()) {
                            if (effect.getType().equals(translator.getPotionEffectType())) {
                                for (TranslationData data : translator.getDatas()) {
                                    if (data.getLowestVersion() <= version && data.getHighestVersion() >= version)
                                        edited.getIntegers().write(7, data.getRemap());
                                }
                            }
                        }
                    }
                }

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, edited, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                event.setCancelled(true);
            }
        } else if (type.equals(PacketType.Play.Server.WORLD_EVENT)) {
            int effectId = packet.getIntegers().read(0);

            if (effectId == 2002 || effectId == 2007) {
                Player player = event.getPlayer();
                int version = VersionUtils.getVersion(player);
                PacketContainer edited = packet.deepClone();

                if (version <= 210)
                    edited.getIntegers().write(0, 2002);

                for (SplashTranslator translator : SplashTranslator.values()) {
                    if (edited.getIntegers().read(1) == translator.getRGB()) {
                        for (TranslationData data : translator.getDatas()) {
                            if (data.getLowestVersion() <= version && data.getHighestVersion() >= version)
                                edited.getIntegers().write(1, data.getRemap());
                        }
                    }
                }

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, edited, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                event.setCancelled(true);
            }
        }
    }

    @Override
    public Plugin getPlugin() {
        return ProtocolSupportPotions.getInstance();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().types(new HashSet<>(ProtocolSupportPotions.getInstance().getAllPackets())).gamePhase(GamePhase.BOTH).priority(ListenerPriority.HIGHEST).build();
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder().types(new HashSet<>(ProtocolSupportPotions.getInstance().getAllPackets())).gamePhase(GamePhase.BOTH).priority(ListenerPriority.HIGHEST).build();
    }

}
