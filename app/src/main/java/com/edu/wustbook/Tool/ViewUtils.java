package com.edu.wustbook.Tool;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ViewUtils {

//    public static void autoInjectAllFiled(Activity activity) {
//
//        try {
//            Class<?> clss  = activity.getClass();
//            Field[] fields = clss.getDeclaredFields();
//            for (Field field : fields) {
//                if (field.isAnnotationPresent(BindView.class)) {
//                    BindView inject=field.getAnnotation(BindView.class);
//                    int id = inject.value();
//                    if (id > 0) {
//                        field.setAccessible(true);
//                        field.set(activity, activity.findViewById(id));
//                    }
//                }
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void autoInjectAllFiled(View v) {
//
//        try {
//            Class<?> clss  = v.getClass();
//            Field[] fields = clss.getDeclaredFields();
//            for (Field field : fields) {
//                if (field.isAnnotationPresent(BindView.class)) {
//                    BindView inject=field.getAnnotation(BindView.class);
//                    int id = inject.value();
//                    if (id > 0) {
//                        field.setAccessible(true);
//                        field.set(v, v.findViewById(id));
//                    }
//                }
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//    }

    public static void autoInjectAllFiled(Object object) {
        int[] viewIds = null;
        int fieldCur = 0;
        View view = null;
        BindView bindView = null;
        Class cls = object.getClass();

        for (Field field : cls.getDeclaredFields()) {
            bindView = field.getAnnotation(BindView.class);
            if (bindView != null && bindView instanceof BindView) {
                viewIds = bindView.values();
                if (viewIds.length == 1) {
                    view = ((Activity) object).findViewById(viewIds[0]);
                } else {
                    view = ((Activity) object).findViewById(viewIds[fieldCur++]);
                    if (viewIds.length == fieldCur) {
                        fieldCur = 0;
                    }
                }
                field.setAccessible(true);
                try {
                    field.set(object, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
