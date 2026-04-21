package course.logic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author jstar
 */
public class BasicGroup implements Group {
    private Student [] g;
    private int n;
    
    public BasicGroup() {
        g = new Student[16];
    }
    
    @Override
    public void add( Student s ) {
        if( g.length == n )
            doubleSize();
        g[n++] = s;
    }
          
    @Override
    public int size() {
        return n;
    }
    
        @Override
    public void sort( Comparator<Student> c) {
        Arrays.sort(g,0,n,c);
    }
    
    private void doubleSize() {
        Student [] ng = new Student[2*n];
        System.arraycopy(g, 0, ng, 0, n);      
        g = ng;
    }
    
    @Override
    public Iterator<Student> iterator() {
        return new Iterator<Student> () {
            private Student [] copy = new Student[n];
            private int current = 0;
            
            { 
                System.arraycopy(g, 0, copy, 0, n );
            }
            
            @Override
            public boolean hasNext() {
               return current < n;
            }

            @Override
            public Student next() {
                return copy[current++];
            }   
        };
    }
}
