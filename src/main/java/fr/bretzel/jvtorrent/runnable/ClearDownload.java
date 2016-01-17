package fr.bretzel.jvtorrent.runnable;

import fr.bretzel.jvtorrent.JVTorrent;
import fr.bretzel.jvtorrent.util.Download;

/**
 * Created by loicn on 16/01/2016.
 */
public class ClearDownload implements Runnable {

    public void run() {
        if (JVTorrent.downloads.size() > 0) {
            for (Download d : JVTorrent.downloads) {
                if(d.getProgresse() >= 100) {
                    d.stop();
                    JVTorrent.downloads.remove(d);
                }
            }
        }
    }
}
