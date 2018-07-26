package com.example.bushmaks.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class TodoCreateActivity extends AppCompatActivity {

    private EditText todoText;
    private ListView projectsList;
    private String projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Добавляем шрифты
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_todo_create);
        // Кнопки в Toolbar
        todoText = (EditText) findViewById(R.id.editText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Перенос категорий из MainActivity
        Intent intent = getIntent();
        ArrayList<String> projectsArray = intent.getStringArrayListExtra("string-array");

        // Список категорий
        projectsList = (ListView) findViewById(R.id.listView);
        projectsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.projectlist_cell);

        adapter.addAll(projectsArray);
        projectsList.setAdapter(adapter);
        projectsList.setItemChecked(0, true);

        projectsList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                ((ListView) parent).setItemChecked(position, true);
                projectID = String.valueOf(id + 1);

            }
        });




    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Функционал кнопки: создание задачи и отправка ее на сервер, переход в MainActivity с обновлением содержимого
        Intent intent = getParentActivityIntent();
        if (item.getItemId() == R.id.submit) {
            if (!todoText.getText().toString().equals("")) {

                String todoTextString = todoText.getText().toString();

                JsonObject todo = new JsonObject();

                todo.addProperty("text", todoTextString);

                todo.addProperty("project_id", projectID);

                JsonObject params = new JsonObject();

                params.add("todo", todo);



                Ion.with(this)
                        .load("http://warm-fjord-39817.herokuapp.com/api/v1/todos")
                        .setJsonObjectBody(params)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                            }
                        });

                Bundle b=new Bundle();
                b.putStringArray("createdTodo", new String[]{projectID, todoTextString});
                Intent i=new Intent(this, MainActivity.class);
                i.putExtras(b);
                startActivity(intent);
                finish();
                return true;
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Вы не ввели текст задачи!", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }

        startActivity(intent);
        finish();
        return true;
    }

}
