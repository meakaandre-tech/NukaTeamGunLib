package com.nukateam.ntgl.platform;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Minimal event bus standing in for Ntgl.EVENT_BUS. It carries NTGL's own events (gun fire,
 * reload, projectile hit, melee, ...) and the small set of game events of
 * {@code com.nukateam.ntgl.platform.event} that NTGL raises from Fabric callbacks and mixins.
 * <p>
 * Listeners are registered either one by one with {@link #addListener(Class, Consumer)} or by
 * handing a class (static handlers) or an object (instance handlers) whose methods are annotated
 * with {@link SubscribeEvent} to {@link #register(Object)}. A listener receives the event class it
 * names and its subclasses.
 */
public final class EventBus {
    private record Listener(EventPriority priority, boolean receiveCanceled, Consumer<Object> handler) {}

    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap<>();
    /** Per concrete event class: the listeners of the class and all its superclasses, sorted. */
    private final Map<Class<?>, Listener[]> resolved = new ConcurrentHashMap<>();

    public <T extends Event> void addListener(Class<T> type, Consumer<? super T> listener) {
        addListener(EventPriority.NORMAL, false, type, listener);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> type, Consumer<? super T> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(new Listener(priority, receiveCanceled, (Consumer<Object>) listener));
        resolved.clear();
    }

    /**
     * Registers every {@link SubscribeEvent} method: static ones when given a Class, instance ones
     * (and static ones) when given an object.
     */
    public void register(Object target) {
        boolean isClass = target instanceof Class<?>;
        Class<?> type = isClass ? (Class<?>) target : target.getClass();
        for (var method : type.getMethods()) {
            var annotation = method.getAnnotation(SubscribeEvent.class);
            if (annotation == null) continue;
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (isClass && !isStatic) continue;
            var params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                throw new IllegalArgumentException("Event handler " + method + " must take exactly one event parameter");
            }
            try {
                method.setAccessible(true);
                MethodHandle handle = MethodHandles.lookup().unreflect(method);
                if (!isStatic) handle = handle.bindTo(target);
                final MethodHandle bound = handle;
                Consumer<Object> consumer = event -> {
                    try {
                        bound.invoke(event);
                    } catch (RuntimeException | Error e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };
                synchronized (this) {
                    listeners.computeIfAbsent(params[0], k -> new ArrayList<>())
                            .add(new Listener(annotation.priority(), annotation.receiveCanceled(), consumer));
                    resolved.clear();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access event handler " + method, e);
            }
        }
    }

    private synchronized Listener[] resolve(Class<?> eventClass) {
        var list = new ArrayList<Listener>();
        for (Class<?> c = eventClass; c != null && c != Object.class; c = c.getSuperclass()) {
            var own = listeners.get(c);
            if (own != null) list.addAll(own);
        }
        list.sort(Comparator.comparing(Listener::priority));
        return list.toArray(new Listener[0]);
    }

    /**
     * Posts the event to its listeners.
     *
     * @return the event, so callers can inspect it (for example {@code post(event).isCanceled()})
     */
    public <T extends Event> T post(T event) {
        var array = resolved.get(event.getClass());
        if (array == null) {
            array = resolve(event.getClass());
            resolved.put(event.getClass(), array);
        }
        for (var listener : array) {
            if (!listener.receiveCanceled() && event.isCanceled()) continue;
            listener.handler().accept(event);
        }
        return event;
    }
}
