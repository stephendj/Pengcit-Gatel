package colorcount;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

public class ColorCount {
  
  public static Set<Integer> getColorCollectionInFile(String filename) throws IOException {
    return getColorCollectionInImage(ImageIO.read(new File(filename)));
  }
  
  public static Set<Integer> getColorCollectionInImage(BufferedImage image) {
    int height = image.getHeight();
    int width = image.getWidth();
    
    Set<Integer> colors = new HashSet<>();
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        colors.add(image.getRGB(x, y));
      }
    }
    return colors;
  }
  
  public static String getColorDetailInFile(String filename) throws IOException {
      StringBuilder detailColor = new StringBuilder("");
      Map<Color,Integer> color = getColorDetailInImage(ImageIO.read(new File(filename)));
	  
	  colorDetailToString(color);
      
      return detailColor.toString();
  }
  
  public static String colorDetailToString(Map<Color,Integer> color) {
      StringBuilder detailColor = new StringBuilder("");
      
      for (Map.Entry<Color, Integer> entry : color.entrySet())
      {
          detailColor.append("Red(");
          detailColor.append(entry.getKey().getRed());
          detailColor.append(") | Green(");
          detailColor.append(entry.getKey().getGreen());
          detailColor.append(") | Blue(");
          detailColor.append(entry.getKey().getBlue());
          detailColor.append("): ");
          detailColor.append(entry.getValue());
          detailColor.append("\n");
      }
      
      return detailColor.toString();
  }
          
  public static Map<Color,Integer> getColorDetailInImage (BufferedImage image) {
      Map<Color,Integer> color = new HashMap<Color,Integer>();
      
      int height = image.getHeight();
      int width = image.getWidth();
      
      for (int x = 0 ;x <width; ++x) {
          for (int y = 0 ;y<height;++y) {
              Color c = new Color(image.getRGB(x,y), true);
              if (color.containsKey(c)) {
                  color.put(c, color.get(c)+1);
              } else {
                  color.put(c,1);
              }
          }
       }
      return color;
  }
}
