// Get filename from parameter: file to process
// Get filename from parameter: output file

// Steps:
// - Open the files

// - Read next line of the input file
// - Use regex pattern to select a color value setting
// - Replace the color value, if found
// - Add the new (or unchanged) line to an ArrayList
// - Repeat until every line has been processed

// - Write everything to the output file

import java.io.*;
import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseScheme {

  static final ArrayList<String> resultList = new ArrayList<String>();
  static final String regexForValue = "value=\".*\"";
  static final String regexForName = "name=\".*?\"";
  static final String regexForVersion = "version=\".*?\"";
  //  static final String userDir = System.getProperty("user.dir");

  public static void main(String[] args) {

    // Temporary fake parameters
//    String[] args = new String[5];
//    args[0] = userDir + "\\src\\app\\test_theme.icls"; // Input file
//    args[1] = userDir + "\\src\\app\\output.icls"; // Output file
//    args[2] = "Default Scheme Name";
//    args[3] = "e0c0e"; // Background color
//    args[4] = "c1c1c1"; // Foreground color

    String inputFile = "";
    String outputFile = "";
    String newName = "";
    String newValue1 = "";
    String newValue2 = "";

    try {
      inputFile = args[0];
      outputFile = args[1];
      newName = "name=\"" + args[2] + "\"";
      newValue1 = "value=\"" + args[3] + "\"";
      newValue2 = "value=\"" + args[4] + "\"";
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.print(
              " Provide the following parameters:\n" +
              "   1: Input file name\n" +
              "   2: Output file name (will overwrite file without asking)\n" +
              "   3: New color-scheme name\n" +
              "   4: Background color (HEX code)\n" +
              "   5: Foreground color (HEX code)\n" +
              " Example:\n" +
              " java ParseScheme inputFile.icls outputFile.icls \"New Scheme Name\" 000000 c1c1c1");
      System.exit(0);
    }

    // There's no need to use regex for this, it's only for practise
    String[] regexString = {
            // Match type 1: change color value to "e0c0e"
            "\"CARET_ROW_COLOR\" value",
            "\"BACKGROUND\" value",
            // Match type 2: change color value to "c1c1c1"
            // "\"ERROR_STRIPE_COLOR\" value",
            "\"FOREGROUND\" value",
            // Match type 3: change scheme name and version
            "scheme name" };

    // TODO: 2019-07-02 Low priority: Add patterns and case for commented lines, for setting darker color

    Pattern[] patternArray = new Pattern[regexString.length];

    // Fill the Pattern array with compiled patterns
    for (int i = 0; i < regexString.length; i++) {
      patternArray[i] = Pattern.compile(regexString[i]);
    }

    System.out.println("Assuming default charset: " + Charset.defaultCharset().name());

    try {
      parseFile(inputFile, newValue1, newValue2, newName, patternArray);
      // Print the resulting lines to console, then to output file
      for (String s : resultList) { System.out.println(s); }
      writeFile(outputFile);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  static void parseFile(String inputFile, String newValue1, String newValue2,
                        String newName, Pattern[] patternArray) throws IOException {
    String inputLine = "-1";
    String outputLine = "-1";
    BufferedReader reader;

    reader = new BufferedReader(new FileReader(inputFile));
    int inputFileLineCount = Math.toIntExact(reader.lines().count());

    reader = new BufferedReader(new FileReader(inputFile));
//            inputFile = new BufferedReader
//                    (new InputStreamReader
//                    (new FileInputStream(args[0]), StandardCharsets.UTF_8));
    do {
      inputLine = reader.readLine();
      System.out.println("Original: " + inputLine);
      outputLine = parseLine(inputLine, newValue1, newValue2, newName, patternArray);
      System.out.println("Result:   " + outputLine);
      resultList.add(outputLine);
    } while (inputFileLineCount > resultList.size());

    System.out.println("Parsing complete.");
    System.out.println("Number of lines in input file: " + inputFileLineCount);
    System.out.println("Number of lines parsed: " + resultList.size());
    reader.close();
  }

  static String parseLine(String inputLine, String newValue1, String newValue2,
                          String newName, Pattern[] patternArray) {
    Matcher matcher;
    String outputLine = inputLine; // Default, in case there's no change

    for (int i = 0; i < patternArray.length; i++) {
      matcher = patternArray[i].matcher(inputLine);
      if (matcher.find()) {
        if (i <= 1) { // Match type 1
          outputLine = inputLine.replaceFirst(regexForValue, newValue1);
          break;
        } else if (i == 2) { // Match type 2
          outputLine = inputLine.replaceFirst(regexForValue, newValue2);
          break;
        } else { // Match type 3
          outputLine = inputLine.replaceFirst(regexForName, newName);
          outputLine = outputLine.replaceFirst(regexForVersion, "version=\"1\"");
          break;
        }
      }
    }
    return outputLine;
  }

  static void writeFile(String fileName) throws IOException {
    PrintWriter outStream = new PrintWriter(
                            new BufferedWriter(
                            new FileWriter(fileName)));
    for (String s : resultList) {
      outStream.println(s);
    }
    outStream.close();
  }

}
