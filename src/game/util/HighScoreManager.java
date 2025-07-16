package game.util;

import java.io.*;
import java.util.Arrays;

/**
 * Manages persistence of the top three (lowest) completion times across sessions.
 * <p>
 * The times are stored in a single file as comma-separated values. On game completion,
 * the player's time is evaluated against these records and updated if it qualifies.
 * </p>
 * <p>
 * This utility class follows a static singleton pattern, providing methods to:
 * <ul>
 *   <li>Retrieve the current best times from persistent storage</li>
 *   <li>Save updated best times to persistent storage</li>
 *   <li>Compare and update the high score list with new completion times</li>
 * </ul>
 * </p>
 * <p>
 * The high scores are stored in ascending order (fastest times first), with
 * {@code Long.MAX_VALUE} used as a placeholder for empty slots. This ensures
 * that new times will always be compared correctly even if fewer than three
 * scores have been recorded.
 * </p>
 * <p>
 * File format: A single line of comma-separated long values representing
 * completion times in milliseconds (e.g., "45678,52341,67890").
 * </p>
 */
public class HighScoreManager {
    /**
     * The file name used for saving and loading the best times.
     * <p>
     * This file is located in the "data" directory and stores the high scores
     * as comma-separated values on a single line.
     * </p>
     */
    private static final String FILE_NAME = "data/best_times.txt";

    /**
     * The number of top scores to store (in this case, three).
     * <p>
     * This constant defines how many high scores are tracked and displayed
     * in the game completion screen.
     * </p>
     */
    private static final int NUM_SCORES = 20;

    /**
     * Reads the top three times from the file.
     * <p>
     * If the file doesn't exist or contains invalid data, this method returns
     * an array filled with {@code Long.MAX_VALUE}.
     * </p>
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Initialize an array with {@code Long.MAX_VALUE} values</li>
     *   <li>Attempt to read the high scores file</li>
     *   <li>Parse the comma-separated values into long values</li>
     *   <li>Return the array of high scores (or the default array if an error occurs)</li>
     * </ol>
     * </p>
     * <p>
     * Error handling: If the file doesn't exist or contains invalid data,
     * the method silently returns the default array without throwing exceptions.
     * </p>
     *
     * @return An array of the top three best completion times (in milliseconds),
     *         sorted in ascending order (fastest first).
     */
    public static long[] getHighScores() {
        long[] scores = new long[NUM_SCORES];
        Arrays.fill(scores, Long.MAX_VALUE);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] tokens = line.split(",");
                for (int i = 0; i < Math.min(tokens.length, NUM_SCORES); i++) {
                    scores[i] = Long.parseLong(tokens[i].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            // File not found or invalid content; default values (Long.MAX_VALUE) remain.
        }
        return scores;
    }

    /**
     * Saves the top three times to the file, overwriting any existing data.
     * <p>
     * This method writes the array of high scores to the file as comma-separated
     * values on a single line. Any existing file content is completely replaced.
     * </p>
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Open the file for writing (creating it if it doesn't exist)</li>
     *   <li>Convert the array of times to a comma-separated string</li>
     *   <li>Write the string to the file</li>
     *   <li>Close the file</li>
     * </ol>
     * </p>
     * <p>
     * Error handling: If an error occurs during file writing, an error message
     * is printed to the console, but no exception is thrown to prevent
     * application crashes.
     * </p>
     *
     * @param scores The array of best times in milliseconds. Must be of size three.
     *               Values are expected to be sorted in ascending order.
     */
    public static void saveHighScores(long[] scores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, false))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < scores.length; i++) {
                sb.append(scores[i]);
                if (i < scores.length - 1) {
                    sb.append(",");
                }
            }
            writer.println(sb.toString());
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
        }
    }

    /**
     * Updates the list of top three times if the new time qualifies.
     * <p>
     * The method reads existing scores, adds the new time, sorts them,
     * and keeps only the top three (lowest) times. It then saves and
     * returns the updated list.
     * </p>
     * <p>
     * The update process follows these steps:
     * <ol>
     *   <li>Retrieve the current high scores using {@link #getHighScores()}</li>
     *   <li>Add the new completion time to the array</li>
     *   <li>Sort all times in ascending order (fastest first)</li>
     *   <li>Keep only the top {@link #NUM_SCORES} times</li>
     *   <li>Save the updated high scores using {@link #saveHighScores(long[])}</li>
     *   <li>Return the updated array</li>
     * </ol>
     * </p>
     * <p>
     * Note: This method will always save the updated scores, even if the new time
     * doesn't qualify for the top three. This ensures the file is created
     * if it doesn't exist yet.
     * </p>
     *
     * @param newTime The elapsed time (in milliseconds) for the current run.
     * @return The updated array of the top three times, in ascending order (fastest first).
     */
    public static long[] updateHighScores(long newTime) {
        long[] scores = getHighScores();

        // Add the new time to the array
        long[] newScores = new long[scores.length + 1];
        System.arraycopy(scores, 0, newScores, 0, scores.length);
        newScores[newScores.length - 1] = newTime;

        // Sort all times in ascending order (fastest first)
        Arrays.sort(newScores);

        // Keep only the top NUM_SCORES times
        long[] bestScores = Arrays.copyOf(newScores, NUM_SCORES);

        // Save and return the updated best times
        saveHighScores(bestScores);
        return bestScores;
    }
}