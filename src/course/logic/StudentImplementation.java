package course.logic;

/**
 *
 * @author jstar
 */
public class StudentImplementation implements Student {
    private final String first;
    private final String sure;
    private final double points;
    
    public StudentImplementation( String f, String s, double p ) {
        first = f;
        sure = s;
        points = p;
    }
    
    @Override
    public String getFullName() {
        return first + " " + sure;
    }
    
    @Override
    public double getPoints() {
        return points;
    }

}
