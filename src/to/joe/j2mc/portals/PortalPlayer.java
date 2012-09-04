package to.joe.j2mc.portals;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalPlayer {
    private boolean inPortal;
    private Location lastNonPortal;
    private final J2MC_Portals portals;
    private final Player player;

    public PortalPlayer(Player player, boolean inPortal, J2MC_Portals portals) {
        this.portals = portals;
        this.player = player;
        this.inPortal = inPortal;
        if (!inPortal) {
            this.lastNonPortal = player.getLocation();
        }
    }

    /**
     * @return true if player has teleported and should be untracked
     */
    public boolean check() {
        final PortalArea portal = this.portals.getPortalForPlayer(this.player);
        if ((portal != null) && !this.inPortal) {
            float yaw = this.lastNonPortal.getYaw();
            if ((yaw += 180) > 360) {
                yaw -= 360;
            }
            this.lastNonPortal.setYaw(yaw);
            this.player.teleport(this.lastNonPortal);
            this.player.sendPluginMessage(this.portals, "RubberBand", portal.getDestination().getBytes());
            return true;
        }
        if ((portal == null) && this.inPortal) {
            this.inPortal = false;
            this.lastNonPortal = this.player.getLocation();
        }
        return false;
    }

    public String getName() {
        return this.player.getName();
    }
}
