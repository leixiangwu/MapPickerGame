/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_account;

/**
 *
 * @author Leixiang
 */
public class RegionStats {

    private int numberOfTimesPlayed;
    private int highestScore;
    private int fastestTime; // in seconds

    public RegionStats(int initPlayed, int initScore, int initTime) {
        numberOfTimesPlayed = initPlayed;
        highestScore = initScore;
        fastestTime = initTime;
    }

    public int getNumberOfTimesPlayed() {
        return numberOfTimesPlayed;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public int getFastestTime() {
        return fastestTime;
    }

    public void setNumberOfTimesPlayed(int newNumber) {
        numberOfTimesPlayed = newNumber;
    }

    public void setHighestScore(int newScore) {
        highestScore = newScore;
    }

    public void setFastestTime(int newTime) {
        fastestTime = newTime;
    }
}
