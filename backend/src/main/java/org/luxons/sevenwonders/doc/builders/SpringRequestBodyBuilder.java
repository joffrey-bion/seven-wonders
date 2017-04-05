package org.luxons.sevenwonders.doc.builders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.jsondoc.core.pojo.ApiBodyObjectDoc;
import org.jsondoc.core.util.JSONDocType;
import org.jsondoc.core.util.JSONDocTypeBuilder;
import org.jsondoc.core.util.JSONDocUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public class SpringRequestBodyBuilder {

    private static final List<Class<?>> NON_BODY_PARAM_TYPES =
            Arrays.asList(MessageHeaders.class, MessageHeaderAccessor.class, Principal.class);

    private static final List<Class<? extends Annotation>> NON_BODY_PARAM_ANNOTATIONS =
            Arrays.asList(Header.class, Headers.class, DestinationVariable.class);

    public static ApiBodyObjectDoc buildRequestBody(Method method) {
        int index = getIndexOfBodyParam(method);
        if (index < 0) {
            return null;
        }
        final Class<?> bodyParamClass = method.getParameterTypes()[index];
        final Type bodyParamType = method.getGenericParameterTypes()[index];
        return new ApiBodyObjectDoc(JSONDocTypeBuilder.build(new JSONDocType(), bodyParamClass, bodyParamType));
    }

    public static int getIndexOfBodyParam(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return getIndexOfBodyParamForRequestMapping(method);
        }
        if (method.isAnnotationPresent(MessageMapping.class)) {
            return getIndexOfBodyParamForMessageMapping(method);
        }
        return -1;
    }

    private static int getIndexOfBodyParamForRequestMapping(Method method) {
        return JSONDocUtils.getIndexOfParameterWithAnnotation(method, RequestBody.class);
    }

    private static int getIndexOfBodyParamForMessageMapping(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            if (isBodyParam(paramTypes[i], paramAnnotations[i])) {
                return i;
            }
        }
        // no body param found
        return -1;
    }

    private static boolean isBodyParam(Class<?> paramType, Annotation[] paramAnnotations) {
        if (paramType.equals(Message.class)) {
            // it is too generic to be useful in the API doc even if it is indeed the body param
            return false;
        }
        if (NON_BODY_PARAM_TYPES.contains(paramType)) {
            return false;
        }
        for (Annotation paramAnnotation : paramAnnotations) {
            // guarantees this param is the payload
            if (paramAnnotation instanceof Payload) {
                return true;
            }
            // guarantees this param is NOT the payload by definition of the annotation
            if (isNonBodyParamAnnotation(paramAnnotation)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNonBodyParamAnnotation(Annotation paramAnnotation) {
        for (Class<? extends Annotation> annotation : NON_BODY_PARAM_ANNOTATIONS) {
            if (annotation.isInstance(paramAnnotation)) {
                return true;
            }
        }
        return false;
    }
}
