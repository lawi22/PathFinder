// PROG VT2023, Inlämningsuppgift, del 2
// Grupp 380
// Louis Guerpillon logu5907

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.WritableImage;
import javafx.stage.WindowEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {
    private boolean isCrosshair = false;
    private boolean changed = false;
    private String localSave = "";
    private String url = "file:europa.gif";
    private Button btnFindPath;
    private Button btnNewPlace;
    private Button btnShowConnection;
    private Button btnNewConnection;
    private Button btnChangeConnection;
    private City citySelected1 = null;
    private City citySelected2 = null;
    private Pane outputArea;
    private FlowPane buttonPane;
    private MenuBar menuBar;
    private Image image;
    private ImageView imageView;
    private Scene scene;
    private Stage primaryStage;
    private ListGraph<City> listGraph = new ListGraph<City>();
    private TextInputDialog textInputDialog;
    private BorderPane root;
    private TextField textField = new TextField();


    @Override
    public void start(Stage primaryStage) {


        // Sätter variabeln primaryStage till samma som lokal variabeln primaryStage.
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(new ExitHandler());


        // Skapar en BorderPane som sätter scenen senare.
        root = new BorderPane();
        root.setStyle("-fx-font-weight: bold; -fx-font-size: 12");

        // Skapar en ny BorderPane som används för MenuBar och ButtonsPane för att få det på Top på original BorderPane.
        BorderPane topBorderPane = new BorderPane();

        // Skapar en Pane för att kunna sätta ut städer och lägger till bilden av europa i europePane.
        outputArea = new Pane();

        // Skapar menyfliken File
        Menu menuFile = new Menu("File");

        // Skapar menyobjekt
        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setOnAction(new NewMapHandler());
        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setOnAction(new OpenHandler());
        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setOnAction(new SaveHandler());
        MenuItem menuSaveImage = new MenuItem("Save Image");
        menuSaveImage.setOnAction(new SaveImageHandler());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(new ExitItemHandler());

        // Lägger till menyobjekt till menyn File.
        menuFile.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);

        // Skapar menybar och lägg till menyn File.
        menuBar = new MenuBar();
        menuBar.getMenus().add(menuFile);

        // Letar efter europa.gif och tillsätter den på image, hittas den inte så visas error. Drf try catch.
        // Viktigt att europa.gif ligger i src mappen annars hittas den inte.





        // Skapar en FlowPane som heter buttonPane o sätter inställningar för den, tex hur nära knapparna är varandra osv.
        buttonPane = new FlowPane();
        buttonPane.setPadding(new Insets(10));
        buttonPane.setHgap(10);
        buttonPane.setVgap(10);
        buttonPane.setAlignment(Pos.CENTER);

        //// Lägger till knapparna högst upp men utan funktionalitet.
        // Find Path knappen skapas och dess action kallar på findPathHandler
        btnFindPath = new Button("Find Path");
        btnFindPath.setOnAction(new FindPathHandler());

        // Show Connections knappen skapas och dess action kallar på showConnectionHandler
        btnShowConnection = new Button("Show Connection");
        btnShowConnection.setOnAction(new ShowConnectionHandler());

        // New Place knappen skapas och dess action kallar på newPlaceHandler
        btnNewPlace = new Button("New Place");
        btnNewPlace.setOnAction(new NewPlaceHandler());
        //btnNewPlace.setOnMouseClicked(new CrosshairMouse());

        // New Connection knappen skapas och dess action kallar på newConnectionHandler
        btnNewConnection = new Button("New Connection");
        btnNewConnection.setOnAction(new NewConnectionHandler());

        // Change Connection knappen skapas och dess action kallar på changeConnectionHandler
        btnChangeConnection = new Button("Change Connection");
        btnChangeConnection.setOnAction(new ChangeConnectionHandler());

        // Lägger till de 5 knapparna till buttonPane noden som är en flow pane så knapparna hamnar efter varanda.
        buttonPane.getChildren().addAll(btnFindPath, btnShowConnection, btnNewPlace, btnNewConnection, btnChangeConnection);


        // Sätter upp topBorderPane noden men menubar och buttonPane. Alltså Fil menyn samt alla knappar under.
        topBorderPane.setTop(menuBar);
        topBorderPane.setCenter(buttonPane);

        // Sätter upp borderPane noden.
        root.setTop(topBorderPane);
        root.setCenter(outputArea);

        // Skapar en ny med borderPane samt hur stor scenen ska vara i pixlar och sedan tilldelar det till scene.
        scene = new Scene(root);


        // Sätter upp stage med scene och sätter stage/fönstrets minsta storlek och största storlek
        //primaryStage.setMinWidth(600);
        //primaryStage.setMaxWidth(600);
        //primaryStage.setMinHeight(120);
        //primaryStage.setMaxHeight(120);

        // Sätter rätt ID på saker för testning.
        menuBar.setId("menu");
        menuFile.setId("menuFile");
        menuNewMap.setId("menuNewMap");
        menuOpenFile.setId("menuOpenFile");
        menuSaveFile.setId("menuSaveFile");
        menuSaveImage.setId("menuSaveImage");
        menuExit.setId("menuExit");
        btnFindPath.setId("btnFindPath");
        btnShowConnection.setId("btnShowConnection");
        btnNewPlace.setId("btnNewPlace");
        btnChangeConnection.setId("btnChangeConnection");
        btnNewConnection.setId("btnNewConnection");
        outputArea.setId("outputArea");


        //primaryStage.setResizable(false); // Förbjuder resizing av fönstret.
        primaryStage.setTitle("PathFinder");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    class ClickerHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            // Skapar en temporär cirkel vilket blir cirklen man precis tryckt på.
            City c = (City) event.getSource();

            // Kollar om cirkelmarked1 är tom och att c (cirkeln man tryckt) på inte är lika med circlemarked2.
            // Tilldelar sedan c till cirkelmarked1.
            if (citySelected1 == null && !c.equals(citySelected2)) {
                citySelected1 = c;
                citySelected1.paintCovered(); // RÖD


            }
            // Kollar om cirkelmarked2 är tom och att c (cirkeln man tryckt) på inte är lika med circlemarked1.
            // Tilldelar sedan c till cirkelmarked2.
            else if (citySelected2 == null && !c.equals(citySelected1)) {
                citySelected2 = c;
                citySelected2.paintCovered(); // RÖD
            }
            // Kollar om c (cirkeln man tryckt) är lika med circleMarked1 och inte lika med circleMarked2 och
            // isåfall omarkerar circleMarked1 genom att göra den blå och sätta den till null.
            else if (c.equals(citySelected1) && !c.equals(citySelected2)) {
                c.paintUncovered(); // BLÅ
                citySelected1 = null;
            }
            // Kollar om c (cirkeln man tryckt) är lika med circleMarked2 och inte lika med circleMarked1 och
            // isåfall omarkerar circleMarked2 genom att göra den blå och sätta den till null.
            else if (c.equals(citySelected2) && !c.equals(citySelected1)) {
                c.paintUncovered(); // BLÅ
                citySelected2 = null;
            }
        }
    }

    // New Map under File
    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Nollställer markerade städer
            citySelected1 = null;
            citySelected2 = null;

            url = "file:europa.gif";
            image = new Image(url);
            imageView = new ImageView(image);

            // Rensar euroPane och öppnar en ny europa.gif bild.
            outputArea.getChildren().clear();
            outputArea.getChildren().add(imageView);
            //primaryStage.setMinWidth(image.getWidth());
            //primaryStage.setMinHeight(image.getHeight());
            primaryStage.sizeToScene();
            ArrayList<City> cityList = new ArrayList(listGraph.getNodes());
            for(City city : cityList)
            {
                listGraph.remove(city);
            }
            changed = true;
        }
    }

    // Open under File
    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Rensar listGraph.
            List<City> clearListGraph = new ArrayList<>(listGraph.getNodes());
            for(City city : clearListGraph){
                listGraph.remove(city);
            }
            // Nollställer markerade städer.
            citySelected1 = null;
            citySelected2 = null;
            /*
            try {
                FileReader fileReader = new FileReader("europa.graph");
                BufferedReader lineReader = new BufferedReader(fileReader);

                url = lineReader.readLine(); // Läser in första raden i europa.graph.
                image = new Image(url);
                imageView = new ImageView(image);

                lineReader.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                return;
                //System.out.println("Hittar inte filen: " + e.getMessage());

            } catch (IOException e) {
                return;
                //System.out.println(e.getMessage());

            }

             */
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, continue anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    event.consume();
            }


            // clickerHandler som används för att markera städer.
            ClickerHandler clickerHandler = new ClickerHandler();

            // Skapar arrayn och stringen där alla städer från europa.graph kommer sparas ner.
            String[] citiesArray = null;
            String citiesSave = "";

            // Skapar arrayn och stringen där alla förbindelser från europa.graph kommer sparas ner.
            String[] connectionsArray = null;
            StringBuilder connectionsSave = new StringBuilder();

            try {

                FileReader fileReader = new FileReader("europa.graph");
                BufferedReader lineReader = new BufferedReader(fileReader);
                url = lineReader.readLine(); // Läser in första raden i europa.graph.
                image = new Image(url);
                imageView = new ImageView(image);
                citiesSave += lineReader.readLine(); // sparar ner andra raden i europa.graph.

                // Sparar ner alla rader i europa.graph efter andra raden i connectionsSave.
                String line;
                while ((line = lineReader.readLine()) != null) {
                    connectionsSave.append(line).append(";");
                }

                lineReader.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                return;
                //System.out.println("Hittar inte filen: " + e.getMessage());

            } catch (IOException e) {
                return;
                //System.out.println(e.getMessage());

            }
            outputArea.getChildren().clear();
            outputArea.getChildren().add(imageView);

            // Delar upp citiesSave till en array och sedan ritar ut de på kartan.
            citiesArray = citiesSave.split(";");

            for (int i = 0; i < citiesArray.length - 2; i += 3) {
                double x1 = Double.parseDouble(citiesArray[i + 1]);
                double y1 = Double.parseDouble(citiesArray[i + 2]);
                City newCity = new City(citiesArray[i], x1, y1);
                newCity.setOnMouseClicked(clickerHandler); // Gör städerna klickbara.
                newCity.setId(newCity.getName());
                listGraph.add(newCity);
                outputArea.getChildren().add(newCity);
            }

            // Gör connectionsSave till en string och fyller connectionsArray med alla ord uppdelade med hjälp av ";".
            connectionsArray = connectionsSave.toString().split(";");

            if(connectionsArray.length != 0){
                City destination1 = null;
                City destination2 = null;
                // Ritar ut alla förbindelser
                for (int i = 0; i < connectionsArray.length - 3; i += 4) {

                    // Letar igenom listgraphs map och hittar städerna via deras namn.
                    for (City city : listGraph.getNodes()) {
                        if (city.getName().equals(connectionsArray[i])) {
                            destination1 = city;
                        }
                        if (city.getName().equals(connectionsArray[i + 1])) {
                            destination2 = city;
                        }
                    }
                    // Skapar en linje, ritar ut den mellan två stöder och sen lägger till den i europePane.
                    if(destination1 != null && destination2 != null){
                        Line drawLine = new Line(destination1.getCenterX(), destination1.getCenterY(), destination2.getCenterX(), destination2.getCenterY());
                        drawLine.setDisable(true);
                        outputArea.getChildren().add(drawLine);
                    }

                }


            }

            // Försöker koppla ihop städer med förbindelser.
            for (int i = 0; i < connectionsArray.length - 3; i += 4) {
                City destination1 = new City("Test1", 100, 200);
                City destination2 = new City("Test2", 100, 200);
                // Letar igenom listgraphs map och hittar städerna via deras namn och sen tilldelas de till destination1 och 2.
                for (City city : listGraph.getNodes()) {
                    if (city.getName().equals(connectionsArray[i])) {
                        destination1 = city;
                    }
                    if (city.getName().equals(connectionsArray[i + 1])) {
                        destination2 = city;
                    }
                }

                int weight = Integer.parseInt(connectionsArray[i + 3]);
                String name = connectionsArray[i + 2];

                if(listGraph.getEdgeBetween(destination1, destination2) == null){
                    listGraph.connect(destination1, destination2, name, weight);
                }
            }

            //primaryStage.setMinWidth(image.getWidth());
            //primaryStage.setMinHeight(image.getHeight());
            primaryStage.sizeToScene();
            changed = true;

        }
    }

    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            ErrorPopup error = new ErrorPopup();
            if (citySelected1 == null || citySelected2 == null) {
                error.showAndWait();
                return;
            }
            else if (!listGraph.pathExists(citySelected1, citySelected2))
            {
                error.setContentText("No connection between " + citySelected1.getName() + " and " + citySelected2.getName());
                error.showAndWait();
                return;
            }
            int totalWeight = 0;
            String alertText = "";

            for(Edge edge : listGraph.getPath(citySelected1,citySelected2)){
                totalWeight += edge.getWeight();
                alertText += "to " +((City) edge.getDestination()).getName() + " by " + edge.getName() + " takes " + edge.getWeight()+ "\n";
            }
            alertText +="Total " + totalWeight;
            Alert alertCity = new Alert(Alert.AlertType.INFORMATION);
            alertCity.setHeaderText("The path from " + citySelected1.getName() + " to " + citySelected2.getName());
            alertCity.setContentText(alertText);
            alertCity.showAndWait();
        }

    }

    class ErrorPopup extends Alert {
        private ErrorPopup() {
            super(AlertType.ERROR);
            setTitle("Error!");
            setHeaderText(null);
            setContentText("Two places must be selected!");

        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //Implementera funktionen hos Show Connection knappen.

            ErrorPopup error = new ErrorPopup();

            // Kollar saker och visar error ifall något inte stämmer.
            if (citySelected1 == null || citySelected2 == null || listGraph.getEdgeBetween(citySelected1, citySelected2) == null) {
                error.showAndWait();
                return;
            }

            // Skapar popup fönstret som ska visa förbindelsen.
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Connection");
            dialog.setHeaderText("Connection from " + citySelected1.getName() + " to " + citySelected2.getName());

            // Skapar textfälten
            TextField nameField = new TextField();
            TextField timeField = new TextField();

            nameField.setText(listGraph.getEdgeBetween(citySelected1, citySelected2).getName());
            nameField.setEditable(false);

            timeField.setText(Integer.toString(listGraph.getEdgeBetween(citySelected1, citySelected2).getWeight()));
            timeField.setEditable(false);



            // Lägger till textfälten till grid.
            GridPane grid = new GridPane();
            grid.add(new Label("Name: "), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Time: "), 0, 1);
            grid.add(timeField, 1, 1);
            dialog.getDialogPane().setContent(grid);
            grid.setAlignment(Pos.CENTER);
            grid.setVgap(10);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                // Process the results
                String value1 = nameField.getText();
                String value2 = timeField.getText();
            }
        }
    }

    class CrosshairMouse implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            if(isCrosshair){
                outputArea.setCursor(Cursor.CROSSHAIR);
            }
        }
    }
    /*
    class NewPlaceMouse implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent2) {
            textInputDialog = new TextInputDialog();
            textField = new TextField();

            if (isCrosshair) {
                isCrosshair = false;

                // Sätter muspekarn till default.
                europePane.setCursor(Cursor.DEFAULT);

                ///////////--------------------------------------------------------

                double x = mouseEvent2.getX() + (europePane.getWidth() - scene.getWidth());
                double y = mouseEvent2.getY() + (europePane.getHeight() - scene.getHeight());


                    textInputDialog.setTitle("Name");
                    textInputDialog.setHeaderText(null);

                    // Lägger till textfälten till grid.
                    GridPane grid = new GridPane();
                    grid.add(new Label("Name of place: "), 0, 0);
                    grid.add(textField, 1, 0);
                    textInputDialog.getDialogPane().setContent(grid);
                    grid.setAlignment(Pos.CENTER);

                    //Optional<String> input = dialog.showAndWait();
                    textInputDialog.showAndWait();
                    textField.setText(textField.getText().trim());
                    if (!textField.getText().isBlank()) {
                        String cityName = textField.getText();
                        City newCity = new City(cityName, x, y);
                        listGraph.add(newCity);
                        europePane.getChildren().add(newCity);
                        changed = true;
                        newCity.setOnMouseClicked(new ClickerHandler());
                        newCity.setId(cityName);
                    }

                    // Aktiverar knappen igen.
                    btnNewPlace.setDisable(false);

            }
        }
    }

    */

    class NewPlaceAlert extends Alert{
        NewPlaceAlert() {
            super(AlertType.CONFIRMATION);
            textField = new TextField();
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setPadding(new Insets(10));
            grid.setHgap(5);
            grid.setVgap(10);
            grid.addRow(0, new Label("Name of place: "), textField);
            setTitle("Name");
            setHeaderText(null);
            getDialogPane().setContent(grid);
        }
        public String getText() {
            return textField.getText().trim();
        }
    }
    class NewPlaceHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // Sätter muspekarn till ett crosshair.
            isCrosshair = true;
            //NewPlaceAlert popup = new NewPlaceAlert();
            textInputDialog = new TextInputDialog();
            //textField = new TextField();

            //btnNewPlace.setDisable(true);
            //europePane.setOnMouseClicked(new NewPlaceMouse());


            // Sätter muspekarn till crosshair så varje gång musen rör på sig om isCrosshair = true;
            scene.setOnMouseMoved(mouseEvent -> {
                if (isCrosshair) {
                    scene.setCursor(Cursor.CROSSHAIR);
                }
            });


            scene.setOnMouseClicked(mouseEvent2 -> {
                if (isCrosshair) {
                    isCrosshair = false;
                    scene.setCursor(Cursor.DEFAULT);
                    // Aktiverar knappen igen.
                    btnNewPlace.setDisable(false);

                    ///////////--------------------------------------------------------

                    double x = mouseEvent2.getX() + (outputArea.getWidth() - scene.getWidth());
                    double y = mouseEvent2.getY() + (outputArea.getHeight() - scene.getHeight());

                    if(x < image.getWidth() && y > 0 && !outputArea.getChildren().isEmpty()) {

                        /*
                        textInputDialog.setTitle("Name");
                        textInputDialog.setHeaderText(null);

                        // Lägger till textfälten till grid.
                        GridPane grid = new GridPane();
                        grid.add(new Label("Name of place: "), 0, 0);
                        grid.add(textField, 1, 0);
                        textInputDialog.getDialogPane().setContent(grid);
                        grid.setAlignment(Pos.CENTER);
                           */
                        TextField test = textInputDialog.getEditor();

                        textInputDialog.setTitle("Name");
                        textInputDialog.setHeaderText(null);
                        textInputDialog.setContentText("Name of place: ");

                        Optional<String> input = textInputDialog.showAndWait();
                        //textInputDialog.showAndWait();


                        //textInputDialog.showAndWait();

                        if (input.isPresent()) {
                            String cityName = test.getText();
                            City newCity = new City(cityName, x, y);
                            newCity.setId(newCity.getName());
                            listGraph.add(newCity);
                            outputArea.getChildren().add(newCity);
                            changed = true;
                            newCity.setOnMouseClicked(new ClickerHandler());

                        }
                    }
                }
            });

            btnNewPlace.setDisable(true);
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //Implementera funktionen hos New Connection knappen.
            ErrorPopup error = new ErrorPopup();
            // Kollar saker och visar error ifall något inte stämmer.
            if (citySelected1 == null || citySelected2 == null) {
                error.showAndWait();
                return;
            }
            if (listGraph.getEdgeBetween(citySelected1, citySelected2) != null)
            {
                error.showAndWait();
            }

            else
            {
                //Skapa popup
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Connection");
                dialog.setHeaderText("Connection from " + citySelected1.getName() + " to " + citySelected2.getName());

                // Skapar textfälten
                TextField nameField = new TextField();
                TextField timeField = new TextField();

                // Lägger till textfälten till grid.
                GridPane grid = new GridPane();
                grid.add(new Label("Name: "), 0, 0);
                grid.add(nameField, 1, 0);
                grid.add(new Label("Time: "), 0, 1);
                grid.add(timeField, 1, 1);
                dialog.getDialogPane().setContent(grid);
                grid.setAlignment(Pos.CENTER);
                grid.setVgap(10);

                // Show the dialog and get the results
                Optional<String> result = dialog.showAndWait();
                //System.out.println(result);

                if (result.isPresent()) {
                    // Process the results
                    String nameResult = nameField.getText();
                    // Check if time input is of type Int
                    try
                    {
                        Integer.parseInt(timeField.getText());
                    }
                    catch (NumberFormatException ex)
                    {
                        error.setContentText("Time must be an integer!");
                        error.showAndWait();
                        return;
                    }
                    //Converts timeField input to type Integer
                    Integer timeResult = Integer.parseInt(timeField.getText());
                    changed = true;
                    listGraph.connect(citySelected1, citySelected2, nameResult, timeResult);
                    Line drawLine = new Line(citySelected1.getCenterX(), citySelected1.getCenterY(), citySelected2.getCenterX(), citySelected2.getCenterY());
                    drawLine.setDisable(true);
                    outputArea.getChildren().add(drawLine);

                    changed = true;
                }
            }
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            ErrorPopup error = new ErrorPopup();

            // Kollar saker och visar error ifall något inte stämmer.
            if (citySelected1 == null || citySelected2 == null || listGraph.getEdgeBetween(citySelected1, citySelected2) == null) {
                error.showAndWait();
                return;
            }

            // Skapar popup fönstret som ska visa förbindelsen.
            textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("Connection");
            textInputDialog.setHeaderText("Connection from " + citySelected1.getName() + " to " + citySelected2.getName());

            // Skapar textfälten
            TextField nameField = new TextField();
            TextField timeField = new TextField();

            nameField.setText(listGraph.getEdgeBetween(citySelected1, citySelected2).getName());
            nameField.setEditable(false);

            //timeField.setText(Integer.toString(listGraph.getEdgeBetween(citySelected1, citySelected2).getWeight()));


            // Lägger till textfälten till grid.
            GridPane grid = new GridPane();
            grid.add(new Label("Name: "), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Time: "), 0, 1);
            grid.add(timeField, 1, 1);
            textInputDialog.getDialogPane().setContent(grid);
            grid.setAlignment(Pos.CENTER);
            grid.setVgap(10);

            // Show the dialog and get the results
            Optional<String> result = textInputDialog.showAndWait();
            if(result.isPresent() && result.get().isBlank()) {
                // Process the results
                String value1 = nameField.getText();
                String value2 = timeField.getText();
                Integer newTime;

                try{
                    newTime = Integer.parseInt(value2);
                    listGraph.setConnectionWeight(citySelected1, citySelected2, newTime);

                }catch (NumberFormatException e){
                    error.setContentText("Wrong input on time (must be whole number)");
                    error.showAndWait();
                    return;
                }
                changed = true;
            }
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override public void handle(WindowEvent event) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, exit anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    event.consume();
            }
        }

    }

    class ExitItemHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            try{
                WritableImage image = root.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            }catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR,"IO-fel "+e.getMessage());
                alert.showAndWait();
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        private String citiesString = "";
        private String edgesString = "";

        @Override
        public void handle(ActionEvent actionEvent) {

            try {
                citiesString = "";
                edgesString = "";
                FileWriter writer = new FileWriter("europa.graph");
                PrintWriter printWriter = new PrintWriter(writer);
                boolean first = false;
                for (City city : listGraph.getNodes()) {
                    if (!first) {
                        citiesString += city.toString();
                        first = true;
                    } else {
                        citiesString += ";" + city.toString();
                    }
                    for(var v : listGraph.getEdgesFrom(city))
                    {
                        edgesString += city.getName() + ";" + v.getDestination().getName() + ";" + v.getName() + ";" + v.getWeight() + "\n";
                    }
                }
                printWriter.println("file:europa.gif");
                printWriter.println(citiesString);
                printWriter.println(edgesString);

                //System.out.println("Saving file");
                writer.close();
                printWriter.close();
            } catch (IOException e) {
                //System.out.println("Error");
                return;
            }
            changed = false;
        }
    }
}