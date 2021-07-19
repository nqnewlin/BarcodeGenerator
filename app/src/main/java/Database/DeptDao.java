package Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeptDao {
    @Query(" SELECT * FROM departmentInfo")
    List<DepartmentInfo> getAll();

    @Insert
    void insertAll(DepartmentInfo...departmentInfo);


    @Delete
    void delete(DepartmentInfo departmentInfo);

    @Query("SELECT * FROM departmentinfo")
    LiveData<List<DepartmentInfo>> findAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(DepartmentInfo departmentInfo);
}
