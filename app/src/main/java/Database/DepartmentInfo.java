package Database;

import android.content.IntentFilter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DepartmentInfo {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "item_scans")
    public String itemScans;

    @ColumnInfo(name = "quantity_scanned")
    public int quantityScanned;

    @ColumnInfo(name = "scanned_departments")
    public String scannedDepartments;

    @ColumnInfo(name = "scan_time")
    public String scanTime;

    public DepartmentInfo(int uid, String itemScans, int quantityScanned, String scannedDepartments, String scanTime) {
        this.uid = uid;
        this.itemScans = itemScans;
        this.quantityScanned = quantityScanned;
        this.scannedDepartments = scannedDepartments;
        this.scanTime = scanTime;
    }

    @Ignore
    public DepartmentInfo(String itemScans, int quantityScanned, String scannedDepartments, String scanTime) {
        this.itemScans = itemScans;
        this.quantityScanned = quantityScanned;
        this.scannedDepartments = scannedDepartments;
        this.scanTime = scanTime;
    }

    public int getName() { return uid; }

    public String getItemScans() { return itemScans; }

    public int getQuantityScanned() { return quantityScanned; }

    public String getScannedDepartments() { return scannedDepartments; }

    public String getScanTime() { return scanTime; }
}
