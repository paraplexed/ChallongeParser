ChallongeParser
=======

A tool to parse challonge tournament results without needing a challonge api key.

Literally just scrapes the tournament pages and grabs the json object that challonge uses to format their page, does a little regex, and prints it out into a csv file

The UI will take a csv file path and print out the tournament results in the following format:

player1Name,player1Score,player2Name,player2Score,tournamentDate

Date is parsed into mm/dd/yyyy format

How to use
=======
- Make sure you have a java 1.8 JRE installed to run the jars
- Download the latest jar or exe from the releases folder
- Run the jar `java -jar challonge_parser-1.0.jar`, or double click the jar/exe if you already have a java jre set up
- In the UI, enter a tournament URL (ie http://apex2015melee.challonge.com/singles)
- Hit parse URL

How to build
=======
- Run `mvn clean install`, this will create a shaded fat jar that contains all dependencies
- Start up launch4j, point it at the fat jar, and manually create an exe, if a .exe file is needed

TODO
=======
- Create wercker pipeline for easy publishing of new jar to the github release
- If there's interest, I can publish it to maven central
- If there's interest, I can separate the UI into a separate git repository and provide the challonge parser as a standalone library
