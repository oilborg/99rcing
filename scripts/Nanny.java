package scripts;

import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.*;

public class Nanny extends Thread{
    private static RSTile PreviousPosition;
    private static ScriptStates PreviousState;
    private static boolean isReady = false;
    public static String happiness = "INIT";
    private static long checkIntervalMs = 90000;

    public void run(){
        while (true) {
            setSnapShot();
            try {
                sleep(checkIntervalMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setSnapShot(){
        if(!isReady) {
            Log("[INFO] Nanny starting.");
            Nanny.PreviousPosition = Player.getPosition();
            Nanny.PreviousState = Main.MAIN_SCRIPT_STATE;
            isReady = true;
        }else{
            if((Nanny.PreviousPosition.equals(Player.getPosition()))&&(Nanny.PreviousState.equals(Main.MAIN_SCRIPT_STATE))){
                Log("[ERROR] Main thread is stuck.");
                Nanny.happiness= "STUCK";
                Main.kill();
            }else{
                Log("[INFO] Everything is ok. Checking again in " + checkIntervalMs);
                Nanny.happiness= "OK";
                Nanny.PreviousPosition = Player.getPosition();
                Nanny.PreviousState = Main.MAIN_SCRIPT_STATE;
            }
        }
    }

    private static void Log(String text){
        System.out.println("[NANNY] " + text);
    }

}
