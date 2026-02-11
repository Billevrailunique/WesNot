package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Interface representing a savable object that can store and hash its state.
 * This interface provides methods for computing a hash of the object's content,
 * writing the content and hash to a file, and clearing the saved file content.
 */
public interface Saveable {

    /**
     * The file path where the save data is stored.
     */
    String FILE_PATH = "ressource/save/save.txt";

    /**
     * Retrieves the content of the object that should be used to generate a hash.
     *
     * @return the content as a string that will be hashed for integrity checks.
     */
    String getContentToHash();

    void load();

    /**
     * Computes a hash of the content obtained from {@link #getContentToHash()}
     * using the folding method, where the string is split into groups and processed
     * in 4-byte chunks. The resulting hash is returned as a positive integer.
     *
     * @return the computed hash value.
     */
    default int computeFoldingHash() {
        String input = getContentToHash();
        int hash = 0;
        int groupSize = 4;

        for (int i = 0; i < input.length(); i += groupSize) {
            int groupValue = 0;

            for (int j = 0; j < groupSize; j++) {
                if (i + j < input.length()) {
                    groupValue <<= 8;
                    groupValue += (int) input.charAt(i + j);
                } else {
                    groupValue <<= 8;
                }
            }

            hash += groupValue;
        }

        return Math.abs(hash);
    }

    /**
     * Writes the content and its corresponding hash value to the file specified
     * by {@link #FILE_PATH}. The content and hash are written in a readable format,
     * followed by a separator line "###".
     */
    default void writeToFile() {
        //System.out.println("writing");
        try (FileWriter writer = new FileWriter(FILE_PATH, true);) {
            //System.out.println(getContentToHash());
            writer.write(getContentToHash());
            writer.write("\nHash : " + computeFoldingHash() + "\n");
            writer.write("###\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the contents of the save file specified by {@link #FILE_PATH}.
     * This method effectively resets the save file by overwriting it with an empty
     * string.
     */
    default void cleanSave() {
        try (FileWriter writer = new FileWriter(FILE_PATH, false);) {
            writer.write("");
        } catch (Exception e) {
            System.err.println("Error during file reset: " + e.getMessage());
        }
    }

    default ArrayList<String> loadSave() {
        String line;
        ArrayList<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            while ((line = reader.readLine()) != null) {
                if (!line.equals("###")) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le chargement de la sauvegarde");
        }
        return data;
    }
}
