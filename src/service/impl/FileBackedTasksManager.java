package service.impl;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) {

    }

    private final File file; //свойство в кот.хранится путь к файлу бэкапа
    private static final String CSV_PATH = "id,type,name,status,description,epic\n";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public String toString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s\n",
                task.getId(), task.getClass().getSimpleName().toUpperCase(),
                task.getName(), task.getStatus(), task.getDescription(), null);
    }

    public void save() { //сохраняет текущее состояние менеджера в указанный файл.
        //id,type,name,status,description,epic
        // 1,TASK,Task1,NEW,Description task1,
        try (Writer writer = new FileWriter(file)) {
            writer.write(CSV_PATH);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }

    }
    /*
Продумать логику и реализовать метод save().
Метод должен сохранять в файл все внесенные в память таски, эпики и сабтаски, а также историю просмотра.
Файл должен быть в формате CSV. Обычно разделителем выступает запятая.
Но так как в описаниях задач могут быть запятые, лучше подобрать другой символ.
Возможно, точка с запятой или знак возведения в степень («^»). Или какая-либо последовательность символов.
Первая строка файла это заголовки столбцов. В них перечислены поля объектов-задач (тасков, эпиков, сабтасков).
Перечислим все поля, которые нам понадобятся:
a) id — идентификатор задачи.
b) type — тип задачи: таск, эпик или сабтаск (нужно завести под тип задачи отдельный enum, см пункт 4).
c) title — название задачи.
d) extraInfo — подробное описание задачи.
e) status — статус задачи (если статус эпика вычисляется на основе статусов сабтасков,
нужно ли сохранять статус эпика? — продумать.
Как вариант, сохранять статус эпика в файл, но при восстановлении данных в памяти из файла
высчитывать статус эпика заново и сравнивать с записанным в файле статусом.
При несовпадении выбрасывать исключение — продумать целесообразность).
f) subTasksID — список идентификаторов сабтасков
(это поле актуально только для эпиков, у других типов задач оно будет пустым).
g) epicID — идентификатор родительского эпика (актуально только для сабтасков, у других типов задач оно будет пустым).
Далее в файле идет пустая строка. Это разделитель между данными задач и списком истории просмотров.
Следующая строка это перечисленные через разделитель идентификаторы просмотренных задач.
     */

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        //save()
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        //save()
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        //save()
    }

    @Override
    public Task getTaskId(int id) {
        Task task = super.getTaskId(id);
        //save()
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = super.getEpicId(id);
        //save()
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        SubTask subTask = super.getSubTaskId(id);
        //save()
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        //save()
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        //save()
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        //save()
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        //save()
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        //save()
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        //save()
    }

}
