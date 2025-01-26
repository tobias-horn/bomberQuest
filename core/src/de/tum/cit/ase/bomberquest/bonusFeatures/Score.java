package de.tum.cit.ase.bomberquest.bonusFeatures;

/**
 * Manages the player's score in the game.
 *
 * Scoring rules:
 * - For each destructible wall destroyed: +15 points
 * - For each enemy killed: +100 points
 * - For each power-up collected: +85 points
 * - For each second remaining when the player wins: +2 points
 */
public class Score {

    private int score;

    public Score() {
        this.score = 0;
    }

    /**
     * Adds points for destroying a destructible wall.
     */
    public void addPointsForWallDestroyed() {
        score += 15;
    }

    /**
     * Adds points for killing an enemy.
     */
    public void addPointsForEnemyKilled() {
        score += 100;
    }

    /**
     * Adds points for collecting a power-up.
     */
    public void addPointsForPowerUp() {
        score += 85;
    }

    /**
     * Adds a time-based bonus when the player wins.
     * @param secondsRemaining The number of seconds left on the timer.
     */
    public void addTimeBonus(int secondsRemaining) {
        score += secondsRemaining * 2;
    }

    /**
     * Returns the current score.
     * @return The current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Resets the score to zero (if needed).
     */
    public void resetScore() {
        this.score = 0;
    }
}
