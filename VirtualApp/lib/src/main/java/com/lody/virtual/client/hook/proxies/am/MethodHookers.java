package com.lody.virtual.client.hook.proxies.am;

import com.lody.virtual.client.hook.base.MethodProxy;

import java.lang.reflect.Method;

/**
 * MethodHooker
 *
 * @author Peng Wei <wpengapp@qq.com>
 * @date 2018/7/31
 */
public class MethodHookers {

    public static MethodHooker sFinishHooker;

    public interface MethodHooker {

        /**
         * 是否启用Hook
         */
        boolean isEnable(MethodProxy methodProxy, Object who, Method method, Object... args);

        /**
         * 如果isEnable返回true，则会调用call方法
         */
        Object call(MethodProxy methodProxy, Object who, Method method, Object... args);
    }
}
