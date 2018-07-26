package com.example.bushmaks.todolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;



public class MainActivity extends AppCompatActivity {

    private CustomAdapter mAdapter;
    private ArrayList<String> projectsList = new ArrayList<String>();
    private ListView list;

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
        setContentView(R.layout.activity_main);

        // Кнопка для обновления содержимого
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Кнопка создания задачи с переносом названий категорий
        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, TodoCreateActivity.class);
                    intent.putExtra("string-array", projectsList);
                    startActivity(intent);
                }
            });


        // Запрос категорий и задач с сервера
        mAdapter = new CustomAdapter(this);
        final List<Project> projects = new ArrayList<Project>();

        Ion.with(this)

                .load("https://warm-fjord-39817.herokuapp.com/projects.json")

                .asJsonArray()

                .setCallback(new FutureCallback<JsonArray>() {

                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                        if (result != null) {

                            for (final JsonElement projectJsonElement : result) {
                                projects.add(new Gson().fromJson(projectJsonElement, Project.class));
                            }
                            mAdapter.clear();
                            for (Project project : projects) {
                                mAdapter.addSectionHeaderItem(project.title);
                                projectsList.add(project.title);

                                for (Todos todo : project.todos) {
                                    mAdapter.addItem(todo.text, todo.isCompleted, Long.valueOf(todo.id));

                                }

                            }

                        }

                    }

                });
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(mAdapter);

        // Обновление задачи на сервере и в приложении при нажатии на нее.
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if (v instanceof RelativeLayout) {
                    String isCompleted;
                    if (((CheckedTextView) v.findViewById(R.id.text)).isChecked() == true) {
                        ((CheckedTextView) v.findViewById(R.id.text)).setChecked(false);
                        // Меняем bool в адаптере, т.к. listview выполняет recycleview
                        mAdapter.changeBool(false, id);
                            isCompleted = "0";
                        ((CheckedTextView) v.findViewById(R.id.text)).setPaintFlags(((CheckedTextView) v.findViewById(R.id.text)).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    } else {
                        ((CheckedTextView) v.findViewById(R.id.text)).setChecked(true);
                            isCompleted = "1";
                        mAdapter.changeBool(true, id);
                        ((CheckedTextView) v.findViewById(R.id.text)).setPaintFlags(((CheckedTextView) v.findViewById(R.id.text)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    JsonObject todo = new JsonObject();

                    todo.addProperty("isCompleted", isCompleted);

                    JsonObject params = new JsonObject();

                    params.add("todo", todo);

                    Ion.with(getBaseContext())
                            .load("PATCH","http://warm-fjord-39817.herokuapp.com/api/v1/todos/" + id)
                            .setJsonObjectBody(params)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    // do stuff with the result or error
                                }
                            });
                }


            }
        });


    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Кнопка обновления контента
        if (id == R.id.action_settings) {
            Intent intent = getIntent();
            finish();
            overridePendingTransition(0,0);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
