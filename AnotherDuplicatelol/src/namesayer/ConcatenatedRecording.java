package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 */
public class ConcatenatedRecording extends Recording implements DemoRecording {
    private HashMap<String, Recording> userAttempts;
    private List<String> listOfFilesNames;


    public ConcatenatedRecording(List<String> fileNames, String fullName) {
        userAttempts = new HashMap<>();
        this.shortName = fullName;
        this.fileName = fullName.replaceAll(" ", "") + ".wav";
        createConcatenatedRecording(fileNames, fullName);

        File folder = new File("ConcatenatedPersonalRecordings/");
        File[] ArrayOfFiles = folder.listFiles();
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && (file.getName()).contains(shortName.replaceAll(" ", "") + "-")) {
                    String recordingFileName = file.getName();
                    Character digit = recordingFileName.charAt(recordingFileName.lastIndexOf("-") + 1);
                    String recordingNumber = digit.toString();
                    String recordingName = (this.shortName + "-" + recordingNumber);
                    userAttempts.put(recordingName, new Recording(recordingFileName, "ConcatenatedPersonalRecordings/"));
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
        path = "ConcatenatedRecordings/";

    }

    public ObservableList<String> getUserAttempts() {
        ObservableList<String> personalRecordingsList = FXCollections.observableArrayList(userAttempts.keySet());
        Collections.sort(personalRecordingsList);
        return personalRecordingsList;
    }

    public int getUnusedAttemptsNumber(){
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

    public Recording getUserRecording(String name) {
        return userAttempts.get(name);
    }

    public void addAttempt(Recording attempt) {
        userAttempts.put(attempt.getShortName() ,attempt);
    }

    public void deleteAttempt(String attempt) {
        userAttempts.remove(attempt);
    }

    private void createConcatenatedRecording(List<String> fileNames, String fullItem) {

        List<String> tmpFiles = new ArrayList<>();
        int index = 1;
        for (String file : fileNames) {
            String tmpFileName = fullItem.replaceAll(" ", "");
            tmpFileName = tmpFileName + index + ".wav";
            tmpFiles.add(tmpFileName);
            index++;
        }

        final List<String> finalTempFiles = new ArrayList<>(tmpFiles);
        System.out.println(finalTempFiles);

        //remove silence either side for all recordings and create temp files for all
        //Temp file names are of the form name1name2name31, where 1 is the index of the temp file
        //Checked and works
        Task<Void> removeSilenceTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int index = 1;
                for (String file : fileNames) {
                    String tmpFileName = fullItem.replaceAll(" ", "");
                    tmpFileName = tmpFileName + index + ".wav";
                    String cmd = "ffmpeg -y -i " + "./Database/" + file + " -af silenceremove=1:0:-50dB " + tmpFileName;
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                    Process process = builder.start();
                    process.waitFor();
                    index++;
                }
                return null;
            }
        };
        Thread silenceThread = new Thread(removeSilenceTask);
        silenceThread.setDaemon(true);
        silenceThread.start();
        try {
            silenceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //regulate volume for all, have checked it changes volume but not that it equalises volume of multiple recordings
        Task<Void> regulateVolumeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (String file : finalTempFiles) {
                    String cmd = "ffmpeg -y -i "+file+" -filter:a \"volume=0.5\" " + file;
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                    Process process = builder.start();
                    process.waitFor();
                }
                return null;
            }
        };
        Thread volumeThread = new Thread(regulateVolumeTask);
        volumeThread.setDaemon(true);
        volumeThread.start();
        try {
            volumeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //concatenates the temp recordings then deletes them
        Task<Void> concatenateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String catInput = "";
                int number = 0;
                String inputStreams = "";
                for (String file : finalTempFiles) {
                    catInput = catInput + "-i " + file + " ";
                    inputStreams = inputStreams + "[" + number + ":0]";
                    number += 1;
                }
                String tmpFileName = "./ConcatenatedRecordings/" + fullItem.replaceAll(" ", "") + ".wav";
                String cmd = "ffmpeg -y " + catInput +
                        "-filter_complex '"+inputStreams+"concat=n="+number+":v=0:a=1[out]' " +
                        "-map '[out]' " + tmpFileName;
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();
                process.waitFor();
                return null;
            }
        };
        Thread catThread = new Thread(concatenateTask);
        catThread.setDaemon(true);
        catThread.start();
        try {
            catThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (String fileName : finalTempFiles) {
            File file = new File(fileName);
            file.delete();
        }
    }
}
