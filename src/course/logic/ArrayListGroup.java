package course.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author jstar
 */
public class ArrayListGroup implements Group {
    private ArrayList<Student> g = new ArrayList<>();
    
    @Override
    public void add( Student s ) {
        g.add(s);
    }
    
    @Override
    public int size() {
        return g.size();
    }
        
    @Override
    public void sort( Comparator<Student> c) {
        Collections.sort(g,c);
    }
    
    @Override
    public Iterator<Student> iterator() {
        return g.iterator();
    }
}
