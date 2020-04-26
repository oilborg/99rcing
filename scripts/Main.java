

package scripts;
import javafx.beans.binding.ObjectExpression;
import javafx.geometry.Pos;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
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

import static scripts.Nanny.happiness;

@ScriptManifest(authors = { "Grazza G" }, category = "Runecrafting", name = "Fire Runes")

public class Main extends Script implements Painting {

    public static ScriptStates MAIN_SCRIPT_STATE = ScriptStates.INIT;
    public static volatile boolean isRunning = true;
    private static final boolean shouldHaveSmallPouch = Skills.getCurrentLevel(Skills.SKILLS.RUNECRAFTING) >= 1;
    private static final boolean shouldHaveMediumPouch = Skills.getCurrentLevel(Skills.SKILLS.RUNECRAFTING) >= 25;
    private static final boolean shouldHaveLargePouch = Skills.getCurrentLevel(Skills.SKILLS.RUNECRAFTING) >= 50;
    private static final boolean shouldHaveGiantPouch = Skills.getCurrentLevel(Skills.SKILLS.RUNECRAFTING) >= 75;

    private static boolean hasDied(){
        return Player.getPosition().distanceTo(new RSTile(3222,3219,0)) < 10;
    }

    private static void checkRunePouches(){
        System.out.println("[INFO] Checking rune pouches.");
        if(shouldHaveSmallPouch && !findItem("Small pouch")){
            System.out.println("[WARNING] You don't have a small pouch. Get one!!");
        }else {
            if(Inventory.find("Small pouch").length == 0){
                System.out.println("grabbing pouch");
                Banking.withdraw(1,"Small pouch");
            }
        }
        if(shouldHaveMediumPouch && !findItem("Medium pouch")){
            System.out.println("WARNING : You don't have a medium pouch. Get one!!");
        }else {
            if(Inventory.find("Medium pouch").length == 0){
                System.out.println("grabbing pouch");
                Banking.withdraw(1,"Medium pouch");
            }
        }
        if(shouldHaveLargePouch && !findItem("Large pouch")){
            System.out.println("WARNING : You don't have a large pouch. Get one!!");
        }else {
            if(Inventory.find("Large pouch").length == 0){
                Banking.withdraw(1,"Large pouch");
            }
        }
        if(shouldHaveGiantPouch && !findItem("Giant pouch")){
            System.out.println("WARNING : You don't have a giant pouch. Get one!!");
        }else {
            if(Inventory.find("Giant pouch").length == 0){
                Banking.withdraw(1,"Giant pouch");
            }
        }
    }

    private static boolean findItem(String item){
        return (Banking.find(item).length == 0)||(Inventory.find(item).length==0);
    }

    public static void kill(){
        System.out.println("[WARNING] >>>>> KILL <<<<< ");
        isRunning = false;
        while(Objects.findNearest(20,4525).length == 0){
            Inventory.find("Teleport to house")[0].click("Break");
            General.sleep(1200 + rand.nextInt(1200));
        }
        General.sleep(1200 + rand.nextInt(1200));
        Login.logout();
        throw new RuntimeException("Nanny kill");
    }

    private void bank() {
        MAIN_SCRIPT_STATE = ScriptStates.BANKING;
        //TODO we assume we are starting in edgeville bank, bank screen not open
        //TODO <-- BANKING -->
        RSArea edgevilleBank= new RSArea(new RSTile(3086,3485,0),new RSTile(3100,3501,0));
        if(!edgevilleBank.contains(Player.getPosition())){
            if(Inventory.find("Teleport to house").length == 0){
                System.out.println("[ERROR] go to edge or put tele to house in inv.");
                kill();
            }
            while(Objects.findNearest(20,4525).length == 0){
                Inventory.find("Teleport to house")[0].click("Break");
                // TODO fix this shite
                sleep(1200 + rand.nextInt(1200));
            }
            while(Objects.findNearest(20,"Portal").length != 0){
                System.out.println("going to edge");
                while(Objects.findNearest(10,"Amulet of glory")[0].getPosition().distanceTo(Player.getPosition())>1){
                    Walking.walkTo(Objects.findNearest(10,"Amulet of glory")[0].getPosition());
                }
                Objects.findNearest(10,"Amulet of glory")[0].click("Edgeville");
                sleep(2000 + rand.nextInt(1200));
            }
        }
        if (Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) > 12 ){
            System.out.println("Topping up health");
            int damage=Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS);
            int numberOfLobsters=damage/12;
            Banking.openBank();
            while (!(Banking.isBankScreenOpen()&&Banking.isBankLoaded())) {
                sleep(100 + rand.nextInt(150));
            }
            Banking.depositAllExcept("Teleport to house","Small pouch","Medium pouch");
            Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_1);
            // Obtain a number between [0 - 7].
            int n = rand.nextInt(3);
            Banking.withdraw(numberOfLobsters + 1 + n,"Lobster");
            sleep(150 + rand.nextInt(150));
            Banking.close();
            while (Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) > 0 ){
                while (Inventory.find("Lobster").length > 0) {
                    Inventory.find("Lobster")[0].click("Eat");
                    sleep(450 + rand.nextInt(150));
                    if((Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS)) == 0){
                        break;
                    }
                }
            }
        }
        if( Inventory.getCount("Teleport to house") < 20 ){
            System.out.println("Grabbing home teleports");
            Banking.openBank();
            while (!(Banking.isBankScreenOpen()&&Banking.isBankLoaded())) {
                sleep(100 + rand.nextInt(150));
            }
            // Obtain a number between [10 - 20].
            int n = 10+rand.nextInt(10);
            Banking.withdraw(n,"Teleport to house");
            sleep(150 + rand.nextInt(200));
            System.out.println("Getting rid of shite");
            Banking.depositAllExcept("Teleport to house","Small pouch","Medium pouch");
        }
        checkRunePouches();
        if( Inventory.getCount("Cosmic rune") > 0 ){
            System.out.println("banking money dolla ");
            if(! Banking.isBankScreenOpen()){
                Banking.openBank();
                while (!(Banking.isBankScreenOpen()&&Banking.isBankLoaded())) {
                    sleep(100 + rand.nextInt(150));
                }
            }
            Banking.depositAllExcept("Teleport to house","Small pouch","Medium pouch");
        }
        if( !Inventory.isFull() ){
            System.out.println("Grabbing rune essences");
            if(! Banking.isBankScreenOpen()){
                Banking.openBank();
                while (!(Banking.isBankScreenOpen()&&Banking.isBankLoaded())) {
                    sleep(100 + rand.nextInt(150));
                }
            }
            System.out.println("Getting rid of shite");
            Banking.depositAllExcept("Teleport to house","Small pouch","Medium pouch");
            Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_ALL);
            Banking.find("Pure essence")[0].click("Withdraw-All");
        }
        while (Inventory.find("Pure essence").length==0) {
            sleep(100 + rand.nextInt(100));
        }
        while (Inventory.find("Pure essence").length==25) {
            Inventory.find("Small pouch")[0].click("Fill");
            sleep(450 + rand.nextInt(100));
        }
        while (Inventory.find("Pure essence").length==22) {
            Inventory.find("Medium pouch")[0].click("Fill");
            sleep(450 + rand.nextInt(100));
        }
        if( !Inventory.isFull() ){
            System.out.println("Grabbing rune essences");
            if(! Banking.isBankScreenOpen()){
                Banking.openBank();
                while (!(Banking.isBankScreenOpen()&&Banking.isBankLoaded())) {
                    sleep(100 + rand.nextInt(150));
                }
            }
            Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_ALL);
            Banking.find("Pure essence")[0].click("Withdraw-All");
        }
        while (Banking.isBankScreenOpen()){
            Banking.close();
            sleep(100 + rand.nextInt(150));
        }
        sleep(rand.nextInt(500));
        if((Game.getRunEnergy()>50)&&(Game.getRunEnergy()<75)){
            if(1<rand.nextInt(2)){
                System.out.println("Turning run on");
                Options.setRunOn(true);
            }
        }
        if(Game.getRunEnergy()>=75){
            System.out.println("Turning run on");
            Options.setRunOn(true);
        }
        MAIN_SCRIPT_STATE = ScriptStates.RUNNING_TO_WILDY;

    }
    private void getToAbyss() {
        //TODO <-- NAVIGATING EDGEVILLE -->
        System.out.println("Getting out of edgeville");
        while(Player.getPosition().getY() < 3518){
            while(!(3104 < Player.getPosition().getX() && Player.getPosition().getX() < 3119)){
                RSTile player_loc=Player.getPosition();
                int newX=player_loc.getX()+7+rand.nextInt(7);
                RSTile dest = new RSTile(newX, player_loc.getY()+7+rand.nextInt(7), 0);
                Walking.walkTo(dest);
                while (Player.isMoving()) {
                    sleep(100 + rand.nextInt(100));
                    if(Player.getPosition().distanceTo(dest) < 3){
                        break;
                    }
                }
            }
            RSTile player_loc=Player.getPosition();
            int newY=player_loc.getY()+7+rand.nextInt(7);
            RSTile dest = new RSTile(player_loc.getX() , newY, 0);
            Walking.walkTo(dest);
            while (Player.isMoving()) {
                sleep(100 + rand.nextInt(100));
                if(Player.getPosition().distanceTo(dest) < 3){
                    break;
                }
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
                if (hasDied()){
                    System.out.println("[WARNING] We've died.");
                    MAIN_SCRIPT_STATE = ScriptStates.BANKING;
                    return;
                }
                RSTile dest = new RSTile(newX , player_loc.getY()+rand.nextInt(7), 0);
                Walking.walkTo(dest);
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    if(Player.getPosition().distanceTo(dest) < 3){
                        sleep(100 + rand.nextInt(100));
                    }
                }
            }
            RSTile player_loc=Player.getPosition();
            int newY=player_loc.getY()+7+rand.nextInt(7);
            if(newY >= 3560){
                RSTile dest = new RSTile(player_loc.getX() , newY, 0);
                Walking.walkTo(dest);
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    if(Player.getPosition().distanceTo(dest) < 3){
                        sleep(100 + rand.nextInt(100));
                    }
                }
                break;
            }else {
                RSTile dest = new RSTile(player_loc.getX(), newY, 0);
                Walking.walkTo(dest);
                while (Player.isMoving()) {
                    if(Player.getPosition().distanceTo(dest) < 3){
                        sleep(100 + rand.nextInt(100));
                    }
                }
            }
            if (hasDied()){
                System.out.println("[WARNING] We've died.");
                MAIN_SCRIPT_STATE = ScriptStates.BANKING;
                return;
            }
        }
        System.out.println("finding mage");
        while(NPCs.findNearest("Mage of Zamorak").length == 0 ){
            Walking.walkTo(new RSTile(3105 + rand.nextInt(3), 3560 + rand.nextInt(3), 0));
            sleep(100 + rand.nextInt(100));
            while (Player.isMoving()) {
                sleep(800 + rand.nextInt(300));
                if(NPCs.findNearest("Mage of Zamorak").length == 0){
                    break;
                }
            }
        }
        if (hasDied()){
            System.out.println("[WARNING] We've died.");
            MAIN_SCRIPT_STATE = ScriptStates.BANKING;
            return;
        }
        System.out.println("talking mage");
        while(NPCs.findNearest("Mage of Zamorak").length != 0 ){
            Walking.walkTo(NPCs.findNearest("Mage of Zamorak")[0].getPosition());
            sleep(100 + rand.nextInt(100));
            while (Player.isMoving()) {
                sleep(800 + rand.nextInt(300));
                if (NPCs.findNearest("Mage of Zamorak")[0].isClickable()){
                    NPCs.findNearest("Mage of Zamorak")[0].click("Teleport");
                    sleep(500);
                }
            }
            sleep(250);
            if(NPCs.findNearest("Mage of Zamorak").length == 0){
                break;
            }
            if (NPCs.findNearest("Mage of Zamorak")[0].isClickable()){
                NPCs.findNearest("Mage of Zamorak")[0].click("Teleport");
                sleep(500);
            }
            sleep(500);
            if(NPCs.findNearest("Mage of Zamorak").length == 0){
                break;
            }
        }
        if (hasDied()){
            System.out.println("[WARNING] We've died.");
            MAIN_SCRIPT_STATE = ScriptStates.BANKING;
            return;
        }
        MAIN_SCRIPT_STATE = ScriptStates.NAVIGATING_ABYSS;
    }
    private void navigateAbyss() {
        boolean Eyes,Gap,Rock;
        Eyes = Gap = Rock = false;
        while( !(new RSArea( new RSTile(3024,4817,0),new RSTile(3054,4846,0))).contains(Player.getPosition())){
            sleep(250 + rand.nextInt(150));
            if(Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) < 15 ){
                System.out.println("[WARNING] Health is dangerously low. Teleporting home!");
                while(Objects.findNearest(20,"Portal").length == 0){
                    System.out.println("teleporting home");
                    Inventory.find("Teleport to house")[0].click("Break");
                    sleep(1200 + rand.nextInt(1200));
                }
                MAIN_SCRIPT_STATE = ScriptStates.BANKING;
                return;
            }
            if (hasDied()){
                System.out.println("[WARNING] We've died.");
                MAIN_SCRIPT_STATE = ScriptStates.BANKING;
                return;
            }
            if(Objects.findNearest(10,"Eyes").length != 0 && (!Gap && !Rock)){
                System.out.println("[INFO] Using Eyes");
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
                System.out.println("[INFO] Using Gap");
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
                System.out.println("[INFO] Using Rock");
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
                System.out.println("[INFO] Not close to anything I know how to get through.");
                System.out.println("[INFO] Trying to walk around the circle a bit to see if I can bump into something.");
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
                Walking.walkTo(new RSTile(X,Y,0));
                sleep(250 + rand.nextInt(150));
                while (Player.isMoving()){
                    sleep(250 + rand.nextInt(150));
                }
            }
        }
        if (hasDied()){
            System.out.println("[WARNING] We've died.");
            MAIN_SCRIPT_STATE = ScriptStates.BANKING;
            return;
        }
        while(Objects.findNearest(15, rune + " rift").length == 0 ){
            System.out.println("[INFO] Can't see the " + rune + " rift. Walking around a little.");
            RSTile dest = new RSTile(3039 ,4830 + rand.nextInt(10), 0);
            Walking.walkTo(dest);
            while (Player.isMoving()){
                sleep(100 + rand.nextInt(100));
                if(Player.getPosition().distanceTo(dest) < 3){
                    break;
                }
            }
        }
        while (Inventory.find(5511).length != 0){
            System.out.println("[INFO] Reparing my Medium pouch.");
            while (NPCs.find("Dark mage")[0].getPosition().distanceTo(Player.getPosition()) > 3){
                Walking.walkTo(NPCs.find("Dark mage")[0].getPosition());
                sleep(100 + rand.nextInt(100));
            }
            NPCs.find("Dark mage")[0].click("Repairs");
        }
        while(Player.getPosition().distanceTo(new RSTile(3032,4842,0)) > 2){
            if((Objects.findNearest(15, rune + " rift").length != 0 )){
                Walking.walkTo(Objects.findNearest(15, rune + " rift")[0].getPosition());
                while (Player.isMoving()){
                    sleep(100+rand.nextInt(100));
                }
                break;
            }
            if(!Player.isMoving()){
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
                Walking.walkTo(new RSTile(X,Y,0));
                sleep(250 + rand.nextInt(150));
            }
            System.out.println("[INFO] Walking closer to the " + rune + " Rift.");
            Walking.walkTo(new RSTile(3032,4842,0));
            while(Player.isMoving()){
                if(Player.getPosition().distanceTo(Objects.findNearest(20, rune + " rift")[0].getPosition()) < 5){
                    break;
                }
                sleep(100 + rand.nextInt(150));
            }
            if(Player.getPosition().distanceTo(Objects.findNearest(20, rune + " rift")[0].getPosition()) < 5){
                break;
            }
        }
        while((Objects.findNearest(15, rune + " rift").length != 0 )){
            System.out.println("[INFO] Entering " + rune + " Rift.");
            if(!Objects.findNearest(15, rune + " rift")[0].getPosition().isClickable()) {
                if((Objects.findNearest(15, rune + " rift").length == 0 )){
                    break;
                }
                Walking.walkTo(Objects.findNearest(15, rune + " rift")[0].getPosition());
                sleep(100 + rand.nextInt(100));
                while (Player.isMoving()) {
                    sleep(100 + rand.nextInt(100));
                }
            }
            if(Objects.findNearest(15, rune + " rift").length != 0){
                Objects.findNearest(15, rune + " rift")[0].click("Exit-through");
                sleep(100 + rand.nextInt(100));
            }
        }
        MAIN_SCRIPT_STATE = ScriptStates.CRAFTING_RUNES;
    }
    private void craftRunes(){
        // TODO craft the runes
        while (Objects.findNearest(20, "Altar").length == 0){
            sleep(100 + rand.nextInt(150));
        }
        while(Inventory.find("Pure essence").length != 0){
            System.out.println("crafting");
            while(Player.getAnimation() != 791 ) {
                Objects.findNearest(20, "Altar")[0].click("Craft-rune");
                sleep(500 + rand.nextInt(1200));
            }
            sleep(100 + rand.nextInt(1200));
        }
        runesMade=runesMade+25;
        while(Player.getAnimation() == 791 ) {
            sleep(150 + rand.nextInt(250));
        }
        while (Inventory.find(7936).length != 3){
            Inventory.find("Small pouch")[0].click("Empty");
            sleep(650 + rand.nextInt(100));
        }
        while (Inventory.find(7936).length != 9){
            Inventory.find("Medium pouch")[0].click("Empty");
            sleep(650 + rand.nextInt(100));
        }
        while(Inventory.find("Pure essence").length != 0){
            while(Player.getAnimation() != 791 ) {
                System.out.println("crafting");
                Objects.findNearest(20, "Altar")[0].click("Craft-rune");
                sleep(550 + rand.nextInt(1200));
                if(Inventory.find("Pure essence").length ==0 ){
                    break;
                }
            }
            sleep(100 + rand.nextInt(1200));
        }
        runesMade=runesMade+9;
        while(Player.getAnimation() == 791 ) {
            sleep(150 + rand.nextInt(250));
        }
        MAIN_SCRIPT_STATE = ScriptStates.BANKING;

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
    private void setRune(){
        if(Skills.getCurrentLevel(Skills.SKILLS.RUNECRAFTING) >= 44){
            rune = "Nature";
            gpPerRune = 244;
        }else{
            rune = "Cosmic";
            gpPerRune = 172;
        }
    }

    private static String rune;
    private int gpPerRune;

    private static Random rand = new Random();
    //BANKING,RUNNING_TO_WILDY,RUNNING_TO_ZAMMY_MAGE,TELEPORTING_TO_ABYSS,NAVIGATING_ABYSS,CRAFTING_RUNES,INIT,GOOD,BAD
    @Override
    public void run() {
        setRune();
        setRandomSolverState(false);
        System.out.println("[INFO] >>>>> New session");
        MAIN_SCRIPT_STATE = ScriptStates.BANKING;
        System.out.println("[INFO] Starting Nanny.");
        Nanny nanny = new Nanny();
        nanny.start();
        // to test nanny
        //sleep(40000);
        makeFile();
        writeFile("Started");
        while (isRunning) {
            switch (MAIN_SCRIPT_STATE) {
                case BANKING:
                    bank();
                    break;
                case RUNNING_TO_WILDY:
                    getToAbyss();
                    break;
                case RUNNING_TO_ZAMMY_MAGE:
                    break;
                case TELEPORTING_TO_ABYSS:
                    break;
                case NAVIGATING_ABYSS:
                    navigateAbyss();
                    break;
                case CRAFTING_RUNES:
                    craftRunes();
                    break;
                case BAD:
                    isRunning = false;
                    break;
                default:
                    isRunning = false;
                    break;
            }
        }
    }

    @Override
    public void onPaint(Graphics g)  {
        long timeRan = System.currentTimeMillis() - startTime;
        g.setFont(font);
        g.setColor(new Color(0, 200, 0));
        g.drawString("Runtime: " + Timing.msToString(timeRan), 300, 360);
        g.drawString("Runes made: " + runesMade, 300, 380);
        g.setColor(new Color(255, 235, 0));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);
        g.drawString("gp made: " +  gpPerRune*runesMade, 300, 400);
        g.drawString("GP/H: " +  df.format((gpPerRune*runesMade)/(((timeRan/1000.0)/60.0)/60.0)) , 50, 400 );
        g.setColor(new Color(200, 0, 0));
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        g.drawString("£ made: " +  df.format((gpPerRune*runesMade)/1000000.00), 300, 420);
        g.drawString(" £/H: " +  df.format(((gpPerRune*runesMade)/1000000.00)/(((timeRan/1000.0)/60.0)/60.0)), 50, 420);
        g.setColor(new Color(0, 9, 200));
        g.drawString("Nanny reports: " +  Nanny.happiness , 300, 440 );
        g.drawString("State: " +  MAIN_SCRIPT_STATE , 50, 440 );
        g.setColor(new Color(154, 0, 200));
        df.setMaximumFractionDigits(0);
        g.drawString("XP: " +  (Skills.getXP(Skills.SKILLS.RUNECRAFTING) - startXP) , 300, 460 );
        g.drawString("XP/H: " +  df.format((Skills.getXP(Skills.SKILLS.RUNECRAFTING) - startXP)/(((timeRan/1000.0)/60.0)/60.0)) , 50, 460 );
    }

    private static final long startTime = System.currentTimeMillis();
    private static final int startXP= Skills.getXP(Skills.SKILLS.RUNECRAFTING);
    private static int runesMade=0;
    Font font = new Font("Verdana", Font.BOLD, 14);
}

