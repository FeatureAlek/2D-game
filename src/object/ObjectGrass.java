package object;

public class ObjectGrass extends SuperObject {

    public static final int BOOST_DURATION = 180; // frames (3 seconds at 60fps)

    public ObjectGrass() {
        name = "SpeedGrass";
        collision = false; // player walks over it, not blocked
    }

    @Override
    public void onPlayerEnter(main.GamePanel gp) {
        gp.player.activateSpeedBoost(BOOST_DURATION);
    }
}