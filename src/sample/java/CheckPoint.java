package sample.java;

import java.io.*;
import java.util.Date;


//класс со всеми необходимыми данными
public class CheckPoint implements Serializable {

    //если isGone = true, то прогресс сбрасывается и создается новый чекпоинт
    public Boolean isGone;
    public Long bornTime;
    private Integer age;

    //время последнего сохранения в мс
    private Long lastTime;
    //показатели сытости и настроения
    private Double food;
    private Double mood;

    //строка, определяющая, какой питомец находится в данном сохранении
    private String currentAnimal;


    public String getCurrentAnimal() {
        return currentAnimal;
    }

    public void setCurrentAnimal(String currentAnimal) {
        this.currentAnimal = currentAnimal;
    }

    public CheckPoint(Long lastTime, Double food, Double mood, String currentAnimal) {
        this.setFood(food);
        this.setLastTime(lastTime);
        this.setMood(mood);
        this.setCurrentAnimal(currentAnimal);
    }

    public CheckPoint(Long lastTime, Double food, Double mood, String currentAnimal, Boolean isGone) {
        this.setFood(food);
        this.setLastTime(lastTime);
        this.setMood(mood);
        this.setCurrentAnimal(currentAnimal);
        this.isGone = isGone;
    }

    //сохранение состояния в файл
    public void save() {
        //   if (lastTime == null || food == null || mood == null || isGone) {
        //      throw new NullPointerException(lastTime.toString() + "  " + food + "  " + mood + " " + isGone);
        //   } else {
        try {
            File file = new File("save.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //  }
    }

    //загрузка сохранения из файла
    public static CheckPoint load() {
        CheckPoint checkPoint;
        try {

            File file = new File("save.txt");
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectOut = new ObjectInputStream(fileIn);
            checkPoint = (CheckPoint) objectOut.readObject();
            objectOut.close();


        } catch (Exception e) {
            e.printStackTrace();
            return new CheckPoint(new Date().getTime(), 0.9D, 0.9D, "cat", true);
        }

        if (checkPoint == null) {
            return new CheckPoint(new Date().getTime(), 0.9D, 0.9D, "cat", true);
        } else {

//            if(checkPoint.isGone == null){
//                checkPoint.isGone = true;
//            }
            long timeDif = (new Date().getTime() - checkPoint.getLastTime());
            double foodReduce = (timeDif / 36000) * 0.0001;
            double moodReduce = (timeDif / 36000) * 0.0002;


            checkPoint.setFood(checkPoint.getFood() - foodReduce);
            checkPoint.setMood(checkPoint.getMood() - moodReduce);
        }

        return checkPoint;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public Double getFood() {
        return food;
    }

    public void setFood(double food) {
        this.food = food;
        if (food > 1) {
            this.food = 1.0D;
            return;
        }
        if (food < 0) {
            this.food = 0.0000001;
        }

    }

    public Double getMood() {
        return mood;
    }

    public void setMood(Double mood) {

        this.mood = mood;

        if (mood > 1) {
            this.mood = 1.0D;
        }

        if (mood < 0) {
            this.mood = 0.0000001D;
        }

    }

    @Override
    public String toString() {
        return "CheckPoint{" +
                "lastTime=" + lastTime +
                ", food=" + food +
                ", mood=" + mood +
                ", currentAnimal='" + currentAnimal + '\'' +
                ", isGone=" + isGone +
                '}';
    }
}

