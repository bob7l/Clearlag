package me.minebuilders.clearlag.config.configupdater;

import me.minebuilders.clearlag.config.configupdater.entries.*;

import java.io.*;
import java.util.*;

/**
 * @author bob7l
 */
public class ConfigUpdater {

    private final List<String> carriedOverPaths = new ArrayList<>();

    private final Set<String> nonMergableKeys = new HashSet<>();

    private List<ConfigEntry> updatingToConfig = null;

    private List<ConfigEntry> updatingFromConfig = null;

    public void setUpdatingToConfig(InputStream in) throws Exception {
        updatingToConfig = loadConfig(in);
    }

    public void setUpdatingFromConfig(InputStream in) throws Exception {
        updatingFromConfig = loadConfig(in);
    }

    public void addCarriedOverPath(String path) {
        carriedOverPaths.add(path);
    }

    public void addNonMergableKey(String key) {
        nonMergableKeys.add(key);
    }

    public int updateConfig(File writeToFile) throws Exception {

        int mergedVariables = 0;

        for (ConfigEntry updatingToEntry : updatingToConfig) {

            if (!(updatingToEntry instanceof ConfigCommentEntry) && !nonMergableKeys.contains(updatingToEntry.getKey())) {

                for (ConfigEntry updatingFromEntry : updatingFromConfig) {

                    if (updatingToEntry.getKey().equals(updatingFromEntry.getKey())) {

                        updatingToEntry.merge(updatingFromEntry);

                        ++mergedVariables;
                    }
                }
            }
        }

        for (String carriedOverPath : carriedOverPaths) {

            for (ConfigEntry updatingFromEntry : updatingFromConfig) {

                if (updatingFromEntry.getKey().equals(carriedOverPath)) {

                    final ListIterator<ConfigEntry> updatingToIter = updatingToConfig.listIterator();

                    while (updatingToIter.hasNext()) {

                        if (updatingToIter.next().getKey().equals(carriedOverPath)) {

                            updatingToIter.set(updatingFromEntry);

                            break;
                        }
                    }
                }
            }
        }

        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(writeToFile))) {

            for (ConfigEntry updatingToEntry : updatingToConfig)
                updatingToEntry.write(bw, 0);
        }

        return (updatingToConfig.size() - mergedVariables);
    }

    private List<ConfigEntry> loadConfig(InputStream in) throws Exception {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        final List<ConfigEntry> configEntryList = new ArrayList<ConfigEntry>();

        final Stack<TreeEntryNode> treeStack = new Stack<>();

        ConfigListEntry currentListEntry = null;

        String line = reader.readLine();

        String nextLine = reader.readLine();

        do {
            final int position = getPosition(line);

            while (!treeStack.isEmpty() && treeStack.peek().position >= position)
                treeStack.pop();

            final String trimmedLine = line.substring(position); //Remove leading white spaces.

            if (trimmedLine.startsWith("-")) {

                if (currentListEntry != null)
                    currentListEntry.add(trimmedLine);

            } else {

                final TreeEntryNode currentTree = (treeStack.isEmpty() ? null : treeStack.peek());

                ConfigEntry configEntry;

                //Comment
                if (isComment(trimmedLine)) {
                    configEntry = new ConfigCommentEntry(line);
                } else {

                    final String[] bits = trimmedLine.split(":", 2);

                    final String key = bits[0];

                    //Small, or value is a comment? Must be a tree-key
                    if (bits.length <= 1 || isComment(bits[1])) {

                        if (nextLine != null && nextLine.trim().startsWith("-")) {
                            currentListEntry = new ConfigListEntry(key);
                            configEntry = currentListEntry;
                        } else {

                            final TreeEntryNode treeEntryNode = new TreeEntryNode(new TreeConfigEntry(key), position);

                            configEntry = treeEntryNode.treeConfigEntry;

                            treeStack.add(treeEntryNode);
                        }

                    } else {
                        configEntry = new ConfigBasicEntry(key, bits[1]);
                    }
                }

                //Are we currently in a tree? If not, add the new value/tree directly
                if (currentTree != null)
                    currentTree.treeConfigEntry.addConfigEntry(configEntry);
                else
                    configEntryList.add(configEntry);
            }

            line = nextLine;

        } while (((nextLine = reader.readLine()) != null && line != null) || line != null);

        return configEntryList;
    }

    private boolean isComment(String str) {

        if (str.isEmpty() || str.startsWith("#"))
            return true;

        final String noSpaceStr = str.replace(" ", "");

        return noSpaceStr.startsWith("#") || noSpaceStr.isEmpty();
    }

    private static class TreeEntryNode {

        private final TreeConfigEntry treeConfigEntry;

        private final int position;

        public TreeEntryNode(TreeConfigEntry treeConfigEntry, int position) {
            this.treeConfigEntry = treeConfigEntry;
            this.position = position;
        }
    }

    private int getPosition(String str) {
        int i = 0;

        for (; i < str.length(); ++i)
            if (str.charAt(i) != ' ')
                break;

        return i;
    }
}
