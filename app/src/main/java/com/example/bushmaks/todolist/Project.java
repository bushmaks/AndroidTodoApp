package com.example.bushmaks.todolist;

import java.util.List;

class Project {
    public int id;
    public String title;
    public List<Todos> todos;
}
class Todos {
    public int id;
    public String text;
    public boolean isCompleted;
    public int project_id;
    public String created_at;
    public String updated_at;
}