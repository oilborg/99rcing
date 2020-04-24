

package scripts;
import javafx.beans.binding.ObjectExpression;
import javafx.geometry.Pos;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import java.awt.Color; //to get different colors
import java.awt.Font; //to change font
import java.awt.Graphics; //paint
import java.awt.Graphics2D; //needed for the image
import java.awt.Image; //same as above
import java.io.File;
import java.io.FileWriter;
import java.io.IOException; //this is needed for the loading of the image
import java.lang.management.GarbageCollectorMXBean;
import java.net.URL; //same as above

import javax.imageio.ImageIO; //same as above
import javax.swing.text.Position;

import org.tribot.api.Timing; //to calculate time things
import org.tribot.script.interfaces.Painting; //for onPaint()

import java.text.DecimalFormat;
import java.util.Random;

@ScriptManifest(authors = { "Grazza G" }, category = "Runecraft", name = "Fire Runes")

public class TutorialScript extends Script implements Painting {

    private void bank() {


    }

    private final String FILE = "C:\\Users\\graha\\AppData\\Roaming\\.tribot\\testFile.txt";
    private void makeFile(){
        try {
            File myObj = new File(FILE);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private void writeFile(String message){
        try {
            FileWriter myWriter = new FileWriter(FILE);
            myWriter.write("[" + Timing.msToString(Timing.currentTimeMillis()) + "]" + message);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        makeFile();
        writeFile("Started" );
        while (true) {
            //TODO we assume we are starting in edgeville bank, bank screen not open
            //TODO <-- BANKING -->
            Random rand = new Random();
            if (Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) > 20 ){
                System.out.println("Topping up health");
                Banking.openBank();
                sleep(3000 + rand.nextInt(3000));
                Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_1);
                // Obtain a number between [0 - 7].
                int n = rand.nextInt(7);
                Banking.withdraw(n,"Lobster");
                sleep(1200 + rand.nextInt(1200));
                Banking.close();
                while (Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) > 20 ){
                    Inventory.find("Lobster")[0].click("Eat");
                    sleep(rand.nextInt(1200));
                }
            }
            if( Inventory.getCount("Teleport to house") < 100 ){
                System.out.println("Grabbing home teleports");
                Banking.openBank();
                sleep(3000 + rand.nextInt(3000));
                // Obtain a number between [0 - 49].
                int n = rand.nextInt(50);
                Banking.withdraw(n,"Teleport to house");
                sleep(1200 + rand.nextInt(1200));
                System.out.println("Getting rid of shite");
                Banking.depositAllExcept("Teleport to house");
            }
            if( Inventory.getCount("Cosmic rune") > 0 ){
                System.out.println("banking money dolla ");
                if(! Banking.isBankScreenOpen()){
                    Banking.openBank();
                    sleep(3000 + rand.nextInt(3000));
                }
                Banking.depositAllExcept("Teleport to house");
            }
            if( Inventory.getCount("Pure essence") < 27 ){
                System.out.println("Grabbing rune essences");
                if(! Banking.isBankScreenOpen()){
                    Banking.openBank();
                    sleep(3000 + rand.nextInt(3000));
                }
                //Banking.withdraw(30,"Pure essence");
                System.out.println("Getting rid of shite");
                Banking.depositAllExcept("Teleport to house");
                Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_ALL);
                Banking.find("Pure essence")[0].click("Withdraw-All");
            }
            if(Banking.isBankScreenOpen()){
                Banking.close();
            }
            sleep(rand.nextInt(500));
            if(Game.getRunEnergy()>50){
                if(1<rand.nextInt(2)){
                    System.out.println("Turning run on");
                    Options.setRunOn(true);
                }
            }
            //TODO <-- NAVIGATING EDGEVILLE -->
            System.out.println("Getting out of edgeville");
            while(Player.getPosition().getY() < 3518){
                while(!(3104 < Player.getPosition().getX() && Player.getPosition().getX() < 3119)){
                    RSTile player_loc=Player.getPosition();
                    int newX=player_loc.getX()+7+rand.nextInt(7);
                    Walking.walkTo( new RSTile(newX, player_loc.getY()+7+rand.nextInt(7), 0));
                    sleep(100 + rand.nextInt(100));
                    while (Player.isMoving()) {
                        sleep(300 + rand.nextInt(300));
                    }
                }
                RSTile player_loc=Player.getPosition();
                int newY=player_loc.getY()+7+rand.nextInt(7);
                Walking.walkTo( new RSTile(player_loc.getX() , newY, 0));
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    sleep(300 + rand.nextInt(300));
                }
            }
            System.out.println("entering wildy");
            while(Player.getPosition().getY() <= 3520 ){
                Objects.findNearest(10,"Wilderness ditch")[0].click("Cross");
                sleep(1000 + rand.nextInt(1200));
            }
            while(Player.getPosition().getY() < 3560){
                while(!(3101 < Player.getPosition().getX() && Player.getPosition().getX() < 3111)){
                    RSTile player_loc=Player.getPosition();
                    int newX = player_loc.getX() + rand.nextInt(5);
                    if(newX >= 3106){
                        newX=3105;
                    }
                    Walking.walkTo( new RSTile(newX , player_loc.getY()+rand.nextInt(7), 0));
                    sleep(300 + rand.nextInt(300));
                }
                RSTile player_loc=Player.getPosition();
                int newY=player_loc.getY()+7+rand.nextInt(7);
                if(newY >= 3560){
                    Walking.walkTo( new RSTile(player_loc.getX() , newY, 0));
                    sleep(300 + rand.nextInt(300));
                    break;
                }else {
                    Walking.walkTo(new RSTile(player_loc.getX(), newY, 0));
                }
                while (Player.isMoving()) {
                    sleep(800 + rand.nextInt(300));
                }
            }
            System.out.println("finding mage");
            while(NPCs.findNearest("Mage of Zamorak").length == 0 ){
                Walking.walkTo(new RSTile(3105 + rand.nextInt(3), 3560 + rand.nextInt(3), 0));
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    sleep(800 + rand.nextInt(300));
                }
            }
            System.out.println("talking mage");
            while(NPCs.findNearest("Mage of Zamorak").length != 0 ){
                Walking.walkTo(NPCs.findNearest("Mage of Zamorak")[0].getPosition());
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    sleep(800 + rand.nextInt(300));
                }
                if(NPCs.findNearest("Mage of Zamorak").length == 0){
                    break;
                }
                NPCs.findNearest("Mage of Zamorak")[0].click("Teleport");
                sleep(3000 + rand.nextInt(1200));
            }
            //TODO <-- NAVIGATING ABYSS -->
            //TODO <-- locate obstice; to tackle
            boolean Eyes,Gap,Rock;
            Eyes = Gap = Rock = false;
            while( !(new RSArea( new RSTile(3024,4817,0),new RSTile(3054,4846,0))).contains(Player.getPosition())){
                sleep(1200 + rand.nextInt(1200));
                if(Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) < 15 ){
                    System.out.println("NIGGA ABORT");
                    while(Objects.findNearest(20,"Portal").length == 0){
                        Inventory.find("Teleport to house")[0].click("Break");
                        // TODO fix this shite
                        sleep(1200 + rand.nextInt(1200));
                    }
                }
                // TODO for String obstical in
                if(Objects.findNearest(10,"Eyes").length != 0 && (!Gap && !Rock)){
                    System.out.println("using eyes");
                    Eyes=true;
                    if(Objects.findNearest(10,"Eyes")[0].getPosition().distanceTo(Player.getPosition()) > 2) {
                        Walking.walkTo(Objects.findNearest(10, "Eyes")[0].getPosition());
                        sleep(1200 + rand.nextInt(1200));
                    }
                    if((new RSArea( new RSTile(3024,4817,0),new RSTile(3054,4846,0))).contains(Player.getPosition())){
                        break;
                    }
                    Objects.findNearest(10,"Eyes")[0].click("Distract");
                    sleep(100+rand.nextInt(1200));
                }
                if(Objects.findNearest(10,"Gap").length != 0  && (!Eyes && !Rock)){
                    System.out.println("using gap");
                    Gap=true;
                    if(Objects.findNearest(10,"Gap")[0].getPosition().distanceTo(Player.getPosition()) > 2) {
                        Walking.walkTo(Objects.findNearest(10, "Gap")[0].getPosition());
                        sleep(1200 + rand.nextInt(1200));
                    }
                    if((new RSArea( new RSTile(3024,4817,0),new RSTile(3054,4846,0))).contains(Player.getPosition())){
                        break;
                    }
                    Objects.findNearest(10,"Gap")[0].click("Squeeze-through");
                    sleep(300 + rand.nextInt(1200));
                }
                if(Objects.findNearest(10,"Rock").length != 0 && (!Eyes && !Gap) ){
                    System.out.println("using rock");
                    Rock=true;
                    if(Objects.findNearest(10,"Rock")[0].getPosition().distanceTo(Player.getPosition()) > 2) {
                        Walking.walkTo(Objects.findNearest(10, "Rock")[0].getPosition());
                        sleep(1200 + rand.nextInt(1200));
                    }
                    if((new RSArea( new RSTile(3024,4817,0),new RSTile(3054,4846,0))).contains(Player.getPosition())){
                        break;
                    }
                    Objects.findNearest(10,"Rock")[0].click("Mine");
                    sleep(200 + rand.nextInt(1200));
                }
                if(!(Eyes || Gap || Rock)){
                    System.out.println("fucking");
                    int X = Player.getPosition().getX();
                    int Y = Player.getPosition().getY();
                    if (X>3039){
                        X = X - ( 1 + rand.nextInt(3) );
                    }else{
                        X = X + ( 1 + rand.nextInt(3) );
                    }
                    if (Y>4831){
                        Y = Y - ( 1 + rand.nextInt(3) );
                    }else{
                        Y = Y + ( 1 + rand.nextInt(3) );
                    }
                    WebWalking.walkTo(new RSTile(X,Y,0));
                    sleep(2200 + rand.nextInt(3000));
                }
            }
            while(Objects.findNearest(15,"Cosmic rift").length == 0 ){
                System.out.println("no cosmic rift, running to middle");
                Walking.walkTo(new RSTile(3039 + rand.nextInt(3), 4835 + rand.nextInt(3), 0));
                sleep(5000 + rand.nextInt(1200));
                if(!Player.isMoving()){
                    RSTile position = Player.getPosition();

                    Walking.walkTo(new RSTile(3039 + rand.nextInt(3), 4835 + rand.nextInt(3), 0));
                }
            }
            while((Objects.findNearest(15,"Cosmic rift").length != 0 )){
                System.out.println("going cosmic");
                Walking.walkTo( Objects.findNearest(20,"Cosmic rift")[0].getPosition() );
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    sleep(100 + rand.nextInt(100));
                }
                if(Objects.findNearest(15,"Cosmic rift").length != 0){
                    Objects.findNearest(20, "Cosmic rift")[0].click("Exit-through");
                    sleep(1000 + rand.nextInt(1200));
                }
            }
            // TODO craft the runes
            while(Inventory.find("Pure essence").length != 0){
                System.out.println("crafting");
                while(Player.getAnimation() != 791 ) {
                    Objects.findNearest(20, "Altar")[0].click("Craft-rune");
                    sleep(100 + rand.nextInt(1200));
                }
                sleep(100 + rand.nextInt(1200));
            }
            runesMade=runesMade+27;
            while(Player.getAnimation() == 791 ) {
                sleep(1000 + rand.nextInt(1200));
            }
            //TODO Go back to edgevill
            while(Objects.findNearest(20,"Altar").length != 0){
                System.out.println("teleporting home");
                Inventory.find("Teleport to house")[0].click("Break");
                sleep(5000 + rand.nextInt(1200));
            }
            while(Objects.findNearest(20,"Portal").length != 0){
                System.out.println("going to edge");
                while(Objects.findNearest(10,"Amulet of glory")[0].getPosition().distanceTo(Player.getPosition())>1){
                    Walking.walkTo(Objects.findNearest(10,"Amulet of glory")[0].getPosition());
                }
                Objects.findNearest(10,"Amulet of glory")[0].click("Edgeville");
                sleep(2000 + rand.nextInt(1200));
            }
            // TODO walk into the bank and repeater
            Walking.walkTo(new RSTile(3093 + rand.nextInt(3), 3491 + rand.nextInt(3), 0));
            sleep(5000 + rand.nextInt(1200));
        }
    }

    @Override
    public void onPaint(Graphics g)  {
        long timeRan = System.currentTimeMillis() - startTime;
        g.setFont(font);
        g.setColor(new Color(0, 200, 0));
        g.drawString("Runtime: " + Timing.msToString(timeRan), 300, 400);
        g.drawString("Runes made: " + runesMade, 300, 420);
        g.drawString("gp made: " +  172*runesMade, 300, 440);
        g.setColor(new Color(200, 0, 0));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        g.drawString("£ made: " +  df.format((172*runesMade)/1000000.00), 300, 460);
        //g.drawString("£ made: " +  (172*1000)/1000000.0, 300, 500);
    }

    private static final long startTime = System.currentTimeMillis();
    private static int runesMade=0;
    Font font = new Font("Verdana", Font.BOLD, 14);
}

