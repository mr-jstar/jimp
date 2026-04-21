package course.logic;

/**
 *
 * @author jstar
 */
import java.lang.annotation.*;

@Target(ElementType.TYPE)   // TYPE = klasa, interfejs, enum
@Retention(RetentionPolicy.RUNTIME)
public @interface ScaleClassAnnotation {
    String value();
}
