package at.cpickl.agrotlin

import android.app.Application
import roboguice.RoboGuice
import roboguice.AnnotationDatabaseImpl

public class SwirlApplication : Application() {
    {
        // workaround for roboguice bug java.lang.ClassNotFoundException: AnnotationDatabaseImpl
        // see: https://github.com/roboguice/roboguice/issues/246
        RoboGuice.setUseAnnotationDatabases(false);
    }
}
