package de.robingrether.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Cloner<T> {
	
	public abstract T clone(T original) throws CloneNotSupportedException;
	
	public static <T> Cloner<T> createBasicReflectionCloner(Class<T> clazz) throws CloneNotSupportedException {
		return new BasicReflectionCloner<T>(clazz);
	}
	
	private static class BasicReflectionCloner<T> extends Cloner<T> {
	
		private final Constructor<T> constructor;
		private final Set<Field> fields;
		
		private BasicReflectionCloner(Class<T> clazz) throws CloneNotSupportedException {
			try {
				constructor = clazz.getDeclaredConstructor();
			} catch(Exception e) {
				throw new CloneNotSupportedException("Instances of the given class cannot be cloned by this cloner.");
			}
			constructor.setAccessible(true);
			Set<Field> fieldsLocal = new HashSet<Field>();
			for(Field field : getFields(clazz)) {
				if(!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					fieldsLocal.add(field);
				}
			}
			fields = Collections.unmodifiableSet(fieldsLocal);
		}
		
		private static Field[] getFields(Class<?> clazz) {
			Set<Field> fields = new HashSet<Field>();
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			if(!clazz.getSuperclass().getName().equals("java.lang.Object")) {
				fields.addAll(Arrays.asList(getFields(clazz.getSuperclass())));
			}
			return fields.toArray(new Field[fields.size()]);
		}
		
		public T clone(T original) throws CloneNotSupportedException {
			try {
				T clone = constructor.newInstance();
				for(Field field : fields) {
					Object object = field.get(original);
					try {
						object = object.getClass().getDeclaredMethod("clone").invoke(object);
					} catch(Exception e) {
					}
					field.set(clone, object);
				}
				return clone;
			} catch(Exception e) {
				throw new CloneNotSupportedException("The given object cannot be cloned.");
			}
		}
		
	}
	
}