package at.cpickl.grotlin.multi.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import at.cpickl.grotlin.multi.service.Role;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@NameBinding
public @interface Secured {

    Role role() default Role.USER;

}
