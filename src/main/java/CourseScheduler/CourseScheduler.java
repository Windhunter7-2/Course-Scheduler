package CourseScheduler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CourseScheduler extends Application {

    Stage stage;
    Scene scene;
    ArrayList<String> profilenames = new ArrayList<>();
    Button scrapeButton;
    Scraper scraper;
    final int GUIHEIGHT = 900, GUIWIDTH = 1100;

    public void start(Stage primaryStage) throws Exception { //All GUI method calls will go in here
        try {
            scraperGUI(primaryStage);
        } catch(Exception e) {
            //Basic Exception catch for now - will expand later.
            System.out.println(e.toString() + " in CourseScheduler.start()\n");
        }
        try {
            scraperGUI(primaryStage);
        } catch(Exception e) {
            //Basic Exception catch for now - will expand later.
            System.out.println(e.toString() + " in CourseScheduler.start()\n");
        }
    }
    public void profileGUI(Stage stage){
        //ArrayList<String> profilenames = new ArrayList<>();
        profilenames.add("Akeem");
        profilenames.add("Jack");
        profilenames.add("Nathan");
        profilenames.add("Evan");

        StackPane pane = new StackPane();
        ComboBox<String> selectprofile = new ComboBox();
        // create a text input dialog
        TextInputDialog td = new TextInputDialog();
        // setHeaderText
        td.setHeaderText("enter your profile name");
        // create a event handler to create new profiles
        EventHandler<ActionEvent> event = e -> {
            // show the text input dialog

            String value = (String) selectprofile.getValue();
            //Creates a new profile when "create new profile" isselected from the combo box
            if(value.equals("Create New Profile")) {
                System.out.println(profilenames);
                td.showAndWait();
                //creates profile object from the Profile class
                createProfile(td.getEditor().getText());
                selectprofile.getItems().add(td.getEditor().getText());
            }
            else{
                loadProfile(value);
                getProfileList();
            }

        };


        selectprofile.getItems().add("Create New Profile");
        //selectprofile.setOnAction(e->createnewprofile(selectprofile));
        selectprofile.getItems().addAll(profilenames);
        selectprofile.setOnAction(event);
       // selectprofile.setOnAction(e->loadProfile());

        // primaryStage.setScene(new Scene(root, 300, 275));
        pane.getChildren().addAll(selectprofile);
        stage.setScene(new Scene(pane, 300, 275));
        stage.show();


    }

    /**
     * creates a new profile.
     * @param name name of the user
     */
    public void createProfile(String name){
        Profile newprofile = new Profile(name);
        newprofile.insertProfileDB();
        System.out.println("this is my profile name "+ newprofile.getName());
    }
    /**
     * loads a profile from the database.
     *
     */
    public void loadProfile(String name){

    }
    /**
     * gets the list of profiles .
     *
     */
    public ArrayList<String> getProfileList(){
        return profilenames;
    }
    //Helper method to update the display of the time last run once scraping is complete.
    private void updateTime(Label timeLabel, DateTimeFormatter dtf) {
        timeLabel.setText("Last updated on " + dtf.format(LocalDateTime.now())); //Change to scraper.getLastRun().toString()); once method is complete.
    }

    public void scraperGUI(Stage stage) {
        //Set up stage, and elements of window.
        stage.setTitle("Rescrape Data");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy - hh:mm:ss");
        String lastUpdatedString = "Last updated on " + dtf.format(LocalDateTime.now());
        Label lastRun = new Label(lastUpdatedString);  //Change to scraper.getLastRun().toString());
        lastRun.setStyle("-fx-font-size:30");
        scrapeButton = new Button("Scrape Now");
        scrapeButton.setPrefSize(300, 80);
        scrapeButton.setStyle("-fx-font-size:30");
        Label scraperStatus = new Label("");

        //Lay out the elements in a GridPane.
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(200);
        gp.add(lastRun, 0, 0);
        gp.add(scrapeButton, 0, 1);
        gp.add(scraperStatus, 0, 2);

        //Set constraints - center everything so it's nice and pretty.
        ColumnConstraints colCon = new ColumnConstraints();
        colCon.setHalignment(HPos.CENTER);
        gp.getColumnConstraints().add(colCon);

        //Set window (stage) size and
        stage.setMaximized(true);
        Scene scene = new Scene(gp);

        //Define task (scraping with message updates)
        Task<Void> scrapeTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                Runnable timeUpdater = new Runnable() {
                    @Override
                    public void run() {
                        updateTime(lastRun, dtf);
                    }
                };
                updateMessage("Scraping...");
                //Replace later with the scrape() function.
                Thread.sleep(3000);
                updateMessage("Scraping Complete");
                Platform.runLater(timeUpdater);
                return null;
            }
        };
        //Define behavior of scrapeButton
        EventHandler<ActionEvent> scrapeBtnPressed = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent scrapeReq) {
                Thread scrapeThread = new Thread(scrapeTask);
                scrapeThread.setDaemon(true);
                scrapeThread.start();
                scrapeButton.setDisable(false);
            }
        };

        //Configure dynamic scene elements.
        scrapeButton.setOnAction(scrapeBtnPressed);
        scraperStatus.textProperty().bind(scrapeTask.messageProperty());

        //Show the scene created above.
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}