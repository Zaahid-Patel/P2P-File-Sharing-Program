package temp_Server;

import java.util.ArrayList;
import java.util.List;
import api.FileData;
/**
 * Class should be used when a client queries for a file name
 */
public class SearchFile {
    /**
     * Returns a filtered list of filedata
     * @param data unfiltered data
     * @param item filter according to this input
     * @return filtered FileData list
     */
    public static List<FileData> searchFile(List<FileData> data, String item) {
        List<FileData> searchedFiles = new ArrayList<FileData>();
        for (int i = 0; i < data.size(); i++) {
            String file = data.get(i).getFileName();

            if (file.contains(item)) {
                searchedFiles.add(data.get(i));
            }
        }

        return searchedFiles;
    }
}
