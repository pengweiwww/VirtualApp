package com.lody.virtual.client.ipc;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.ipcbus.IPCSingleton;
import com.lody.virtual.server.interfaces.INotificationManager;
import com.lody.virtual.server.notification.NotificationCompat;

import java.lang.reflect.Field;

/**
 * Fake notification manager
 */
public class VNotificationManager {
    public static final String ACTION_NOTIFICATION = "com.pw.pa.ACTION_NOTIFICATION";
    public static final String EX_PKG = "ex_pkg";
    public static final String EX_ID = "ex_id";
    private static final VNotificationManager sInstance = new VNotificationManager();
    private final NotificationCompat mNotificationCompat;
    private IPCSingleton<INotificationManager> singleton = new IPCSingleton<>(INotificationManager.class);

    private VNotificationManager() {
        mNotificationCompat = NotificationCompat.create();
    }

    public static VNotificationManager get() {
        return sInstance;
    }

    public INotificationManager getService() {
        return singleton.get();
    }

    @SuppressLint("InlinedApi")
    public boolean dealNotification(int id, Notification notification, String packageName) {
        if (notification == null) return false;
        try {
            Intent intent = new Intent(ACTION_NOTIFICATION);
            intent.putExtra(EX_PKG, packageName);
            intent.putExtra(EX_ID, id);

            // 其它的信息存在一个bundle中，此bundle在android4.3及之前是私有的，需要通过反射来获取；android4.3之后可以直接获取
            Bundle extras = null;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                // android 4.3
                try {
                    Field field = Notification.class.getDeclaredField("extras");
                    extras = (Bundle) field.get(notification);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                // android 4.3之后
                extras = notification.extras;
            }
            if (extras != null) {
                intent.putExtra(Notification.EXTRA_TITLE, extras.getString(Notification.EXTRA_TITLE));
                intent.putExtra(Notification.EXTRA_TEXT, extras.getString(Notification.EXTRA_TEXT));
            }
            VActivityManager.get().sendBroadcast(intent, VirtualCore.get().myUserId());
        } catch (Exception ignored) {
        }
        return VirtualCore.get().getHostPkg().equals(packageName)
                || mNotificationCompat.dealNotification(id, notification, packageName);
    }

    public int dealNotificationId(int id, String packageName, String tag, int userId) {
        try {
            return getService().dealNotificationId(id, packageName, tag, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String dealNotificationTag(int id, String packageName, String tag, int userId) {
        try {
            return getService().dealNotificationTag(id, packageName, tag, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return tag;
    }

    public boolean areNotificationsEnabledForPackage(String packageName, int userId) {
        try {
            return getService().areNotificationsEnabledForPackage(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void setNotificationsEnabledForPackage(String packageName, boolean enable, int userId) {
        try {
            getService().setNotificationsEnabledForPackage(packageName, enable, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addNotification(int id, String tag, String packageName, int userId) {
        try {
            getService().addNotification(id, tag, packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancelAllNotification(String packageName, int userId) {
        try {
            getService().cancelAllNotification(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
