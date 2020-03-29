package com.hackanet.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/14/19
 */
@Component
@Slf4j
public class ProfilingHandlerBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class> map = new HashMap<>();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Profiling profiling = beanClass.getAnnotation(Profiling.class);
        if (profiling != null && profiling.enabled()) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            // creates an object of the class it generated
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                long before = System.nanoTime();
                Object retVal = method.invoke(bean, args);
                long after = System.nanoTime();
                log.info(beanClass.getName() + "." + method.getName() + " method execution time is " + (after - before));
                return retVal;
            });
        }
        return bean;
    }
}
