package gatel.instacit;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class PersonClassifier {

    private static final List<Person> dataset = new ArrayList<>();
    private static boolean datasetInitialized = false;

    public static void loadDataset(Context context) {
        try {
            InputStream stream = context.getResources().openRawResource(R.raw.data);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

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

                person.setLeftEye(leftEye);
                person.setRightEye(rightEye);
                person.setNose(nose);
                person.setMouth(mouth);

                dataset.add(person);
            }
            br.close();
            Log.d("PersonClassifier", dataset.size() + " faces was registered");
            datasetInitialized = true;
        } catch(Exception e) {
            Log.e("PersonClassifier", "Error occured when loading dataset", e);
            datasetInitialized = false;
        }
    }

    public static void classify(Person person) {
        if (!datasetInitialized) {
            Log.e("PersonClassifier", "Dataset is not initialized!");
        }
        List<Double> similarities = new ArrayList<>();

        for(Person p : dataset) {
            similarities.add(person.getSimilarity(p));
        }

        double max = similarities.get(0);
        int index = 0;
        for(int i = 0; i < similarities.size(); ++i) {
            if(similarities.get(i) > max) {
                max = similarities.get(i);
                index = i;
            }
            Log.d("PersonClassifier", "similarity with " + dataset.get(i).getName() + " is " + similarities.get(i));
        }
        person.setName(dataset.get(index).getName());
        person.setSimilarity(max);
    }
}
