package com.catchingnow.delegatedscopesmanager.customAbilty;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;

import com.catchingnow.delegatedscopesmanager.util.Hack;

/**
 * @author heruoxin @ CatchingNow Inc.
 * @since 2019-03-31
 */
public class AppOpsUtil {
    private static AppOpsManager sManager;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void setMode(Context context, int opCode, int uid, String packageName, int mode) {
        if (sManager == null) {
            sManager = context.getSystemService(AppOpsManager.class);
        }
        Hack.into(AppOpsManager.class)
                .method("setMode")
                .returning(void.class)
                .withParams(int.class, int.class, String.class, int.class)
                .invoke(opCode, uid, packageName, mode)
                .on(sManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void resetAllModes(int userId, String packageName) throws RemoteException {
        IBinder binder = Hack.into("android.os.ServiceManager")
                .staticMethod("getService")
                .returning(IBinder.class)
                .withParams(String.class)
                .invoke(Context.APP_OPS_SERVICE)
                .statically();

        Object appopsService;
        try {
            appopsService = Hack.into("com.android.internal.app.IAppOpsService$Stub")
                    .staticMethod("asInterface")
                    .returning(Class.forName("com.android.internal.app.IAppOpsService"))
                    .withParams(IBinder.class)
                    .invoke(binder)
                    .statically();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Hack.into("com.android.internal.app.IAppOpsService")
                .method("resetAllModes")
                .returning(void.class)
                .throwing(RemoteException.class)
                .withParams(int.class, String.class)
                .invoke(userId, packageName)
                .on(appopsService);
    }

}
