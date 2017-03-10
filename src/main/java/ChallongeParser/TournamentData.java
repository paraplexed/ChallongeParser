package ChallongeParser;

import Data.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TournamentData {
    // Parses a single line of javascript that looks like this:
    // window._initialStoreState['TournamentStore'] = {"key":"value pairs"};
    //
    // These objects contain all tournament data, and since minified javascript objects
    // are out of the box valid json, we put load it into a Data.Storage object so we can reformat into the data we wan:w
    public static Storage getTournamentData(String challongeUrl) throws IOException {
        URL url = new URL(challongeUrl);
        InputStream inputStream = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String unStrippedData = null;

        while((line = br.readLine()) != null) {
            if (line.contains("['TournamentStore']")) {
                unStrippedData = line;
            }
        }

        if (unStrippedData != null) {
            Storage tournamentData = parseTournamentData(unStrippedData);
            return tournamentData;
        }
        else {
            throw new RuntimeException("Could not find tournament data in url provided: " + challongeUrl);
        }
    }

    private static Storage parseTournamentData(String unStrippedData) {
        Storage tournamentData = new Storage();

        // Regex searches for the first { and groups everything between { and ;
        // so {"key","value"}; would find and group {"key","value"} -- leaving off the ';' for our json parser
        String regex = "(\\{.+?(?=\\}; ))";
        Matcher matcher = Pattern.compile(regex).matcher(unStrippedData);

        while(matcher.find()) {
            // Our regex strips off the }; at the end since that's our delimiter, but we actually want the '}' for the json parser
            tournamentData = new Storage(matcher.group(1) + "}");
            if (tournamentData.has("tournament"))
                break;
        }

        if (! tournamentData.has("tournament"))
            throw new RuntimeException("Something went wrong while parsing the tournament");

        return tournamentData;

    }
}
