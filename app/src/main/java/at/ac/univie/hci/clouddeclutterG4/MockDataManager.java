//new File Naomi

package at.ac.univie.hci.clouddeclutterG4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDataManager {
    private static MockDataManager instance;

    public static class CloudService {
        public String name;
        public boolean isConnected;
        public boolean isActive;

        public CloudService(String name, boolean isConnected, boolean isActive) {
            this.name = name;
            this.isConnected = isConnected;
            this.isActive = isActive;
        }
    }

    public List<FileItem> cleanupItems = new ArrayList<>();
    public List<FileItem> trashItems = new ArrayList<>();
    public Map<String, CloudService> cloudServices = new HashMap<>();

    private MockDataManager() {
        cloudServices.put("Google Drive", new CloudService("Google Drive", true, true));
        cloudServices.put("Dropbox", new CloudService("Dropbox", false, false));
        cloudServices.put("OneDrive", new CloudService("OneDrive", true, false));
        cloudServices.put("iCloud", new CloudService("iCloud", true, true));

        long now = System.currentTimeMillis();
        long day = 24 * 60 * 60 * 1000L;

        cleanupItems.add(new FileItem("Urlaub 2023.jpg", 1200000000L, "1.2 GB", "Google Drive", "Bild", now - 10 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Filmprojekt.mp4", 5400000000L, "5.4 GB", "Dropbox", "Video", now - 5 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Rechnung.pdf", 800000L, "0.8 MB", "OneDrive", "Dokument", now - 30 * day, android.R.drawable.ic_menu_agenda));
        cleanupItems.add(new FileItem("Backup_iPhone.zip", 2100000000L, "2.1 GB", "iCloud", "Backup", now - 2 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Party_Foto_1.png", 5000000L, "5 MB", "Google Drive", "Bild", now - day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Meeting_Audio.mp3", 15000000L, "15 MB", "Google Drive", "Audio", now - 12 * day, android.R.drawable.ic_menu_gallery));
    }

    public static synchronized MockDataManager getInstance() {
        if (instance == null) {
            instance = new MockDataManager();
        }
        return instance;
    }

    public List<FileItem> getFilteredCleanupItems() {
        List<FileItem> filtered = new ArrayList<>();
        for (FileItem item : cleanupItems) {
            CloudService service = cloudServices.get(item.source);
            if (service != null && service.isConnected && service.isActive) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
