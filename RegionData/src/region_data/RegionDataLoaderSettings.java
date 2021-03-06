package region_data;

/**
 * This class lists all of the constants used for loading region data. The point
 * of doing this is that we can find and change constants more easily. Also,
 * it's easier to organize them.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class RegionDataLoaderSettings {
    
    //THIS IS THE NAME OF OUR ROOT DIRECTORY
    public static final String WORLD = "The World";
    
    
    // THIS IS THE NAME OF THE SCHEMA THAT THIS PLUGIN USES. NOTE THAT
    // THIS SCHEMA FILE WILL BE INCLUDED IN THE LIBRARY'S JAR FILE
    // WHEN WE DEPLOY IT
    public static final String WORLD_REGIONS_SCHEMA = "RegionData.xsd";
    
    // THESE CONSTANTS ARE THE ATTRIBUTES THAT ARE IN XML FILES.
    public static final String REGION_NODE = "region";
    public static final String SUB_REGION_NODE = "sub_region";
    public static final String RED_ATTRIBUTE = "red";
    public static final String BLUE_ATTRIBUTE = "blue";
    public static final String GREEN_ATTRIBUTE = "green";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String CAPITAL_ATTRIBUTE = "capital";
    public static final String LEADER_ATTRIBUTE = "leader";
    public static final String RGB_ATTRIBUTE = "RGB";
    
    // THE EXTENSION OF DATA XML FILES, MAP IMAGES, AND ANTHEMS. 
    public static final String XML_FILE_EXTENSION = " Data.xml";
    public static final String MAP_FILE_EXTENSION = " Map.png";
    public static final String ANTHEM_FILE_EXTENSION = " National Anthem.mid";
    
    
    public static final String FLAG_FILE_EXTENSION = " Flag.jpg";
    public static final String LEADER_FILE_EXTENSION = " Leader.jpg";
}
