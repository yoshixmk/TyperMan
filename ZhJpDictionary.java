import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ZhJpDictionary {

    public static ArrayList<ZhJpWordItem> getWords(String file) throws FileNotFoundException {

        Scanner input =  new Scanner(new File(file), "UTF-8");

        ArrayList<ZhJpWordItem> words = new ArrayList<>();
        while(input.hasNext()) {
            String[] splitedWords = input.nextLine().split(",");
            if (splitedWords.length < 3) {
            	continue;
            }
			words.add(new ZhJpWordItem(splitedWords[0], splitedWords[1], splitedWords[2]));
        }
        input.close();

        return words;
    }
}
