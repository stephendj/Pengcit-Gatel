package gatel.facedetectionagain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class PersonClassifier {
    private List<Person> dataset = new ArrayList<>();

    public PersonClassifier() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data.txt"));
            String line = br.readLine();

            while (line != null) {
                Person person = new Person();

                // Set the person's name
                person.setName(line);
                line = br.readLine();

                // Set the person't left eye
                int[][] leftEye = new int[60][40];
                for(int i = 0; i < line.length(); ++i) {
                    leftEye[i/40][i%40] = Character.getNumericValue(line.charAt(i));
                }
                line = br.readLine();

                // Set the person't left eye
                int[][] rightEye = new int[60][40];
                for(int i = 0; i < line.length(); ++i) {
                    rightEye[i/40][i%40] = Character.getNumericValue(line.charAt(i));
                }
                line = br.readLine();

                // Set the person't left eye
                int[][] nose = new int[40][60];
                for(int i = 0; i < line.length(); ++i) {
                    nose[i/60][i%60] = Character.getNumericValue(line.charAt(i));
                }
                line = br.readLine();

                // Set the person't left eye
                int[][] mouth = new int[60][40];
                for(int i = 0; i < line.length(); ++i) {
                    mouth[i/40][i%40] = Character.getNumericValue(line.charAt(i));
                }
                line = br.readLine();

                dataset.add(person);
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String classify(Person person) {
        List<Double> similarities = new ArrayList<>();

        for(Person p : dataset) {
            similarities.add(p.getSimilarity(person));
        }

        double max = similarities.get(0);
        int index = 0;
        for(int i = 1; i < similarities.size(); ++i) {
            if(similarities.get(i) > max) {
                max = similarities.get(i);
                index = i;
            }
        }

        return dataset.get(index).getName();
    }
}
