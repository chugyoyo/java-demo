package com.chugyoyo.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.chugyoyo.annotation.NonNullField") // 处理的注解
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NonNullFieldProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(NonNullField.class)) {
            // 检查类中所有字段
            for (Element enclosed : element.getEnclosedElements()) {
                if (enclosed.getKind().isField()) { // 只处理字段
                    VariableElement field = (VariableElement) enclosed;

                    // 检查是否有初始化值
                    if (field.getConstantValue() == null) {
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "字段 '" + field.getSimpleName() + "' 必须初始化！",
                                field
                        );
                    }
                }
            }
        }
        return true;
    }
}
