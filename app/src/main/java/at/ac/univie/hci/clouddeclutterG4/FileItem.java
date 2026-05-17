package at.ac.univie.hci.clouddeclutterG4;

import java.io.Serializable;

public class FileItem implements Serializable {
    public String name;
    public long sizeBytes;
    public String sizeDisplay;
    public String source;
    public String type;
    public long dateMillis;
    public int iconResId;

    public FileItem(String name, long sizeBytes, String sizeDisplay, String source, String type, long dateMillis, int iconResId) {
        this.name = name;
        this.sizeBytes = sizeBytes;
        this.sizeDisplay = sizeDisplay;
        this.source = source;
        this.type = type;
        this.dateMillis = dateMillis;
        this.iconResId = iconResId;
    }
}
