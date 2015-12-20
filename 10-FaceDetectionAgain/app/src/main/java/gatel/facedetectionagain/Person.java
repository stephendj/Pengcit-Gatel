package gatel.facedetectionagain;

public class Person {

    private String name;
    private int[][] leftEye = new int[60][40];
    private int[][] rightEye = new int[60][40];
    private int[][] nose = new int[40][60];
    private int[][] mouth = new int[60][40];

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
                if(otherPerson.getLeftEye()[i][j] == leftEye[i][j]) {
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
                if(otherPerson.getRightEye()[i][j] == rightEye[i][j]) {
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
                if(otherPerson.getNose()[i][j] == nose[i][j]) {
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
                if(otherPerson.getMouth()[i][j] == mouth[i][j]) {
                    mouthSimilarity++;
                }
            }
        }

        return mouthSimilarity / 2400.0;
    }

    public double getSimilarity(Person otherPerson) {
        double leftEyeSimilarity = getLeftEyeSimilarity(otherPerson);
        double rightEyeSimilarity = getRightEyeSimilarity(otherPerson);
        double noseSimilarity = getNoseSimilarity(otherPerson);
        double mouthSimilarity = getMouthSimilarity(otherPerson);

        return (leftEyeSimilarity + rightEyeSimilarity + noseSimilarity + mouthSimilarity) / 4;
    }
}
