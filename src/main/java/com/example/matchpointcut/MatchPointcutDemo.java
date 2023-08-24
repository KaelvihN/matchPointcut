package com.example.matchpointcut;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * @author : KaelvihN
 * @date : 2023/8/24 14:31
 */
public class MatchPointcutDemo {
    static class T1 {
        @Transactional
        public void foo() {
        }

        public void bar() {
        }
    }

    @Transactional
    static class T2 {
        public void foo() {

        }
    }

    @Transactional
    interface I3 {
        void foo();
    }

    static class T3 implements I3 {
        @Override
        public void foo() {

        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
//        test1();
//        test2();
        test3();
    }

    public static StaticMethodMatcherPointcut getMatcherPointcut() {
        StaticMethodMatcherPointcut matcherPointcut = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                //方法匹配
                MergedAnnotations methodAnnotation = MergedAnnotations.from(method);
                boolean methodPresent = methodAnnotation.isPresent(Transactional.class);
                //类匹配
                MergedAnnotations classAnnotation = MergedAnnotations.from(targetClass,
                        MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                boolean classPresent = classAnnotation.isPresent(Transactional.class);
                return false || methodPresent || classPresent;
            }
        };
        return matcherPointcut;
    }


    public static void test1() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //按照方法匹配
        pointcut.setExpression("execution(* bar())");
        System.out.println(pointcut.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pointcut.matches(T1.class.getMethod("bar"), T1.class));
    }

    public static void test2() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //按照注解匹配
        pointcut.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println(pointcut.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pointcut.matches(T1.class.getMethod("bar"), T1.class));
    }

    public static void test3() throws NoSuchMethodException {
        StaticMethodMatcherPointcut matcherPointcut = getMatcherPointcut();
        System.out.println(matcherPointcut.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(matcherPointcut.matches(T1.class.getMethod("bar"), T1.class));
        System.out.println(matcherPointcut.matches(T2.class.getMethod("foo"), T2.class));
        System.out.println(matcherPointcut.matches(T3.class.getMethod("foo"), T3.class));
    }


}
