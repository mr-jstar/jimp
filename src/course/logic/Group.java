
package course.logic;

import java.util.Comparator;

/**
 *
 * @author jstar
 */
public interface Group extends Iterable<Student> {
    public void add(Student s);
    public int size();
    public void sort( Comparator<Student> c );
}
