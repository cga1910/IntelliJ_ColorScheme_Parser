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

package app;

import java.io.*;
import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseScheme {

  static final ArrayList<String> resultList = new ArrayList<String>();
  static final String userDir = System.getProperty("user.dir");

  public static void main(String[] args00) {

    System.out.println("Assuming default charset: " + Charset.defaultCharset().name());

    // TODO: 2019-07-02 Get output color-scheme name from parameter, or set predefined
    // TODO: 2019-07-01 Get filenames from parameters instead
    String[] args = new String[1];
    args[0] = userDir + "\\src\\app\\test_theme.icls";
    String outputFile = userDir + "\\src\\app\\output.icls";

    String regexForColor = "value=\".*\"";
    String replacementStr1 = "value=\"e0c0e\"";
    String replacementStr2 = "value=\"c1c1c1\"";

    // There's no need to use regex for this, it's only for practise
    String[] regexString = {
            // Match type 1: change color value to "e0c0e"
            "\"CARET_ROW_COLOR\" value",
            "\"BACKGROUND\" value",
            // Match type 2: change color value to "c1c1c1"
            // "\"ERROR_STRIPE_COLOR\" value",
            "\"FOREGROUND\" value" };
    // TODO: 2019-07-02 Add patterns for "scheme name=" and "version="
    // TODO: 2019-07-02 Low priority: Add patterns for commented lines, for setting darker color

    Pattern[] patternArray = new Pattern[regexString.length];

    // Fill the Pattern array with compiled patterns
    for (int i = 0; i < regexString.length; i++) {
      patternArray[i] = Pattern.compile(regexString[i]);
    }

    try {
      parseFile(regexForColor, replacementStr1, replacementStr2, patternArray, args);
      // Print the resulting lines to console, then to output file
      for (String s : resultList) { System.out.println(s); }
      writeFile(outputFile);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  static void parseFile(String regexForColor, String replacementStr1,
                        String replacementStr2, Pattern[] patternArray,
                        String[] args) throws IOException {
    String inputLine = "-1";
    String outputLine = "-1";
    BufferedReader inputFile;

    inputFile = new BufferedReader(new FileReader(args[0]));
    int inputFileLineCount = Math.toIntExact(inputFile.lines().count());

    inputFile = new BufferedReader(new FileReader(args[0]));
//            inputFile = new BufferedReader
//                    (new InputStreamReader
//                    (new FileInputStream(args[0]), StandardCharsets.UTF_8));
    do {
      inputLine = inputFile.readLine();
      System.out.println("Original: " + inputLine);
      outputLine = parseLine(inputLine, regexForColor, replacementStr1,
                             replacementStr2, patternArray);
      System.out.println("Result:   " + outputLine);
      resultList.add(outputLine);
    } while (inputFileLineCount > resultList.size());

    System.out.println("Parsing complete.");
    System.out.println("Number of lines in input file: " + inputFileLineCount);
    System.out.println("Number of lines parsed: " + resultList.size());
    inputFile.close();
  }

  static String parseLine(String inputLine, String regexForColor,
                          String replacementStr1, String replacementStr2,
                          Pattern[] patternArray) {
    Matcher matcher;
    String outputLine = inputLine; // Default, in case there's no change

    for (int i = 0; i < patternArray.length; i++) {
      matcher = patternArray[i].matcher(inputLine);
      if (matcher.find()) {
        if (i <= 1) {
          outputLine = inputLine.replaceFirst(regexForColor, replacementStr1);
          break;
        } else {
          outputLine = inputLine.replaceFirst(regexForColor, replacementStr2);
          break;
        }
        // TODO: 2019-07-02 Add cases for "scheme name=" and "version="
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
