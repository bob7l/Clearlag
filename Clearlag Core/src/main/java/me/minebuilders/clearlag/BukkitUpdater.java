package me.minebuilders.clearlag;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BukkitUpdater implements Runnable {

    private static final String HOST = "https://api.curseforge.com/servermods/files?projectIds=37824";

    private String newversion;
    private String download;
    private final File file;

    public BukkitUpdater(File file) {
        this.file = file;

        new Thread(this).start();
    }

    private int versionToInt(String s) {
        return Integer.parseInt(s.replaceAll("[^\\d.]", "").replace(".", "").trim());
    }

    private boolean updateAvailable() throws Exception {

        Util.log("Checking for updates compatible with your bukkit version [" + Util.getBukkitVersion() + "]...");

        final HttpURLConnection conn = createConnection(HOST, 6000);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        final String response = reader.readLine();

        final String bukkitversion = Util.getBukkitVersion();

        final JSONArray array = (JSONArray) JSONValue.parse(response);

        JSONObject line = null;

        boolean isLegacy = bukkitversion.contains("1.7");

        for (int i = array.size() - 1; i > 17; i--) {

            line = ((JSONObject) array.get(i));

            final String ver = (String) line.get("gameVersion");

            if (ver.contains(bukkitversion) || (!isLegacy && !ver.contains("1.7"))) {
                break;
            }
        }

        this.newversion = line.get("name").toString();
        this.download = (String) line.get("downloadUrl");

        return (versionToInt(Clearlag.getInstance().getDescription().getVersion()) < versionToInt(newversion));
    }

    private HttpURLConnection createConnection(String url, int timeout) throws Exception {

        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        conn.setConnectTimeout(timeout);

        conn.setInstanceFollowRedirects(true);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");

        return conn;
    }

    public void run() {

        try {

            if (updateAvailable()) {

                Util.log("Clearlag version " + newversion + " update available! Downloading...");

                if (!Bukkit.getUpdateFolderFile().exists())
                    Bukkit.getUpdateFolderFile().mkdir();

                HttpURLConnection conn = createConnection(download, 15000);

                int response = conn.getResponseCode();

                if (response == HttpURLConnection.HTTP_MOVED_PERM || response == HttpURLConnection.HTTP_MOVED_TEMP) {
                    download = conn.getHeaderField("Location");
                    conn = createConnection(download, 15000);
                }

                try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                     FileOutputStream fout = new FileOutputStream(Bukkit.getUpdateFolderFile().getAbsolutePath() + "/" + file.getName())) {

                    final byte[] data = new byte[1024];
                    int count;

                    while ((count = in.read(data, 0, 1024)) != -1) {
                        fout.write(data, 0, count);
                    }

                    Util.log("Updating finished! Restart your server for files to take effect");

                } catch (Exception e) {
                    Util.log("Failed to download the latest update!");
                }

            } else {
                Util.log("No updates found!");
            }
        } catch (Exception e) {
            Util.warning("Clearlag failed to check for updates - bukkit may be down");

            e.printStackTrace();
        }
    }
}
