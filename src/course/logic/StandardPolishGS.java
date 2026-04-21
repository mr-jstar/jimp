package course.logic;

/**
 *
 * @author jstar
 */
@ScaleClassAnnotation("Standardowa polska skala ocen")
public class StandardPolishGS implements GradingScale {
    private static double [] thresh = { 50, 60, 70, 80, 90 };
    private static final String [] grades = { "3.0", "3.5", "4.0", "4.5", "5.0" };

    @Override
    public String grade(double points) {
        String ret = "2.0";
        for( int i= 0; i < thresh.length; i++)
            if( points > thresh[i])
                ret = grades[i];
        return ret;
    }

    public static void setThresh( double [] thresh ) {
        if( thresh.length == StandardPolishGS.thresh.length )
            StandardPolishGS.thresh = thresh;
        else 
            throw new IllegalArgumentException( "StandardPolishGS.setThresh: lenght of the argument should be = " + StandardPolishGS.thresh.length );
    }
}
