package sample.java;

import javafx.scene.image.Image;


//класс, упрощающий доступп к спрайтам
public class ResHandler {
    private static String currentAnimal = "dog";
    private static String SPRITE_PATH = "../../resources/sprites/" + currentAnimal + "/";
    private static ResHandler resHandler;

    public static void setCurrentAnimal(String animal){
        currentAnimal = animal;
        SPRITE_PATH = "../../resources/sprites/" + currentAnimal + "/";
    }

  static {
        resHandler = new ResHandler();
    }

    public static Image getConditionImage(Condition condition) {
        switch (condition) {
            case HUNGRY:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "hungry.png")));

            case NORMAL:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "calm.png")));

            case SAD:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "sad.png")));
            case EATING:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "eating.png")));
            case LOVE:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "love.png")));
            case PRESENT:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "present.png")));
            case BROKEN:
                return (new Image(resHandler.getClass().getResourceAsStream(SPRITE_PATH + "broken.png")));
        }
        return null;
    }



    public static String getCurrentAnimal() {
        return currentAnimal;
    }
}
