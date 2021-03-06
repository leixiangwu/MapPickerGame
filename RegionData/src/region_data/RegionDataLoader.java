package region_data;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static region_data.RegionDataLoaderSettings.*;
import region_data_exceptions.*;
import xml_utilities.InvalidXMLFileFormatException;
import xml_utilities.XMLUtilities;

/**
 * This class serves as a plugin for reading data files that are in a directory.
 * Files include leader images, maps, anthems, flag images, and XML files. Also,
 * its family type is RegionDataImporter. Note that if anyone wants to change
 * how this data gets loaded, he can define a new member of RegionDataImporter
 * and swap with this class without changing the this library.
 *
 * @author Leixiang
 */
public class RegionDataLoader implements RegionDataImporter {

    // THIS WILL HELP US PARSE THE XML FILES
    private XMLUtilities xmlUtil;
    // THIS IS THE SCHEMA WE'LL USE
    private File regionDataSchema;
    //THIS IS THE XML THAT THIS LOADER IS LOADING.
    private File currentXMLFile;
    //THIS IS THE REGION NODE FOR THE CURRENT REGION.
    private Node currentRegionNode;

    /**
     * Constructor for making our importer. Note that it initializes the XML
     * utility for processing XML files and it sets up the schema for use.
     */
    public RegionDataLoader(File initRegionSchema) {
        xmlUtil = new XMLUtilities();

        // WE'LL USE THE SCHEMA FILE TO VALIDATE THE XML FILES
        regionDataSchema = initRegionSchema;
    }

    /**
     * Get the files in the directory and only load map, anthem and the data
     * from XML file into regionDataManager.
     *
     * @param regionDirectoryPathName The directory path where the data files
     * are stored.
     *
     * @param regionDataManager The manager that manages the data of a region.
     *
     * @return true if the world loads successfully, false otherwise.
     *
     * @throws DirectoryIsMissingException Thrown if the directory is not
     * existed.
     * @throws FlagImagesNotFoundException Thrown if flag images are not found.
     * @throws FileIsMissingException Thrown if an important file is missing,
     * like XML or map.
     * @throws AnthemIsMissingException Thrown if the anthem file is missing.
     * @throws InvalidRGBValuesException Thrown if one of RGB is out of 0-255.
     * like XML, map and anthem.
     * @throws InvalidXMLFileFormatException Thrown if the xml file validation
     * fails. 0-255. like XML, map and anthem.
     * @throws BadXMLDataException Thrown if the xml doesn't have a name, RGB,
     * or other data for a region.
     *
     */
    @Override
    public boolean loadRegionData(String regionDirectoryPathName, RegionDataManager regionDataManager) throws InvalidRGBValuesException,
            DirectoryIsMissingException, FileIsMissingException, InvalidXMLFileFormatException,
            BadXMLDataException {
        // LOAD THE REGION DIRECTORY AS A FILE
        File directoryFile = new File(regionDirectoryPathName);

        // IS THE DIRECTORY EXISTED?
        if (!directoryFile.exists()) {
            // IF NOT EXISTED, THROW AN EXCEPTION TO TELL THE USER WHAT WENT WRONG.
            throw new DirectoryIsMissingException(directoryFile.getName());
        }
        //GET THE REGION NAME AND GET ALL THE DATA FILES WE NEED TO LOAD THIS REGION.
        String tempRegionName = directoryFile.getName();
        File xmlFile = new File(directoryFile.getAbsolutePath() + "/" + tempRegionName + XML_FILE_EXTENSION);
        File mapFile = new File(directoryFile.getAbsolutePath() + "/" + tempRegionName + MAP_FILE_EXTENSION);

        //CHECK TO SEE IF EVERY FILE IS EXISTED. IF NOT, THROW AN EXCEPTION
        if (!xmlFile.exists()) {
            throw new FileIsMissingException(xmlFile.getName());
        }

        //CHECK IF THE MAP IS EXISTED.
        if (!mapFile.exists()) {
            throw new FileIsMissingException(mapFile.getName());
        }

        // FIRST LOAD ALL THE XML INTO A TREE
        Document doc;
        doc = xmlUtil.loadXMLDocument(xmlFile.getAbsolutePath(),
                regionDataSchema.getAbsolutePath());

        // CLEARN THE PREVIOUS REGION DATA
        regionDataManager.clearRegionData();

        //SET THE MAP SINCE THE MAP IS EXISTED.
        setRegionMap(mapFile, regionDataManager);


        // FIRST GET THE REGION THAT NEEDS TO LOAD
        Node regionNode = doc.getElementsByTagName(REGION_NODE).item(0);

        //CHECK TO SEE IF IT HAS A NAME BEFORE WE GET THE REGION NAME NODE
        if (regionNode.getAttributes().getNamedItem(NAME_ATTRIBUTE) == null) {
            throw new BadXMLDataException(NAME_ATTRIBUTE, xmlFile.getName());
        }
        String regionName = regionNode.getAttributes().getNamedItem(NAME_ATTRIBUTE).getNodeValue();

        //BEING EMPTY MEANS THAT THE DATA IS MISSING, THROW AN EXCEPTION.
        if (regionName.isEmpty()) {
            //REGION NAME IS MISSING, THROW AN EXCEPTION TO THE USER THIS ERROR
            throw new BadXMLDataException(NAME_ATTRIBUTE, xmlFile.getName());
        }

        //SET THE REGION NAME IN REGION MANAGER TO THE REGION THAT IS CURRENTLY LOADING.
        regionDataManager.setRegionName(regionName);

        //LOAD ALL THE SUBREGIONS IN XML FILE.
        loadSubRegions(xmlFile, regionNode, regionDataManager);

        currentXMLFile = xmlFile;
        currentRegionNode = regionNode;

        return true;
    }

    /**
     * A helper method for loadRegionData(). Its function is to get the java
     * file of image, and add it into manager.
     *
     * @param initRegionMap The map image that is in java file format.
     *
     * @param regionManager The manager that manages the data of a region.
     *
     */
    private void setRegionMap(File initRegionMap, RegionDataManager regionManager) {
        try {
            //CONVERT THE JAVA FILE INTO BUFFERED IMAGE AND SET IT AS THE MAP.
            BufferedImage regionMap = ImageIO.read(initRegionMap);
            regionManager.setRegionMap(regionMap);
        } catch (IOException ex) {
            System.out.println("There is something wrong with setting map!!");
        }
    }

    /**
     * A helper method for loadRegionData(). Its function is to load the data of
     * sub-regions from XML file into regionDataManager when the region is the
     * world and its subregions are continents.
     *
     * @param xmlFile the xml data File for the world in java file format.
     *
     * @param regionNode The node of the world region.
     *
     * @param regionDataManager The manager that manages the data of a region.
     *
     * @throws InvalidRGBValuesException Thrown if the RGB is invalid.
     * @throws BadXMLDataException Thrown if RGB or region names are missing.
     */
    private boolean loadSubRegions(File xmlFile, Node regionNode, RegionDataManager regionDataManager)
            throws InvalidRGBValuesException, BadXMLDataException {

        //WE WILL REUSE THESE GUYS
        Node subRegionNameNode, redNode, greenNode, blueNode;
        int red, green, blue;

        //GET ALL THE SUB REGIONS AND GO THROUGH ALL OF THEM
        ArrayList<Node> subRegionNodes = xmlUtil.getChildNodesWithName(regionNode, SUB_REGION_NODE);
        for (int i = 0; i < subRegionNodes.size(); i++) {
            // GET THEIR DATA FROM THE DOC
            Node subRegionNode = subRegionNodes.get(i);
            NamedNodeMap subRegionAttributes = subRegionNode.getAttributes();

            subRegionNameNode = subRegionAttributes.getNamedItem(NAME_ATTRIBUTE);

            if (subRegionNameNode == null) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, xmlFile.getName());
            }

            String subRegionName = subRegionNameNode.getNodeValue();

            if (subRegionName.isEmpty()) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, xmlFile.getName());
            } else {

                //ADD THE SUB REGION INTO REGION MANAGER
                regionDataManager.addSubRegionName(subRegionName);

                //GET NODES OF THE VALUES OF RGB.
                redNode = subRegionAttributes.getNamedItem(RED_ATTRIBUTE);
                greenNode = subRegionAttributes.getNamedItem(GREEN_ATTRIBUTE);
                blueNode = subRegionAttributes.getNamedItem(BLUE_ATTRIBUTE);
                if (redNode == null || greenNode == null || blueNode == null) {
                    throw new BadXMLDataException(RGB_ATTRIBUTE, xmlFile.getName());
                }
                //GET THE COLOR FOR THIS REGION AND MAP THE COLOR TO THE SUB REGION
                red = Integer.parseInt(redNode.getNodeValue());
                green = Integer.parseInt(greenNode.getNodeValue());
                blue = Integer.parseInt(blueNode.getNodeValue());

                //MAKE SURE THAT IT'S IN RANGE OF 0-255.
                if (red < 0 || red > 255) {
                    throw new InvalidRGBValuesException(subRegionName);
                }
                if (green < 0 || green > 255) {
                    throw new InvalidRGBValuesException(subRegionName);
                }
                if (blue < 0 || blue > 255) {
                    throw new InvalidRGBValuesException(subRegionName);
                }
                Color color = new Color(red, blue, green);
                // ADD THE COLOR INTO THE DATA MANAGER.
                regionDataManager.addColorToSubRegionMappings(color, subRegionName);
                regionDataManager.addSubRegionToColorMappings(subRegionName, color);



            }
        }
        // NO ERROR, WE RETURN TRUE.
        return true;
    }

    /**
     * Get the files in the directory and only load region capitals and names
     * from XML file into regionDataManager.
     *
     * @param regionDataManager The manager that manages the data of a region.
     *
     * @return true if the world loads successfully, false otherwise.
     *
     * @throws BadXMLDataException Thrown if the directory is not existed.
     */
    @Override
    public boolean loadCapitalNames(RegionDataManager regionDataManager) throws
            BadXMLDataException {

        //GET ALL THE SUB REGIONS.
        ArrayList<Node> subRegionNodes = xmlUtil.getChildNodesWithName(currentRegionNode, SUB_REGION_NODE);

        //WE WILL REUSE THESE VARIABLES.
        Node subRegionNameNode, capitalNameNode;
        String capitalName;

        for (int i = 0; i < subRegionNodes.size(); i++) {
            // GET THEIR DATA FROM THE DOC
            Node subRegionNode = subRegionNodes.get(i);
            NamedNodeMap subRegionAttributes = subRegionNode.getAttributes();

            subRegionNameNode = subRegionAttributes.getNamedItem(NAME_ATTRIBUTE);

            if (subRegionNameNode == null) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, currentXMLFile.getName());
            }

            String subRegionName = subRegionNameNode.getNodeValue();

            if (subRegionName.isEmpty()) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, currentXMLFile.getName());
            }

            capitalNameNode = subRegionAttributes.getNamedItem(CAPITAL_ATTRIBUTE);

            if (capitalNameNode == null) {
                throw new BadXMLDataException(CAPITAL_ATTRIBUTE, currentXMLFile.getName());
            }

            capitalName = capitalNameNode.getNodeValue();

            if (capitalName.isEmpty()) {
                throw new BadXMLDataException(CAPITAL_ATTRIBUTE, currentXMLFile.getName());
            }

            //ADD THE CAPITAL INTO THE REGION MANAGER
            regionDataManager.addSubRegionToCapitalMappings(subRegionName, capitalName);
            regionDataManager.addCapitalToSubRegionMappings(capitalName, subRegionName);
        }
        return true;
    }

    /**
     * A helper method for loadRegionData(). Its function is to load the data of
     * sub-regions from XML file into regionDataManager when the region is
     * continent and its subregions are countries.
     *
     * @param xmlFile the xml data File for the continent in java file format.
     *
     * @param regionNode The node of that continent region.
     *
     * @param manager The manager that manages the data of a region.
     *
     * @throws BadXMLDataException Thrown if the xml file doesn't contain RGB
     * value or name for a subregion.
     *
     */
    public boolean loadLeaderNames(RegionDataManager regionDataManager)
            throws BadXMLDataException {

        //GET ALL THE SUB REGIONS.
        ArrayList<Node> subRegionNodes = xmlUtil.getChildNodesWithName(currentRegionNode, SUB_REGION_NODE);

        //WE WILL REUSE THESE VARIABLES.
        String leaderName, subRegionName;
        Node subRegionNameNode, leaderNameNode;

        //GO THROUGH ALL OF THEM
        for (int i = 0; i < subRegionNodes.size(); i++) {
            // GET THEIR DATA FROM THE DOC AND ALSO HAVE SOME CHECK METHODS TO MAKE SURE THE DATA IS THERE.
            Node subRegionNode = subRegionNodes.get(i);
            NamedNodeMap subRegionAttributes = subRegionNode.getAttributes();
            subRegionNameNode = subRegionAttributes.getNamedItem(NAME_ATTRIBUTE);

            if (subRegionNameNode == null) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, currentXMLFile.getName());
            }

            // GET THE SUBREGION NAME
            subRegionName = subRegionNameNode.getNodeValue();

            if (subRegionName.isEmpty()) {
                throw new BadXMLDataException(NAME_ATTRIBUTE, currentXMLFile.getName());
            } else {

                //GET THE LEADER NODE
                leaderNameNode = subRegionAttributes.getNamedItem(LEADER_ATTRIBUTE);

                if (leaderNameNode == null) {
                    throw new BadXMLDataException(LEADER_ATTRIBUTE, currentXMLFile.getName());
                }

                //GET THE SUBREGION LEADER NAME.
                leaderName = leaderNameNode.getNodeValue();

                if (leaderName.isEmpty()) {
                    throw new BadXMLDataException(LEADER_ATTRIBUTE, currentXMLFile.getName());
                }
                //ADD THE LEADER DATA TO THE MANAGER.
                regionDataManager.addSubRegionToLeaderNameMappings(subRegionName, leaderName);
                regionDataManager.addLeaderNameToSubRegionMappings(leaderName, subRegionName);
            }
        }
        //NO ERROR OCCURS, THEN WE RETURN TRUE.
        return true;
    }

    /**
     *
     * Get the files in the directory and only loads flag images into
     * regionDataManager.
     *
     * @param regionDirectoryPathName The directory path where the flag images
     * are stored.
     *
     * @param regionDataManager The manager that manages the data of a region.
     *
     * @return true if the world loads successfully, false otherwise.
     *
     * @throws ImageInvalidDimensionException Thrown if a flag image has invalid
     * dimension.
     *
     * @throws DirectoryIsMissingException Thrown if the directory is not
     * existed.
     * @throws FlagImageNotFoundException Thrown if flag image was not found.
     */
    @Override
    public boolean loadRegionFlags(String regionDirectoryPathName, RegionDataManager regionDataManager) throws
            ImageNotFoundException, ImageInvalidDimensionException {

        /*//CLEAN THE DATA MANAGER.
         regionDataManager.clearRegionData();

         // LOAD THE REGION DIRECTORY AS A FILE
         //File directoryFile = new File(regionDirectoryPathName);

         // IS THE DIRECTORY EXISTED?
         if (!directoryFile.exists()) {
         // IF NOT EXISTED, THROW AN EXCEPTION TO TELL THE USER WHAT WENT WRONG.
         throw new DirectoryIsMissingException(regionDirectoryPathName);
         }

         // SET THE CURRENTLY LOADING REGION
         regionDataManager.setRegionName(directoryFile.getName());

         // LOAD THE IMAGES
         File[] files = directoryFile.listFiles();
         String fileName;
         ArrayList<File> flagFiles = new ArrayList();*/

        // WE ADD ALL THE IMAGES IF THEY END WITH FLAG.jpg.
        /*for (int i = 0; i < files.length; i++) {
         if (files[i].isFile()) {
         fileName = files[i].getName();
         if (fileName.endsWith(" Flag.jpg")) {
         flagFiles.add(files[i]);
         }
         }
         }*/

        //GET ALL THE SUBREGIONS
        Iterator<String> subRegionNames = regionDataManager.getSubRegions().iterator();
        //WE WILL REUSE THESE GUYS
        Image flagImage;
        String subRegionName;
        File imgFile;


        //GO THROUGH ALL THE SUB REGIONS AND CONVERT THEM INTO IMAGES.
        while (subRegionNames.hasNext()) {
            subRegionName = subRegionNames.next();
            imgFile = new File(regionDirectoryPathName + File.separator + subRegionName + FLAG_FILE_EXTENSION);
            if (!imgFile.exists()) {
                //THROW AN CHECKED EXCEPTION BECAUSE THE IMAGE WAS NOT FOUND.
                throw new ImageNotFoundException(subRegionName + FLAG_FILE_EXTENSION);
            }
            flagImage = Toolkit.getDefaultToolkit().createImage(regionDirectoryPathName
                    + File.separator + subRegionName + FLAG_FILE_EXTENSION);
            MediaTracker tracker = new MediaTracker(new Container());
            tracker.addImage(flagImage, 0);
            // AND WAIT FOR IT TO BE FULLY IN MEMORY
            try {
                // AND WAIT FOR IT TO BE FULLY IN MEMORY
                tracker.waitForID(0);
            } catch (InterruptedException ex) {
                System.out.println("MediaTracker error!!");
            }
            if (flagImage.getWidth(null) != 200 || flagImage.getHeight(null) < 1) {
                throw new ImageInvalidDimensionException(subRegionName + FLAG_FILE_EXTENSION);
            }
            //MAPPING SUBREGION AND FLAG IMAGE INTO THE MANAGER
            BufferedImage bufferedFlagImg = new BufferedImage(flagImage.getWidth(null), flagImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedFlagImg.getGraphics();
            g.drawImage(flagImage, 0, 0, null);
            regionDataManager.addFlagToFlagImgMappings(bufferedFlagImg, subRegionName);
            regionDataManager.addSubRegionToFlagImgMappings(subRegionName, bufferedFlagImg);
        }
        return true;
    }

    /**
     * Get the files in the directory and only loads leader images into
     * regionDataManager.
     *
     * @param regionDirectoryPathName The directory path where leader images are
     * stored.
     *
     * @param regionDataManager The manager that manages the data of a region.
     *
     * @return true if the world loads successfully, false otherwise.
     *
     * @throws ImageInvalidDimensionException Thrown if a flag image has invalid
     * dimension.
     *
     * @throws DirectoryIsMissingException Thrown if the directory is not
     * existed.
     * @throws FlagImagsNotFoundException Thrown if leader image not found.
     */
    @Override
    public boolean loadRegionLeaderImgs(String regionDirectoryPathName, RegionDataManager regionDataManager) throws
            ImageNotFoundException, ImageInvalidDimensionException {
        //GET ALL THE SUBREGIONS
        Iterator<String> subRegionNames = regionDataManager.getSubRegions().iterator();

        //WE WILL REUSE THESE GUYS
        BufferedImage leaderImage;
        String subRegionName;
        File imgFile;
        //GO THROUGH ALL THE SUB REGIONS AND CONVERT THEM INTO IMAGES.
        while (subRegionNames.hasNext()) {
            subRegionName = subRegionNames.next();
            imgFile = new File(regionDirectoryPathName + File.separator + subRegionName + LEADER_FILE_EXTENSION);
            if (!imgFile.exists()) {
                //THROW AN CHECKED EXCEPTION BECAUSE THE IMAGE WAS NOT FOUND.
                throw new ImageNotFoundException(subRegionName + LEADER_FILE_EXTENSION);
            }

            leaderImage = loadImage(regionDirectoryPathName + File.separator
                    + subRegionName + LEADER_FILE_EXTENSION);
            
            if (leaderImage.getWidth(null) != 100 || leaderImage.getHeight(null) != 150) {
                throw new ImageInvalidDimensionException(subRegionName + LEADER_FILE_EXTENSION);
            }
            //MAPPING SUBREGION AND ADERL IMAGE INTO THE MANAGER
            regionDataManager.addLeaderImgToSubRegionMappings(leaderImage, subRegionName);
            regionDataManager.addSubRegionToLeaderImgMappings(subRegionName, leaderImage);
        }
        return true;
    }

    private BufferedImage loadImage(String fileName) {
        // LOAD THE IMAGE
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.createImage(fileName);

        // AND WAIT FOR IT TO BE FULLY IN MEMORY BEFORE RETURNING IT
        MediaTracker tracker = new MediaTracker(new Container());
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ie) {
            System.out.println("MT INTERRUPTED");
        }

        // WE'LL USE BUFFERED IMAGES
        BufferedImage imageToLoad = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = imageToLoad.getGraphics();
        g.drawImage(img, 0, 0, null);

        return imageToLoad;
    }

    /**
     * A method for loading anthem. Its function is to get the java file of
     * sequencer , and add it into manager.
     *
     * @param regionAnthem The national anthem that in java file format.
     *
     * @param manager The manager that manages the data of a region.
     *
     * @throws AnthemIsMissingException Thrown if given anthem is not found.
     */
    public void loadRegionAnthem(String regionAnthemPath, RegionDataManager regionManager) throws AnthemIsMissingException {
        try {
            File regionAnthemDirectory = new File(regionAnthemPath);
            File regionAnthemFile = new File(regionAnthemDirectory.getAbsoluteFile()
                    + File.separator + regionAnthemDirectory.getName() + ANTHEM_FILE_EXTENSION);
            if (!regionAnthemFile.exists()) {
                throw new AnthemIsMissingException(regionAnthemFile.getName());
            }
            //CONVERT THE JAVA FILE INTO SEQUENCER AND SET IT AS THE ANTHEM.
            Sequence sequence = MidiSystem.getSequence(regionAnthemFile);
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            regionManager.setRegionAnthem(sequencer);
        } catch (InvalidMidiDataException | IOException | MidiUnavailableException ex) {
            ex.printStackTrace();
        }
    }
}
