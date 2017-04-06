package org.luxons.sevenwonders.doc.scanner;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.jsondoc.core.pojo.ApiObjectDoc;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.pojo.JSONDoc.MethodDisplay;
import org.jsondoc.core.pojo.JSONDocTemplate;
import org.jsondoc.core.scanner.builder.JSONDocApiMethodDocBuilder;
import org.jsondoc.core.util.JSONDocUtils;
import org.jsondoc.springmvc.scanner.Spring4JSONDocScanner;
import org.jsondoc.springmvc.scanner.builder.SpringConsumesBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringHeaderBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringPathVariableBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringProducesBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringQueryParamBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringResponseStatusBuilder;
import org.jsondoc.springmvc.scanner.builder.SpringVerbBuilder;
import org.luxons.sevenwonders.doc.builders.JSONDocApiObjectDocBuilder;
import org.luxons.sevenwonders.doc.builders.JSONDocTemplateBuilder;
import org.luxons.sevenwonders.doc.builders.SpringObjectBuilder;
import org.luxons.sevenwonders.doc.builders.SpringPathBuilder;
import org.luxons.sevenwonders.doc.builders.SpringRequestBodyBuilder;
import org.luxons.sevenwonders.doc.builders.SpringResponseBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public class JsonDocWebSocketScanner extends Spring4JSONDocScanner {

    @Override
    public JSONDoc getJSONDoc(String version, String basePath, List<String> packages, boolean playgroundEnabled,
                              MethodDisplay displayMethodAs) {
        Set<URL> urls = new HashSet<URL>();
        FilterBuilder filter = new FilterBuilder();

        log.debug("Found " + packages.size() + " package(s) to scan...");
        for (String pkg : packages) {
            log.debug("Adding package to JSONDoc recursive scan: " + pkg);
            urls.addAll(ClasspathHelper.forPackage(pkg));
            filter.includePackage(pkg);
        }

        reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
                                                                .setUrls(urls)
                                                                .addScanners(new MethodAnnotationsScanner()));

        JSONDoc jsondocDoc = new JSONDoc(version, basePath);
        jsondocDoc.setPlaygroundEnabled(playgroundEnabled);
        jsondocDoc.setDisplayMethodAs(displayMethodAs);

        jsondocControllers = jsondocControllers();
        jsondocObjects = jsondocObjects(packages);
        jsondocFlows = jsondocFlows();
        jsondocGlobal = jsondocGlobal();
        jsondocChangelogs = jsondocChangelogs();
        jsondocMigrations = jsondocMigrations();

        for (Class<?> clazz : jsondocObjects) {
            jsondocTemplates.put(clazz, JSONDocTemplateBuilder.build(clazz, jsondocObjects));
        }

        jsondocDoc.setApis(getApiDocsMap(jsondocControllers, displayMethodAs));
        jsondocDoc.setObjects(getApiObjectsMap(jsondocObjects));
        jsondocDoc.setFlows(getApiFlowDocsMap(jsondocFlows, allApiMethodDocs));
        jsondocDoc.setGlobal(getApiGlobalDoc(jsondocGlobal, jsondocChangelogs, jsondocMigrations));

        return jsondocDoc;
    }

    @Override
    public Set<Method> jsondocMethods(Class<?> controller) {
        Set<Method> annotatedMethods = new LinkedHashSet<Method>();
        for (Method method : controller.getDeclaredMethods()) {
            if (shouldDocument(method)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    private boolean shouldDocument(Method method) {
        return method.isAnnotationPresent(RequestMapping.class) || method.isAnnotationPresent(MessageMapping.class)
                || method.isAnnotationPresent(SubscribeMapping.class);
    }

    @Override
    public ApiMethodDoc initApiMethodDoc(Method method, Map<Class<?>, JSONDocTemplate> jsondocTemplates) {
        ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
        apiMethodDoc.setPath(SpringPathBuilder.buildPath(method));
        apiMethodDoc.setMethod(method.getName());
        apiMethodDoc.setVerb(SpringVerbBuilder.buildVerb(method));
        apiMethodDoc.setProduces(SpringProducesBuilder.buildProduces(method));
        apiMethodDoc.setConsumes(SpringConsumesBuilder.buildConsumes(method));
        apiMethodDoc.setHeaders(SpringHeaderBuilder.buildHeaders(method));
        apiMethodDoc.setPathparameters(SpringPathVariableBuilder.buildPathVariable(method));
        apiMethodDoc.setQueryparameters(SpringQueryParamBuilder.buildQueryParams(method));
        apiMethodDoc.setBodyobject(SpringRequestBodyBuilder.buildRequestBody(method));
        apiMethodDoc.setResponse(SpringResponseBuilder.buildResponse(method));
        apiMethodDoc.setResponsestatuscode(SpringResponseStatusBuilder.buildResponseStatusCode(method));

        Integer index = JSONDocUtils.getIndexOfParameterWithAnnotation(method, RequestBody.class);
        if (index != -1) {
            apiMethodDoc.getBodyobject().setJsondocTemplate(jsondocTemplates.get(method.getParameterTypes()[index]));
        }

        return apiMethodDoc;
    }

    @Override
    public ApiMethodDoc mergeApiMethodDoc(Method method, ApiMethodDoc apiMethodDoc) {
        if (method.isAnnotationPresent(ApiMethod.class) && method.getDeclaringClass().isAnnotationPresent(Api.class)) {
            ApiMethodDoc jsondocApiMethodDoc = JSONDocApiMethodDocBuilder.build(method);
            BeanUtils.copyProperties(jsondocApiMethodDoc, apiMethodDoc, "path", "verb", "produces", "consumes",
                    "headers", "pathparameters", "queryparameters", "bodyobject", "response", "responsestatuscode",
                    "apierrors", "supportedversions", "auth", "displayMethodAs");
        }
        return apiMethodDoc;
    }

    @Override
    public Set<Class<?>> jsondocObjects(List<String> packages) {
        return new ObjectsScanner(reflections).findJsondocObjects(packages);
    }

    @Override
    public ApiObjectDoc initApiObjectDoc(Class<?> clazz) {
        return SpringObjectBuilder.buildObject(clazz);
    }

    @Override
    public ApiObjectDoc mergeApiObjectDoc(Class<?> clazz, ApiObjectDoc apiObjectDoc) {
        if (clazz.isAnnotationPresent(ApiObject.class)) {
            ApiObjectDoc jsondocApiObjectDoc = JSONDocApiObjectDocBuilder.build(clazz);
            BeanUtils.copyProperties(jsondocApiObjectDoc, apiObjectDoc);
        }
        return apiObjectDoc;
    }
}
