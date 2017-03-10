package ChallongeParser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import Data.Storage;
import org.joda.time.DateTime;

public class CustomForm {
    private JTextArea LogMessages;
    private JButton browseCSVFileButton;
    public JPanel mainPanel;
    private JTextArea PrintTournamentResults;
    private JButton ParseTournament;
    private JTextArea TournamentUrl;
    private File selectedFile;
    private String tournamentUrl;

    public CustomForm() {
        browseCSVFileButton.addActionListener(e -> getFileFromBrowse());
        ParseTournament.addActionListener(e -> parseTournament());
    }

    public void getFileFromBrowse () {
        JFileChooser fc =  new JFileChooser();
        fc.showOpenDialog(browseCSVFileButton);
        selectedFile = fc.getSelectedFile();
        logMessage("Got file: " + selectedFile.getAbsolutePath());
    }

    private void getTournamentUrl() {
        tournamentUrl = TournamentUrl.getText();
    }

    private void logMessage(String errorMessage) {
        LogMessages.append(errorMessage + "\n");
    }

    private void parseTournament() {
        PrintTournamentResults.setText("");
        getTournamentUrl();
        if (tournamentUrl == null) {
            PrintTournamentResults.append("Must pass in a challonge url in order to parse");
            return;
        }
        if (selectedFile == null) {
            PrintTournamentResults.append("CSV file was not selected to append to, just printing results. CSV file will NOT be changed.\n\n");
        }
        String csvData = getCSVData();
        if (csvData.isEmpty()) {
            PrintTournamentResults.setText("Failed to parse tournament");
            return;
        }
        PrintTournamentResults.append(csvData);
        if (selectedFile != null)
            appendToCsv(csvData);
    }

    private void appendToCsv(String csvData) {
        try {
            // TODO: Do we want to backup the file before we write to it? A simple Files.copy could do the trick pretty easily
            Files.write(Paths.get(selectedFile.getAbsolutePath()), csvData.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logMessage("There was a problem appending to your csv: " + e.getMessage());
        }
    }

    // Make an exe that opens a window
    // - Remove | and everything before them in tags
    // - ask for csv file
    // - ask for challonge url
    // - verify successful
    // - print every player in tournament, and their overall score
    // format:
    //player1Name,player1Score,player2Name,player2Score,tournamentDate (mm/dd/yyyy)
    private String getCSVData() {
        Storage tournamentData = null;
        String csvData = "";
        try {
            tournamentData = TournamentData.getTournamentData(tournamentUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tournamentData == null)
            return "";

        // Get the first date we find in the tournament, and use that for the rest of it.
        // TODO: Do we care if the tournament goes past midnight and a new date starts
        String dateString = getDateString(tournamentData.getString(tournamentData.findPaths("underway_at").get(0)));
        if (dateString == null)
            logMessage("Tournament had no findable dates to use in CSV");

        HashMap map = tournamentData.getAsDataStorage("matches_by_round").toMap();
        Set keyset = map.keySet();
        Iterator iter = keyset.iterator();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            ArrayList matches = tournamentData.getAsDataStorage(new String[] {"matches_by_round", key}).toArray();
            for (Object match : matches) {
                Storage m = new Storage(match.toString());
                String csvLine = "";
                csvLine += sanitizePlayerName(m.getString(new String[] {"player1", "display_name"})) + ",";
                csvLine += sanitizePlayerName(m.getString(new String[] {"player2", "display_name"})) + ",";
                csvLine += m.getInt(new String[] {"scores", "0"}) + ",";
                csvLine += m.getInt(new String[] {"scores", "1"}) + ",";
                csvLine += dateString + "\n";
                csvData += csvLine;
            }
        }
        return csvData;
    }

    // Translate a string from "2017-01-26T20:25:32.208-07:00" to "01/26/2017"
    private String getDateString(String unparsedDateString) {
        DateTime time = new DateTime(unparsedDateString);
        return time.getMonthOfYear() + "/" + time.getDayOfMonth() + "/" + time.getYear();
    }

    // Remove team tags, ie:
    // -> VK | OkayP.
    // -> OkayP.
    private String sanitizePlayerName(String playerName) {
        String regex = "^(.*?)\\| ?";
        return playerName.replaceAll(regex, "");
    }
}
