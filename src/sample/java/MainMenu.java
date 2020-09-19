package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

//главное меню, где можно выбрать питомца и начать игру
public class MainMenu extends Application {
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


        if (!checkPoint.isGone) {
            launchMain(stage);
            return;
        }

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
                CheckPoint newGameCheckPoint = CheckPoint.createNewAnimal(ResHandler.getCurrentAnimal());
                newGameCheckPoint.save();
                launchMain(stage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void launchMain(Stage stage) {
        try {
            stage.hide();
            Parent tvParent;
            tvParent = FXMLLoader.load(getClass().getResource("../startScreen.fxml"));
            Scene scene2 = new Scene(tvParent);
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
