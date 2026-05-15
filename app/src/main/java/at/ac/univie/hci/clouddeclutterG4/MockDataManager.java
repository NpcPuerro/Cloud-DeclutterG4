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
        public long totalCapacity; // in bytes
        public long baseUsedCapacity; // in bytes

        public CloudService(String name, boolean isConnected, boolean isActive, long totalCapacity, long baseUsedCapacity) {
            this.name = name;
            this.isConnected = isConnected;
            this.isActive = isActive;
            this.totalCapacity = totalCapacity;
            this.baseUsedCapacity = baseUsedCapacity;
        }

        public long getCurrentUsedCapacity(List<FileItem> allCleanupItems) {
            long used = 0;
            for (FileItem item : allCleanupItems) {
                if (item.source.equals(name)) {
                    used += item.sizeBytes;
                }
            }
            return used;
        }
    }

    public List<FileItem> cleanupItems = new ArrayList<>();
    public List<FileItem> trashItems = new ArrayList<>();
    public Map<String, CloudService> cloudServices = new HashMap<>();

    private MockDataManager() {
        cloudServices.put("Google Drive", new CloudService("Google Drive", true, true, 15L * 1024 * 1024 * 1024, 0));
        cloudServices.put("Dropbox", new CloudService("Dropbox", false, false, 2L * 1024 * 1024 * 1024, 0));
        cloudServices.put("OneDrive", new CloudService("OneDrive", true, false, 5L * 1024 * 1024 * 1024, 0));
        cloudServices.put("iCloud", new CloudService("iCloud", true, true, 5L * 1024 * 1024 * 1024, 0));

        long now = System.currentTimeMillis();
        long day = 24 * 60 * 60 * 1000L;

        cleanupItems.add(new FileItem("Urlaub_2023.jpg", 1200000000L, "1.2 GB", "Google Drive", "Bild", now - 10 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Filmprojekt.mp4", 5400000000L, "5.4 GB", "Google Drive", "Video", now - 5 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Rechnung.pdf", 800000000L, "0.8 GB", "Google Drive", "Dokument", now - 30 * day, android.R.drawable.ic_menu_agenda));
        cleanupItems.add(new FileItem("Backup_SamsungA25.zip", 2100000000L, "2.1 GB", "Google Drive", "Backup", now - 2 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Party_Foto_1.png", 5000000L, "5 MB", "Google Drive", "Bild", now - day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Meeting_Audio.mp3", 15000000L, "15 MB", "Google Drive", "Audio", now - 12 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Hochzeit_2015.mp4", 3450000000L, "3.45 GB", "Google Drive", "Video", now - 1000 * day, android.R.drawable.ic_menu_gallery));

        cleanupItems.add(new FileItem("Sommerurlaub_Kroatien.jpg", 95000000L, "95 MB", "Dropbox", "Bild", now - 5 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Tutorial_Final.mp4", 1100000000L, "1.1 GB", "Dropbox", "Video", now - 10 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Vertrag_Miete.pdf", 25000000L, "25 MB", "Dropbox", "Dokument", now - 15 * day, android.R.drawable.ic_menu_agenda));
        cleanupItems.add(new FileItem("iPod_Backup_Mai.zip", 170000000L, "170 MB", "Dropbox", "Backup", now - 4 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Profilbild_Neu.png", 8500000L, "8.5 MB", "Dropbox", "Bild", now - 2 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Podcast_Folge_12.mp3", 42000000L, "42 MB", "Dropbox", "Audio", now - 6 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Geburtstag_2020.mp4", 190000000L, "190 MB", "Dropbox", "Video", now - 2000 * day, android.R.drawable.ic_menu_gallery));

        cleanupItems.add(new FileItem("Sommerurlaub_Alpen.jpg", 150000000L, "150 MB", "OneDrive", "Bild", now - 15 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Event_Zusammenschnitt.mp4", 1400000000L, "1.4 GB", "OneDrive", "Video", now - 40 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Mietvertrag_2024.pdf", 50000000L, "50 MB", "OneDrive", "Dokument", now - 45 * day, android.R.drawable.ic_menu_agenda));
        cleanupItems.add(new FileItem("Tablet_Backup_April.zip", 900000000L, "900 MB", "OneDrive", "Backup", now - 16 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Profilfoto_Final.png", 100000000L, "100 MB", "OneDrive", "Bild", now - 6 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Interview_Aufnahme.mp3", 300000000L, "300 MB", "OneDrive", "Audio", now - 24 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Familienfeier_2019.mp4", 1800000000L, "1.8 GB", "OneDrive", "Video", now - 600 * day, android.R.drawable.ic_menu_gallery));

        cleanupItems.add(new FileItem("Winterurlaub_Zillertal.jpg", 180000000L, "180 MB", "iCloud", "Bild", now - 15 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Produktdemo_2025.mp4", 1250000000L, "1.25 GB", "iCloud", "Video", now - 40 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Versicherung_Unterlagen.pdf", 35000000L, "35 MB", "iCloud", "Dokument", now - 45 * day, android.R.drawable.ic_menu_agenda));
        cleanupItems.add(new FileItem("Laptop_Backup_Juni.zip", 980000000L, "980 MB", "iCloud", "Backup", now - 16 * day, android.R.drawable.ic_menu_save));
        cleanupItems.add(new FileItem("Bewerbungsfoto.png", 12000000L, "12 MB", "iCloud", "Bild", now - 6 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Sprachnotiz_Kurs.mp3", 85000000L, "85 MB", "iCloud", "Audio", now - 24 * day, android.R.drawable.ic_menu_gallery));
        cleanupItems.add(new FileItem("Jubiläum_2018.mp4", 1750000000L, "1.75 GB", "iCloud", "Video", now - 600 * day, android.R.drawable.ic_menu_gallery));
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
