package namesayer.recordingTypes;

import javafx.concurrent.Task;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.helperClasses.DatabaseList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
        this.shortName = "";
        String[] splitName = fullName.split(" ");
        for (String namePart : splitName) {
            this.shortName = this.shortName + namePart.substring(0,1).toUpperCase() + namePart.substring(1).toLowerCase() + " ";
        }
        this.shortName = this.shortName.trim();
        this.fileName = fullName.replaceAll("[\\s+\\(\\)]", "") + ".wav";
        removeRecordingSilence(fileNames, fullName);

		//loads all the personal recordings into the map
        File folder = new File("Resources/ConcatenatedPersonalRecordings/");
        File[] ArrayOfFiles = folder.listFiles();
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && (file.getName()).contains(shortName.replaceAll(" ", "") + "-")) {
                    String recordingFileName = file.getName();
                    Character digit = recordingFileName.charAt(recordingFileName.lastIndexOf("-") + 1);
                    String recordingNumber = digit.toString();
                    String recordingName = (this.shortName + "-" + recordingNumber);
                    userAttempts.put(recordingName, new PersonalRecording(recordingFileName, "Resources/ConcatenatedPersonalRecordings/"));
                }
            }
        } else {
            //Cant find any personal recordings
        }
        path = "Resources/ConcatenatedRecordings/";

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
            tmpFiles.add(tmpFileName.replaceAll("[\\(\\)]", ""));
            index++;
        }

        //remove silence either side for all recordings and creates the temp files for all
        Task<Void> removeSilenceTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int index = 1;
                for (String file : fileNames) {
                    String tmpFileName = fullItem.replaceAll("[\\(\\)\\s+]", "");
                    tmpFileName = tmpFileName + index + ".wav";
                    String cmd = "ffmpeg -y -i " + "./Resources/Database/" + file + " -af silenceremove=1:0:-50dB " + tmpFileName;
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
                String fileName = "./Resources/ConcatenatedRecordings/" + fullItem.replaceAll("[\\s+\\)\\(]", "") + ".wav";
                String cmd = "ffmpeg -y " + catInput +
                        "-filter_complex '"+inputStreams+"concat=n="+number+":v=0:a=1[out]' " +
                        "-map '[out]' " + fileName;
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();
                process.waitFor();
                return null;
            }
            @Override
            protected void succeeded() {
                DatabaseList helperList = new DatabaseList();
                helperList.add(getThisRecording());
                WorkspaceModel model = WorkspaceModel.getInstance();
                model.addToCurrentWorkspaceRecordings(helperList);
                model.notifyOfConcatenateCompletion();
                deletetmpFiles(tmpFiles);
            }
        };
        Thread catThread = new Thread(concatenateTask);
        catThread.setDaemon(true);
        catThread.start();

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
