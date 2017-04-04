package org.luxons.sevenwonders.doc.builders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class SpringPathBuilder {

    public static Set<String> buildPath(Method method) {
        Set<String> paths = new HashSet<String>();

        if (method.isAnnotationPresent(MessageMapping.class)) {
            paths.addAll(getMappings(method, MessageMapping.class));
        }
        if (method.isAnnotationPresent(SubscribeMapping.class)) {
            paths.addAll(getMappings(method, SubscribeMapping.class));
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            paths.addAll(getMappings(method, RequestMapping.class));
        }

        return paths;
    }

    private static Set<String> getMappings(Method method, Class<? extends Annotation> annotationClass) {
        Set<String> controllerMappings = getControllerMappings(method, annotationClass);
        Set<String> methodMappings = getMappedPaths(method.getAnnotation(annotationClass));

        Set<String> mappings = new HashSet<>();
        for (String controllerPath : controllerMappings) {
            for (String methodPath : methodMappings) {
                mappings.add(join(controllerPath, methodPath));
            }
        }
        return mappings;
    }

    private static String join(String path1, String path2) {
        boolean path1HasSep = path1.endsWith("/");
        boolean path2HasSep = path2.startsWith("/");
        if (path1HasSep && path2HasSep) {
            return path1 + path2.substring(1);
        }
        if (!path1HasSep && !path2HasSep && (path1.isEmpty() || !path2.isEmpty())) {
            return path1 + '/' + path2;
        }
        return path1 + path2;
    }

    private static Set<String> getControllerMappings(Method method, Class<? extends Annotation> annotationClass) {
        Class<?> controller = method.getDeclaringClass();
        if (controller.isAnnotationPresent(annotationClass)) {
            return getMappedPaths(controller.getAnnotation(annotationClass));
        }
        return Collections.singleton("");
    }

    private static Set<String> getMappedPaths(Annotation mapping) {
        Set<String> paths = new HashSet<>();
        paths.addAll(Arrays.asList(valueMapping(mapping)));
        paths.addAll(Arrays.asList(pathMapping(mapping)));
        if (paths.isEmpty()) {
            paths.add("");
        }
        return paths;
    }

    private static String[] pathMapping(Annotation mapping) {
        try {
            if (mapping instanceof RequestMapping) {
                return ((RequestMapping) mapping).path();
            }
            return new String[0];
        } catch (NoSuchMethodError e) {
            //Handle the fact that this method is only in Spring 4, not available in Spring 3
            return new String[0];
        }
    }

    private static String[] valueMapping(Annotation mapping) {
        if (mapping instanceof RequestMapping) {
            return ((RequestMapping) mapping).value();
        }
        if (mapping instanceof MessageMapping) {
            return ((MessageMapping) mapping).value();
        }
        if (mapping instanceof SubscribeMapping) {
            return ((SubscribeMapping) mapping).value();
        }
        return new String[0];
    }
}
