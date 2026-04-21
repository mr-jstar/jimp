package course.logic;

/**
 *
 * @author jstar
 */
@ScaleClassAnnotation("Standard US Grading Scale")
public class StandardUSScale implements GradingScale {
    private static double [] thresh =       { 60,  69,  79,  89  };
    private static final String [] grades = { "D", "C", "B", "A" };

    @Override
    public String grade(double points) {
        String ret = "F";
        for( int i= 0; i < thresh.length; i++)
            if( points > thresh[i])
                ret = grades[i];
        return ret;
    }

    public static void setThresh( double [] thresh ) {
        if( thresh.length == StandardUSScale.thresh.length )
            StandardUSScale.thresh = thresh;
        else 
            throw new IllegalArgumentException( "StandardUSScale.setThresh: lenght of the argument should be = " + StandardUSScale.thresh.length );
    }
}
