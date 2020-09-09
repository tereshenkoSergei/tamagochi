package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static java.lang.Thread.onSpinWait;
import static java.lang.Thread.sleep;

//класс, отвечающий за обработку конца игры и сброс прогресса
public class GameOver extends Application implements Runnable {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group group = new Group();
        Scene scene = new Scene(group, 600, 350);


        Parent content = FXMLLoader.load((getClass().getResource("../gameOver.fxml")));
        BorderPane root = new BorderPane();
        root.setCenter(content);
        group.getChildren().add(root);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        ImageView gameOverBg = (ImageView) (scene.lookup("#gameOverBg"));
        ImageView animalImageView = (ImageView) (scene.lookup("#animalImageView"));

        animalImageView.setImage(ResHandler.getConditionImage(Condition.BROKEN));
        gameOverBg.setImage(new Image(getClass().getResourceAsStream("../../resources/sprites/bg/gameoverBg.jpg")));
        CheckPoint checkPoint = CheckPoint.load();
        checkPoint.isGone = true;
        checkPoint.save();
        System.out.println(CheckPoint.load());

        primaryStage.setOnCloseRequest(windowEvent -> Platform.exit());
    }

    @Override
    public void run() {
        Main.hide();
        Stage gameOverStage;
        try {
            gameOverStage = new Stage();
            GameOver gameOver = new GameOver();
            gameOver.start(gameOverStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        Platform.exit();
    }
}
