package org.luxons.sevenwonders.doc.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.jsondoc.core.pojo.JSONDoc;
import org.luxons.sevenwonders.doc.builders.SpringRequestBodyBuilder;
import org.reflections.Reflections;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class ObjectsScanner {

    private final Reflections reflections;

    public ObjectsScanner(Reflections reflections) {
        this.reflections = reflections;
    }

    public Set<Class<?>> findJsondocObjects(List<String> packages) {
        Set<Class<?>> candidates = getRootApiObjects();
        Set<Class<?>> subCandidates = Sets.newHashSet();

        // This is to get objects' fields that are not returned nor part of the body request of a method, but that
        // are a field of an object returned or a body  of a request of a method
        for (Class<?> clazz : candidates) {
            appendSubCandidates(clazz, subCandidates);
        }
        candidates.addAll(subCandidates);

        return candidates.stream().filter(clazz -> inWhiteListedPackages(packages, clazz)).collect(Collectors.toSet());
    }

    private Set<Class<?>> getRootApiObjects() {
        Set<Class<?>> candidates = Sets.newHashSet();
        Set<Method> methodsToDocument = getMethodsToDocument();
        for (Method method : methodsToDocument) {
            addReturnType(candidates, method);
            addBodyParam(candidates, method);
        }
        return candidates;
    }

    private void addReturnType(Set<Class<?>> candidates, Method method) {
        Class<?> returnValueClass = method.getReturnType();
        if (returnValueClass.isPrimitive() || returnValueClass.equals(JSONDoc.class)) {
            return;
        }
        buildJSONDocObjectsCandidates(candidates, returnValueClass, method.getGenericReturnType(), reflections);
    }

    private void addBodyParam(Set<Class<?>> candidates, Method method) {
        int bodyParamIndex = SpringRequestBodyBuilder.getIndexOfBodyParam(method);
        if (bodyParamIndex >= 0) {
            Class<?> bodyParamClass = method.getParameterTypes()[bodyParamIndex];
            Type bodyParamType = method.getGenericParameterTypes()[bodyParamIndex];
            buildJSONDocObjectsCandidates(candidates, bodyParamClass, bodyParamType, reflections);
        }
    }

    private Set<Method> getMethodsToDocument() {
        Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(RequestMapping.class);
        methodsAnnotatedWith.addAll(reflections.getMethodsAnnotatedWith(SubscribeMapping.class));
        methodsAnnotatedWith.addAll(reflections.getMethodsAnnotatedWith(MessageMapping.class));
        return methodsAnnotatedWith;
    }

    private boolean inWhiteListedPackages(List<String> packages, Class<?> clazz) {
        Package p = clazz.getPackage();
        return p != null && packages.stream().anyMatch(whiteListedPkg -> p.getName().startsWith(whiteListedPkg));
    }

    private void appendSubCandidates(Class<?> clazz, Set<Class<?>> subCandidates) {
        if (clazz.isPrimitive() || clazz.equals(Class.class)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!isValidForRecursion(field)) {
                continue;
            }

            Class<?> fieldClass = field.getType();
            Set<Class<?>> fieldCandidates = new HashSet<>();
            buildJSONDocObjectsCandidates(fieldCandidates, fieldClass, field.getGenericType(), reflections);

            for (Class<?> candidate : fieldCandidates) {
                if (!subCandidates.contains(candidate)) {
                    subCandidates.add(candidate);

                    appendSubCandidates(candidate, subCandidates);
                }
            }
        }
    }

    private static boolean isValidForRecursion(Field field) {
        //        return !field.isSynthetic() && !field.getType().isPrimitive() && !Modifier.isTransient(field
        // .getModifiers());
        return true;
    }

    public static Set<Class<?>> buildJSONDocObjectsCandidates(Set<Class<?>> candidates, Class<?> clazz, Type type,
                                                              Reflections reflections) {

        if (Map.class.isAssignableFrom(clazz)) {

            if (type instanceof ParameterizedType) {
                Type mapKeyType = ((ParameterizedType) type).getActualTypeArguments()[0];
                Type mapValueType = ((ParameterizedType) type).getActualTypeArguments()[1];

                if (mapKeyType instanceof Class) {
                    candidates.add((Class<?>) mapKeyType);
                } else if (mapKeyType instanceof WildcardType) {
                    candidates.add(Void.class);
                } else {
                    if (mapKeyType instanceof ParameterizedType) {
                        candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                                (Class<?>) ((ParameterizedType) mapKeyType).getRawType(), mapKeyType, reflections));
                    }
                }

                if (mapValueType instanceof Class) {
                    candidates.add((Class<?>) mapValueType);
                } else if (mapValueType instanceof WildcardType) {
                    candidates.add(Void.class);
                } else {
                    if (mapValueType instanceof ParameterizedType) {
                        candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                                (Class<?>) ((ParameterizedType) mapValueType).getRawType(), mapValueType, reflections));
                    }
                }
            }
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                Type parametrizedType = ((ParameterizedType) type).getActualTypeArguments()[0];
                candidates.add(clazz);

                if (parametrizedType instanceof Class) {
                    candidates.add((Class<?>) parametrizedType);
                } else if (parametrizedType instanceof WildcardType) {
                    candidates.add(Void.class);
                } else {
                    candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                            (Class<?>) ((ParameterizedType) parametrizedType).getRawType(), parametrizedType,
                            reflections));
                }
            } else if (type instanceof GenericArrayType) {
                candidates.addAll(buildJSONDocObjectsCandidates(candidates, clazz,
                        ((GenericArrayType) type).getGenericComponentType(), reflections));
            } else {
                candidates.add(clazz);
            }
        } else if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            candidates.addAll(buildJSONDocObjectsCandidates(candidates, componentType, type, reflections));
        } else {
            if (type instanceof ParameterizedType) {
                Type parametrizedType = ((ParameterizedType) type).getActualTypeArguments()[0];

                if (parametrizedType instanceof Class) {
                    Class<?> candidate = (Class<?>) parametrizedType;
                    if (candidate.isInterface()) {
                        for (Class<?> implementation : reflections.getSubTypesOf(candidate)) {
                            buildJSONDocObjectsCandidates(candidates, implementation, parametrizedType, reflections);
                        }
                    } else {
                        candidates.add(candidate);
                        candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                                (Class<?>) ((ParameterizedType) type).getRawType(), parametrizedType, reflections));
                    }
                } else if (parametrizedType instanceof WildcardType) {
                    candidates.add(Void.class);
                    candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                            (Class<?>) ((ParameterizedType) type).getRawType(), parametrizedType, reflections));
                } else if (parametrizedType instanceof TypeVariable<?>) {
                    candidates.add(Void.class);
                    candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                            (Class<?>) ((ParameterizedType) type).getRawType(), parametrizedType, reflections));
                } else {
                    candidates.addAll(buildJSONDocObjectsCandidates(candidates,
                            (Class<?>) ((ParameterizedType) parametrizedType).getRawType(), parametrizedType,
                            reflections));
                }
            } else if (clazz.isInterface()) {
                for (Class<?> implementation : reflections.getSubTypesOf(clazz)) {
                    candidates.addAll(buildJSONDocObjectsCandidates(candidates, implementation, type, reflections));
                }
            } else {
                candidates.add(clazz);
            }
        }

        return candidates;
    }
}
