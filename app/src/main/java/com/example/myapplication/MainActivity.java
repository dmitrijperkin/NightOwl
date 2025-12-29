package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout daysContainer;
    private TextView tvWeekNumber;
    private MireaApi mireaApi;

    private String selectedGroup = "БАСО-03-24";
    private int selectedDayIndex = 0;
    private Calendar currentWeekCalendar;
    private LessonAdapter adapter;
    private List<Lesson> lessonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentWeekCalendar = Calendar.getInstance(new Locale("ru"));
        currentWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        currentWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        selectedDayIndex = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - 2;

        initUI();
        initRetrofit();
        setupNavigation();
        updateUI();
    }
    private void initUI() {
        recyclerView = findViewById(R.id.recyclerView);
        daysContainer = findViewById(R.id.daysContainer);
        tvWeekNumber = findViewById(R.id.tvWeekNumber);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LessonAdapter(lessonList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnPrevWeek).setOnClickListener(v -> {
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateUI();
        });
        findViewById(R.id.btnNextWeek).setOnClickListener(v -> {
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateUI();
        });
    }
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cyberowlapimirea.cloudpub.ru/") // IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mireaApi = retrofit.create(MireaApi.class);
    }
    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_groups) {
                showGroupDialog();
                return true;
            }
            return id == R.id.nav_schedule;
        });
    }
    private void updateUI() {
        renderCalendar();
        loadDataFromApi();
    }
    private void renderCalendar() {
        daysContainer.removeAllViews();
        String[] dayNames = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};
        Calendar tempCal = (Calendar) currentWeekCalendar.clone();

        for (int i = 0; i < 7; i++) {
            View dayView = getLayoutInflater().inflate(R.layout.item_day, null);
            TextView tvNum = dayView.findViewById(R.id.tvDayNumber);
            TextView tvName = dayView.findViewById(R.id.tvDayName);

            tvName.setText(dayNames[i]);
            tvNum.setText(String.valueOf(tempCal.get(Calendar.DAY_OF_MONTH)));

            if (i == selectedDayIndex) {
                tvNum.setBackgroundResource(R.drawable.selected_day_bg);
                tvNum.setTextColor(Color.BLACK);
            } else {
                tvNum.setBackground(null);
                tvNum.setTextColor(Color.WHITE);
            }
            final int idx = i;
            dayView.setOnClickListener(v -> {
                selectedDayIndex = idx;
                updateUI();
            });
            daysContainer.addView(dayView, new LinearLayout.LayoutParams(0, -2, 1f));
            tempCal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    private void loadDataFromApi() {
        Calendar requestCal = (Calendar) currentWeekCalendar.clone();
        requestCal.add(Calendar.DAY_OF_YEAR, selectedDayIndex);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(requestCal.getTime());

        mireaApi.getDaySchedule(selectedGroup, dateStr).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayData(response.body());
                }
            }
            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                tvWeekNumber.setText("Ошибка связи");
            }
        });
    }
    private void displayData(ScheduleResponse data) {
        lessonList.clear();

        if (data.schedule != null && !data.schedule.isEmpty()) {
            tvWeekNumber.setText(data.schedule.get(0).period + " [" + selectedGroup + "]");

            for (LessonModel m : data.schedule) {
                String fullType;
                int color;
                String apiType = m.type.toUpperCase().trim();

                if (apiType.equals("Э") || apiType.contains("ЭКЗАМЕН")) {
                    fullType = "Экзамен";
                    color = Color.parseColor("#F44336"); // Красный
                } else if (apiType.contains("КОНС")) {
                    fullType = "Консультация";
                    color = Color.parseColor("#9C27B0"); // Фиолетовый
                } else if (apiType.contains("ЛК") || apiType.contains("ЛЕКЦИЯ")) {
                    fullType = "Лекция";
                    color = Color.parseColor("#4CAF50"); // Зеленый
                } else if (apiType.contains("ПР") || apiType.contains("ПРАКТИКА")) {
                    fullType = "Практика";
                    color = Color.parseColor("#FF9800"); // Оранжевый
                } else if (apiType.contains("ЛР") || apiType.contains("ЛАБОРАТОРНАЯ")) {
                    fullType = "Лабораторная работа";
                    color = Color.parseColor("#2196F3"); // Синий
                } else if (apiType.contains("ЗАЧ")) {
                    fullType = "Зачёт";
                    color = Color.parseColor("#00BCD4"); // Бирюзовый
                } else if (apiType.contains("КП") || apiType.contains("КУРС")) {
                    fullType = "Курсовая работа";
                    color = Color.parseColor("#795548"); // Коричневый
                } else {
                    fullType = m.type;
                    color = Color.GRAY;
                }
                lessonList.add(new Lesson(m.time, m.subject, m.teacher, m.room, fullType, color));
            }
        } else {
            tvWeekNumber.setText("Пар нет [" + selectedGroup + "]");
            lessonList.add(new Lesson("--", "Свободный день", "", "", "Отдых", Color.DKGRAY));
        }

        adapter.notifyDataSetChanged();
    }
    private void showGroupDialog() {
        String[] groups = {"БАСО-01-24", "БАСО-02-24", "БАСО-03-24", "БАСО-04-24"};
        new AlertDialog.Builder(this)
                .setTitle("Выберите группу")
                .setItems(groups, (dialog, which) -> {
                    selectedGroup = groups[which];
                    updateUI();
                }).show();
    }
}