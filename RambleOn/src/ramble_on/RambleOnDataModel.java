package ramble_on;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.Sprite;
import mini_game.SpriteType;
import static ramble_on.RambleOnSettings.*;
import ramble_on_account.Account;
import ramble_on_account.AccountManager;
import ramble_on_account.RegionStats;
import ramble_on_events.GoBackHandler;
import ramble_on_events.GoForwardHandler;
import ramble_on_events.ModeClickHandler;
import ramble_on_sprites.MapSprite;
import ramble_on_sprites.RegionSprite;
import region_data.RegionDataLoader;
import region_data.RegionDataManager;
import region_data_exceptions.*;
import xml_utilities.InvalidXMLFileFormatException;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Leixiang
 */
public class RambleOnDataModel extends MiniGameDataModel {
    
    private HashMap<String, ArrayList<Point2D.Double>> pixels;
    private AccountManager accountManager;
    private RegionDataLoader regionDataLoader;
    private GameScreenState screenState;
    private RegionDataManager regionDataManager;
    private Sprite currentMap;
    private LinkedList<String> redSubRegions;
    private LinkedList<RegionSprite> subRegionStack;
    private GameModeState gameMode;
    //STATS
    private int incorrectGuesses;
    private GregorianCalendar startTime;
    private GregorianCalendar winEndingTime;
    private File currentDirectory;
    private LinkedList<String> badSubRegions;
    private LinkedList<String> dirList;
    private String wholeName;
    private String mode;

    /**
     *
     *
     */
    public RambleOnDataModel() {
        accountManager = new AccountManager();
        accountManager.load();
        regionDataLoader = new RegionDataLoader(new File(xmlSchemaNameAndPath));
        regionDataManager = new RegionDataManager();
        screenState = GameScreenState.WELCOME_SCREEN;
        subRegionStack = new LinkedList();
        redSubRegions = new LinkedList();
        badSubRegions = new LinkedList();
        dirList = new LinkedList();
    }
    
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    public RegionDataManager getRegionDataManager() {
        return regionDataManager;
    }
    
    public GameScreenState getScreenState() {
        return screenState;
    }
    
    public boolean isWelcomeScreen() {
        return screenState == GameScreenState.WELCOME_SCREEN;
    }
    
    public boolean isAccountScreen() {
        return screenState == GameScreenState.ACCOUNT_SCREEN;
    }
    
    public boolean isAccountCreationScreen() {
        return screenState == GameScreenState.ACCOUNT_CREATION_SCREEN;
    }
    
    public boolean isCurrentAccountScreen() {
        return screenState == GameScreenState.CURRENT_ACCOUNT_SCREEN;
    }
    
    public boolean isGamePlayScreen() {
        return screenState == GameScreenState.GAME_PLAY_SCREEN;
    }
    
    public boolean isGameEndScreen() {
        return screenState == GameScreenState.GAME_END_SCREEN;
    }
    
    public boolean isSubRegionMode() {
        return gameMode == GameModeState.SUB_REGION_MODE;
    }
    
    public boolean isFlagMode() {
        return gameMode == GameModeState.FLAG_MODE;
    }
    
    public boolean isLeaderMode() {
        return gameMode == GameModeState.LEADER_MODE;
    }
    
    public boolean isCapitalMode() {
        return gameMode == GameModeState.CAPITAL_MODE;
    }
    
    public void createAccount(RambleOn game, String accountName) {
        if (!accountManager.hasAccount(accountName.trim())) {
            if (accountName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(game.getCanvas(), "Error: invalid input because user name is empty.");
            } else {
                Account newAccount = new Account(accountName);
                accountManager.addAccount(accountName, newAccount);
                accountManager.setCurrentAccount(newAccount);
                
                game.setACScreenButtonsInvisible();
                game.clearTextInAccountNameField();
                
                screenState = GameScreenState.CURRENT_ACCOUNT_SCREEN;
                
                game.clearRegionStats();
                game.initTree();
                game.setCurrentAccountGUIVisible();
                game.putCAGUIControlsInWindow();
                game.initCurrentAccountHandlers();
                accountManager.save();
            }
        } else {
            JOptionPane.showMessageDialog(game.getCanvas(), "Account name is already existed.");
        }
    }
    
    @Override
    public void checkMousePressOnSprites(MiniGame game, int x, int y) {
    }
    
    @Override
    public void reset(MiniGame game) {
        if (regionDataManager.getRegionAnthem() == null) {
            ((RambleOn) game).getAudio().stop(STANDARD_VICTORY_SONG);
        } else {
            regionDataManager.getRegionAnthem().stop();
        }
        ((RambleOn) game).getAudio().play(BACKGROUND_MUSIC, false);
        
        winEndingTime = null;
        startTime = new GregorianCalendar();
        incorrectGuesses = 0;
        subRegionStack.clear();
        redSubRegions.clear();
        dirList.clear();
        wholeName = "";
        
        File worldDirectory = new File(THE_WORLD_PATH);
        File tempFile = currentDirectory;
        while (!worldDirectory.getName().equalsIgnoreCase(tempFile.getName())) {
            dirList.add(tempFile.getName());
            tempFile = tempFile.getParentFile();
        }
        dirList.add(worldDirectory.getName());
        Collections.reverse(dirList);
        for (int x = 0; x < dirList.size(); x++) {
            if (x != dirList.size() - 1) {
                wholeName = wholeName + dirList.get(x) + "_";
            } else {
                wholeName = wholeName + dirList.get(x);
            }
        }
        RambleOn.getRambleOnApp().getGUIDecor().get(BACKGROUND_TYPE).setState(BACKGROUND1_STATE);
        makeMapSprite();
    }

    /**
     * Called each frame, this thread already has a lock on the data. This
     * method updates all the game sprites as needed.
     *
     * @param game the game in progress
     */
    @Override
    public void updateAll(MiniGame game) {
        for (Sprite s : subRegionStack) {
            s.update(game);
        }
        if (!subRegionStack.isEmpty()) {
            Sprite bottomOfStack = subRegionStack.get(0);
            if (isSubRegionMode() || isCapitalMode()) {
                if (bottomOfStack.getY() == FIRST_REGION_Y_IN_REGION_STACK) {
                    for (Sprite s : subRegionStack) {
                        s.setVy(0);
                    }
                }
            } else {
                int firstRegionYInStack = (int) (GAME_HEIGHT - bottomOfStack.getAABBheight());
                if ((int) (bottomOfStack.getY()) == (int) (firstRegionYInStack)) {
                    for (Sprite s : subRegionStack) {
                        s.setVy(0);
                    }
                }
            }
        }
    }
    
    @Override
    public void updateDebugText(MiniGame game) {
    }
    
    public void goForwardOneScreen(RambleOn game) {
        if (isWelcomeScreen()) {
            RambleOn.getRambleOnApp().getGUIButtons().get(WELCOME_TYPE).setActionListener(null);
            RambleOn.getRambleOnApp().getGUIButtons().get(WELCOME_TYPE).setState(INVISIBLE_STATE);
            screenState = GameScreenState.ACCOUNT_SCREEN;
            game.setAccountsScreenGUIVisible();
            game.putASGUIControlsInWindow();
        } else if (isAccountScreen()) {
            game.setAccountsScreenGUIInvisible();
            screenState = GameScreenState.CURRENT_ACCOUNT_SCREEN;
            game.clearRegionStats();
            game.initTree();
            game.setCurrentAccountGUIVisible();
            game.putCAGUIControlsInWindow();
            game.initCurrentAccountHandlers();
        } else if (isCurrentAccountScreen()) {
            disableCurrentAccountButtons();
            game.setCurrentAccountGUIInvisible();
            game.removeTree();
            RambleOn.getRambleOnApp().addRambleOnEventRelayer();
            screenState = GameScreenState.GAME_PLAY_SCREEN;
            enableAllModes(true);
            loadTheWorld();
        }
    }
    
    public void goBackOneScreen(RambleOn game) {
        if (isAccountCreationScreen()) {
            game.clearTextInAccountNameField();
            game.setACScreenButtonsInvisible();
            screenState = GameScreenState.ACCOUNT_SCREEN;
            game.setAccountsScreenGUIVisible();
            game.putASGUIControlsInWindow();
        } else if (isCurrentAccountScreen()) {
            game.removeTree();
            game.setCurrentAccountGUIInvisible();
            disableCurrentAccountButtons();
            screenState = GameScreenState.ACCOUNT_SCREEN;
            game.setAccountsScreenGUIVisible();
            game.putASGUIControlsInWindow();
        } else if (isGamePlayScreen()) {
            gameMode = null;
            super.endGameAsLoss();
            RambleOn.getRambleOnApp().removeRambleOnEventRelayer();
            enableAllModes(false);
            screenState = GameScreenState.CURRENT_ACCOUNT_SCREEN;
            enableCurrentAccountButtons();
            game.initTree();
            game.setCurrentAccountGUIVisible();
        } else if (isGameEndScreen()) {
            RambleOn.getRambleOnApp().getGUIButtons().get(EXIT_TYPE).setActionListener(null);
            RambleOn.getRambleOnApp().getGUIButtons().get(EXIT_GAME_TYPE).setActionListener(new ModeClickHandler(this));
            screenState = GameScreenState.GAME_PLAY_SCREEN;
            game.addRambleOnEventRelayer();
            reset(RambleOn.getRambleOnApp());
            changeBadSubRegionsColor(Color.WHITE);
        }
    }
    
    public void goToAccountCreationScreen(RambleOn game) {
        if (isAccountScreen()) {
            screenState = GameScreenState.ACCOUNT_CREATION_SCREEN;
            game.setACScreenButtonsVisible();
            game.setAccountsScreenGUIInvisible();
            game.putACGUIControlsInWindow();
        }
    }
    
    private void loadTheWorld() {
        importRegionData(THE_WORLD_PATH);
        importRegionFlags(THE_WORLD_PATH);
        importRegionLeaderImgs(THE_WORLD_PATH);
        enableCapitalMode(false);
        badSubRegions.clear();
        findBadSubRegions();
        changeBadSubRegionsColor(Color.white);
    }
    
    private void importRegionData(String regionDirectory) {
        try {
            regionDataLoader.loadRegionData(regionDirectory, regionDataManager);
            currentDirectory = new File(regionDirectory);
            makeMapSprite();
            enableSubRegionMode(true);
        } catch (InvalidRGBValuesException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: Unable to load the region because "
            //       + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableSubRegionMode(false);
        } catch (DirectoryIsMissingException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: Unable to load the region because "
            //      + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableSubRegionMode(false);
        } catch (FileIsMissingException ex) {
            // JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: Unable to load the region because "
            //       + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableSubRegionMode(false);
        } catch (InvalidXMLFileFormatException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: Unable to load the region because "
            //      + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableSubRegionMode(false);
        } catch (BadXMLDataException ex) {
            // JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: Unable to load the region because "
            //       + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableSubRegionMode(false);
        }
    }
    
    private void importCapitalNames() {
        try {
            regionDataLoader.loadCapitalNames(regionDataManager);
            enableCapitalMode(true);
        } catch (BadXMLDataException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: The capital mode will be disabled because "
            //      + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableCapitalMode(false);
        }
    }
    
    private void importRegionFlags(String regionDirectory) {
        try {
            regionDataLoader.loadRegionFlags(regionDirectory, regionDataManager);
            enableFlagMode(true);
        } catch (ImageNotFoundException ex) {
            // JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error:  The flag mode will be disabled because "
            //           + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableFlagMode(false);
        } catch (ImageInvalidDimensionException ex) {
            // JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error:  The flag mode will be disabled because "
            //       + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableFlagMode(false);
        }
    }
    
    private void importRegionLeaderImgs(String regionDirectory) {
        try {
            regionDataLoader.loadRegionLeaderImgs(regionDirectory, regionDataManager);
            regionDataLoader.loadLeaderNames(regionDataManager);
            enableLeaderMode(true);
        } catch (ImageNotFoundException ex) {
            // JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: The leader mode will be disabled because "
            //       + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableLeaderMode(false);
        } catch (ImageInvalidDimensionException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: The leader mode will be disabled because "
            //      + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableLeaderMode(false);
        } catch (BadXMLDataException ex) {
            //JOptionPane.showMessageDialog(RambleOn.getRambleOnApp().getCanvas(), "Error: The leader mode will be disabled because "
            //      + ex.toString() + "\nPlease contact Game Developer Leixiang Wu for this issue", GAME_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            enableLeaderMode(false);
        }
    }
    
    public void changeBadSubRegionsColor(Color color) {
        for (String regionName : badSubRegions) {
            changeSubRegionColorOnMap(regionName, color);
        }
    }
    
    private void findBadSubRegions() {
        Iterator<String> subRegions = regionDataManager.getSubRegions().iterator();
        String tempDirectory, regionName;
        RegionDataManager testManager = new RegionDataManager();
        RegionDataLoader loaderTest = new RegionDataLoader(new File(xmlSchemaNameAndPath));
        while (subRegions.hasNext()) {
            regionName = subRegions.next();
            try {
                tempDirectory = currentDirectory.getAbsoluteFile() + File.separator + regionName;
                loaderTest.loadRegionData(tempDirectory, testManager);
                testManager.clearRegionData();
            } catch (InvalidRGBValuesException | DirectoryIsMissingException | FileIsMissingException |
                    InvalidXMLFileFormatException | BadXMLDataException ex) {
                badSubRegions.add(regionName);
            }
        }
    }
    
    private void makeMapSprite() {
        SpriteType sT;
        BufferedImage img, tempImg;
        int x, y, vX, vY;
        MapSprite mS;
        
        sT = new SpriteType(MAP_TYPE);
        tempImg = regionDataManager.getRegionMap();
        
        img = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(tempImg, 0, 0, null);
        
        x = 0;
        y = 0;
        vX = 0;
        vY = 0;
        sT.addState(DEFAULT_STATE, img);
        mS = new MapSprite(sT, x, y, vX, vY, DEFAULT_STATE);
        
        setMapSprite(mS);

        // LET'S RECORD ALL THE PIXELS
        pixels = new HashMap();
        
        Iterator<String> iterator = regionDataManager.getSubRegions().iterator();
        while (iterator.hasNext()) {
            pixels.put(iterator.next(), new ArrayList());
        }
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i, j);
                Color c = new Color(rgb);
                if (regionDataManager.getSubRegionMappedToColor(c) != null) {
                    String subRegion = regionDataManager.getSubRegionMappedToColor(c);
                    ArrayList<Point2D.Double> subRegionPixels = pixels.get(subRegion);
                    subRegionPixels.add(new Point2D.Double(i, j));
                }
            }
        }
    }
    
    public boolean isClickOnMap(int x, int y) {
        return currentMap.containsPoint(x, y);
    }
    
    public void respondToLeftClick(int x, int y) {
        
        SpriteType mapType = currentMap.getSpriteType();
        BufferedImage img = mapType.getStateImage(currentMap.getState());
        int rgb = img.getRGB(x, y);
        Color pixelColor = new Color(rgb);
        
        String clickedSubRegion = regionDataManager.getSubRegionMappedToColor(pixelColor);
        
        if (clickedSubRegion == null) {
            return;
        }
        String tempDirectory = currentDirectory.getAbsolutePath() + File.separator + clickedSubRegion;
        importRegionData(tempDirectory);
        importRegionFlags(tempDirectory);
        importRegionLeaderImgs(tempDirectory);
        importCapitalNames();
        badSubRegions.clear();
        findBadSubRegions();
        changeBadSubRegionsColor(Color.WHITE);
    }
    
    public void respondToRightClick() {
        if (regionDataManager.getRegionName().equalsIgnoreCase("The World")) {
            return;
        }
        currentDirectory = new File(currentDirectory.getParent());
        importRegionData(currentDirectory.getAbsolutePath());
        importRegionFlags(currentDirectory.getAbsolutePath());
        importRegionLeaderImgs(currentDirectory.getAbsolutePath());
        importCapitalNames();
        badSubRegions.clear();
        findBadSubRegions();
        changeBadSubRegionsColor(Color.WHITE);
    }
    
    public void respondToMapClick(int x, int y) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
        // PLAYER CLICKED NO THE CORRECT SUBREGION
        SpriteType mapType = currentMap.getSpriteType();
        BufferedImage img = mapType.getStateImage(currentMap.getState());
        int rgb = img.getRGB(x, y);
        Color pixelColor = new Color(rgb);
        String clickedSubRegion = regionDataManager.getSubRegionMappedToColor(pixelColor);
        if ((clickedSubRegion == null) || (subRegionStack.isEmpty())) {
            return;
        }
        if (clickedSubRegion.equals(subRegionStack.get(0).getRegionName())) {
            // YAY, CORRECT ANSWER
            RambleOn.getRambleOnApp().getAudio().play(SUCCESS, false);

            // TURN THE TERRITORY GREEN
            changeSubRegionColorOnMap(clickedSubRegion, Color.GREEN);

            // REMOVE THE BOTTOM ELEMENT FROM THE STACK
            subRegionStack.removeFirst();

            // AND LET'S CHANGE THE RED ONES BACK TO THEIR PROPER COLORS
            for (String redRegionName : redSubRegions) {
                Color subRegionColor = regionDataManager.getColorMappedToSubRegion(redRegionName);
                changeSubRegionColorOnMap(redRegionName, subRegionColor);
            }
            redSubRegions.clear();
            
            startStackSpritesMovingDown();
            
            if (subRegionStack.isEmpty()) {
                endGame();
            }
        } else {
            if (!redSubRegions.contains(clickedSubRegion)) {
                // BOO WRONG ANSWER
                RambleOn.getRambleOnApp().getAudio().play(FAILURE, false);
                
                incorrectGuesses++;

                // TURN THE TERRITORY TEMPORARILY RED
                changeSubRegionColorOnMap(clickedSubRegion, Color.RED);
                redSubRegions.add(clickedSubRegion);
            }
        }
    }
    
    private void endGame() {
        RambleOn game = RambleOn.getRambleOnApp();
        game.getAudio().stop(BACKGROUND_MUSIC);
        try {
            regionDataLoader.loadRegionAnthem(currentDirectory.getAbsolutePath(), regionDataManager);
            regionDataManager.getRegionAnthem().setTickPosition(0);
            regionDataManager.getRegionAnthem().start();
        } catch (AnthemIsMissingException ex) {
            game.getAudio().play(STANDARD_VICTORY_SONG, false);
        }
        
        winEndingTime = new GregorianCalendar();
        this.endGameAsWin();
        
        refreshPlayerStats();
        accountManager.save();
        
        screenState = GameScreenState.GAME_END_SCREEN;
        gameMode = null;
        
        game.getGUIButtons().get(EXIT_GAME_TYPE).setActionListener(null);
        
        game.getGUIDecor().get(WIN_DISPLAY_TYPE).setState(VISIBLE_STATE);
        GoBackHandler gbh = new GoBackHandler(this, game);
        game.getGUIButtons().get(EXIT_TYPE).setActionListener(gbh);
        game.getGUIButtons().get(EXIT_TYPE).setState(VISIBLE_STATE);
        game.removeRambleOnEventRelayer();
    }
    
    private void refreshPlayerStats() {
        System.out.println(wholeName);
        if (gameMode == GameModeState.SUB_REGION_MODE) {
            refreshSubRegionStats();
        } else if (gameMode == GameModeState.FLAG_MODE) {
            refreshFlagStats();
        } else if (gameMode == GameModeState.LEADER_MODE) {
            refreshLeaderStats();
        } else if (gameMode == GameModeState.CAPITAL_MODE) {
            refreshCapitalStats();
        }
    }
    
    private void refreshSubRegionStats() {
        Account currentAccount = accountManager.getCurrentAccount();
        if (!currentAccount.isRegionPlayed(wholeName)) {
            currentAccount.addRegionPlayed(wholeName);
        }
        RegionStats oldStats = currentAccount.getSubRegionModeStats(wholeName);
        int newScore = calculateScore();
        int newTime = (int) ((winEndingTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000L);
        if (oldStats == null) {
            int numberOfTimesPlayed = 1;
            RegionStats newStats = new RegionStats(numberOfTimesPlayed, newScore, (int) newTime);
            currentAccount.addSubRegionModeStats(wholeName, newStats);
        } else {
            oldStats.setNumberOfTimesPlayed(oldStats.getNumberOfTimesPlayed() + 1);
            if (oldStats.getFastestTime() > newTime) {
                oldStats.setFastestTime(newTime);
            }
            if (oldStats.getHighestScore() < newScore) {
                oldStats.setHighestScore(newScore);
            }
        }
    }
    
    private void refreshFlagStats() {
        
        Account currentAccount = accountManager.getCurrentAccount();
        if (!currentAccount.isRegionPlayed(wholeName)) {
            currentAccount.addRegionPlayed(wholeName);
        }
        RegionStats oldStats = currentAccount.getFlagModeStats(wholeName);
        int newScore = calculateScore();
        int newTime = (int) ((winEndingTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000L);
        if (oldStats == null) {
            int numberOfTimesPlayed = 1;
            RegionStats newStats = new RegionStats(numberOfTimesPlayed, newScore, (int) newTime);
            currentAccount.addFlagModeStats(wholeName, newStats);
        } else {
            oldStats.setNumberOfTimesPlayed(oldStats.getNumberOfTimesPlayed() + 1);
            if (oldStats.getFastestTime() > newTime) {
                oldStats.setFastestTime(newTime);
            }
            if (oldStats.getHighestScore() < newScore) {
                oldStats.setHighestScore(newScore);
            }
        }
    }
    
    private void refreshLeaderStats() {
        Account currentAccount = accountManager.getCurrentAccount();
        if (!currentAccount.isRegionPlayed(wholeName)) {
            currentAccount.addRegionPlayed(wholeName);
        }
        RegionStats oldStats = currentAccount.getLeaderModeStats(wholeName);
        int newScore = calculateScore();
        int newTime = (int) ((winEndingTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000L);
        if (oldStats == null) {
            int numberOfTimesPlayed = 1;
            RegionStats newStats = new RegionStats(numberOfTimesPlayed, newScore, (int) newTime);
            currentAccount.addLeaderModeStats(wholeName, newStats);
        } else {
            oldStats.setNumberOfTimesPlayed(oldStats.getNumberOfTimesPlayed() + 1);
            if (oldStats.getFastestTime() > newTime) {
                oldStats.setFastestTime(newTime);
            }
            if (oldStats.getHighestScore() < newScore) {
                oldStats.setHighestScore(newScore);
            }
        }
    }
    
    private void refreshCapitalStats() {
        Account currentAccount = accountManager.getCurrentAccount();
        if (!currentAccount.isRegionPlayed(wholeName)) {
            currentAccount.addRegionPlayed(wholeName);
        }
        RegionStats oldStats = currentAccount.getCapitalModeStats(wholeName);
        int newScore = calculateScore();
        int newTime = (int) ((winEndingTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000L);
        if (oldStats == null) {
            int numberOfTimesPlayed = 1;
            RegionStats newStats = new RegionStats(numberOfTimesPlayed, newScore, (int) newTime);
            currentAccount.addCapitalModeStats(wholeName, newStats);
        } else {
            oldStats.setNumberOfTimesPlayed(oldStats.getNumberOfTimesPlayed() + 1);
            if (oldStats.getFastestTime() > newTime) {
                oldStats.setFastestTime(newTime);
            }
            if (oldStats.getHighestScore() < newScore) {
                oldStats.setHighestScore(newScore);
            }
        }
    }
    
    public LinkedList<RegionSprite> getSubRegionStack() {
        return subRegionStack;
    }
    
    public Sprite getMapSprite() {
        return currentMap;
    }
    
    public void setMapSprite(MapSprite mapSprite) {
        currentMap = mapSprite;
    }
    
    public GameModeState getModeState() {
        return gameMode;
    }
    
    public void startSubRegionMode() {
        this.reset(RambleOn.getRambleOnApp());
        gameMode = GameModeState.SUB_REGION_MODE;
        createSubRegionStack();
        beginGame();
        mode = "Subregion Mode";
    }
    
    public void startFlagMode() {
        this.reset(RambleOn.getRambleOnApp());
        gameMode = GameModeState.FLAG_MODE;
        createFlagStack();
        beginGame();
        mode = "Flag Mode";
    }
    
    public void startLeaderMode() {
        this.reset(RambleOn.getRambleOnApp());
        gameMode = GameModeState.LEADER_MODE;
        createLeaderStack();
        beginGame();
        mode = "Leader Mode";
    }
    
    public void startCapitalMode() {
        this.reset(RambleOn.getRambleOnApp());
        gameMode = GameModeState.CAPITAL_MODE;
        createCapitalStack();
        beginGame();
        mode = "Capital Mode";
    }
    
    public String getMode() {
        return mode;
    }
    
    public void createSubRegionStack() {
        SpriteType sT;
        BufferedImage img;
        RegionSprite tS;
        
        sT = new SpriteType(SUB_REGION_TYPE);
        
        LinkedList<String> subRegions = regionDataManager.getSubRegions();
        Collections.shuffle(subRegions);
        int n = 650;
        for (String subRegionName : subRegions) {
            RegionSprite subRegionSprite = new RegionSprite(subRegionName, sT, 900, n, 0, 0, VISIBLE_STATE);
            subRegionStack.add(subRegionSprite);
            n -= 50;
        }
    }
    
    private void createFlagStack() {
        SpriteType sT;
        BufferedImage img, tempImg;
        RegionSprite tS;
        String state;
        
        
        LinkedList<String> subRegions = regionDataManager.getSubRegions();
        Collections.shuffle(subRegions);
        
        int y = 700;
        for (String subRegionName : subRegions) {
            
            sT = new SpriteType(subRegionName + "_Type");
            tempImg = regionDataManager.getFlagImgMappedToSubRegion(subRegionName);
            img = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(tempImg, 0, 0, null);
            sT.addState(DEFAULT_STATE, img);
            y -= tempImg.getHeight();
            RegionSprite subRegionSprite = new RegionSprite(subRegionName, sT, 900, y, 0, 0, DEFAULT_STATE);
            subRegionStack.add(subRegionSprite);
        }
    }
    
    private void createLeaderStack() {
        SpriteType sT;
        BufferedImage img, tempImg;
        
        int y = 700;
        LinkedList<String> subRegions = regionDataManager.getSubRegions();
        Collections.shuffle(subRegions);
        
        for (String subRegionName : subRegions) {
            sT = new SpriteType(subRegionName + "_Type");
            tempImg = regionDataManager.getLeaderMappedToSubRegion(subRegionName);
            img = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(tempImg, 0, 0, null);
            sT.addState(DEFAULT_STATE, img);
            y = y - img.getHeight();
            RegionSprite subRegionSprite = new RegionSprite(subRegionName, sT, 900, y, 0, 0, DEFAULT_STATE);
            subRegionStack.add(subRegionSprite);
        }
    }
    
    private void createCapitalStack() {
        SpriteType sT;
        RegionSprite tS;
        String regionName;
        
        
        LinkedList<String> subRegions = regionDataManager.getSubRegions();
        Collections.shuffle(subRegions);
        int n = 650;
        for (String subRegionName : subRegions) {
            sT = new SpriteType(subRegionName + "_Type");
            RegionSprite subRegionSprite = new RegionSprite(subRegionName, sT, 900, n, 0, 0, VISIBLE_STATE);
            subRegionStack.add(subRegionSprite);
            n -= 50;
        }
    }

    //FOR ENABLE OR DISABLE THE SUBREGION MODE BUTTON.
    public void enableSubRegionMode(boolean enabled) {
        Sprite s;
        s = RambleOn.getRambleOnApp().getGUIButtons().get(SUB_REGION_MODE_TYPE);
        if (enabled == true) {
            s.setState(ENABLED_STATE);
            ModeClickHandler mch = new ModeClickHandler(this);
            s.setActionListener(mch);
        } else {
            //FOR DISABLE THE SUBREGION MODE BUTTON.
            s.setState(DISABLED_STATE);
            s.setActionListener(null);
        }
    }

    //FOR ENABLE OR DISABLE FLAG MODE BUTTON.
    public void enableFlagMode(boolean enabled) {
        Sprite s;
        s = RambleOn.getRambleOnApp().getGUIButtons().get(FLAG_MODE_TYPE);
        if (enabled == true) {
            s.setState(ENABLED_STATE);
            ModeClickHandler mch = new ModeClickHandler(this);
            s.setActionListener(mch);
        } else {
            //FOR DISABLE FLAG MODE BUTTON.
            s.setState(DISABLED_STATE);
            s.setActionListener(null);
        }
    }

    //FOR DISABLE OR DISABLE LEADER MODE BUTTON.
    public void enableLeaderMode(boolean enabled) {
        Sprite s;
        s = RambleOn.getRambleOnApp().getGUIButtons().get(LEADER_MODE_TYPE);
        if (enabled == true) {
            s.setState(ENABLED_STATE);
            ModeClickHandler mch = new ModeClickHandler(this);
            s.setActionListener(mch);
        } else {
            //FOR DISABLE LEADER MODE BUTTON.
            s.setState(DISABLED_STATE);
            s.setActionListener(null);
        }
    }

    //FOR DISABLE OR DISABLE CAPITAL MODE BUTTON.
    public void enableCapitalMode(boolean enabled) {
        Sprite s;
        s = RambleOn.getRambleOnApp().getGUIButtons().get(CAPITAL_MODE_TYPE);
        if (enabled == true) {
            s.setState(ENABLED_STATE);
            ModeClickHandler mch = new ModeClickHandler(this);
            s.setActionListener(mch);
        } else {
            //FOR DISABLE CAPITAL MODE BUTTON.
            s.setState(DISABLED_STATE);
            s.setActionListener(null);
        }
    }
    
    public void enableAllModes(boolean enabled) {
        enableSubRegionMode(enabled);
        enableFlagMode(enabled);
        enableLeaderMode(enabled);
        enableCapitalMode(enabled);
        Sprite back = RambleOn.getRambleOnApp().getGUIButtons().get(LEFT_ARROW_TYPE);
        Sprite exit = RambleOn.getRambleOnApp().getGUIButtons().get(EXIT_GAME_TYPE);
        if (enabled == true) {
            ModeClickHandler mch = new ModeClickHandler(this);
            exit.setActionListener(mch);
            back.setState(ENABLED_STATE);
            GoBackHandler gbh = new GoBackHandler(this, RambleOn.getRambleOnApp());
            back.setActionListener(gbh);
        } else {
            exit.setActionListener(null);
            //FOR DISABLE CAPITAL MODE BUTTON.
            back.setState(DISABLED_STATE);
            back.setActionListener(null);
        }
    }
    
    public void changeSubRegionColorOnMap(String subRegion, Color color) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
        // PLAYER CLICKED NO THE CORRECT SUBREGION
        SpriteType mapType = currentMap.getSpriteType();
        BufferedImage img = mapType.getStateImage(currentMap.getState());
        ArrayList<Point2D.Double> subRegionPixels = pixels.get(subRegion);
        for (Point2D.Double p : subRegionPixels) {
            int rgb = color.getRGB();
            img.setRGB((int) (p.x), (int) (p.y), rgb);
        }
    }
    
    private void startStackSpritesMovingDown() {
        // AND START THE REST MOVING DOWN
        for (Sprite s : subRegionStack) {
            s.setVy(SUB_STACK_VELOCITY);
        }
    }
    
    private void enableCurrentAccountButtons() {
        Sprite button;
        button = RambleOn.getRambleOnApp().getGUIButtons().get(GO_FORWARD_BUTTON_TYPE);
        GoForwardHandler gfh = new GoForwardHandler(this, RambleOn.getRambleOnApp());
        button.setActionListener(gfh);
        
        button = RambleOn.getRambleOnApp().getGUIButtons().get(GO_BACK_BUTTON_TYPE);
        GoBackHandler gbh = new GoBackHandler(this, RambleOn.getRambleOnApp());
        button.setActionListener(gbh);
    }
    
    private void disableCurrentAccountButtons() {
        Sprite button;
        button = RambleOn.getRambleOnApp().getGUIButtons().get(GO_FORWARD_BUTTON_TYPE);
        button.setActionListener(null);
        button = RambleOn.getRambleOnApp().getGUIButtons().get(GO_BACK_BUTTON_TYPE);
        button.setActionListener(null);
    }
    
    public String getGameTimeText() {
        if (startTime == null) {
            return "";
        }
        GregorianCalendar now = new GregorianCalendar();
        long diff = now.getTimeInMillis() - startTime.getTimeInMillis();
        long numSeconds = diff / 1000L;
        
        return getSecondsAsTimeText(numSeconds);
    }
    
    public String getGameWinDurationText() {
        long numMilliseconds = winEndingTime.getTimeInMillis() - startTime.getTimeInMillis();
        long numSeconds = numMilliseconds / 1000L;
        return getSecondsAsTimeText(numSeconds);
    }
    
    public String getSecondsAsTimeText(long numSeconds) {
        long numHours = numSeconds / 3600;
        numSeconds = numSeconds - (numHours * 3600);
        long numMinutes = numSeconds / 60;
        numSeconds = numSeconds - (numMinutes * 60);
        
        String timeText = "";
        if (numHours > 0) {
            timeText += numHours + ":";
        }
        timeText += numMinutes + ":";
        if (numSeconds < 10) {
            timeText += "0" + numSeconds;
        } else {
            timeText += numSeconds;
        }
        return timeText;
    }
    
    public int calculateScore() {
        int points = MAX_SCORE;
        long numMilliseconds = winEndingTime.getTimeInMillis() - startTime.getTimeInMillis();
        long numSeconds = numMilliseconds / 1000L;
        points -= numSeconds;
        points -= (100 * incorrectGuesses);
        if (points < 0) {
            return 0;
        } else {
            return points;
        }
    }
    
    public int getNumberOfSubRegions() {
        return subRegionStack.size();
    }
    
    public int getIncorrectGuesses() {
        return incorrectGuesses;
    }
    
    public int getRegionsFound() {
        return regionDataManager.getNumberOfSubRegions() - subRegionStack.size();
    }
    
    public int getRegionsNotFound() {
        return subRegionStack.size();
    }
    
    public void removeAllButOneFromeStack() {
        // AND LET'S CHANGE THE RED ONES BACK TO THEIR PROPER COLORS FIRST, THEN WE CHEAT.
        for (String redRegionName : redSubRegions) {
            Color subRegionColor = regionDataManager.getColorMappedToSubRegion(redRegionName);
            changeSubRegionColorOnMap(redRegionName, subRegionColor);
        }
        redSubRegions.clear();
        boolean tempIndicator = false;
        if (subRegionStack.size() > 1) {
            if (subRegionStack.getLast().getY() < 0) {
                tempIndicator = true;
            }
        }
        while (subRegionStack.size() > 1) {
            RegionSprite tS = subRegionStack.removeFirst();
            String subRegionName = tS.getRegionName();

            // TURN THE TERRITORY GREEN
            changeSubRegionColorOnMap(subRegionName, Color.GREEN);
        }
        if (tempIndicator == true) {
            subRegionStack.getFirst().setY(0);
        }
        startStackSpritesMovingDown();
    }
}
