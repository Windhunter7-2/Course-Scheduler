package CourseScheduler;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class CourseScheduler extends Application {

    Stage stage;
    Scene scene;
    ArrayList<String> profilenames = new ArrayList<>();
   //Button scrapeButton;
    Scraper scraper;
    final int GUIHEIGHT = 900, GUIWIDTH = 1100;

    public void start(Stage primaryStage) throws Exception { //All GUI method calls will go in here
        try {
//            scraper = new Scraper(new Catalog(new Database("testDB")));
//            scraperGUI(primaryStage);
            ArrayList<Course> courses = new ArrayList<Course>();
            Course test1 = new Course("Software Engineering", "CS 321", "CS",
                    3, "cee ess, threehundredtwentyone", "CS-321",
                    new ArrayList<String>(), new ArrayList<String>(), 300, "someParent");
            Course test2 = new Course("Fake Class", "FAKE 001", "FAKE",
                    4, "eff ay kay ee zerozeroone", "FAKE-001",
                    new ArrayList<String>(), new ArrayList<String>(), 300, "someParent");
            Course test3 = new Course("Introduction to Biology", "BIOL 101", "BIOL",
                    4, "bee eye oh ell onezeroone", "BIOL-101",
                    new ArrayList<String>(), new ArrayList<String>(), 300, "someParent");
            ArrayList<String> prereq = new ArrayList<String>();
            prereq.add(test1.getName());
            courses.add(new Course("Software Architecture", "CS 310", "CS",
                    3, "Cee ess threehundredten", "CS-310",
                    prereq, new ArrayList<String>(), 6969, "Everything"));
            courses.add(test1);
            courses.add(test2);
            courses.add(test3);
            checkListGUI(courses, new Profile("Jack"), primaryStage, this);
        } catch(Exception e) {
            //Basic Exception catch for now - will expand later.
            e.printStackTrace();
        }
//        try {
//            scraperGUI(primaryStage);
//        } catch(Exception e) {
//            //Basic Exception catch for now - will expand later.
//            System.out.println(e.toString() + " in CourseScheduler.start()\n");
//        }
    }
    public void profileGUI(Stage stage){
        //ArrayList<String> profilenames = new ArrayList<>();
        profilenames.add("Akeem");
        profilenames.add("Jack");
        profilenames.add("Nathan");
        profilenames.add("Evan");

       // StackPane pane = new StackPane();
        GridPane pane = new GridPane();

        ComboBox<String> selectprofile = new ComboBox();
        Label welcomeLabel = new Label("Welcome To Course Scheduler");
        Button btnContinue = new Button("Continue");
        btnContinue.setOnAction(e->stage.setScene(new Scene(pane, 300, 275)));
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
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
       /* pane.prefHeight(50.0);
        pane.prefWidth(50.0);*/
        pane.setVgap(50);
        pane.add(selectprofile, 0, 1);
        pane.add(welcomeLabel, 0, 0);
        pane.add(btnContinue, 1,1 );
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

    /**
     * CourseHelper is exactly akin to a course, but it tracks locally whether or not it has
     * been added to the "needed" or "done" lists of a corresponding profile.
     */
    private static class CourseHelper {
        public Course course;
        public BooleanProperty reqd;
        public BooleanProperty done;

        public CourseHelper(Course _course, Boolean _reqd, Boolean _done) {
            this.course = _course;
            reqd = new SimpleBooleanProperty(_reqd);
            done = new SimpleBooleanProperty(_done);
        }

        public BooleanProperty reqdProperty() {
            return reqd;
        }

        public final boolean isReqd() {
            return reqdProperty().get();
        }

        public final void setReqd(boolean required) {
            reqdProperty().set(required);
        }

        public BooleanProperty doneProperty() {
            return done;
        }

        public final boolean isDone() {
            return doneProperty().get();
        }

        public final void setDone(boolean completed) {
            doneProperty().set(completed);
        }

        public String toString() {
            return this.course.getCode() + "\n(" + Integer.toString(this.course.getCredits()) + " credits)";
        }
    }

    /**
     * Turns a List of courses into a list of CourseHelpers
     * @param courseList the list of courses to be converted.
     * @param profile the profile with which to check if courses have been marked "needed" or "done".
     * @return the List or CourseHelpers
     */
    private static List<CourseHelper> toHelperList(List<Course> courseList, Profile profile) {
        List<CourseHelper> helperList = new ArrayList<>();
        for(int i = 0; i < courseList.size(); i++) {
            if(courseList.get(i) != null) {
                helperList.add(new CourseHelper(
                        courseList.get(i),
                        profile.getNeededCourses().contains(courseList.get(i).getCode()),
                        profile.getDoneCourses().contains(courseList.get(i).getCode())));
            }
        }
        return helperList;
    }

    /**
     * Takes the course codes found in profile's needed and doneClasses lists and finds their corresponding courses.
     * @param courses the list of all courses.
     * @param code the code from which to find the corresponding course.
     * @return the course corresponding to the code provided.
     */
    private Course getCourseByCode(List<Course> courses, String code) {
        Course ret = courses.get(0);
        for(int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getCode().equals(code)) {
                ret = courses.get(i);
            }
        }
        return ret;
    }

    /**
     * This converts a course to a string containing all of its information - for comparison
     * to the user's search in checkListGUI().
     * @param course the course to be converted to a String.
     */
    private static String getAllCourseInfo(Course course) {
        return (course.getFullName() + course.getName() + Integer.toString(course.getCredits()) + course.getDesc());
    }

    /**
     * This is the GUI for the Course checklist displayed by the program, where the user can use a Course search,
     * as well as change their current Profile settings via checkbox selection of Courses and/or changing their
     * credits or semester counts. The parameters are the list of *all* Courses, as well as the user’s chosen Profile.
     * The user has the option to click on "Back" to go back to the previous, main GUI (selectProfile()); after the user
     * has selected the "Save" button, generateSchedule() is called, with the list of "Needed" Courses chosen by the
     * user, but *without* any instances of the "Done" Courses as prerequisites to *any* of those Courses, as the
     * parameter.
     */
    public void checkListGUI(List<Course> courseList, Profile profile, Stage stage, CourseScheduler cs) {
        TableView<Course> courseTable = new TableView<Course>();
        TableView<CourseHelper> checkBoxTable = new TableView<CourseHelper>();
        List<CourseHelper> helperList = CourseScheduler.toHelperList(courseList, profile);
        List<CourseHelper> newDoneHelpers = new ArrayList<CourseHelper>();
        List<CourseHelper> newNeededHelpers = new ArrayList<CourseHelper>();
        for(int i = 0; i < helperList.size(); i++) {
            CourseHelper temp = helperList.get(i);
            if(temp.isDone()) {
                newDoneHelpers.add(temp);
            }
            if(temp.isReqd()) {
                newNeededHelpers.add(temp);
            }
        }
        ObservableList<CourseHelper> helpers = FXCollections.observableArrayList(helperList);
        ObservableList<Course> courses = FXCollections.observableArrayList(courseList);
        stage.setTitle("Create Your Schedule");
        Scene scene = new Scene(new Group());
        //BEGIN RIGHT MODULE
        final Label neededHeader = new Label("Needed Classes");
        neededHeader.setFont(new Font("Arial", 20));
        ListView<CourseHelper> neededList = new ListView<>();
        neededList.getItems().addAll(newNeededHelpers);
        neededList.setCellFactory(CheckBoxListCell.forListView(CourseHelper::reqdProperty));
        final Label doneHeader = new Label("Done Classes");
        doneHeader.setFont(new Font("Arial", 20));
        ListView<CourseHelper> doneList = new ListView<>();
        doneList.getItems().addAll(newDoneHelpers);
        doneList.setCellFactory(CheckBoxListCell.forListView(CourseHelper::doneProperty));
        VBox neededDoneVBox = new VBox(5, neededHeader, neededList, doneHeader, doneList);
        //END RIGHT MODULE
        //BEGIN MIDDLE MODULE
        final Label selectClassesHeader = new Label("Select Classes");
        selectClassesHeader.setFont(new Font("Arial", 20));

        courseTable.setEditable(true);
        checkBoxTable.setEditable(true);
        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        checkBoxTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CourseHelper, Boolean> needBoxes = new TableColumn("Required");
        needBoxes.setMinWidth(75);
        needBoxes.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<CourseHelper, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<CourseHelper, Boolean> param) {
                        if(param.getValue().isReqd() && !neededList.getItems().contains(param.getValue())) {
                            neededList.getItems().add(param.getValue());
                        }
                        else if(!param.getValue().isReqd() && neededList.getItems().contains(param.getValue())) {
                            neededList.getItems().remove(param.getValue());
                        }
                        return param.getValue().reqd;
                    }
                }
        );
        needBoxes.setCellFactory(CheckBoxTableCell.forTableColumn(needBoxes));

        TableColumn<CourseHelper, Boolean> doneBoxes = new TableColumn("Completed");
        doneBoxes.setMinWidth(75);
        doneBoxes.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<CourseHelper, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<CourseHelper, Boolean> param) {
                        if(param.getValue().isDone() && !doneList.getItems().contains(param.getValue())) {
                            doneList.getItems().add(param.getValue());
                        }
                        else if(!param.getValue().isDone() && doneList.getItems().contains(param.getValue())) {
                            doneList.getItems().remove(param.getValue());
                        }
                        return param.getValue().done;
                    }
                }
        );
        doneBoxes.setCellFactory(CheckBoxTableCell.forTableColumn(doneBoxes));

        TableColumn courseCreds = new TableColumn("Credits");
        courseCreds.setMinWidth(50);
        courseCreds.setCellValueFactory(new PropertyValueFactory<Course, String>("credits"));

        TableColumn courseAbbrevs = new TableColumn("Course Code");
        courseAbbrevs.setMinWidth(100);
        courseAbbrevs.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));

        TableColumn courseNames = new TableColumn("Course Name");
        courseNames.setMinWidth(150);
        courseNames.setCellValueFactory(new PropertyValueFactory<Course, String>("fullName"));

        FilteredList<Course> flCourse = new FilteredList(courses, p -> true);
        FilteredList<CourseHelper> flChecks = new FilteredList(helpers, p -> true);
        courseTable.setItems(flCourse);
        checkBoxTable.setItems(flChecks);
        courseTable.getColumns().addAll(courseCreds, courseAbbrevs, courseNames);
        checkBoxTable.getColumns().addAll(needBoxes, doneBoxes);

        TextField courseSearchBox = new TextField();
        courseSearchBox.setPromptText("Search a course...");
        courseSearchBox.setOnKeyReleased(keyEvent -> {
            flCourse.setPredicate(p -> CourseScheduler.getAllCourseInfo(p).toLowerCase().trim().contains(courseSearchBox.getText().toLowerCase().trim()));
            flChecks.setPredicate(p -> CourseScheduler.getAllCourseInfo(p.course).toLowerCase().trim().contains(courseSearchBox.getText().toLowerCase().trim()));
        });

        HBox searchHBox = new HBox(courseSearchBox);
        searchHBox.setAlignment(Pos.CENTER_LEFT);
        HBox tableHBox = new HBox(checkBoxTable, courseTable);
        tableHBox.setAlignment(Pos.CENTER);

        final VBox tableSearchVBox = new VBox();
        tableSearchVBox.setSpacing(5);
        tableSearchVBox.setPadding(new Insets(10, 0, 0, 10));
        tableSearchVBox.getChildren().addAll(selectClassesHeader, searchHBox, tableHBox);
        //END MIDDLE MODULE
        //BEGIN LEFT MODULE
        final Label optionsHeader = new Label("Options");
        optionsHeader.setFont(new Font("Arial", 20));
        final Label creditsLabel = new Label("Maximum Credits\nper Semester");
        final Label semestersLabel = new Label("Maximum Number\nof Semesters");
        TextField creditsTextBox = new TextField();
        creditsTextBox.setPromptText("15");
        TextField semestersTextBox = new TextField();
        semestersTextBox.setPromptText("8");
        Button backButton = new Button("Back");
        backButton.setPrefSize(150, 40);
        backButton.setStyle("-fx-font-size:15");
        backButton.setOnAction(e -> {
            cs.profileGUI(stage);
        });
        Button saveButton = new Button("Save");
        saveButton.setPrefSize(150, 40);
        saveButton.setStyle("-fx-font-size:15");
        saveButton.setOnAction(e -> {
            profile.neededCourses = new ArrayList<String>();
            profile.doneCourses = new ArrayList<String>();
            for(int i = 0; i < helperList.size(); i++) {
                if(helperList.get(i).reqd.getValue()) {
                    profile.neededCourses.add(helperList.get(i).course.getCode());
                }
                if(helperList.get(i).done.getValue()) {
                    profile.doneCourses.add(helperList.get(i).course.getCode());
                }
            }
            profile.numcredits = Integer.parseInt(creditsTextBox.getText());
            profile.numsemesters = Integer.parseInt(semestersTextBox.getText());
        });
        VBox optionsVBox = new VBox();
        optionsVBox.setSpacing(12);
        optionsVBox.setPadding(new Insets(10, 0, 0, 10));
        optionsVBox.getChildren().addAll(optionsHeader, creditsLabel, creditsTextBox, semestersLabel, semestersTextBox, backButton, saveButton);
        //END LEFT MODULE
        //BEGIN COALESCENCE AND FINALIZATION
        HBox grandHBox = new HBox(5, optionsVBox, tableSearchVBox, neededDoneVBox);
        ((Group)scene.getRoot()).getChildren().addAll(grandHBox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This is a GUI that has buttons to activate the scraper program via accessing Scraper.java’s
     * run() method, and it will also display the time it was last run, via the getLastRun() method.
     * @param stage the stage on which to display the GUI.
     */
    public void scraperGUI(Stage stage) throws IOException {
        //Set up stage, and elements of window.
        stage.setTitle("Rescrape Data");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss");
        Label lastRun = new Label(scraper.getLastRun().toString());
        lastRun.setStyle("-fx-font-size:30");
        Button scrapeButton = new Button("Scrape Now");
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
                //Replace later with the scrape() function.//////////////////////////////////////////
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