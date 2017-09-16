/**
 * Copyright (C) 漏 2014 娣卞湷甯傛帉鐜╃綉缁滄妧鏈湁闄愬叕鍙�
 * KoTvGameBox
 * KoTvBaseActivity.java
 */
package top.huyuxin.handledemo;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ivo
 * @Create at 2015骞�鏈�9鏃�涓嬪崍7:51:00
 * @Version 1.0
 *          <p>
 *          <strong>Features draft description.涓昏鍔熻兘浠嬬粛</strong>
 *          </p>
 */
public class Reflect {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * 琚寘瑁呯殑瀵硅薄
     */
    private final Object  object;

    /**
     * 鍙嶅皠鐨勬槸涓�釜Class杩樻槸涓�釜Object瀹炰緥?
     */
    private final boolean isClass;

    // ===========================================================
    // Constructors
    // ===========================================================

    private Reflect(Class<?> type) {
        this.object = type;
        this.isClass = true;
    }

    private Reflect(Object object) {
        this.object = object;
        this.isClass = false;
    }
    
    /**
     * 璋冪敤涓�釜鏃犲弬鏋勯�鍣�
     * <p/>
     * 绛変环浜�<code>create(new Object[0])</code>
     * 
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     * @see #create(Object...)
     */
    public Reflect create() throws ReflectException {
        return create(new Object[0]);
    }

    /**
     * 璋冪敤涓�釜鏈夊弬鏋勯�鍣�
     * 
     * @param args
     *            鏋勯�鍣ㄥ弬鏁�
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     */
    public Reflect create(Object... args) throws ReflectException {
        Class<?>[] types = types(args);

        try {
            Constructor<?> constructor = type().getDeclaredConstructor(types);
            return on(constructor, args);
        }

        // 杩欑鎯呭喌涓嬶紝鏋勯�鍣ㄥ線寰�槸绉佹湁鐨勶紝澶氱敤浜庡伐鍘傛柟娉曪紝鍒绘剰鐨勯殣钘忎簡鏋勯�鍣ㄣ�
        catch (NoSuchMethodException e) {
            // private闃绘涓嶄簡鍙嶅皠鐨勮剼姝�)
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return on(constructor, args);
                }
            }

            throw new ReflectException(e);
        }
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * 寰楀埌褰撳墠鍖呰鐨勫璞�
     */
    public <T> T get() {
        // 娉涘瀷鐨勫ソ澶勭灛闂村氨浣撶幇鍑烘潵浜�
        return (T) object;
    }
    
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reflect) {
            return object.equals(((Reflect) obj).get());
        }
        return false;
    }

    @Override
    public String toString() {
        return object.toString();
    }
    
    @Override
    public int hashCode() {
        return object.hashCode();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 灏佽Class.forName(name)
     * <p/>
     * 鍙互杩欐牱璋冪敤: <code>on(Class.forName(name))</code>
     * 
     * @param name
     *            瀹屾暣绫诲悕
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     *             鍙嶅皠鏃跺彂鐢熺殑寮傚父
     * @see #on(Class)
     */
    public static Reflect on(String name) throws ReflectException {
        return on(forName(name));
    }

    /**
     * 灏佽Class.forName(name)
     * <p/>
     * 鍙互杩欐牱璋冪敤: <code>on(Xxx.class)</code>
     * 
     * @param clazz
     *            绫�
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     *             鍙嶅皠鏃跺彂鐢熺殑寮傚父
     * @see #on(Class)
     */
    public static Reflect on(Class<?> clazz) {
        return new Reflect(clazz);
    }

    /**
     * 鍖呰璧蜂竴涓璞�
     * <p/>
     * 褰撲綘闇�璁块棶瀹炰緥鐨勫瓧娈靛拰鏂规硶鏃跺彲浠ヤ娇鐢ㄦ鏂规硶 {@link Object}
     * 
     * @param object
     *            闇�琚寘瑁呯殑瀵硅薄
     * @return 宸ュ叿绫昏嚜韬�
     */
    public static Reflect on(Object object) {
        return new Reflect(object);
    }
    
    /**
     * 浣垮彈璁块棶鏉冮檺闄愬埗鐨勫璞¤浆涓轰笉鍙楅檺鍒躲� 涓�埇鎯呭喌涓嬶紝 涓�釜绫荤殑绉佹湁瀛楁鍜屾柟娉曟槸鏃犳硶鑾峰彇鍜岃皟鐢ㄧ殑锛�鍘熷洜鍦ㄤ簬璋冪敤鍓岼ava浼氭鏌ユ槸鍚﹀叿鏈夊彲璁块棶鏉冮檺锛�
     * 褰撹皟鐢ㄦ鏂规硶鍚庯紝 璁块棶鏉冮檺妫�煡鏈哄埗灏嗚鍏抽棴銆�
     * 
     * @param accessible
     *            鍙楄闂檺鍒剁殑瀵硅薄
     * @return 涓嶅彈璁块棶闄愬埗鐨勫璞�
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;

            if (Modifier.isPublic(member.getModifiers())
                && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {

                return accessible;
            }
        }

        // 榛樿涓篺alse,鍗冲弽灏勬椂妫�煡璁块棶鏉冮檺锛�
        // 璁句负true鏃朵笉妫�煡璁块棶鏉冮檺,鍙互璁块棶private瀛楁鍜屾柟娉�
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }

        return accessible;
    }

    /**
     * 寰楀埌瀛楁瀵瑰�
     * 
     * @param name
     *            瀛楁鍚�
     * @return The field value
     * @throws ReflectException
     * @see #field(String)
     */
    public <T> T get(String name) throws ReflectException {
        return field(name).<T> get();
    }
    
    /**
     * 淇敼涓�釜瀛楁鐨勫�
     * <p/>
     * 绛変环浜�{@link Field#set(Object, Object)}. 濡傛灉鍖呰鐨勫璞℃槸涓�釜
     * {@link Class}, 閭ｄ箞淇敼鐨勫皢鏄竴涓潤鎬佸瓧娈碉紝 濡傛灉鍖呰鐨勫璞℃槸涓�釜{@link Object}, 閭ｄ箞淇敼鐨勫氨鏄竴涓疄渚嬪瓧娈点�
     * 
     * @param name
     *            瀛楁鍚�
     * @param value
     *            瀛楁鐨勫�
     * @return 瀹屼簨鍚庣殑宸ュ叿绫�
     * @throws ReflectException
     */
    public Reflect set(String name, Object value) throws ReflectException {
        try {
            Field field = field0(name);
            field.set(object, unwrap(value));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }
    
    /**
     * 鍙栧緱瀛楁
     * 
     * @param name
     *            瀛楁鍚�
     * @return 瀛楁
     * @throws ReflectException
     */
    public Reflect field(String name) throws ReflectException {
        try {
            Field field = field0(name);
            return on(field.get(object));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private Field field0(String name) throws ReflectException {
        Class<?> type = type();

        // 灏濊瘯浣滀负鍏湁瀛楁澶勭悊
        try {
            return type.getField(name);
        }

        // 灏濊瘯浠ョ鏈夋柟寮忓鐞�
        catch (NoSuchFieldException e) {
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                } catch (NoSuchFieldException ignore) {
                }

                type = type.getSuperclass();
            } while (type != null);

            throw new ReflectException(e);
        }
    }

    /**
     * 灏嗕竴涓璞＄殑鎵�湁瀵硅薄鏄犲皠鍒颁竴涓狹ap涓�key涓哄瓧娈靛悕銆�
     * 
     * @return 鍖呭惈鎵�湁瀛楁鐨刴ap
     */
    public Map<String, Reflect> fields() {
        Map<String, Reflect> result = new LinkedHashMap<String, Reflect>();
        Class<?> type = type();

        do {
            for (Field field : type.getDeclaredFields()) {
                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();

                    if (!result.containsKey(name))
                        result.put(name, field(name));
                }
            }

            type = type.getSuperclass();
        } while (type != null);

        return result;
    }
    
    /**
     * 缁欏畾鏂规硶鍚嶇О锛岃皟鐢ㄦ棤鍙傛柟娉�
     * <p/>
     * 绛変环浜�<code>call(name, new Object[0])</code>
     * 
     * @param name
     *            鏂规硶鍚�
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     * @see #call(String, Object...)
     */
    public Reflect call(String name) throws ReflectException {
        return call(name, new Object[0]);
    }

    /**
     * 缁欏畾鏂规硶鍚嶅拰鍙傛暟锛岃皟鐢ㄤ竴涓柟娉曘�
     * <p/>
     * 灏佽鑷�{@link Method#invoke(Object, Object...)}, 鍙互鎺ュ彈鍩烘湰绫诲瀷
     * 
     * @param name
     *            鏂规硶鍚�
     * @param args
     *            鏂规硶鍙傛暟
     * @return 宸ュ叿绫昏嚜韬�
     * @throws ReflectException
     */
    public Reflect call(String name, Object... args) throws ReflectException {
        Class<?>[] types = types(args);

        // 灏濊瘯璋冪敤鏂规硶
        try {
            Method method = exactMethod(name, types);
            return on(method, object, args);
        }

        // 濡傛灉娌℃湁绗﹀悎鍙傛暟鐨勬柟娉曪紝
        // 鍒欏尮閰嶄竴涓笌鏂规硶鍚嶆渶鎺ヨ繎鐨勬柟娉曘�
        catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(name, types);
                return on(method, object, args);
            } catch (NoSuchMethodException e1) {

                throw new ReflectException(e1);
            }
        }
    }
    
    /**
     * 鏍规嵁鏂规硶鍚嶅拰鏂规硶鍙傛暟寰楀埌璇ユ柟娉曘�
     */
    private Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        // 鍏堝皾璇曠洿鎺ヨ皟鐢�
        try {
            return type.getMethod(name, types);
        }

        // 涔熻杩欐槸涓�釜绉佹湁鏂规硶
        catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ignore) {
                }

                type = type.getSuperclass();
            } while (type != null);

            throw new NoSuchMethodException();
        }
    }

    /**
     * 缁欏畾鏂规硶鍚嶅拰鍙傛暟锛屽尮閰嶄竴涓渶鎺ヨ繎鐨勬柟娉�
     */
    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        // 瀵逛簬鍏湁鏂规硶:
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }

        // 瀵逛簬绉佹湁鏂规硶锛�
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }

            type = type.getSuperclass();
        } while (type != null);

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types)
            + " could be found on type " + type() + ".");
    }

    /**
     * 鍐嶆纭鏂规硶绛惧悕涓庡疄闄呮槸鍚﹀尮閰嶏紝 灏嗗熀鏈被鍨嬭浆鎹㈡垚瀵瑰簲鐨勫璞＄被鍨嬶紝 濡俰nt杞崲鎴怚nt
     */
    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName,
                                       Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName)
            && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    
    /**
     * 涓哄寘瑁呯殑瀵硅薄鍒涘缓涓�釜浠ｇ悊銆�
     * 
     * @param proxyType
     *            浠ｇ悊绫诲瀷
     * @return 鍖呰瀵硅薄鐨勪唬鐞嗚�銆�
     */
    @SuppressWarnings("unchecked")
    public <P> P as(Class<P> proxyType) {
        final boolean isMap = (object instanceof Map);
        final InvocationHandler handler = new InvocationHandler() {
            @SuppressWarnings("null")
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();

                try {
                    return on(object).call(name, args).get();
                } catch (ReflectException e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) object;
                        int length = (args == null ? 0 : args.length);

                        if (length == 0 && name.startsWith("get")) {
                            return map.get(property(name.substring(3)));
                        } else if (length == 0 && name.startsWith("is")) {
                            return map.get(property(name.substring(2)));
                        } else if (length == 1 && name.startsWith("set")) {
                            map.put(property(name.substring(3)), args[0]);
                            return null;
                        }
                    }

                    throw e;
                }
            }
        };

        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
    }

    private static String property(String string) {
        int length = string.length();

        if (length == 0) {
            return "";
        } else if (length == 1) {
            return string.toLowerCase();
        } else {
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        }
    }
    
    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;

                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
                    continue;

                return false;
            }

            return true;
        } else {
            return false;
        }
    }
    
    private static Reflect on(Constructor<?> constructor, Object... args) throws ReflectException {
        try {
            return on(accessible(constructor).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Reflect on(Method method, Object object, Object... args) throws ReflectException {
        try {
            accessible(method);

            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return on(object);
            } else {
                return on(method.invoke(object, args));
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 鍐呴儴绫伙紝浣夸竴涓璞¤劚绂诲寘瑁�
     */
    private static Object unwrap(Object object) {
        if (object instanceof Reflect) {
            return ((Reflect) object).get();
        }

        return object;
    }

    /**
     * 鍐呴儴绫伙紝 缁欏畾涓�郴鍒楀弬鏁帮紝杩斿洖瀹冧滑鐨勭被鍨�
     * 
     * @see Object#getClass()
     */
    private static Class<?>[] types(Object... values) {
        if (values == null) {
            // 绌�
            return new Class[0];
        }

        Class<?>[] result = new Class[values.length];

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }

        return result;
    }

    /**
     * 鍔犺浇涓�釜绫�
     * 
     * @see Class#forName(String)
     */
    private static Class<?> forName(String name) throws ReflectException {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 鑾峰彇鍖呰鐨勫璞＄殑绫诲瀷
     * 
     * @see Object#getClass()
     */
    public Class<?> type() {
        if (isClass) {
            return (Class<?>) object;
        } else {
            return object.getClass();
        }
    }

    /**
     * 寰楀埌鍖呰鐨勫璞＄殑绫诲瀷锛�濡傛灉鏄熀鏈被鍨�鍍廼nt,float,boolean杩欑, 閭ｄ箞灏嗚杞崲鎴愮浉搴旂殑瀵硅薄绫诲瀷銆�
     */
    public static Class<?> wrapper(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }

        return type;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    /**
     * 瀹氫箟浜嗕竴涓猲ull绫诲瀷
     */
    private static class NULL {
    }
    
}
