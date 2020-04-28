package CourseScheduler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class CourseScheduler extends Application {
    
    private Catalog catalog;
    private Scraper scraper;

    public void start(Stage primaryStage) throws Exception { //All GUI method calls will go in here
        // TODO some kind of intialization screen?
        catalog = new Catalog(new Database("catalog"));
        scraper = new Scraper(catalog);

	//CURRENTLY TESTING GENERATESCHEDULE() BY ITSELF

        if (Profile.db == null) {
            Profile.db = new ProfileDB().create();
        }
        if (scraper.needsToRun()) {
            scraperGUI(primaryStage, this);
        } else {
            profileGUI(primaryStage, this);
        }
    }
    
    public void profileGUI(Stage stage, CourseScheduler cs) {
       // StackPane pane = new StackPane();
        GridPane pane = new GridPane();
    
        // TODO proper error handling
        List<Course> courseList;
        try {
            courseList = catalog.getCourses();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            return;
        }
    
        ComboBox<String> selectprofile = new ComboBox();
        selectprofile.setPrefSize(300, 80);
        selectprofile.setStyle("-fx-font-size:30");

        Label welcomeLabel = new Label("Welcome To Course Scheduler \n Select Your Profile Below:");
        welcomeLabel.setStyle("-fx-font-size:30");

        Button btnContinue = new Button("Continue");
        btnContinue.setPrefSize(300, 80);
        btnContinue.setStyle("-fx-font-size:30");

        Button btnRemove = new Button("Remove Selected");
        btnRemove.setPrefSize(300, 80);
        btnRemove.setStyle("-fx-font-size:30");

        // create a text input dialog
        TextInputDialog td = new TextInputDialog();
        // setHeaderText
        td.setHeaderText("enter your profile name");
        // create a event handler to create new profiles
        EventHandler<ActionEvent> event = e -> {
            // show the text input dialog
            String value = selectprofile.getValue();
            //Creates a new profile when "create new profile" isselected from the combo box
            if(value.equals("Create New Profile")) {
                td.showAndWait();
                /*prevents empty strings*/
                if(!td.getEditor().getText().isEmpty()) {
                    selectprofile.getItems().add(td.getEditor().getText());
                    //Profile.db.insertProfile(td.getEditor().getText());
                    try {
                        Profile.load(td.getEditor().getText());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
       Profile.db.deleteProfile(" ");
        selectprofile.getItems().add("Create New Profile");
        selectprofile.getItems().addAll(Profile.db.getProfiles());
        selectprofile.setOnAction(event);
       // selectprofile.setOnAction(e->loadProfile());
        btnContinue.setOnAction(e-> {
            try {
                checkListGUI(courseList, Profile.load(selectprofile.getValue()), stage, cs);
            } catch (SQLException | IOException ex) {
                ex.printStackTrace();
            }
        });

        btnRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                int selectedIdx = selectprofile.getSelectionModel().getSelectedIndex();
                if (selectedIdx != -1) {
                    String itemToRemove = selectprofile.getSelectionModel().getSelectedItem();

                    int newSelectedIdx =
                            (selectedIdx == selectprofile.getItems().size() - 1)
                                    ? selectedIdx - 1
                                    : selectedIdx;
                    /*prevents create new profile from being deleted*/
                    if(!selectprofile.getSelectionModel().getSelectedItem().equals("Create New Profile")){
                        Profile.db.deleteProfile( selectprofile.getSelectionModel().getSelectedItem());
                        selectprofile.getItems().remove(selectedIdx);

                    //status.setText("Removed " + itemToRemove);
                        selectprofile.getSelectionModel().select(newSelectedIdx);
                    }
                }
            }
        });
        // primaryStage.setScene(new Scene(root, 300, 275));
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
       /* pane.prefHeight(50.0);
        pane.prefWidth(50.0);*/
        pane.setVgap(50);
        pane.add(selectprofile, 0, 1);
        pane.add(welcomeLabel, 0, 0);
        pane.add(btnContinue, 1,1 );
        pane.add(btnRemove, 1,2 );
        //pane.getChildren().addAll(selectprofile);
        stage.setMaximized(true);
        stage.setScene(new Scene(pane, 300, 275));
        stage.setTitle("Select Profile");
        stage.centerOnScreen();
        stage.show();
    }
    
    //Helper method to update the display of the time last run once scraping is complete.
    private void updateTime(Label timeLabel, DateTimeFormatter dtf) throws IOException {
        timeLabel.setText("Last updated on " + dtf.format(scraper.getLastRun()));
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

        public BooleanProperty doneProperty() {
            return done;
        }

        public final boolean isDone() {
            return doneProperty().get();
        }

        public String toString() {
            return this.course.getCode() + "\n(" + this.course.getCredits() + " credits)";
        }
    }

    /**
     * Turns a List of courses into a list of CourseHelpers
     * @param courseList the list of courses to be converted.
     * @param profile the profile with which to check if courses have been marked "needed" or "done".
     * @return the List or CourseHelpers
     */
    private static List<CourseHelper> toHelperList(List<Course> courseList, Profile profile) throws SQLException {
        List<CourseHelper> helperList = new ArrayList<>();
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i) != null) {
                helperList.add(new CourseHelper(
                        courseList.get(i),
                        /*profile.getNeededCourses().contains(courseList.get(i).getCode()),
                        profile.getDoneCourses().contains(courseList.get(i).getCode())));*/
                        profile.getNeeded().contains(courseList.get(i).getCode()),
                        profile.getDone().contains(courseList.get(i).getCode())));
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
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCode().equals(code)) {
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
        return (course.getFullName() + course.getName() + course.getCredits() + course.getDesc());
    }

    /**
     * This is the GUI for the Course checklist displayed by the program, where the user can use a Course search,
     * as well as change their current Profile settings via checkbox selection of Courses and/or changing their
     * credits or semester counts. The parameters are the list of *all* Courses, as well as the user’s chosen Profile.
     * The user has the option to click on "Back" to go back to the previous, main GUI (selectProfile()); after the user
     * has selected the "Save" button, generateSchedule() is called, with the list of "Needed" Courses chosen by the
     * user, but *without* any instances of the "Done" Courses as prerequisites to *any* of those Courses, as the
     * parameter.
     * @param courseList the list of all courses that could be taken.
     * @param profile the profile selected in the previous GUI.
     * @param stage the stage on which to display this GUI
     * @param cs the instance of CourseScheduler calling this method.
     */
    public void checkListGUI(List<Course> courseList, Profile profile, Stage stage, CourseScheduler cs) throws SQLException {
        //Set up the table in which courses will dynamically populate rows as user searches.
        TableView<Course> courseTable = new TableView<>();
        TableView<CourseHelper> checkBoxTable = new TableView<>();
        //Create and populate lists of CourseHelpers to aid in the GUI's dynamic elements.
        /*List<CourseHelper> helperList = CourseScheduler.toHelperList(courseList, profile);*/
        List<CourseHelper> helperList = CourseScheduler.toHelperList(courseList, profile);
        List<CourseHelper> newDoneHelpers = new ArrayList<>();
        List<CourseHelper> newNeededHelpers = new ArrayList<>();

        for(int i = 0; i < helperList.size(); i++) {
            CourseHelper temp = helperList.get(i);
            if(temp.isDone()) {
                newDoneHelpers.add(temp);
            }
            if(temp.isReqd()) {
                newNeededHelpers.add(temp);
            }
        }
        //Cast the two main lists to ObservableLists for change detection.
        ObservableList<CourseHelper> helpers = FXCollections.observableArrayList(helperList);
        ObservableList<Course> courses = FXCollections.observableArrayList(courseList);
        //Configure basic scene elements.
        stage.setTitle("Create Your Schedule");
        Scene scene = new Scene(new Group());
        //BEGIN RIGHT MODULE
        //Create elements and configure sizes.
        final Label neededHeader = new Label("Needed Classes");
        neededHeader.setFont(new Font("Arial", 20));
        ListView<CourseHelper> neededList = new ListView<>();
        neededList.setMaxHeight(200);
        neededList.getItems().addAll(newNeededHelpers);
        //Set checkbox state to align with CourseHelper's boolean for needed.
        neededList.setCellFactory(CheckBoxListCell.forListView(CourseHelper::reqdProperty));
        //Create elements and configure sizes.
        final Label doneHeader = new Label("Done Classes");
        doneHeader.setFont(new Font("Arial", 20));
        ListView<CourseHelper> doneList = new ListView<>();
        doneList.setMaxHeight(200);
        doneList.getItems().addAll(newDoneHelpers);
        //Set checkbox state to align with CourseHelper's boolean for done.
        doneList.setCellFactory(CheckBoxListCell.forListView(CourseHelper::doneProperty));
        //Cram all the above elements into a VBox
        VBox neededDoneVBox = new VBox(5, neededHeader, neededList, doneHeader, doneList);
        //END RIGHT MODULE
        //BEGIN MIDDLE MODULE
        //Configure basic elements and their size properties.
        final Label selectClassesHeader = new Label("Select Classes");
        selectClassesHeader.setFont(new Font("Arial", 20));

        courseTable.setEditable(true);
        checkBoxTable.setEditable(true);
        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        checkBoxTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Configure how columns of the center tables are populated.
        TableColumn needBoxes = new TableColumn("Required");
        needBoxes.setMinWidth(75);
        needBoxes.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<CourseHelper, Boolean>, ObservableValue<Boolean>>) param -> {
                    if(param.getValue().isReqd() && !neededList.getItems().contains(param.getValue())) {
                        neededList.getItems().add(param.getValue());
                        profile.getNeeded().add(param.getValue().course.getCode());
                    }
                    else if(!param.getValue().isReqd() && neededList.getItems().contains(param.getValue())) {
                        neededList.getItems().remove(param.getValue());
                        profile.getNeeded().remove(param.getValue().course.getCode());
                    }
                    return param.getValue().reqd;
                }
        );
        needBoxes.setCellFactory(CheckBoxTableCell.forTableColumn(needBoxes));

        TableColumn<CourseHelper, Boolean> doneBoxes = new TableColumn("Completed");
        doneBoxes.setMinWidth(75);
        doneBoxes.setCellValueFactory(param -> {
                if (param.getValue().isDone() && !doneList.getItems().contains(param.getValue())) {
                    doneList.getItems().add(param.getValue());
                    profile.getDone().add(param.getValue().course.getCode());
                } else if (!param.getValue().isDone() && doneList.getItems().contains(param.getValue())) {
                    doneList.getItems().remove(param.getValue());
                    profile.getDone().remove(param.getValue().course.getCode());
                }
                return param.getValue().done;
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
        tableSearchVBox.setPadding(new Insets(0, 10, 0, 10));
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
        backButton.setOnAction(e -> cs.profileGUI(stage, cs));
        Button saveButton = new Button("Save");
        saveButton.setPrefSize(150, 40);
        saveButton.setStyle("-fx-font-size:15");
        saveButton.setOnAction(e -> {
            profile.save();
            if(!creditsTextBox.getText().equals("")) {
                profile.setNumCredits(Integer.parseInt(creditsTextBox.getText().replaceAll("[^\\d]", "")));
            } else {
                profile.setNumCredits(15);
            }
            if(!semestersTextBox.getText().equals("")) {
                profile.setNumSemesters(Integer.parseInt(semestersTextBox.getText().replaceAll("[^\\d]", "")));
            } else {
                profile.setNumSemesters(8);
            }
            List<Course> neededCourseList = new ArrayList<>();
            for(int i = 0; i < profile.getNeeded().size(); i++) {
                neededCourseList.add(getCourseByCode(courseList, profile.getNeeded().get(i)));
            }
            List<Course> doneCourseList = new ArrayList<>();
            for(int i = 0; i < profile.getDone().size(); i++) {
                doneCourseList.add(getCourseByCode(courseList, profile.getDone().get(i)));
            }
            generateSchedule(courseList, neededCourseList, doneCourseList, profile.getNumCredits(), profile.getNumSemesters(), profile, stage, cs);
        });
        VBox optionsVBox = new VBox();
        optionsVBox.setSpacing(12);
        optionsVBox.getChildren().addAll(optionsHeader, creditsLabel, creditsTextBox, semestersLabel, semestersTextBox, backButton, saveButton);
        //END LEFT MODULE
        //BEGIN COALESCENCE AND FINALIZATION
        HBox grandHBox = new HBox(5, optionsVBox, tableSearchVBox, neededDoneVBox);
        grandHBox.setPadding(new Insets(10, 10, 10, 10));
        ((Group)scene.getRoot()).getChildren().addAll(grandHBox);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        ScrollBar courseScrollBar = new ScrollBar();
        ScrollBar checksScrollBar = new ScrollBar();
        for (Node node : courseTable.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                courseScrollBar = (ScrollBar) node;
                break;
            }
        }
        for (Node node : checkBoxTable.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                checksScrollBar = (ScrollBar) node;
                break;
            }
        }
        this.bindScrollBars(courseScrollBar, checksScrollBar);
    }
    
    /**
     * This is a GUI that has buttons to activate the scraper program via accessing Scraper.java’s
     * run() method, and it will also display the time it was last run, via the getLastRun() method.
     * @param stage the stage on which to display the GUI.
     */
    public void scraperGUI(Stage stage, CourseScheduler cs) {
        //Set up stage, and elements of window.
        Button backButton = new Button("Continue to\nProfile Select");
        backButton.setPrefSize(150, 60);
        backButton.setStyle("-fx-font-size:15");
        stage.setTitle("Rescrape Data");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss");
        Label lastRun = new Label("Last updated on " + dtf.format(Scraper.getLastRun()));
        lastRun.setStyle("-fx-font-size:30");
        Button scrapeButton = new Button("Scrape Now");
        scrapeButton.setPrefSize(300, 80);
        scrapeButton.setStyle("-fx-font-size:30");
        Label scraperStatus = new Label("");
        ProgressBar scrapingProgress = new ProgressBar();
        scrapingProgress.setProgress(0.0);
        scrapingProgress.setScaleX(3);
        
        //Lay out the elements in a GridPane.
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(50);
        gp.add(lastRun, 0, 0);
        gp.add(scrapeButton, 0, 3);
        gp.add(backButton, 0, 10);
        gp.add(scrapingProgress, 0, 5);
        gp.add(scraperStatus, 0, 6);
        
        //Set constraints - center everything so it's nice and pretty.
        ColumnConstraints colCon = new ColumnConstraints();
        colCon.setHalignment(HPos.CENTER);
        gp.getColumnConstraints().add(colCon);
        
        //Set window (stage) size and
        stage.setMaximized(true);
        Scene scene = new Scene(gp);
        
        //Define task (scraping with message updates)
        Task<Void> scrapeTask = new Task<>() {
            @Override
            public Void call() throws IOException, SQLException {
                Runnable timeUpdater = () -> {
                    try {
                        updateTime(lastRun, dtf);
                    } catch (IOException ioe) {
                        System.out.println(ioe.getMessage());
                    }
                };
                try{
                scraper.run(u -> {
                    System.out.println(u.getPercent());
                    if(u.getPercent() != null) {
                        scrapingProgress.setProgress(u.getPercent());
                    }
                    updateMessage(u.getMessage());
                });
                }catch (Exception e){ e.printStackTrace();}
                updateMessage("Scraping Complete");
                Platform.runLater(timeUpdater);
                succeeded();
                return null;
            }
        };
        //Define behavior of buttons
        EventHandler<ActionEvent> scrapeBtnPressed = scrapeReq -> {
            Thread scrapeThread = new Thread(scrapeTask);
            scrapeThread.setDaemon(true);
            scrapeThread.start();
            scrapeButton.setDisable(true);
        };
        
        EventHandler<ActionEvent> backBtnPressed = scrapeReq -> cs.profileGUI(stage, cs);
        
        //Configure dynamic scene elements.
        scrapeButton.setOnAction(scrapeBtnPressed);
        backButton.setOnAction(backBtnPressed);
        scraperStatus.textProperty().bind(scrapeTask.messageProperty());
        
        //Show the scene created above.
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Helper function to bind the two scrollbars of checkListGUI() to scroll in unison.
     * @param sb1 the first scrollbar to bind.
     * @param sb2 the second scrollbar to bind.
     */
    private void bindScrollBars(ScrollBar sb1, ScrollBar sb2) {
        sb1.valueProperty().addListener((src, ov, nv) -> sb2.setValue(nv.doubleValue() / sb1.getMax()));
        sb2.valueProperty().addListener((src, ov, nv) -> sb1.setValue(nv.doubleValue() * sb2.getMax()));
    }

    /**
     * This method takes as its parameters, in this order, the *ordered* Course list, the maximum number of
     * credits per semester, and the maximum number of semesters to calculate for. It returns an *ordered*
     * list of Semesters, where each Semester is determined based on the maximums for the numbers of credits
     * and semesters to calculate.
     * @param orderedList The ordered list of Courses gotten from RunAlgorithm
     * @param maxCredits The maximum number of credits per semester to compute
     * @param maxSemesters The maximum number of semesters to compute
     * @return returned The ordered list of Semesters, each containing an ordered list of Courses
     */
    public List<Semester> cutOffCalc(Course[] orderedList, int maxCredits, int maxSemesters) {
        //Variables
        int totalCount = 0;
        List<Semester> returned = new ArrayList<Semester>();
        Semester lastEntered = new Semester();
        int numSemesters = (maxSemesters * maxCredits);
        int i = 0;
    
        //Get Total Count
        for (int j = 0; j < orderedList.length; j++) {
            totalCount += orderedList[j].getCredits();
        }
    
        //Semester Counter
        for (int j = 0; (numSemesters > 0); j++) {
            //Credit Counter
            int numCredits = maxCredits;
            Semester tempS = new Semester();
            //Break #1
            if (i >= orderedList.length) {
                break;
            }
            //Add to Semester
            while ((numCredits - orderedList[i].getCredits()) >= 0) {
                if (i == (orderedList.length - 1)) {
                    break;
                }
                Course current = orderedList[i];
                int credits = current.getCredits();
                tempS.getSemester().add(current);
                lastEntered = tempS;
                numCredits -= credits;
                i++;
            }
            //For Adding Last Course
            if (maxCredits >= totalCount) {
                Course current = orderedList[i];
                int credits = current.getCredits();
                tempS.getSemester().add(current);
                lastEntered = tempS;
                numCredits -= credits;
                i++;
            }
            //Break #2
            if (tempS.getSemester().isEmpty()) {
                break;
            }
            //Add to List of Semesters
            returned.add(tempS);
            numSemesters -= maxCredits;
        }
    
        //For Adding Very Last Course
        if (i == (orderedList.length - 1)) {
            int index = returned.lastIndexOf(lastEntered);
            Semester tempS = returned.get(index);
            Course current = orderedList[i];
            tempS.getSemester().add(current);
        }
		return returned;
    }
    
    /**
     * This starts with running the algorithm, passing in the given list as the parameter for runAlgorithm() in
     * the RunAlgorithm class. It returns an *ordered* Course list, which, combined with the maximum number of
     * credits and semesters (Gotten from the user’s Profile), respectively, are used as the parameters to call
     * cutOffCalc(). This then returns an *ordered* list of Semesters, which is then displayed to the user. This
     * displays the classes in a semesterly fashion, as part of a GUI, and in this GUI, the user can also click
     * the "Back" button to go back to the previous step of the checklist (selectProfileInit()), or the "Home"
     * button to return to the initial program startup, with the main GUI of Profile selection (guiDisplay()).
     * @param courseList The list of all Courses
     * @param orig_doneCourses The list of needed Courses for the user to take
     * @param orig_neededCourses The l		//Clone Listsist of Courses the user has already done
     * @param maxCredits The maximum number of credits per semester
     * @param maxSemesters The maximum number of semesters
     * @param profile The user's profile
     * @param stage The stage on which to display this GUI
     * @param cs the instance of CourseScheduler calling this method.
     */
    public void generateSchedule(List<Course> courseList, List<Course> orig_neededCourses, List<Course> orig_doneCourses,
    		int maxCredits, int maxSemesters, Profile profile, Stage stage, CourseScheduler cs)
    {
    
		List<Course> neededCourses = orig_neededCourses.stream().map(Course::new).collect(Collectors.toList());
		List<Course> doneCourses = orig_doneCourses.stream().map(Course::new).collect(Collectors.toList());
		
		//GUI Initialization
		Button backButton = new Button("Back");
		backButton.setPrefSize(150, 60);	//Size of Button
		backButton.setStyle("-fx-font-size:18");	//Font Size of Button
		Button homeButton = new Button("Home");
		homeButton.setPrefSize(150, 60);	//Size of Button
		homeButton.setStyle("-fx-font-size:18");	//Font Size of Button
		stage.setTitle("Generated Schedule");	//Title of Window
		GridPane gp = new GridPane();	//A Pane to List Elements
		gp.setAlignment(Pos.CENTER);	//Center on Screen
		gp.setVgap(200);	//Distance Between Buttons, etc.
		
		//VBox and HBox Setup (Main)
		VBox fullGUI = new VBox(10);
		HBox buttons = new HBox(10);
		HBox semesters = new HBox(10);
		fullGUI.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);
		semesters.setAlignment(Pos.CENTER);
		
		//If NO Needed Courses, Return
		if (neededCourses.size() < 1)
		{
			JOptionPane.showMessageDialog(null, "You did not select any courses that are \"needed\", and so "
					+ "you are returning to the checklist, to add some courses. Please press Ok to continue.");
			try {
				cs.checkListGUI(courseList, profile, stage, cs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Treat all unselected prerequisites as done
        for (int i = 0; i < neededCourses.size(); i++) {
            for(int j = 0; j < neededCourses.get(i).getPrerequisites().size(); j++) {
                if(!doneCourses.contains(getCourseByCode(courseList, neededCourses.get(i).getPrerequisites().get(j)))
                        || !neededCourses.contains(getCourseByCode(courseList, neededCourses.get(i).getPrerequisites().get(j)))) {
                    doneCourses.add(getCourseByCode(courseList, neededCourses.get(i).getPrerequisites().get(j)));
                }
            }
        }

		//Swap Name and Code
		for (int i = 0; i < neededCourses.size(); i++)
		{
			String tempName = neededCourses.get(i).getName();
			neededCourses.get(i).setName( neededCourses.get(i).getCode() );
			neededCourses.get(i).setCode(tempName);
		}

		//Remove Done Courses from Prerequisites and Parents
		for (int i = 0; i < neededCourses.size(); i++)
		{
			Course current = neededCourses.get(i);
			for (int j = 0; j < doneCourses.size(); j++)
			{
				Course curDone = doneCourses.get(j);
				if ( (current.getPrerequisites().contains(curDone.getName())) ||
						(current.getPrerequisites().contains(curDone.getCode())) )
				{
					//Remove from Prerequisites
					String removedA = curDone.getName();
					String removedB = curDone.getCode();
					neededCourses.get(i).getPrerequisites().remove( removedA );
					neededCourses.get(i).getPrerequisites().remove( removedB );
					//Remove from Parents -> Start
					String parentString = current.getParents();
					parentString = parentString.replaceAll(removedA, "");
					parentString = parentString.replaceAll(removedB, "");
					current.setParents(parentString);
				}
			}
			String parentString = current.getParents();
			if ( !parentString.equals("") )
			{
				//Remove from Parents -> Leftovers (Part A; Duplicate Symbols)
				parentString = parentString.replaceAll("&&", "&");
				parentString = parentString.replaceAll("\\|\\|", "\\|");
				//Remove from Parents -> Leftovers (Part B; Symbols & Parentheses)
				parentString = parentString.replaceAll("\\(&", "\\(");
				parentString = parentString.replaceAll("\\(\\|", "\\(");
				parentString = parentString.replaceAll("&\\)", "\\)");
				parentString = parentString.replaceAll("\\|\\)", "\\)");
				//Remove from Parents -> Leftovers (Part C; Empty Parentheses)
				parentString = parentString.replaceAll("&\\(\\)", "");
				parentString = parentString.replaceAll("\\|\\(\\)", "");
				parentString = parentString.replaceAll("\\(\\)&", "");
				parentString = parentString.replaceAll("\\(\\)\\|", "");
				//Remove from Parents -> Leftovers (Part D; First and/or Last Character Is Symbol)
				char firstChar = parentString.charAt(0);
				char lastChar = parentString.charAt( parentString.length() - 1 );
				String middleChars = "";
				int currentChar = 0;
				while (true)
				{
					//End of String; Break
					if ( parentString.length() == (currentChar + 1) )
						break;
					char tempC = parentString.charAt(currentChar);
					if (currentChar != 0)
						middleChars = (middleChars + tempC);
					currentChar++;
				}
				if ( (firstChar == '|') || (firstChar == '&') )
				{
					if ( (lastChar == '|') || (lastChar == '&') )	//Remove Both
						parentString = middleChars;
					else	//Remove First Only
						parentString = (middleChars + lastChar);
				}
				else if ( (lastChar == '|') || (lastChar == '&') )	//Remove Last Only
					parentString = (firstChar + middleChars);
				current.setParents(parentString);
			}
		}
		
		//Remove Done Courses from neededCourses
		Stack<Integer> indicesToRemove = new Stack<Integer>();
		for (int i = 0; i < neededCourses.size(); i++)
		{
			Course current = neededCourses.get(i);
			//Find if current Is in doneCourses
			for (int j = 0; j < doneCourses.size(); j++)
			{
				Course curDone = doneCourses.get(j);
				String nameA = curDone.getName();
				String nameB = curDone.getCode();
				String nameC = current.getName();
				//Add to Deletion Queue
				if ( (nameA.equals(nameC)) || (nameB.equals(nameC)) )
					indicesToRemove.push(i);
			}
		}
		//Remove Stuff in Deletion Queue
		while ( !indicesToRemove.empty() )
		{
			int index = indicesToRemove.pop();
			neededCourses.remove(index);
		}
		
		//Algorithm
		RunAlgorithm runAlg = new RunAlgorithm();
		Course [] orderedList = runAlg.runAlgorithm(neededCourses);
		
		//Swap Name and Code (Again)
		for (int i = 0; i < orderedList.length; i++)
		{
			String tempName = orderedList[i].getName();
			orderedList[i].setName( orderedList[i].getCode() );
			orderedList[i].setCode(tempName);
		}
		
		//Semesterly Order
		List<Semester> orderedSemesters = cutOffCalc(orderedList, maxCredits, maxSemesters);
		
		//GUI Interaction
		for (int i = 0; i < orderedSemesters.size(); i++)
		{
			//Make Similar Semester Box
			VBox semester = new VBox(10);
			String semNum = ( "Semester #" + (i + 1) );
			Label semLabel = new Label(semNum);
			semLabel.setStyle("-fx-font-size:14");
			semester.getChildren().add(semLabel);
			//Initialize Adding Line
			VBox line = new VBox();
			line.setAlignment(Pos.CENTER);
			Label lineSegA = new Label("   |   ");
			Label lineSegB = new Label("   |   ");
			line.getChildren().add(lineSegA);
			line.getChildren().add(lineSegB);
			//Algorithmic Part
			List<Course> tempS = orderedSemesters.get(i).getSemester();
			for (int j = 0; j < tempS.size(); j++)
			{
				Course tempC = tempS.get(j);
				//Add Course to Semester
				String courseName = tempC.getName();
				String courseFullName = tempC.getFullName();
				int credits = tempC.getCredits();
				String totalName = ( (j + 1) + ") " + courseName + " (" + credits
						+ " Credits): " + courseFullName);
				Label courseLabel = new Label(totalName);
				courseLabel.setStyle("-fx-font-size:12");
				semester.getChildren().add(courseLabel);
				//Add Line Segments
				Label lineSegC = new Label("   |   ");
				Label lineSegD = new Label("   |   ");
				line.getChildren().add(lineSegC);
				line.getChildren().add(lineSegD);
			}
			//Add Line and Semester to Total
			semesters.getChildren().add(semester);
			semesters.getChildren().add(line);
		}
		
		//Combine GUIs
		buttons.getChildren().add(backButton);
		buttons.getChildren().add(homeButton);
		fullGUI.getChildren().add(semesters);
		fullGUI.getChildren().add(buttons);
		gp.add(fullGUI, 0, 0);	//Column, Row
		
		//Center Buttons, etc.
		ColumnConstraints colCon = new ColumnConstraints();
		colCon.setHalignment(HPos.CENTER);
		gp.getColumnConstraints().add(colCon);
		
		//Set Window Size and Scene
		stage.setMaximized(true);
		Scene scene = new Scene(gp);	//Copy of Panel; Required
		
		//Event Handlers for Buttons
		//Back Button
		EventHandler<ActionEvent> backBtnPressed = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent backBtn) {
				try {
					cs.checkListGUI(courseList, profile, stage, cs);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		backButton.setOnAction(backBtnPressed);	//Associates the Button
		//Home Button
		EventHandler<ActionEvent> homeBtnPressed = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent homeBtn) {
				cs.profileGUI(stage, cs);
			}
		};
		homeButton.setOnAction(homeBtnPressed);	//Associates the Button
		
		//Show Scene; Required
		stage.setScene(scene);
		stage.show();
    }
    
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}