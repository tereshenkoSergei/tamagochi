package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Date;

import static java.lang.Thread.sleep;

public class Main extends Application {

    //класс со всеми необходимыми данными
    private CheckPoint checkPoint;
    //картинка животного на главном экране
    private ImageView animalImage;
    //шкала сытости
    private ProgressBar foodPB;
    //шкала настроения
    private ProgressBar moodPB;
    //inner класс, отвечающий за изменение сытости и настроения, пока приложение запущено
    private ParamsController paramsController;

    // inner класс, отвечающий за измениение визуального состояния питомца (веселый, грустный, голодный и тд)
    //в зависимости от параметров
    private ConditionController conditionController;
    //копия stage из main(), нужен для корректной отработки stage.hidden() вне класса Main
    private static Stage stageCopy;

    //картинка, где находится место для миски с кормом
    ImageView foodPlaceImage;

    //спрайт с миской для еды
    ImageView foodPic;

    ImageView goodFood;
    ImageView regularGood;
    ImageView badFood;

    Button feedButton;
    Button walkButton;
    Button toShopButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stageCopy = primaryStage;
        Group group = new Group();
        Scene scene = new Scene(group, 600, 350);

        Parent content = FXMLLoader.load((getClass().getResource("../mainPage.fxml")));
        BorderPane root = new BorderPane();
        root.setCenter(content);
        group.getChildren().add(root);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        //иницилизация объявленных в классе переменных
        initVariables(scene);

        //настройка обработки нажатий на кнопки
        setupActions();

        //обработка нажатия на "крестик"
        primaryStage.setOnCloseRequest(windowEvent -> stop());

        //класс, отвечающий за изменение сытости и настроения, пока приложение запущено
        paramsController = new ParamsController();
        paramsController.start();

        //класс, отвечающий за измениение визуального состояния питомца (веселый, грустный, голодный и тд)
        //в зависимости от параметров
        conditionController = new ConditionController();
        conditionController.start();


    }

    //inner класс, отвечающий за изменение сытости и настроения, пока приложение запущено
    private class ParamsController extends Thread {


        @Override
        public void run() {

            long dif = 0;

            while (true) {

                dif = (new Date().getTime() - checkPoint.getLastTime());
                if (dif > 360) {

                    checkPoint.setLastTime(new Date().getTime());

                    //снижение настроения через промежутоквремени
                    if ((foodPB.getProgress() - (0.0001) * (dif / 360) >= 0)) {

                        foodPB.setProgress(foodPB.getProgress() - (0.0001) * (dif / 360));
                    }
                    //снижение сытости через промежуок времени
                    if ((moodPB.getProgress() - (0.0004) * (dif / 360) >= 0)) {

                        moodPB.setProgress(moodPB.getProgress() - (0.0002) * (dif / 360));

                        //доаолнительное снижение настроения при низкой сытости
                        if (foodPB.getProgress() < 0.3 && (moodPB.getProgress() - 0.0002 * (dif / 360)) >= 0) {
                            moodPB.setProgress(moodPB.getProgress() - 0.0002 * (dif / 360));
                        }
                    }


                }


            }

        }
    }


    private class ConditionController extends Thread {
        public boolean isPaused = false;

        private boolean isAnimalAlive = true;

        @Override
        public void run() {


            while (true) {


                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isPaused) {
                    //если с питомцем всё в порядке
                    if (moodPB.getProgress() > 0.6 && foodPB.getProgress() > 0.3) {
                        animalImage.setImage(ResHandler.getConditionImage(Condition.NORMAL));
                        continue;
                    }

                    //конец игры, если один из параметров питомца опустился до нуля
                    if (foodPB.getProgress() <= 0.01 || moodPB.getProgress() <= 0.01) {

                        if (isAnimalAlive) {
                            isAnimalAlive = false;
                            GameOver gameOver = new GameOver();
                            Platform.runLater(gameOver);

                        }

                    }

                    //питомец голоден
                    if (foodPB.getProgress() < 0.3) {
                        animalImage.setImage(ResHandler.getConditionImage(Condition.HUNGRY));
                        continue;
                    }
                    //питомец грустный
                    if (moodPB.getProgress() < 0.5) {
                        animalImage.setImage(ResHandler.getConditionImage(Condition.SAD));
                        continue;
                    }
                }

            }
        }
    }


    @Override
    public void stop() {


        paramsController.stop();
        conditionController.stop();

        Boolean isGone = false;
        if (foodPB.getProgress() <= 0.01 || moodPB.getProgress() <= 0.01) {
            isGone = true;
        }
        //автоматические сохранение прогресса при выходе
        CheckPoint checkPoint = new CheckPoint(
                new Date().getTime(),
                foodPB.getProgress(),
                moodPB.getProgress(),
                ResHandler.getCurrentAnimal(),
                isGone
        );
        checkPoint.save();

        Platform.exit();
        System.exit(0);


    }

    //позволяет скрыть окно за пределами класса. Вспомогательнаяя функция
    public static void hide() {
        stageCopy.hide();
    }


    //установка обработчиков кнопок
    private void setupActions() {


        walkButton.setOnAction(actionEvent -> moodPB.setProgress(moodPB.getProgress() + 0.3));

        toShopButton.setOnAction(actionEvent -> foodPB.setProgress(0));

        //процес "кормления" питомца
        feedButton.setOnAction(actionEvent -> {
            foodPB.setProgress(foodPB.getProgress() + 0.3);



        });

        goodFood.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double tempFood = foodPB.getProgress() + 0.2;
                double tempMood = moodPB.getProgress() + 0.3;

                if (tempFood > 1) tempFood = 1;
                if(tempMood > 1) tempMood = 1;

                foodPB.setProgress(tempFood);
                moodPB.setProgress(tempMood);


                new Thread(() -> {

                    try {

                        conditionController.isPaused = true;
                        foodPlaceImage.setImage(foodPic.getImage());
                        animalImage.setImage(ResHandler.getConditionImage(Condition.EATING));

                        sleep(3000);

                        animalImage.setImage(ResHandler.getConditionImage(Condition.LOVE));
                        sleep(3000);

                        conditionController.isPaused = false;


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
        //regularGood
        //badFood


    }

    //иницилизирует переменные, которые используются в процессе игры
    private void initVariables(Scene scene) {

        foodPB = (ProgressBar) scene.lookup("#foodPB");
        moodPB = (ProgressBar) scene.lookup("#moodPB");

        //загрузка последнего сохранения из файла
        checkPoint = CheckPoint.load();

        //определение, какое животное в конкретный момент будет находиться на экране
        ResHandler.setCurrentAnimal(checkPoint.getCurrentAnimal());

        foodPB.setProgress(checkPoint.getFood());
        moodPB.setProgress(checkPoint.getMood());


        foodPic = new ImageView(new Image(getClass().getResourceAsStream("../../resources/sprites/other/catBowl.png")));

        foodPlaceImage = (ImageView) (scene.lookup("#foodPlaceImage"));
        animalImage = (ImageView) (scene.lookup("#animalImage"));
        animalImage.setImage(ResHandler.getConditionImage(Condition.SAD));

        feedButton = (Button) scene.lookup("#feedButton");
        walkButton = (Button) scene.lookup("#walkButton");
        toShopButton = (Button) scene.lookup("#toShopButton");


        goodFood = (ImageView) scene.lookup("#goodFood");
        regularGood = (ImageView) scene.lookup("#regularGood");
        badFood = (ImageView) scene.lookup("#badFood");

        goodFood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/good.png")));
        regularGood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/regular.png")));
        badFood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/bad.png")));

    }
}
