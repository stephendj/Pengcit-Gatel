package gatel.instacit;

import android.util.Log;

public class Person {

    private String name;
    private int[][] leftEye = new int[60][40];
    private int[][] rightEye = new int[60][40];
    private int[][] nose = new int[40][60];
    private int[][] mouth = new int[60][40];
    private double similarity = 0;

    public Person() {

    }

    public Person(String name, int[][] leftEye, int[][] rightEye, int[][] nose, int[][] mouth) {
        this.name = name;
        this.leftEye = leftEye;
        this.rightEye = rightEye;
        this.nose = nose;
        this.mouth = mouth;
    }

    public String getName() {
        return name;
    }

    public int[][] getLeftEye() {
        return leftEye;
    }

    public int[][] getRightEye() {
        return rightEye;
    }

    public int[][] getNose() {
        return nose;
    }

    public int[][] getMouth() {
        return mouth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeftEye(int[][] leftEye) {
        this.leftEye = leftEye;
    }

    public void setRightEye(int[][] rightEye) {
        this.rightEye = rightEye;
    }

    public void setNose(int[][] nose) {
        this.nose = nose;
    }

    public void setMouth(int[][] mouth) {
        this.mouth = mouth;
    }

    private double getLeftEyeSimilarity(Person otherPerson) {
        double leftEyeSimilarity = 0.0;

        for(int i = 0; i < leftEye.length; ++i) {
            for(int j = 0; j < leftEye[i].length; ++j) {
                if ((otherPerson.leftEye[i][j] & leftEye[i][j]) > 0) {
                    leftEyeSimilarity++;
                }
            }
        }

        return leftEyeSimilarity / 2400.0;
    }

    private double getRightEyeSimilarity(Person otherPerson) {
        double rightEyeSimilarity = 0.0;

        for(int i = 0; i < rightEye.length; ++i) {
            for(int j = 0; j < rightEye[i].length; ++j) {
                if ((otherPerson.rightEye[i][j] & rightEye[i][j]) > 0) {
                    rightEyeSimilarity++;
                }
            }
        }

        return rightEyeSimilarity / 2400.0;
    }

    private double getNoseSimilarity(Person otherPerson) {
        double noseSimilarity = 0.0;

        for(int i = 0; i < nose.length; ++i) {
            for(int j = 0; j < nose[i].length; ++j) {
                if ((otherPerson.nose[i][j] & nose[i][j]) > 0) {
                    noseSimilarity++;
                }
            }
        }

        return noseSimilarity / 2400.0;
    }

    private double getMouthSimilarity(Person otherPerson) {
        double mouthSimilarity = 0.0;

        for(int i = 0; i < mouth.length; ++i) {
            for(int j = 0; j < mouth[i].length; ++j) {
                if ((otherPerson.mouth[i][j] & mouth[i][j]) > 0) {
                    mouthSimilarity++;
                }
            }
        }

        return mouthSimilarity / 2400.0;
    }

    public double getSimilarity(Person otherPerson) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 60; ++i) for (int j = 0; j < 40; ++j) {
            builder.append(leftEye[i][j]);
        }
        double leftEyeSimilarity = getLeftEyeSimilarity(otherPerson);
        builder.append(": ").append(leftEyeSimilarity * 2400.);
        Log.d("PersonClassifier", builder.toString());
        double rightEyeSimilarity = getRightEyeSimilarity(otherPerson);
        double noseSimilarity = getNoseSimilarity(otherPerson);
        double mouthSimilarity = getMouthSimilarity(otherPerson);

        return (leftEyeSimilarity + rightEyeSimilarity + noseSimilarity + mouthSimilarity) / 4;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

}
