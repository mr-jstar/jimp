package course.logic;

/**
 *
 * @author jstar
 */
public class CourseException extends Exception {
    private String path;
    private int linNo;

    public CourseException( String msg ) {
        super(msg);
    }
    
    public CourseException( String p, int ln, String msg ) {
        super(msg);
        path = p;
        linNo = ln;
    }
    
    public String getPath() {
        return path;
    }
    
    public int getLineNumber() {
        return linNo;
    }
}
