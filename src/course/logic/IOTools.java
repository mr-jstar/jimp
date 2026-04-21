package course.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

/**
 *
 * @author jstar
 */
public class IOTools {

    public static Group readGroup(String path) throws CourseException {
        Group g = new BasicGroup();
        try {
            LineNumberReader r = new LineNumberReader(new BufferedReader(new FileReader(path)));
            try {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] w = line.strip().split("\\s+");
                    g.add(new StudentImplementation(w[0], w[1], Double.parseDouble(w[2])));
                }
            } catch (IOException e) {
                throw new CourseException(e.getMessage());
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                throw new CourseException(path, r.getLineNumber(), "Bad data file");
            }
        } catch (FileNotFoundException e) {
            throw new CourseException(e.getMessage());
        }
        return g;
    }

    public static void saveGroup(Group g, String path)  throws CourseException {
        try (PrintWriter w = new PrintWriter(new FileWriter(path))) {
            for (Student s : g) {
                w.println(s.getFullName() + " " + s.getPoints());
            }
        } catch (IOException e) {
            throw new CourseException(e.getMessage());
        }
    }

    public static Group readGroupPatiently(String path) throws CourseException {
        ArrayListGroup g = new ArrayListGroup();
        try {
            LineNumberReader r = new LineNumberReader(new BufferedReader(new FileReader(path)));
            try {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] w = line.strip().split("\\s+");
                    try {
                        g.add(new StudentImplementation(w[0], w[1], Double.parseDouble(w[2])));
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        // skipping bad lines
                    }
                }
            } catch (IOException e) {
                throw new CourseException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            throw new CourseException(e.getMessage());
        }
        return g;
    }
}
