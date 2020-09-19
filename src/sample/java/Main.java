package sample.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class Main extends Application {

    //класс со всеми необходимыми данными
    public static CheckPoint checkPoint;
    //картинка животного на главном экране

    //шкала сытости
    private ProgressBar foodPB;
    //шкала настроения
    private ProgressBar moodPB;
    //inner класс, отвечающий за изменение сытости и настроения, пока приложение запущено

    private static Label currCoins;

    // inner класс, отвечающий за измениение визуального состояния питомца (веселый, грустный, голодный и тд)
    //в зависимости от параметров
    private ConditionController conditionController;

    private static Stage stageCopy;

    //изображения: главный фон, три кнопки еды (вкусная, обычная и невкусная), кнопка начала мини игры, игра с питомцем, изображение питомца
    private ImageView mainBg, goodFood, regularGood, badFood, miniGameImageView, toysImageView, animalImage;


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


        //класс, отвечающий за измениение визуального состояния питомца (веселый, грустный, голодный и тд)
        //в зависимости от параметров
        conditionController = new ConditionController();
        conditionController.start();
    }

    //inner класс, отвечающий за изменение сытости и настроения, пока приложение запущено
    private class ParamsController extends TimerTask {
        @Override
        public void run() {

            //снижение сытости через промежуок времени
            checkPoint.setFood(foodPB.getProgress() - (0.01));
            foodPB.setProgress(checkPoint.getFood());

            //снижение настроения через промежуток времени
            checkPoint.setMood(moodPB.getProgress() - (0.02));
            moodPB.setProgress(checkPoint.getMood());

        }
    }

    //inner класс, отвечающий за изменение визуального состояния питомца
    private class ConditionController extends Thread {
        //позволяет приостановить выполнение потока
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

                    //низкий показатель сытости
                    if (foodPB.getProgress() < 0.3) {
                        animalImage.setImage(ResHandler.getConditionImage(Condition.HUNGRY));
                        continue;
                    }
                    //низкий показатель настроения
                    if (moodPB.getProgress() < 0.5) {
                        animalImage.setImage(ResHandler.getConditionImage(Condition.SAD));
                    }
                }

            }
        }
    }

    //происходит при выходе из игры
    @Override
    public void stop() {

        conditionController.stop();
        saveCheckpoint();
        Platform.exit();
        System.exit(0);
    }

    //сохранение прогресса в локальный файл
    private void saveCheckpoint() {
        //сброс прогресса, если игра завершилась по причине низких показателей
        boolean isGone = false;
        if (foodPB.getProgress() <= 0.01 || moodPB.getProgress() <= 0.01) {
            isGone = true;
        }

        CheckPoint checkPoint1 = new CheckPoint(
                new Date().getTime(),
                foodPB.getProgress(),
                moodPB.getProgress(),
                ResHandler.getCurrentAnimal(),
                isGone,
                checkPoint.bornTime
        );
        checkPoint1.setCoins(checkPoint.getCoins());
        checkPoint1.save();

    }

    //позволяет скрыть окно за пределами класса. Вспомогательнаяя функция
    public static void hide() {
        stageCopy.hide();
    }

    //установка обработчиков кнопок
    private void setupActions() {

        //если покормить питомца вкусной едой
        goodFood.setOnMouseClicked(mouseEvent -> {
            if (checkPoint.getCoins() >= 8) {
                checkPoint.setCoins(checkPoint.getCoins() - 8);
                updateCoins();


                double tempFood = foodPB.getProgress() + 0.2;
                double tempMood = moodPB.getProgress() + 0.3;

                if (tempFood > 1) tempFood = 1;
                if (tempMood > 1) tempMood = 1;

                foodPB.setProgress(tempFood);
                moodPB.setProgress(tempMood);

                new Thread(() -> {

                    hideButtons();

                    try {
                        conditionController.isPaused = true;
                        animalImage.setImage(ResHandler.getConditionImage(Condition.EATING));
                        sleep(3000);
                        animalImage.setImage(ResHandler.getConditionImage(Condition.LOVE));
                        sleep(3000);
                        conditionController.isPaused = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showButtons();
                }).start();
            }

        });

        //если покормить питомца обычной едой
        regularGood.setOnMouseClicked(mouseEvent -> {

            if (checkPoint.getCoins() >= 4) {
                checkPoint.setCoins(checkPoint.getCoins() - 4);
                updateCoins();

                checkPoint.setFood(foodPB.getProgress() + 0.3);

                foodPB.setProgress(checkPoint.getFood());

                new Thread(() -> {
                    hideButtons();
                    try {
                        conditionController.isPaused = true;
                        animalImage.setImage(ResHandler.getConditionImage(Condition.EATING));
                        sleep(3000);
                        conditionController.isPaused = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showButtons();
                }).start();
            }
        });

        //если покормить питомца невкусной едой
        badFood.setOnMouseClicked(mouseEvent -> {
            double tempFood = foodPB.getProgress() + 0.3;
            double tempMood = moodPB.getProgress() - 0.2;

            if (tempFood > 1) tempFood = 1;
            if (tempMood <= 0.05) tempMood = 0.05;

            foodPB.setProgress(tempFood);
            moodPB.setProgress(tempMood);
            new Thread(() -> {
                try {
                    hideButtons();
                    conditionController.isPaused = true;
                    animalImage.setImage(ResHandler.getConditionImage(Condition.EATING));
                    sleep(3000);
                    animalImage.setImage(ResHandler.getConditionImage(Condition.UNHAPPY));
                    sleep(3000);

                    conditionController.isPaused = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showButtons();
            }).start();
        });

        //начало мини-игры
        miniGameImageView.setOnMouseClicked(mouseEvent -> {
            MiniGame miniGame = new MiniGame();
            Stage gameStage = new Stage();
            miniGame.start(gameStage);
        });

        //поиграться с питомцем
        toysImageView.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new Thread((new Runnable() {
                    @Override
                    public void run() {
                        conditionController.isPaused = true;
                        hideButtons();

                        if ((moodPB.getProgress() + 0.3) > 1) {
                            moodPB.setProgress(1);
                        } else {

                            moodPB.setProgress(checkPoint.getMood());
                        }


                        switch ((new Random().nextInt(3) + 1)) {
                            case 1:
                                animalImage.setImage(ResHandler.getConditionImage(Condition.PLAYING1));
                                break;
                            case 2:
                                animalImage.setImage(ResHandler.getConditionImage(Condition.PLAYING2));
                                break;
                            case 3:
                                animalImage.setImage(ResHandler.getConditionImage(Condition.PLAYING3));
                                break;
                            default:
                                System.out.println("not within 1 - 3");
                                break;
                        }


                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mainBg.setImage(new Image(getClass().getResourceAsStream("../../resources/sprites/bg/bg.jpg")));
                        conditionController.isPaused = false;
                        showButtons();

                    }
                })).start();
            }
        });

    }

    public static void updateCoins() {
        currCoins.setText(checkPoint.getCoins().toString());
    }


    //второстепенные элементы, которые можно скрыть при необходимости: изображения монет и стоимости еды
    private ImageView coin1, coin2, coin3;
    private Label food8, food4, food0;

    //иницилизирует переменные, которые используются в процессе игры.
    private void initVariables(Scene scene) {



        //загрузка последнего сохранения из файла
        checkPoint = CheckPoint.load();

        //определение, какой питомец в конкретный момент будет находиться на экране
        ResHandler.setCurrentAnimal(checkPoint.getCurrentAnimal());

        //label с счетчиком виртуальной валюты
        currCoins = (Label) scene.lookup("#currCoins");
        currCoins.setText(checkPoint.getCoins().toString());

        mainBg = (ImageView) scene.lookup("#bdImage");

        foodPB = (ProgressBar) scene.lookup("#foodPB");
        moodPB = (ProgressBar) scene.lookup("#moodPB");

        foodPB.setProgress(checkPoint.getFood());
        moodPB.setProgress(checkPoint.getMood());

        animalImage = (ImageView) (scene.lookup("#animalImage"));
        animalImage.setImage(ResHandler.getConditionImage(Condition.SAD));

        goodFood = (ImageView) scene.lookup("#goodFood");
        regularGood = (ImageView) scene.lookup("#regularGood");
        badFood = (ImageView) scene.lookup("#badFood");

        miniGameImageView = (ImageView) scene.lookup("#toyImageView");
        toysImageView = (ImageView) scene.lookup("#toysImageView");

        miniGameImageView.setImage(new Image(getClass().getResourceAsStream("../../resources/sprites/other/mgIco.png")));

        toysImageView.setImage(new Image(getClass().getResourceAsStream("../../resources/sprites/other/toyBox.png")));

        goodFood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/good.png")));
        regularGood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/regular.png")));
        badFood.setImage(new Image(getClass()
                .getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/food/bad.png")));

        //запуск потока, отвечающего за изменение сытости и настроениz, пока приложение запущено
        Timer timer = new Timer();
        timer.schedule(new ParamsController(), 0, 1000 * 60 * 60);

        updateCoins();

        //определениe роста питомца
        if (checkPoint.getAge() > 6) {
            animalImage.setFitWidth(180);
            animalImage.setFitHeight(180);
        }
        if (checkPoint.getAge() <= 6) {
            animalImage.setFitWidth(140);
            animalImage.setFitHeight(140);
            animalImage.setY(60);

            if (checkPoint.getAge() <= 4) {
                animalImage.setFitWidth(100);
                animalImage.setFitHeight(100);
                animalImage.setY(60);

                if (checkPoint.getAge() <= 2) {
                    animalImage.setFitWidth(60);
                    animalImage.setFitHeight(60);
                    animalImage.setY(100);
                }
            }
        }
//второстепенные элементы, которые можно скрыть при необходимости: изображения монет и стоимости еды
        food8 = (Label) scene.lookup("#food8");
        food4 = (Label) scene.lookup("#food4");
        food0 = (Label) scene.lookup("#food0");

        coin1 = (ImageView) scene.lookup("#coin1");
        coin2 = (ImageView) scene.lookup("#coin2");
        coin3 = (ImageView) scene.lookup("#coin3");
    }


    private void hideButtons() {
        goodFood.setVisible(false);
        regularGood.setVisible(false);
        badFood.setVisible(false);
        toysImageView.setVisible(false);
        miniGameImageView.setVisible(false);

        food8.setVisible(false);
        food4.setVisible(false);
        food0.setVisible(false);
        coin1.setVisible(false);
        coin2.setVisible(false);
        coin3.setVisible(false);


    }

    private void showButtons() {
        goodFood.setVisible(true);
        regularGood.setVisible(true);
        badFood.setVisible(true);
        toysImageView.setVisible(true);
        miniGameImageView.setVisible(true);

        food4.setVisible(true);
        food0.setVisible(true);
        food8.setVisible(true);
        coin1.setVisible(true);
        coin2.setVisible(true);
        coin3.setVisible(true);

    }
}
