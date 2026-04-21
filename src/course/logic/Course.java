package course.logic;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author jstar
 */
public class Course {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Group g = IOTools.readGroupPatiently(args[0]);
            GradingScale scale
                    = new GradingScale() {
                @Override
                public String grade(double points) {
                    return "Wspaniale!";
                }
            };
            if (args.length > 1) {
                try {
                    Class c = Class.forName(args[1]);
                    Object o = c.getConstructor().newInstance();
                    scale = (GradingScale)o;
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't find " + args[1] + ".class");
                } catch( IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                    System.err.println(e.getMessage() );
                } catch( ClassCastException e ) {
                    System.err.println(args[1] + " does not implement GradingScale");
                }
            }

            for (Student s : g) {
                System.out.println(s.getFullName() + " " + scale.grade(s.getPoints()));
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                e.printStackTrace();
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

}
