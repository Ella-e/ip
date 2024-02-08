package duke;

import java.io.IOException;
import java.util.Objects;

import duke.exceptions.BaseException;
import duke.storage.Storage;
import duke.tasks.Task;
import duke.tasks.TaskList;
import duke.ui.UI;
import duke.utils.KeyEnum;
import duke.utils.Parser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;


/**
 * Main chatbot class.
 */
public class Duke {
    private TaskList tasks;
    private Storage storage;
    private UI ui;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/bot.png"));

    /**
     * Initializes the chatbot.
     */
    public Duke() {
        this("./data/duke.txt");
    }

    /**
     * Initializes by loading the stored file.
     *
     * @param filePath Path of stored file.
     */
    public Duke(String filePath) {
        this.ui = new UI();
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            this.tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot.
     */
    public void run() {
        ui.onEnter();
        String response = "";
        do {
            response = getResponse(ui.getUserInput());
        } while (!Objects.equals(response, "Bye. Hope to see you again soon!"));
    }

    public static void main(String[] args) {
        Duke duke = new Duke("./data/duke.txt");
        duke.run();
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    public String getResponse(String userInput) {
        String response = "";
        try {
            // use parser to process the userInput
            // the parser object contains all the current user input line information
            Parser parser = new Parser();
            parser.parse(userInput);
            System.out.println(userInput);

            // check for end the session
            if (parser.getCurrentKey().equals(KeyEnum.EXITKEY)) {
                response = ui.onExit();
            }

            // continue for the functionality
            switch (parser.getCurrentKey()) {
            case DEADLINE:
            case TODO:
            case EVENT:
                Task task = tasks.addTask(parser.getInputDetail(),
                    parser.getFrom(), parser.getTo(), parser.getCurrentKey());
                System.out.println("success");
                storage.writeTasksToFile(tasks);
                System.out.println("fail");
                response = ui.onAddSuccess(task, tasks.getNumOfTasks());
                break;
            case LIST:
                response = ui.showList(tasks);
                break;
            case MARK:
                Task markedTask = tasks.markTaskById(parser.getIndex(), true);
                storage.writeTasksToFile(tasks);
                response = ui.onMarkDone(markedTask);
                break;
            case UNMARK:
                Task unMarkedTask = tasks.markTaskById(parser.getIndex(), false);
                storage.writeTasksToFile(tasks);
                response = ui.onUnmarkDone(unMarkedTask);
                break;
            case DELETE:
                Task deletedTask = tasks.deleteTaskById(parser.getIndex());
                storage.writeTasksToFile(tasks);
                response = ui.onDelete(deletedTask, tasks);
                break;
            case FIND:
                TaskList matchedTasks = tasks.findTasks(parser.getInputDetail());
                response = ui.showMatchedList(matchedTasks);
                break;
            default:
                break;
            }
        } catch (BaseException e) {
            response = ui.showErrorMsg(e.getMessage());
        } catch (IOException e) {
            response = ui.showLoadingError();
        }
        return response;
    }
}
