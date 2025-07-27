package com.chugyoyo.checker;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.chugyoyo.checker.SpringCodeCheck")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SpringCodeCheckProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(SpringCodeCheck.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                continue; // 确保是类元素
            }

            TypeElement classElement = (TypeElement) element;

            // 1. 检查类命名规范
            checkClassNameConvention(classElement);

            // 2. 检查构造方法规范
            checkConstructorRules(classElement);

            // 3. 检查字段注入规范
            checkFieldInjection(classElement);

            // 4. 检查事务方法规范
            checkTransactionalMethods(classElement);

            // 5. 检查Spring Bean作用域
            checkBeanScope(classElement);
        }
        return true;
    }

    // 1. 检查类名是否符合Spring惯例（可选后缀）
    private void checkClassNameConvention(TypeElement classElement) {
        String className = classElement.getSimpleName().toString();

        // 常见的Spring组件后缀
        String[] validSuffixes = {"Service", "ServiceImpl", "Controller",
                "Repository", "Component", "Config", "Aspect"};

        boolean valid = false;
        for (String suffix : validSuffixes) {
            if (className.endsWith(suffix)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "Spring组件类名建议使用标准后缀: " + String.join(", ", validSuffixes),
                    classElement);
        }
    }

    // 2. 检查构造方法：必须存在无参构造或单一构造
    private void checkConstructorRules(TypeElement classElement) {
        boolean hasNoArgConstructor = false;
        boolean hasAutowiredConstructor = false;
        int constructorCount = 0;

        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                constructorCount++;

                // 检查无参构造
                if (enclosed.getModifiers().contains(Modifier.PUBLIC)) {
                    if (enclosed.getEnclosedElements().isEmpty()) {
                        hasNoArgConstructor = true;
                    }
                }

                // 检查是否标注@Autowired
                if (enclosed.getAnnotationMirrors().stream()
                        .anyMatch(am -> am.getAnnotationType().asElement()
                                .getSimpleName().contentEquals("Autowired"))) {
                    hasAutowiredConstructor = true;
                }
            }
        }

        // Spring规范：要么有无参构造，要么有单一构造
        if (constructorCount > 1 && !hasNoArgConstructor) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Spring Bean应有无参构造或单一构造方法",
                    classElement);
        }

        // 构造方法上的@Autowired在Spring 4.3+后可不标注
        if (hasAutowiredConstructor) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "@Autowired在构造方法上可省略（Spring 4.3+）",
                    classElement);
        }
    }

    // 3. 检查字段注入：避免字段注入，推荐构造方法注入
    private void checkFieldInjection(TypeElement classElement) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                // 检查是否使用@Autowired字段注入
                boolean isAutowired = enclosed.getAnnotationMirrors().stream()
                        .anyMatch(am -> am.getAnnotationType().asElement()
                                .getSimpleName().contentEquals("Autowired"));

                if (isAutowired) {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            "避免字段注入，推荐使用构造方法注入: " + enclosed.getSimpleName(),
                            enclosed);
                }
            }
        }
    }

    // 4. 检查@Transactional方法：不能是private
    private void checkTransactionalMethods(TypeElement classElement) {
        TypeMirror transactionalType = elementUtils
                .getTypeElement("org.springframework.transaction.annotation.Transactional")
                .asType();

        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                boolean hasTransactional = enclosed.getAnnotationMirrors().stream()
                        .anyMatch(am -> typeUtils.isSameType(
                                am.getAnnotationType(), transactionalType));

                if (hasTransactional && enclosed.getModifiers().contains(Modifier.PRIVATE)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "@Transactional方法不能是private: " + enclosed.getSimpleName(),
                            enclosed);
                }
            }
        }
    }

    // 5. 检查Bean作用域：Singleton Bean不能有可变状态
    private void checkBeanScope(TypeElement classElement) {
        TypeMirror scopeType = elementUtils
                .getTypeElement("org.springframework.context.annotation.Scope")
                .asType();

        boolean isSingleton = true;
        for (AnnotationMirror am : classElement.getAnnotationMirrors()) {
            if (typeUtils.isSameType(am.getAnnotationType(), scopeType)) {
                // 解析@Scope("prototype")等值
                String scopeValue = am.getElementValues().entrySet().stream()
                        .filter(e -> e.getKey().getSimpleName().contentEquals("value"))
                        .map(e -> e.getValue().getValue().toString())
                        .findFirst()
                        .orElse("");

                if ("prototype".equals(scopeValue) ||
                        "request".equals(scopeValue) ||
                        "session".equals(scopeValue)) {
                    isSingleton = false;
                }
            }
        }

        if (isSingleton) {
            // 检查是否有非final的可变状态
            for (Element enclosed : classElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.FIELD) {
                    Set<Modifier> modifiers = enclosed.getModifiers();
                    if (!modifiers.contains(Modifier.FINAL) &&
                            !modifiers.contains(Modifier.STATIC)) {

                        messager.printMessage(Diagnostic.Kind.WARNING,
                                "Singleton Bean中的字段应避免可变状态: " + enclosed.getSimpleName() +
                                        "。考虑使用ThreadLocal或改为prototype作用域",
                                enclosed);
                    }
                }
            }
        }
    }
}
