package io.github.splotycode.mosaik.annotationbase.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface PropertyWriter {

    static PropertyWriter fromField(Field field, Object object) {
        field.setAccessible(true);
        return value -> {
            try {
                field.set(object, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new PropertyWriteException("Failed to write data from field", e);
            } catch (ExceptionInInitializerError e) {
                throw new PropertyWriteException("Provoked initialization failed", e);
            }
        };
    }

    static PropertyWriter writeToMethod(Method method, Object object) {
        method.setAccessible(true);
        return value -> {
            try {
                method.invoke(object, value);
            } catch (InvocationTargetException | ExceptionInInitializerError e) {
                throw new PropertyReader.PropertyReadException("Error while calling the underlying method", e);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new PropertyReader.PropertyReadException("Failed to call underlying method", e);
            }
        };
    }

    class PropertyWriteException extends RuntimeException {

        public PropertyWriteException(String message) {
            super(message);
        }

        public PropertyWriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    void writeValue(Object value);

}
