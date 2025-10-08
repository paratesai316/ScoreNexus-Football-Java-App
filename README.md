# ScoreNexus: Football ⚽

ScoreNexus is a desktop application built with Java Swing for tracking live football match scores. It's designed for local games, college tournaments, or any situation where a digital scorecard is needed. The application features a dynamic UI for managing game events, saves all data to a local database using JDBC, and allows for exporting game statistics to CSV files.

##
## Features
- Custom Game Setup: Configure team names, game duration, and the number of players and substitutes.
- Drag & Drop Formations: Interactively set player positions on a visual pitch before the game starts.
- Live Game Tracking: A real-time timer with controls to start, pause, and resume the match.
- Detailed Player Stats: Record goals, assists, and issue yellow/red cards for each player by right-clicking on them.
- Substitution Management: Easily substitute players on the pitch with those from the bench.
- Persistent Storage: All game events, scores, and player stats are automatically saved to a local SQLite database via JDBC.
- CSV Data Export: After the game, export a complete summary, a game event timeline, and detailed player statistics to three separate `.csv ` files.

##
## Technologies Used
- Java: Core programming language.
- Java Swing: For the graphical user interface (GUI).
- JDBC (Java Database Connectivity): For database interaction.
- SQLite: The lightweight, file-based SQL database engine.

##
## Setup and Installation
Follow these steps to get the project running on your local machine.

### Prerequisites
Make sure you have the Java Development Kit (JDK) version 8 or higher installed on your system.

### Steps
1. Clone the repository:

    ```
    git clone https://github.com/paratesai316/ScoreNexus-Java-App.git
    cd ScoreNexus-Java-App
    ```

2. Set up dependencies:
    Create a folder named `\lib` in the project's root directory.
    Download the SQLite JDBC Driver JAR file from the ([official repository](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar)) and place it inside the `lib` folder.

3. Add resources:
    - Ensure the `resources` folder exists.
    - Place all necessary .png image files inside this folder:
        * `app_icon.png`
        * `app_logo.png`
        * `image_goal.png`, `image_assist.png`, etc.

##
## How to Compile and Run
   Open a terminal or Git Bash in the project's root directory (`ScoreNexus-Java-App/`) and run the following commands.

1. Compile the source code:
    ```
    javac -d . -cp ".;lib/sqlite-jdbc-3.43.0.0.jar" src/com/scorenexus/main/ScoreNexusApp.java src/com/scorenexus/model/*.java src/com/scorenexus/ui/*.java src/com/scorenexus/db/*.java src/com/scorenexus/util/*.java
    ```
    

2. Run the application:
    ```
    java -cp ".;lib/sqlite-jdbc-3.43.0.0.jar" com.scorenexus.main.ScoreNexusApp
    ```

3. Or alternatively:
    Execute the `.bat` file named `RunScoreNexus.bat`

The application window should now appear.
