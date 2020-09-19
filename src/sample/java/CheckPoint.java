package sample.java;

import java.io.*;
import java.util.Date;


//класс со всеми необходимыми данными
public class CheckPoint implements Serializable {

    //если isGone = true, то прогресс сбрасывается и создается новый чекпоинт
    public Boolean isGone;

    //время создания питомца
    public Long bornTime;

    //время последнего сохранения в мс
    private Long lastTime;
    //показатели сытости и настроения
    private Double food;
    private Double mood;

    //строка, определяющая, какой питомец находится в данном сохранении
    private String currentAnimal;

    private Integer coins;

    public Integer getCoins() {
        if (this.coins == null) {
            return 0;
        }
        return coins;
    }

    public void setCoins(Integer coins) {
        if (coins <= 0) {
            this.coins = 0;
        } else {
            this.coins = coins;
        }
    }

    public String getCurrentAnimal() {
        return currentAnimal;
    }

    public void setCurrentAnimal(String currentAnimal) {
        this.currentAnimal = currentAnimal;
    }


    public CheckPoint(Long lastTime, Double food, Double mood, String currentAnimal, Boolean isGone, Long bornTime) {
        this.setFood(food);
        this.setLastTime(lastTime);
        this.setMood(mood);
        this.setCurrentAnimal(currentAnimal);
        this.isGone = isGone;
        this.bornTime = bornTime;
    }

    //сохранение состояния в файл
    public void save() {

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
            return new CheckPoint(new Date().getTime(), 0.9D, 0.9D, "cat", true, new Date().getTime());
        }

        if (checkPoint == null) {
            return new CheckPoint(new Date().getTime(), 0.9D, 0.9D, "cat", true, new Date().getTime());
        } else {

            //изменение показателей при загрузке. На выходе объект с измененными параметрами (сытостью и настроением)
            long timeDif = (new Date().getTime() - checkPoint.getLastTime());

            double foodReduce = (timeDif / (1000 * 60 * 60)) * 0.01;
            double moodReduce = (timeDif / (1000 * 60 * 60)) * 0.02;


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
        if (food > 1) {
            this.food = 1.0D;
            return;
        }
        if (food < 0) {
            this.food = 0.0000001;
        }
        this.food = food;

    }

    public Double getMood() {
        return mood;
    }

    public void setMood(Double mood) {
        if (mood > 1) {
            this.mood = 1.0D;
        }
        if (mood < 0) {
            this.mood = 0.0000001D;
        }
        this.mood = mood;

    }

    //условный возраст питомца. увеличивается на 1 каждые 3 часа.
    public int getAge() {
        return (int) (new Date().getTime() - bornTime) / (1000 * 60 * 60 * 3);
    }

    public static CheckPoint createNewAnimal(String currentAnimal) {
        return new CheckPoint(new Date().getTime(), 0.7, 0.7, currentAnimal, false, new Date().getTime());
    }

    @Override
    public String toString() {
        return "CheckPoint{" +
                "isGone=" + isGone +
                ", bornTime=" + bornTime +
                ", lastTime=" + lastTime +
                ", food=" + food +
                ", mood=" + mood +
                ", currentAnimal='" + currentAnimal + '\'' +
                ", coins=" + coins +
                '}';
    }
}

