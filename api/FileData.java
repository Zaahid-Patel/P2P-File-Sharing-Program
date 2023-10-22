package api;

/**
 * Server should store file names in this format
 */
public class FileData {
    //NOTE: id should be unique, but has no internal check. Please make sure when creating a new FileData that id is indeed unique
    int id;
    private String username;
    private String fileName;

    /**
     * Initializer
     * @param id Choose a unique id
     * @param username Username of client file belongs to
     * @param fileName Name of file
     */
    public FileData(int id, String username, String fileName) {
        this.id = id;
        this.username = username;
        this.fileName = fileName;
    }

    /**
     * Returns file id
     * @return id
     */
    public int getID() {
        return id;
    }

    /**
     * Returns Username of client file belongs to
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns file's name
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }
}
