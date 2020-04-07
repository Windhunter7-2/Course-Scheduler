package CourseScheduler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CourseScheduler extends Application {// implements EventHandler<ActionEvent> {

    Stage stage;
    Scene scene;
    Button scrapeButton, contFromProfBtn;
    Scraper scraper;
    final int GUIHEIGHT = 900, GUIWIDTH = 1100;

    public void start(Stage primaryStage) throws Exception { //scraperGUI()
        try {
            primaryStage.setTitle("Rescrape Data");
            Text lastRunText = new Text("Data last updated on Nov 22, 1963");//scraper.getLastRun().toString());
            lastRunText.setStyle("-fx-font-size:30");
            scrapeButton = new Button("Scrape Now");
            scrapeButton.setPrefSize(300, 80);
            scrapeButton.setStyle("-fx-font-size:30");
            GridPane gp = new GridPane();
            gp.add(lastRunText, 0, 0);
            gp.add(scrapeButton, 0, 1);
            gp.setAlignment(Pos.CENTER);
            gp.setVgap(200);

            ColumnConstraints colCon = new ColumnConstraints();
            colCon.setHalignment(HPos.CENTER);
            gp.getColumnConstraints().add(colCon);

            primaryStage.setMaximized(true);
            Scene scene = new Scene(gp);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            System.out.println(e.toString() + " in CourseScheduler.start()\n");
        }
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}