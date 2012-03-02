package jp.cafebabe.commons.bcul.updater;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Current version of BCUL is not use this annotation.
 *
 * @author Haruaki TAMADA
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@interface NoUpdate{
}
