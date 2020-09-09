package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;


public class MainMenuScene extends Application {
    private String currentAnimal = "cat";
    private final String SPRITE_PATH = "../../resources/sprites/" + currentAnimal + "/";


    ImageView animalImageView;
    ImageView bgImageView;

    Button anotherAnimalButton;
    Button getAnimalButton;

    Main main;


    @Override
    public void start(Stage stage) throws IOException {

        CheckPoint checkPoint = CheckPoint.load();
        System.out.println(checkPoint);

        if (!checkPoint.isGone) {

            launchMain();
            stage.hide();
            return;
        }
        System.out.println(checkPoint);


        Group group = new Group();
        Scene scene = new Scene(group, 600, 350);


        Parent content = FXMLLoader.load((getClass().getResource("../startScreen.fxml")));
        BorderPane root = new BorderPane();
        root.setCenter(content);
        group.getChildren().add(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();


        animalImageView = (ImageView) (scene.lookup("#animalImage"));
        animalImageView.setImage(ResHandler.getConditionImage(Condition.NORMAL));

        bgImageView = (ImageView) (scene.lookup("#bgImage"));
        bgImageView.setImage(new Image(getClass().getResourceAsStream("../../resources/sprites/bg/MenuBG.jpg")));

        anotherAnimalButton = (Button) (scene.lookup("#anotherAnimalButton"));
        getAnimalButton = (Button) (scene.lookup("#getAnimalButton"));

        stage.setOnCloseRequest(windowEvent -> stop());

        anotherAnimalButton.setOnAction(value ->
        {
            switch (ResHandler.getCurrentAnimal()) {
                case "cat":
                    ResHandler.setCurrentAnimal("dog");
                    animalImageView.setImage(ResHandler.getConditionImage(Condition.NORMAL));
                    break;
                case "dog":
                    ResHandler.setCurrentAnimal("cat");
                    animalImageView.setImage(ResHandler.getConditionImage(Condition.NORMAL));
                    break;
            }
        });




        getAnimalButton.setOnAction(actionEvent -> {

            try {
                CheckPoint newGameCheckPoint =
                        new CheckPoint(new Date().getTime(), 0.5, 0.5, ResHandler.getCurrentAnimal(), Boolean.FALSE);

                newGameCheckPoint.save();
                launchMain();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void launchMain() {
        try {
            Parent tvParent = null;
            tvParent = FXMLLoader.load(getClass().getResource("../startScreen.fxml"));
            Scene scene2 = new Scene(tvParent);
            // Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Stage window = new Stage();
            main = new Main();
            window.setScene(scene2);
            main.start(window);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {


        try {
            super.stop();
            main.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.exit();
    }
}
