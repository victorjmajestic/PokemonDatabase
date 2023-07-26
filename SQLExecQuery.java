/* EXECUTE THE POKEMON DATABASE
 * Project by: Victor Majestic, Jonathan Lutch, James Farmer
 * 
 * To run the program, insert the following commands in the VSCode Terminal or in the Windows Terminal:
 * javac SQLExecQuery.java
 * java SQLExecQuery
 * 
 * Type "Help" to see the list of queries we have implemented.
 * Type the respective number to run te query.
 * 
 * Requires the mssql-jdbc-11.2.0.jre11.jar and JDK 11.
 * Will work properly on this VM.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.*;

public class SQLExecQuery {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        List<Object> dbConnectionInfo = connectToDatabase();
        Connection connection = (Connection) dbConnectionInfo.get(0);
        Statement statement = (Statement) dbConnectionInfo.get(1);
        ResultSet resultSet = null;
        
        //Starting UI
        Scanner myScanner = new Scanner(System.in);
        System.out.println(ANSI_CYAN + "Welcome to the world of Pokemon!" + ANSI_RESET);
        while (true)
        {
            System.out.println("Please enter a number to choose which query you would like to execute. Type \"Help\" for help page.");
            String userResponse = myScanner.nextLine();
            if (userResponse.equalsIgnoreCase("Help"))
            {
                //print out the help page
                System.out.println("Type \"Quit\" to exit the program.");
                System.out.println("Type \"1\" to: change the nickname of all of Misty's Pokemon to \"JonathansPokemon\"");
                System.out.println("Type \"2\" to: select the entire record of all Pokemon who have the Water Primary_Type, over level 40, and can evolve.");
                System.out.println("Type \"3\" to: add a record of a battle between two Trainers.");
                System.out.println("Type \"4\" to: remove a Trainer from the database.");
                System.out.println("Type \"5\" to: increase the level of all Pokemon who won a user-specified battle by 1.");
                System.out.println("Type \"6\" to: select all Trainers who won at least a user-specified amount of battles.");
                System.out.println("Type \"2sp\" to: use a stored query in the SQL Server to return Pokemon with a level higher than a user's"
                + " arbitrary choice of level and with specific typings.");
                System.out.println("Type \"Species\" to: select all Species stored in the database.");
                System.out.println("Type \"Trainer\" to: select all Trainers stored in the database.");
                System.out.println("Type \"Pokemon\" to: select all individually owned Pokemon stored in the database.");
                System.out.println("Type \"TrainedBy\" to: select the ID of all Pokemon with the respective ID of its trainer.");
                System.out.println("Type \"Battle\" to: select all battle records stored in the database.");
                System.out.println("Type \"Evolution\" to: select all evolutions stored in the database.");
                System.out.println("Type \"PokemonBattleHistory\" or \"PBH\" to: view all Pokemon that participated in a battle.");
            }
            else if (userResponse.equalsIgnoreCase("Quit"))
            {
                //end the program
                System.out.println("Goodbye!");
                myScanner.close();
                System.exit(0);
            }
            else if (userResponse.equals("1"))
            {
                //Jonathan's 1st Query
                String jonathan1stQuery = "UPDATE Pokemon set Nickname = 'JonathansPokemon' FROM Pokemon" 
                + " JOIN TrainedBy ON Pokemon.ID = TrainedBy.Pokemon_ID JOIN Trainer ON Trainer.ID = TrainedBy.Trainer_ID WHERE Trainer.Name = 'Misty';";
                try 
                {
                    //Printing out names before they are changed
                    System.out.println("\n" + "Pokemon names before update:");
                    printPokemonNames("Misty", resultSet, statement);
                    try
                    {
                        System.out.println("Executing: " + ANSI_GREEN + jonathan1stQuery + ANSI_RESET + "\n");
                        //Want to make sure our transactions are atomic
                        connection.setAutoCommit(false);
                        statement.execute(jonathan1stQuery);
                        connection.commit();
                    }
                    catch (SQLException e)
                    {
                        System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                        e.printStackTrace();
                        connection.rollback();
                    }
                    finally
                    {
                        connection.setAutoCommit(true);
                    }
                    //Printing out names after they are changed
                    System.out.println("Pokemon names after update:");
                    printPokemonNames("Misty", resultSet, statement);
                } 
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equals("2sp"))
            {
                //Jonathan's SQL Server stored procedure experiment
                //Just the 2nd use case but better in every way.
                //Gives the user options to give custom inputs without feat of injection attacks.
                String GetPokemonByTypesLevelAndCanEvolve = 
                "{call dbo.GetPokemonByTypesLevelAndCanEvolve(?,?,?)}";
                
                try
                {
                    PreparedStatement prepsSelectPokemon =
                    connection.prepareStatement(GetPokemonByTypesLevelAndCanEvolve);
                    
                    System.out.println("What level do the Pokemon need to be above?");
                    int pokemonLevel = getUserInt(myScanner);
                
                    System.out.println("What is the primary type of Pokemon are you looking for?");
                    System.out.println("* for any primary type, and \"type0,type1, ...\" for a set.");
                    String pokemonPrimaryType = myScanner.nextLine();
                    //SQL table values dont play nice with random whitespaces
                    pokemonPrimaryType = pokemonPrimaryType.replaceAll("\\s", "");

                    System.out.println("What is the secondary type of Pokemon are you looking for?");
                    System.out.println("* for any secondary type, and \"type0,type1, ...\" for a set. \"null\" for null.");
                    String pokemonSecondaryType = myScanner.nextLine();
                    pokemonSecondaryType = pokemonSecondaryType.replaceAll("\\s", "");

                    prepsSelectPokemon.setInt(1, pokemonLevel);
                    prepsSelectPokemon.setString(2, pokemonPrimaryType);
                    prepsSelectPokemon.setString(3, pokemonSecondaryType);
                    resultSet = prepsSelectPokemon.executeQuery();
                    System.out.println("\n" + ANSI_GREEN + "Getting all Pokemon of primary type " + pokemonPrimaryType
                    + " with secondary type " + pokemonSecondaryType + " over level " + pokemonLevel + " that can evolve!" + ANSI_RESET + "\n");
                    System.out.println("[Pokemon_ID] [Pokedex_ID] [Nickname] [Height] [Weight] [Level]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2)
                            + " " + resultSet.getString(3) + " " + resultSet.getString(4)
                            + " " + resultSet.getString(5) + " " + resultSet.getString(6));
                    }
                    System.out.println("");
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equals("2"))
            {
                //Jonathan's 2nd query
                String jonathan2ndQuery = "SELECT * FROM Pokemon JOIN Species ON Pokemon.Pokedex_ID = Species.Pokedex_ID" 
                + " JOIN Evolution ON Pokemon.Pokedex_ID = Evolution.Pokedex_ID WHERE Level > 40 AND Primary_Type = 'Water';";
                try 
                {
                    resultSet = statement.executeQuery(jonathan2ndQuery);
                    //Print out the data of the pokemon that meet our criteria
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + jonathan2ndQuery + ANSI_RESET + "\n");
                    System.out.println("[Pokemon_ID] [Pokedex_ID] [Nickname] [Height] [Weight] [Level]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2)
                            + " " + resultSet.getString(3) + " " + resultSet.getString(4)
                            + " " + resultSet.getString(5) + " " + resultSet.getString(6));
                    }
                } 
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
                System.out.println("");
            }
            else if (userResponse.equals("3"))
            {   
                System.out.println("Please enter the winning trainer's ID.");
                String WTrainer = myScanner.nextLine();
                System.out.println("Please enter the winning pokemon's ID.");
                String WPokemon = myScanner.nextLine();//scanner var
                System.out.println("Please enter the losing trainer's ID.");
                String LTrainer = myScanner.nextLine();//scanner var
                System.out.println("Please enter the losing pokemon's ID.");
                String LPokemon = myScanner.nextLine();//scanner var

                // Add Battle Entry
                String insert = new String("INSERT INTO Battle (Winning_Trainer_ID, Losing_Trainer_ID) VALUES ('" + WTrainer +"', '" + LTrainer + "')");
                int battleID = 0;
                try{
                    try {
                        connection.setAutoCommit(false);
                        statement.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS); 
                        connection.commit();
                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                battleID = (generatedKeys.getInt(1));
                                System.out.println(battleID);
                            }
                            else {
                                throw new SQLException("Creating entry failed, no ID obtained.");
                            }
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        System.out.println("Couldn't grab Battle ID");
                        } 
                    } catch (SQLException e) {
                            connection.rollback();
                    } finally {
                            connection.setAutoCommit(true);
                    }
                    //Add Winner's Battle History
                    String winnerInsert = "INSERT INTO PokemonBattleHistory (Battle_ID, Pokemon_ID, Trainer_ID) VALUES ('" + battleID +"', '" + WTrainer + "', '" + WPokemon + "')";
                    try {
                        connection.setAutoCommit(false);
                        statement.executeUpdate(winnerInsert);
                        connection.commit();
                    } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("Winner update failed");
                            e.printStackTrace();
                    } finally {
                            connection.setAutoCommit(true);
                    }
    
                    //Add Loser's Battle History
                    String loserInsert = "INSERT INTO PokemonBattleHistory (Battle_ID, Pokemon_ID, Trainer_ID) VALUES ('" + battleID +"', '" + LTrainer + "', '" + LPokemon + "')";
                    try {
                        connection.setAutoCommit(false);
                        statement.executeUpdate(loserInsert);
                        connection.commit();
                    } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("Loser update failed");
                            e.printStackTrace();
                    } finally {
                        connection.setAutoCommit(true);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (userResponse.equals("4"))
            { 
                System.out.println("Please enter the ID of the trainer you would like to delete");
                String delTrainer = myScanner.nextLine();

                String selectSql = new String("SELECT pokemon.Nickname FROM pokemon JOIN TrainedBy on pokemon.ID = TrainedBy.Pokemon_ID WHERE TrainedBy.Trainer_ID = " + delTrainer);
                try {
                    resultSet = statement.executeQuery(selectSql);

                    // Print results from select statement
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1) + " is now wild.");
                    }
                    String deleteBattles = "DELETE FROM Battle WHERE Winning_Trainer_ID = " + delTrainer + " OR Losing_Trainer_ID = " + delTrainer;
                    
                    try {
                        connection.setAutoCommit(false);
                        statement.executeUpdate(deleteBattles);
                        connection.commit();
                    } catch (SQLException e) {
                        System.out.println("Could not delete prerequisites");
                        connection.rollback();
                    } finally {
                        connection.setAutoCommit(true);
                    }
                    String delete = "DELETE FROM Trainer WHERE id = " + delTrainer;
                    
                    try {
                        connection.setAutoCommit(false);
                        statement.executeUpdate(delete);
                        connection.commit();
                    } catch (SQLException e) {
                        System.out.println("Could not delete trainer");
                        connection.rollback();
                    } finally {
                        connection.setAutoCommit(true);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (userResponse.equals("5"))
            {
                // Victor's 1st Query
                System.out.println("Please enter the battle ID for which the winner's Pokemon gain experience.");
                String battID = myScanner.nextLine();
                String victorQuery1Sel = "SELECT Pokemon.ID, Nickname, Level from Pokemon JOIN PokemonBattleHistory ON Pokemon.ID = PokemonBattleHistory.Pokemon_ID JOIN Battle ON PokemonBattleHistory.Battle_ID = Battle.ID where PokemonBattleHistory.Trainer_ID = Battle.Winning_Trainer_ID and Battle_ID = " + battID;
                String victorQuery1Upd = "UPDATE Pokemon set Level = Level + 1 from Pokemon JOIN PokemonBattleHistory ON Pokemon.ID = PokemonBattleHistory.Pokemon_ID JOIN Battle ON PokemonBattleHistory.Battle_ID = Battle.ID where PokemonBattleHistory.Trainer_ID = Battle.Winning_Trainer_ID and Battle_ID = " + battID;
                try
                {
                    try
                    {
                        resultSet = statement.executeQuery(victorQuery1Sel);
                        System.out.println("\n" + "Trainer's Pokemon Before the Update:");
                        System.out.println("\n" + "Executing: " + ANSI_GREEN + victorQuery1Sel + ANSI_RESET + "\n");
                        System.out.println("[ID] [Nickname] [Level]");
                        while (resultSet.next())
                        {
                            System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                        }
                    }
                    catch (SQLException e)
                    {
                        System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                        e.printStackTrace();
                    }
                    try
                    {
                        connection.setAutoCommit(false);
                        statement.execute(victorQuery1Upd);
                        connection.commit();
                    }
                    catch (SQLException e) 
                    {
                        connection.rollback();
                        System.out.println(ANSI_RED + "There was an error updating the database. The transaction has been rolled back." + ANSI_RESET);
                    }
                    finally {
                        connection.setAutoCommit(true);
                    }
                    try
                    {
                        resultSet = statement.executeQuery(victorQuery1Sel);
                        System.out.println("\n" + "Trainer's Pokemon After the Update:");
                        System.out.println("\n" + "Executing: " + ANSI_GREEN + victorQuery1Sel + ANSI_RESET + "\n");
                        System.out.println("[ID] [Nickname] [Level]");
                        while (resultSet.next()) {
                            System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                        }
                    }
                    catch (SQLException e)
                    {
                        System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                        e.printStackTrace();
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equals("6"))
            {
                // Victor's 2nd Query
                System.out.println("Please enter the minimum number of wins.");
                String numWins = myScanner.nextLine();
                String victorQuery2 = "SELECT * from Trainer WHERE (SELECT COUNT(*) from Battle where Trainer.ID = Battle.Winning_Trainer_ID) >= " + numWins;
                try
                {
                    resultSet = statement.executeQuery(victorQuery2);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + victorQuery2 + ANSI_RESET + "\n");
                    System.out.println("[ID] [Name] [Age] [Height] [Weight]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("Species"))
            {
                // Return the entire species table.
                String speciesQuery = "SELECT * from Species";
                try
                {
                    resultSet = statement.executeQuery(speciesQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + speciesQuery + ANSI_RESET + "\n");
                    System.out.println("[Pokedex ID] [Name] [Primary Type] [Secondary Type]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("Trainer"))
            {
                // Return the entire trainer table.
                String trainerQuery = "SELECT * from Trainer";
                try
                {
                    resultSet = statement.executeQuery(trainerQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + trainerQuery + ANSI_RESET + "\n");
                    System.out.println("[ID] [Name] [Age] [Height] [Weight]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("Pokemon"))
            {
                // return the pokemon table
                String pkmnQuery = "SELECT * from Pokemon";
                try
                {
                    resultSet = statement.executeQuery(pkmnQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + pkmnQuery + ANSI_RESET + "\n");
                    System.out.println("[ID] [Pokedex ID] [Nickname] [Height] [Weight]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("TrainedBy"))
            {
                // return the trainedby table
                String trainerQuery = "SELECT * from TrainedBy";
                try
                {
                    resultSet = statement.executeQuery(trainerQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + trainerQuery + ANSI_RESET + "\n");
                    System.out.println("[Pokemon ID] [Trainer ID]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("Battle"))
            {
                // return the battle table
                String battleQuery = "SELECT * from Battle";
                try
                {
                    resultSet = statement.executeQuery(battleQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + battleQuery + ANSI_RESET + "\n");
                    System.out.println("[Battle ID] [Winning Trainer ID] [Losing Trainer ID]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("Evolution"))
            {
                // return the evolution table
                String evoQuery = "SELECT * from Evolution";
                try
                {
                    resultSet = statement.executeQuery(evoQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + evoQuery + ANSI_RESET + "\n");
                    System.out.println("[Pokedex ID] [Evolution ID]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else if (userResponse.equalsIgnoreCase("PokemonBattleHistory") || userResponse.equalsIgnoreCase("PBH"))
            {
                // return the pokemon battle history table
                String pbhQuery = "SELECT * from PokemonBattleHistory";
                try
                {
                    resultSet = statement.executeQuery(pbhQuery);
                    System.out.println("\n" + "Executing: " + ANSI_GREEN + pbhQuery + ANSI_RESET + "\n");
                    System.out.println("[Battle ID] [Pokemon ID] [Trainer ID]");
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(ANSI_RED + "The input is invalid. Please try a valid input." + ANSI_RESET);
            }
        }
    }
    
    /**
     * Get an int from the user via stdin
     * @param myScanner Scanner object we have been using to interact with user
     * @return  An int that signifies a Pokemon's Level
     */
    public static int getUserInt(Scanner myScanner)
    {
        boolean noValidInput = true;
        while (noValidInput)
        {
            try
            {
                //user may not give int!
                int pokemonLevel = Integer.parseInt(myScanner.nextLine());
                return pokemonLevel;
            }
            catch (Exception e)
            {
                System.out.println("Please enter an integer!");
            }
        }
        return -1;
    }   
    
    /**
     * This prints out all the names of a specific trainer's Pokemon.
     * @param trainerName The name of the trainer whose Pokemon we are renaming
     * @param resultSet Results of our query
     * @param statement Object we use to connect to database
     */
    public static void printPokemonNames(String trainerName, ResultSet resultSet, Statement statement)
    {
        System.out.println("[Nickname]");
        try 
        {
            resultSet = statement.executeQuery("SELECT Nickname FROM Pokemon JOIN TrainedBy ON Pokemon.ID = TrainedBy.Pokemon_ID" 
            + " JOIN Trainer ON Trainer.ID = TrainedBy.Trainer_ID WHERE Trainer.Name = 'Misty';");
            while (resultSet.next())
            {
               System.out.println(resultSet.getString(1));
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(ANSI_RED + "Something went wrong when querying the database! Here is the error message:" + ANSI_RESET);
            e.printStackTrace();
        }
        System.out.println("");
    } 
    
    /**
     * Initalizes the connection to the database.
     */
    public static List<Object> connectToDatabase()
    {
        String connectionUrl = 
        "jdbc:sqlserver://localhost;"
                + "database=pokemon;"
                + "user=dbuser;"
                + "password=scsd431134dscs;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=15;";
        try
        {
            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();
            return Arrays.asList(connection, statement);
        }
        catch (SQLException e)
        {
            System.out.println("Something went wrong when connecting to the database!");
            System.out.println("Make sure that the database is running. Closing program for now. Error is below:");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}