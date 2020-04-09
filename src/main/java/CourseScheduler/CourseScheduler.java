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
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class CourseScheduler extends Application {

    Stage stage;
    Scene scene;
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