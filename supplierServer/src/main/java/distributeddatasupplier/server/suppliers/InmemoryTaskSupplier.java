package distributeddatasupplier.server.suppliers;

import tasks.Task;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class InmemoryTaskSupplier implements TaskSupplier {

    private final Queue<Task> tasks;

    public InmemoryTaskSupplier() {
        tasks = new ArrayDeque<>();
    }

    @Override
    public Task getTask() {
        if (!isEmpty()) {
            return tasks.poll();
        } else {
            return Task.EMPTY_TASK;
        }
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

}
