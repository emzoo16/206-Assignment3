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
 *This class is used to represent a recording which has been concatenated from database recordings 
 *It functions similar to database recordings except the process for building the concatenated recording
 *takes place in the class and the method of obtaining the user attempts is slightly different
 *
 */
public class ConcatenatedRecording extends DemoRecording {
	//The file names of the parts of the concatenation
    private List<String> listOfFilesNames;
    private ConcatenatedRecordingLoader controller;


    public ConcatenatedRecording(List<String> fileNames, String fullName, ConcatenatedRecordingLoader controller) {
        this.controller = controller;
        userAttempts = new HashMap<>();
		//Short name is the name which is displayed
        this.shortName = fullName;
        this.fileName = fullName.replaceAll(" ", "") + ".wav";
        removeRecordingSilence(fileNames, fullName);

		//loads all the personal recordings into the map
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
                    userAttempts.put(recordingName, new PersonalRecording(recordingFileName, "ConcatenatedPersonalRecordings/"));
                }
            }
        } else {
            //Cant find any personal recordings
        }
        path = "ConcatenatedRecordings/";

    }


	/**
	*Creates the concatenated recording by first removing the excess silence and normalising the volume
	*/
    private void removeRecordingSilence(List<String> fileNames, String fullItem) {
	
		//Get the names of all temporary files to concatenate
        List<String> tmpFiles = new ArrayList<>();
        int index = 1;
        for (String file : fileNames) {
            String tmpFileName = fullItem.replaceAll(" ", "");
            tmpFileName = tmpFileName + index + ".wav";
            tmpFiles.add(tmpFileName);
            index++;
        }

        final List<String> finalTempFiles = new ArrayList<>(tmpFiles);

        //remove silence either side for all recordings and creates the temp files for all
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
            @Override
            protected void succeeded() {
                concatenateRecordings(tmpFiles, fullItem);
            }
        };
        Thread silenceThread = new Thread(removeSilenceTask);
        silenceThread.setDaemon(true);
        silenceThread.start();
    }

    private void concatenateRecordings(List<String> tmpFiles, String fullItem) {
        //concatenates the temp recordings then deletes them
        Task<Void> concatenateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String catInput = "";
                int number = 0;
                String inputStreams = "";
                for (String file : tmpFiles) {
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
            @Override
            protected void succeeded() {
                regulateRecordingVolume(tmpFiles, fullItem);
            }
        };
        Thread catThread = new Thread(concatenateTask);
        catThread.setDaemon(true);
        catThread.start();

    }

    private void regulateRecordingVolume(List<String> tmpFiles, String fullItem) {
        //regulate volume for all
        Task<Void> regulateVolumeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (String file : tmpFiles) {
                    String cmd = "ffmpeg -y -i "+file+" -filter:a \"volume=0.5\" " + file;
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                    Process process = builder.start();
                    process.waitFor();
                }
                return null;
            }
            @Override
            protected void succeeded() {
                //This list allows us to reuse the addPlaylistRecordings method.
                DatabaseList helperList = new DatabaseList();
                helperList.add(getThisRecording());
                controller.addPlaylistRecordings(helperList);
                deletetmpFiles(tmpFiles);
            }
        };
        Thread volumeThread = new Thread(regulateVolumeTask);
        volumeThread.setDaemon(true);
        volumeThread.start();
    }

    private void deletetmpFiles(List<String> tmpFiles) {
        //deletes the temp files
        for (String fileName : tmpFiles) {
            File file = new File(fileName);
            file.delete();
        }
    }

    private ConcatenatedRecording getThisRecording() {
        return this;
    }
}
