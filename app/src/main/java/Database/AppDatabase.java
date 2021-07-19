package Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities =  {DepartmentInfo.class}, exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "database-name";
    public static final int NUMBER_OF_THREADS = 4;
    private static AppDatabase instance;
    private static final Object sLock = new Object();
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            DB_NAME).addCallback(sAppDatabaseCallback).build();
                }
            }
        }
        return instance;
    }

    public abstract  DeptDao deptDao();

    public static RoomDatabase.Callback sAppDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };
}
