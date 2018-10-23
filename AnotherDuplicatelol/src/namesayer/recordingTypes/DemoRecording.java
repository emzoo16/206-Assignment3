package namesayer.recordingTypes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public abstract class DemoRecording extends Recording {

    //A map of all personal recording attempts
    protected HashMap<String, PersonalRecording> userAttempts;
    protected double rating;

    /*
     * Returns all personal recordings
     */
    public ObservableList<String> getUserAttempts() {
        ObservableList<String> personalRecordingsList = FXCollections.observableArrayList(userAttempts.keySet());
        Collections.sort(personalRecordingsList);
        return personalRecordingsList;
    }

    /*
     * Returns an unused number to be used in naming a new personal recording.
     */
    public int getUnusedAttemptsNumber() {
        int[] usedNumbers = new int[userAttempts.size()];
        int index = 0;
        for (String attempt : userAttempts.keySet()) {
            Recording recording = userAttempts.get(attempt);
            usedNumbers[index] = recording.getNumber();
            index++;
        }
        java.util.Arrays.sort(usedNumbers);
        for (int i = 0; i < usedNumbers.length; i++) {
            if (usedNumbers[i] != i + 1) {
                return i + 1;
            }
        }
        return usedNumbers.length + 1;
    }

    /**
     *returns the requested user attempt
     */
    public PersonalRecording getUserRecording(String name) {
        return userAttempts.get(name);
    }

    /**
     *Adds a a user attempt
     */
    public void addAttempt(PersonalRecording attempt) {
        userAttempts.put(attempt.getShortName() ,attempt);
    }

    /**
     *removes a user attempt
     */
    public void deleteAttempt(String attempt) {
        userAttempts.remove(attempt);
    }

    public void rate(int rating) {
        File rateFile = new File("./Resources/Review/" + shortName + ".txt");
        double finalRating = rating;
        if (!rateFile.exists()) {
            try {
                PrintWriter writer = new PrintWriter(rateFile);
                writer.println(rating);
                writer.println(1);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            int[] ratingArray = new int[2];
            Scanner scanner = null;
            int count = 0;
            try {
                scanner = new Scanner(rateFile);
                while (scanner.hasNextLine()) {
                    ratingArray[count] = Integer.parseInt(scanner.nextLine());
                    count++;
                }
                int ratingSum = ratingArray[0] + rating;
                double averageRating = (double) ratingSum / (ratingArray[1] + 1);
                finalRating = averageRating;
                PrintWriter writer = new PrintWriter(rateFile);
                writer.println(ratingSum);
                writer.println(ratingArray[1] + 1);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        updateRating(finalRating);
    }

    public double getRating() {
        File rateFile = new File("./Resources/Review/" + shortName + ".txt");
        if (rateFile.exists()) {
            int[] ratingArray = new int[2];
            Scanner scanner = null;
            int count = 0;
            try {
                scanner = new Scanner(rateFile);
                while (scanner.hasNextLine()) {
                    ratingArray[count] = Integer.parseInt(scanner.nextLine());
                    count++;
                }

                //Calculates the average score of the recording by adding the ratings and
                //dividing by the total number of reviews.
                int ratingSum = ratingArray[0];
                double averageRating = (double) ratingSum / (ratingArray[1]);
                return averageRating;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void updateRating(double rating) {
        //If the rating is above 2.5, print the rating normally.
        if (rating > 2.5) {
            if (isBadRecording()) {
                removeFromBadFile();
            }
        }
        //If the rating is below or equal to 2.5, add a bad quality warning to warn the user.
        else if (rating >= 0) {
            if (!isBadRecording()) {
                addToBadFile();
            }
        } else {
            if (isBadRecording()) {
                removeFromBadFile();
            }
        }
    }

    /*
     * This method removes the name of a recording from the 'bad recording' list once it's
     * rating exceeds 2.5.
     */
    private void removeFromBadFile() {

        File tmpFile = new File("./Resources/Reviewtemp.txt");
        File file = new File("./Resources/ReviewBadRecordings.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));

            String lineToRemove = this.shortName;
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This method adds the name of the recording to the bad recording list once its rating falls
     * below 2.5
     */
    private void addToBadFile() {
        File file = new File("./Resources/ReviewBadRecordings.txt");

        //Append the given name to the BadRecordings file.
        if (file.exists()) {
            try {
                Writer output;
                output = new BufferedWriter(new FileWriter(file, true));
                output.append(this.shortName);
                ((BufferedWriter) output).newLine();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println(this.shortName);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * This method checks if the recording of the name passed is in the BadRecordings file.
     */
    private Boolean isBadRecording() {
        File file = new File("./Resources/ReviewBadRecordings.txt");

        //Scans the file line by line to check if the given name is in the file.
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    if (scanner.nextLine().contains(this.shortName)) {
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return false;
    }
}
