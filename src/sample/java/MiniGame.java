package sample.java;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Timer;
import java.util.TimerTask;


public class MiniGame extends Application {
    final int size = 500, dot_size = 30, up = 1, right = 2, down = 3, left = 4;
    int delay = 100, length = 3, dir = 2, food_x, food_y, totalWin = -1;
    Canvas canvas;
    GraphicsContext gc;
    int[] x = new int[size * size];
    int[] y = new int[size * size];
    TimerTask game;
    boolean lost = false;

    ImagePattern fishRing;
    ImagePattern catRingFace;
    ImagePattern catRing;


    @Override
    public void start(Stage primaryStage) {


        StackPane root = new StackPane();
        canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        root.getChildren().add(canvas);
        startGame();

//root.set

        fishRing = new ImagePattern(new Image(getClass().getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/foodRing.png")));
        catRingFace = new ImagePattern(new Image(getClass().getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/ringFace1.png")));
        catRing = new ImagePattern(new Image(getClass().getResourceAsStream("../../resources/sprites/" + ResHandler.getCurrentAnimal() + "/ring.png")));

        canvas.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();
            if (key.equals(KeyCode.UP)) dir = up;
            if (key.equals(KeyCode.DOWN)) dir = down;
            if (key.equals(KeyCode.LEFT)) dir = left;
            if (key.equals(KeyCode.RIGHT)) dir = right;
        });
        Scene scene = new Scene(root, size, size);


        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                Main.checkPoint.setCoins(Main.checkPoint.getCoins() + totalWin);
                Main.updateCoins();

            }
        });

    }


    private void draw(GraphicsContext gc) {

        gc.clearRect(0, 0, size, size);
        if (!lost) {

            gc.setFill(fishRing);
            gc.fillOval(food_x, food_y, dot_size, dot_size);

            gc.setFill(catRingFace);
            gc.fillOval(x[0], y[0], dot_size, dot_size);


            gc.setFill(catRing);


            for (int i = 1; i < length; i++) {
                gc.fillOval(x[i], y[i], dot_size, dot_size);
            }

        } else {
            gc.setFill(Paint.valueOf("black"));
            gc.fillText("Game Over", size / 2 - 50, size / 2 - 15);
            game.cancel();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startGame() {
        length = 3;
        for (int i = 0; i < length; i++) {
            x[i] = dot_size - i * dot_size;
            y[i] = dot_size;
        }
        locateFood();


        game = new TimerTask() {
            @Override
            public void run() {
                if (!lost) {
                    checkFood();
                    checkCollision();
                    move();
                }
                draw(gc);
            }
        };
        Timer timer = new Timer();
        timer.schedule(game, 0L, delay);

    }

    private void locateFood() {
        totalWin++;
        food_x = (int) (Math.random() * ((size / dot_size) - 1)) * dot_size;
        food_y = (int) (Math.random() * ((size / dot_size) - 1)) * dot_size;
    }

    private void checkFood() {
        if (x[0] == food_x && y[0] == food_y) {
            length++;
            locateFood();
        }
    }

    private void checkCollision() {
        if (x[0] >= size) lost = true;
        if (y[0] >= size) lost = true;
        if (x[0] < 0) lost = true;
        if (y[0] < 0) lost = true;
        for (int i = 3; i < length; i++)
            if (x[0] == x[i] && y[0] == y[i]) {
                lost = true;
                break;
            }
    }

    private void move() {
        for (int i = length - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (dir == up) y[0] -= dot_size;
        if (dir == down) y[0] += dot_size;
        if (dir == right) x[0] += dot_size;
        if (dir == left) x[0] -= dot_size;
    }
}
