package duke.storage;

import duke.tasks.Task;
import duke.tasks.Deadline;
import duke.tasks.Event;
import duke.tasks.Todo;
import duke.tasks.TaskList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class represent a file and contains functions processing a file.
 */
public class Storage {
	private String filePath = "./data/duke.txt";
	private File file;

    /**
     * Creates new file object and stores it.
     * @param filePath path to load file from.
     */
    public Storage(String filePath) {
        this.file = new File(filePath);
        this.filePath = filePath;
    }

    /**
     * Reads tasks in a text file from the given filePath recursively.
     * If the file or folder does not exist, create new folders and files based on the given filePath.
     * @return task list read from the file.
     * @throws IOException
     */
    public ArrayList<Task> load() throws IOException {
        if (!this.file.getParentFile().exists())
            this.file.getParentFile().mkdirs();
        if (!this.file.exists())
            this.file.createNewFile();
        return this.readFile();
    }

    /**
     * Reads content of the file into task list.
     * @return task list read from file
     * @throws FileNotFoundException if the file is not found
     */
    private ArrayList<Task> readFile() throws FileNotFoundException{
        ArrayList<Task> taskList = new ArrayList<>();
        Scanner s = new Scanner(file); // create a Scanner using the File as the source
        while (s.hasNext()) {
            String currentTaskString = s.nextLine();
            Task newTask = this.stringToTask(currentTaskString);
            taskList.add(newTask);
        }
        return taskList;
    }

    /**
     * Writes all the duke tasks in the task list to the file stored.
     * @param taskList taskList to be stored.
     * @throws IOException
     */
    public void writeTasksToFile(TaskList taskList) throws IOException {
        FileWriter fw = new FileWriter(this.filePath);
        for(int i = 0; i< taskList.getNumOfTasks(); i++) {
            fw.write(taskList.getTaskInfileStringFormat(i) + "\n");
        }
        fw.close();
    }

    /**
     * Changes task from string format to task object
     * @param str String format of the task
     * @return The respective task object
     */
    private Task stringToTask(String str) {
        String[] strSplit = str.split("\\|");
        Boolean status = strSplit[1].equals("1");
        String detail = strSplit[2];
        Task task = new Task(status, detail);
        switch (strSplit[0]) {
            case "T":
                task = new Todo(status, detail);
                break;
            case "D":
                String by = strSplit[3];
                task = new Deadline(status, detail, by);
                break;
            case "E":
                String from = strSplit[3];
                String to = strSplit[4];
                task = new Event(status, detail, from, to);
                break;
        }
        return task;
    }
}
