/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package region_data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.sound.midi.Sequencer;

/**
 * This is a data management class for managing region data, providing a means
 * to add subregion, color, leader images, and other information about a this
 * region's sub-regions.
 *
 * @author Leixiang Stony Brook Student
 * @version Version 1.0
 */
public class RegionDataManager {

    // THE NAME OF THE REGION THAT IS CURRENTLY BEING PLAYED.
    private String regionName;
    // THE TYPE OF SUBREGION.
    private String subRegionsType;
    // STORES THE COLOR TO THE CORRESPONDING SUBREGION, AND VISER VERSA.
    private HashMap<Color, String> colorToSubRegionMappings;
    private HashMap<String, Color> subRegionToColorMappings;
    // STORES THE FLAG IMAGE TO THE CORRESPONDING SUBREGION, AND VISER VERSA.
    private HashMap<String, BufferedImage> subRegionToFlagImgMappings;
    private HashMap<BufferedImage, String> flagImgToSubRegionMappings;
    // STORES THE LEADER IMAGE TO THE CORRESPONDING SUBREGION, AND VISER VERSA.
    private HashMap<String, BufferedImage> subRegionToLeaderImgMappings;
    private HashMap<BufferedImage, String> leaderImgToSubRegionMappings;
    // STORES THE LEADER NAME TO THE CORRESPONDING SUBREGION, AND VISER VERSA.
    private HashMap<String, String> subRegionToLeaderNameMappings;
    private HashMap<String, String> leaderNameToSubRegionMappings;
    // STORES THE CAPITAL NAME TO THE CORRESPONDING SUBREGION, AND VISER VERSA.
    private HashMap<String, String> subRegionToCapitalMappings;
    private HashMap<String, String> capitalToSubRegionMappings;
    // THE LIST OF SUBREGIONS
    private LinkedList<String> subRegionStack;
    // THE MAP OF THE REGION THAT IS CURRENTLY BEING PLAYED.
    private BufferedImage regionMap;
    //THE ANTHEM OF THIS REGION
    private Sequencer regionAnthem;

    /**
     * Default constructor, it initializes all data structures for managing the
     * sub region data.
     */
    public RegionDataManager() {
        // INITIALIZE OUR DATA STRUCTURES
        colorToSubRegionMappings = new HashMap();
        subRegionToColorMappings = new HashMap();

        subRegionToFlagImgMappings = new HashMap();
        flagImgToSubRegionMappings = new HashMap();

        subRegionToLeaderImgMappings = new HashMap();
        leaderImgToSubRegionMappings = new HashMap();

        subRegionToLeaderNameMappings = new HashMap();
        leaderNameToSubRegionMappings = new HashMap();

        subRegionToCapitalMappings = new HashMap();
        capitalToSubRegionMappings = new HashMap();

        subRegionStack = new LinkedList();

    }

    /**
     * For accessing the name of the region that is being played.
     *
     * @return the String that represents the name of this region.
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * Mutator method for setting the current region that is being played.
     *
     * @param initRegionName the name of new region that user wants to play.
     *
     */
    public void setRegionName(String initRegionName) {
        regionName = initRegionName;
    }

    /**
     * For accessing the type of subregions.
     *
     * @return the String that represents the type of subregions.
     */
    public String getSubRegionsType() {
        return subRegionsType;
    }

    /**
     * Mutator method for setting the type of sub-regions.
     *
     * @param initSubRegionsType the current type of subregions.
     *
     */
    public void setSubRegionsType(String initSubRegionsType) {
        subRegionsType = initSubRegionsType;
    }

    /**
     * Adds the subRegionName parameter to colorToSubRegionMappings data model
     * using colorKey as the binary search tree key.
     *
     * @param colorKey the color that corresponds to the sub-region.
     *
     * @param subRegionName the sub-region that wants to be stored.
     */
    public void addColorToSubRegionMappings(Color colorKey, String subRegionName) {
        colorToSubRegionMappings.put(colorKey, subRegionName);
    }

    /**
     * For accessing the sub-region that corresponds to the colorKey.
     *
     * @param colorKey the key that is used to access the desired subRegion.
     *
     * @return the String which is the name of the subregion that corresponds to
     * the colorKey.
     */
    public String getSubRegionMappedToColor(Color colorKey) {
        return colorToSubRegionMappings.get(colorKey);
    }

    /**
     * For accessing all subregions in subRegionToColorMappings data structure.
     *
     * @return the set of regions that are in subRegionToColorMappings data
     * structure.
     */
    public Set<String> getSubRegionsInColorTree() {
        return subRegionToColorMappings.keySet();
    }

    /**
     * Adds the colorKey parameter to subRegionToColorMappings data model using
     * subRegionName as the binary search tree key.
     *
     * @param subRegionName the name of sub-region that corresponds to the
     * colorKey..
     *
     * @param colorKey the color that wants to be stored.
     */
    public void addSubRegionToColorMappings(String subRegionName, Color colorKey) {
        subRegionToColorMappings.put(subRegionName, colorKey);
    }

    /**
     * For accessing the color that corresponds to the subRegionName.
     *
     * @param subRegionName the sub-region that is used to access the
     * corresponding color.
     *
     * @return the Color that corresponds to subRegionName.
     */
    public Color getColorMappedToSubRegion(String subRegionName) {
        return subRegionToColorMappings.get(subRegionName);
    }

    /**
     * Adds the leaderPic parameter to subRegionToLeaderImgMappings data model
     * using subRegionName as the binary search tree key.
     *
     * @param subRegionName the name of sub-region that corresponds to correct
     * leader.
     *
     * @param leaderPic the image of a leader that wants to be stored.
     */
    public void addSubRegionToLeaderImgMappings(String subRegionName, BufferedImage leaderPic) {
        subRegionToLeaderImgMappings.put(subRegionName, leaderPic);
    }

    /**
     * For accessing BufferedImage that corresponds to the subRegion.
     *
     * @param subRegionName the sub-region that is used to access the
     * corresponding leader image.
     *
     * @return the BufferedImage that corresponds to subRegionName.
     */
    public BufferedImage getLeaderMappedToSubRegion(String subRegionName) {
        return subRegionToLeaderImgMappings.get(subRegionName);
    }

    /**
     * Adds the subRegionName parameter to leaderImgToSubRegionMappings data
     * model using leaderPic as the binary search tree key.
     *
     * @param leaderPic the image of sub-region's leader. leader.
     *
     * @param subRegionName the name of that sub-region.
     */
    public void addLeaderImgToSubRegionMappings(BufferedImage leaderPic, String subRegionName) {
        leaderImgToSubRegionMappings.put(leaderPic, subRegionName);
    }

    /**
     * For accessing all subregions in subRegionToLeaderImgMappings data
     * structure.
     *
     * @return the set of regions that are in subRegionToLeaderImgMappings data
     * structure.
     */
    public Set<String> getSubRegionsInLeaderTree() {
        return subRegionToLeaderImgMappings.keySet();
    }

    /**
     * For accessing sub-region that corresponds to the given leader image.
     *
     * @param leaderPic the leader picture that is used to access the
     * corresponding sub-region.
     *
     * @return the String which is the name of a sub-region that corresponds to
     * leaderPic.
     */
    public String getSubRegionMappedToLeaderImg(BufferedImage leaderPic) {
        return leaderImgToSubRegionMappings.get(leaderPic);
    }

    /**
     * Adds the leaderName parameter to subRegionToLeaderNameMappings data model
     * using subRegionName as the binary search tree key.
     *
     * @param subRegionName the sub-region's name that corresponds to
     * leaderName.
     *
     * @param leaderName the leader name of that sub-region.
     */
    public void addSubRegionToLeaderNameMappings(String subRegionName, String leaderName) {
        subRegionToLeaderNameMappings.put(subRegionName, leaderName);
    }

    /**
     * For accessing leader name that corresponds to the given sub-region.
     *
     * @param subRegionName the sub-region picture that is used to access the
     * corresponding leader name.
     *
     * @return the String which is the name of that sub-region's leader.
     */
    public String getLeaderNameMappedToSubRegion(String subRegionName) {
        return subRegionToLeaderNameMappings.get(subRegionName);
    }

    /**
     * Adds the subRegionName parameter to leaderNameToSubRegionMappings data
     * model using leaderName as the binary search tree key.
     *
     * @param leaderName the sub-region's leader name.
     *
     * @param subRegionName the name of that subregion.
     */
    public void addLeaderNameToSubRegionMappings(String leaderName, String subRegionName) {
        leaderNameToSubRegionMappings.put(leaderName, subRegionName);
    }

    /**
     * For accessing the sub-region name that corresponds to the given leader
     * name.
     *
     * @param leaderName the name of a sub-region leader.
     *
     * @return the String which is the name of that sub-region.
     */
    public String getSubRegionMappedToLeaderName(String leaderName) {
        return leaderNameToSubRegionMappings.get(leaderName);
    }

    /**
     * Adds the flagImg parameter to subRegionToFlagImgMappings data model using
     * subRegionName as the binary search tree key.
     *
     * @param subRegionName the name of a sub-region .
     *
     * @param flagImg the image that wants to be stored.
     */
    public void addSubRegionToFlagImgMappings(String subRegionName, BufferedImage flagImg) {
        subRegionToFlagImgMappings.put(subRegionName, flagImg);
    }

    /**
     * For accessing the flag image that corresponds to the given sub-region.
     *
     * @param subRegionName the name of a sub-region.
     *
     * @return the subRegionName which is the given sub-region's flag.
     */
    public BufferedImage getFlagImgMappedToSubRegion(String subRegionName) {
        return subRegionToFlagImgMappings.get(subRegionName);
    }

    /**
     * For accessing all subregions in subRegionToFlagImgMappings data
     * structure.
     *
     * @return the set of regions that are in subRegionToFlagImgMappings data
     * structure.
     */
    public Set<String> getSubRegionsInFlagTree() {
        return subRegionToFlagImgMappings.keySet();
    }

    /**
     * Adds the subRegionName parameter to flagImgToSubRegionMappings data model
     * using flagImg as the binary search tree key.
     *
     * @param flagImg the image of a sub-region .
     *
     * @param subRegionName the name of a sub-region that wants to be stored.
     */
    public void addFlagToFlagImgMappings(BufferedImage flagImg, String subRegionName) {
        flagImgToSubRegionMappings.put(flagImg, subRegionName);
    }

    /**
     * For accessing the name of a sub-region that corresponds to the given flag
     * image.
     *
     * @param flagImg the image of a sub-region.
     *
     * @return the String which corresponds to the given flag image.
     */
    public String getSubRegionMappedToFlagImg(BufferedImage flagImg) {
        return flagImgToSubRegionMappings.get(flagImg);
    }

    /**
     * Adds the capital parameter to subRegionToCapitalMappings data model using
     * subRegionName as the binary search tree key.
     *
     * @param subRegionName the name of a sub-region .
     *
     * @param capital the capital name that wants to be stored.
     */
    public void addSubRegionToCapitalMappings(String subRegionName, String capital) {
        subRegionToCapitalMappings.put(subRegionName, capital);
    }

    /**
     * For accessing the name of a sub-region capital that corresponds to the
     * given sub-region name.
     *
     * @param subRegionName the name of a sub-region.
     *
     * @return the capital String which corresponds to the given flag image.
     */
    public String getCapitalMappedToSubRegion(String subRegionName) {
        return subRegionToCapitalMappings.get(subRegionName);
    }

    /**
     * For accessing all subregions in subRegionToCapitalMappings data
     * structure.
     *
     * @return the set of regions that are in subRegionToCapitalMappings data
     * structure.
     */
    public Set<String> getSubRegionsInCapitalTree() {
        return subRegionToCapitalMappings.keySet();
    }

    /**
     * Adds the subRegionName parameter to capitalToSubRegionMappings data model
     * using capital as the binary search tree key.
     *
     * @param capital the capital of the given sub-region.
     *
     * @param subRegionName the name of a sub-region that wants to be stored.
     */
    public void addCapitalToSubRegionMappings(String capital, String subRegionName) {
        capitalToSubRegionMappings.put(capital, subRegionName);
    }

    /**
     * For accessing the sub-region that corresponds to the given capital name.
     *
     * @param capital the capital name of a sub-region.
     *
     * @return the subregion String which corresponds to the given capital.
     */
    public String getSubRegionToMappedCapital(String capital) {
        return capitalToSubRegionMappings.get(capital);
    }


    /**
     * Accessor method for getting all of this regions subregions in the form of
     * an LinkedList.
     *
     * @return An LinkedList that is the list of sub-regions.
     */
    public LinkedList<String> getSubRegions() {
        return subRegionStack;
    }

    /**
     * Accessor method for getting all the number of subregions that this region
     * has.
     *
     * @return the total number of sub-regions that this region has.
     */
    public int getNumberOfSubRegions() {
        return colorToSubRegionMappings.keySet().size();
    }

    /**
     * Mutator method for setting the regionAnthem.
     *
     * @param initAnthem the new anthem that corresponds to this region.
     */
    public void setRegionAnthem(Sequencer initAnthem) {
        regionAnthem = initAnthem;
    }

    /**
     * Accessor method for getting anthem of this region.
     *
     * @return the Sequencer which is the anthem of this region.
     */
    public Sequencer getRegionAnthem() {
        return regionAnthem;
    }

    /**
     * Mutator method for setting the regionMap.
     *
     * @param initMap the new map that corresponds to this region.
     */
    public void setRegionMap(BufferedImage initMap) {
        regionMap = initMap;
    }

    /**
     * Accessor method for getting the map of this region.
     *
     * @return the BufferedImage which is the map of this region.
     */
    public BufferedImage getRegionMap() {
        return regionMap;
    }

    /**
     * Adds the subRegionName parameter to subRegionStack data model.
     *
     * @param subRegionName the name of a sub-region that wants to be added. .
     */
    public void addSubRegionName(String subRegionName) {
        subRegionStack.add(subRegionName);
    }

    /**
     * Cleaning method for cleaning all the data.
     */
    public void clearRegionData() {
        regionName = "";
        subRegionsType = "";
        colorToSubRegionMappings.clear();
        subRegionToColorMappings.clear();
        subRegionToFlagImgMappings.clear();
        flagImgToSubRegionMappings.clear();
        subRegionToLeaderImgMappings.clear();
        leaderImgToSubRegionMappings.clear();
        subRegionToLeaderNameMappings.clear();
        leaderNameToSubRegionMappings.clear();
        subRegionToCapitalMappings.clear();
        capitalToSubRegionMappings.clear();
        subRegionStack.clear();
        regionAnthem = null;
        regionMap = null;
    }
}