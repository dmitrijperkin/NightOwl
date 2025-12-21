package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout daysContainer;
    private TextView tvWeekNumber;
    private int currentWeekIndex = 0;
    private int selectedDayIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        daysContainer = findViewById(R.id.daysContainer);
        tvWeekNumber = findViewById(R.id.tvWeekNumber);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnPrevWeek).setOnClickListener(v -> { if (currentWeekIndex > 0) { currentWeekIndex--; updateUI(); } });
        findViewById(R.id.btnNextWeek).setOnClickListener(v -> { if (currentWeekIndex < 1) { currentWeekIndex++; updateUI(); } });

        updateUI();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_schedule) {
                return true;
            }
            else if (id == R.id.nav_map) {
                android.widget.Toast.makeText(MainActivity.this, "Карта находится в разработке", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (id == R.id.nav_qr) {
                android.widget.Toast.makeText(MainActivity.this, "Сканер QR находится в разработке", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void updateUI() {
        tvWeekNumber.setText((currentWeekIndex == 0 ? "16" : "17") + " учебная неделя");
        View btnPrev = findViewById(R.id.btnPrevWeek);
        View btnNext = findViewById(R.id.btnNextWeek);

        btnPrev.setAlpha(currentWeekIndex == 0 ? 0.2f : 1.0f);
        btnNext.setAlpha(currentWeekIndex == 1 ? 0.2f : 1.0f);

        renderCalendar();
        loadLessons();
    }
    private boolean hasTestOnThisDay(int weekIdx, int dayIdx) {
        if (weekIdx == 0) return false;
        return weekIdx == 1 && (dayIdx >= 1 && dayIdx <= 4);
    }
    private void renderCalendar() {
        daysContainer.removeAllViews();
        String[] days = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};
        int start = (currentWeekIndex == 0) ? 15 : 22;

        for (int i = 0; i < 7; i++) {
            View dayView = getLayoutInflater().inflate(R.layout.item_day, null);
            TextView tvNum = dayView.findViewById(R.id.tvDayNumber);
            TextView tvName = dayView.findViewById(R.id.tvDayName);

            tvName.setText(days[i]);
            tvNum.setText(String.valueOf(start + i));

            boolean isTestDay = hasTestOnThisDay(currentWeekIndex, i);

            if (i == selectedDayIndex) {
                tvNum.setBackgroundResource(R.drawable.selected_day_bg);
                tvNum.setTextColor(Color.BLACK);
                tvName.setTextColor(Color.WHITE);
            } else if (isTestDay) {
                tvNum.setBackgroundResource(R.drawable.test_day_bg);
                tvNum.setTextColor(Color.WHITE);
                tvName.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                tvNum.setBackground(null);
                tvNum.setTextColor(Color.WHITE);
                tvName.setTextColor(Color.parseColor("#888888"));
            }

            final int idx = i;
            dayView.setOnClickListener(v -> {
                selectedDayIndex = idx;
                renderCalendar();
                loadLessons();
            });

            daysContainer.addView(dayView, new LinearLayout.LayoutParams(0, -2, 1f));
        }
    }

    private void loadLessons() {
        List<Lesson> list = new ArrayList<>();
        int lec = Color.YELLOW;
        int prac = Color.parseColor("#FF9800");
        int test = Color.parseColor("#4CAF50");

        if (currentWeekIndex == 0) {
            if (selectedDayIndex == 0) { // ПН
                list.add(new Lesson("1 пара · 09:00–10:30", "Математический анализ", "Головешкин Василий Адамович", "330 (С-20)", "Лекция", lec));
                list.add(new Lesson("2 пара · 10:40–12:10", "Правоведение", "Протопопов Егор Ефимович", "455а (С-20)", "Практика", prac));
                list.add(new Lesson("3 пара · 12:40–14:10", "Стандарты информационной безопасности", "Шутов Василий Александрович", "13 (С-20)", "Практика", prac));
            } else if (selectedDayIndex == 1) { // ВТ
                list.add(new Lesson("1 пара · 09:00–10:30", "Технологии и методы программирования", "Филатов Вячеслав Валерьевич", "330 (С-20)", "Лекция", lec));
                list.add(new Lesson("2 пара · 10:40–12:10", "Математическая логика и теория алгоритмов", "Алексеенко Анна Станиславовна", "330 (С-20)", "Лекция", lec));
            } else if (selectedDayIndex == 2) { // СР
                list.add(new Lesson("1 пара · 09:00–10:30", "Дифференциальные уравнения", "Бессарабская Ирина Эдуардовна", "441 (С-20)", "Лекция", lec));
                list.add(new Lesson("2 пара · 10:40–12:10", "Физическая культура и спорт", "", "ФОК-9 (С-20)", "Практика", prac));
                list.add(new Lesson("3 пара · 12:40–14:10", "Технологии и методы программирования", "Русаков Алексей Михайлович", "333 (С-20)", "Практика", prac));
            } else if (selectedDayIndex == 3) { // ЧТ
                list.add(new Lesson("1 пара · 09:00–10:30", "Системы искусственного интеллекта и большие данные", "Болшин Роман Геннадьевич", "330 (С-20)", "Лекция", lec));
                list.add(new Lesson("2 пара · 10:40–12:10", "Математическая логика и теория алгоритмов", "Долженков Сергей Сергеевич", "325 (С-20)", "Практика", prac));
                list.add(new Lesson("3 пара · 12:40–14:10", "Электротехника и электроника", "Микаева Светлана Анатольевна", "441", "Лекция", lec));
            } else if (selectedDayIndex == 4) { // ПТ
                list.add(new Lesson("1 пара · 09:00–10:30", "Системы искусственного интеллекта и большие данные", "Вяткин Артем Андреевич", "Б-214 (МП-1)", "Практика", prac));
                list.add(new Lesson("2 пара · 10:40–12:10", "Системы искусственного интеллекта и большие данные", "Вяткин Артем Андреевич", "Б-214 (МП-1)", "Практика", prac));
            } else if (selectedDayIndex == 5) { // СБ
                list.add(new Lesson("1 пара · 09:00–10:30", "Математический анализ", "Головешкин Василий Адамович", "330 (С-20)", "Практика", prac));
                list.add(new Lesson("2 пара · 10:40–12:10", "Дифференциальные уравнения", "Шадрина Наталья Николаевна", "200г (С-20)", "Практика", prac));
                list.add(new Lesson("3 пара · 12:40–14:10", "Дифференциальные уравнения", "Шадрина Наталья Николаевна", "135 (С-20)", "Практика", prac));
            }
        } else {
            if (selectedDayIndex == 0) { // ПН
                list.add(new Lesson("1 пара · 09:00–10:30", "Технологии и методы программирования", "Филатов Вячеслав Валерьевич", "330 (С-20)", "Лекция", lec));
                list.add(new Lesson("2 пара · 10:40–12:10", "Математическая логика и теория алгоритмов", "Алексеенко Анна Станиславовна", "330 (С-20)", "Лекция", lec));
            } else if (selectedDayIndex == 1) { // ВТ
                list.add(new Lesson("3 пара · 12:40–14:10", "Социальная психология и педагогика", "Мусатова Оксана Алексеевна", "370 (С-20)", "ЗАЧЁТ", test));
            } else if (selectedDayIndex == 2) { // СР
                list.add(new Lesson("1 пара · 09:00–10:30", "Стандарты информационной безопасности", "Шутов Василий Александрович", "13 (С-20)", "ЗАЧЁТ", test));
                list.add(new Lesson("2 пара · 10:40–12:10", "Физическая культура и спорт", "", "ФОК-9 (С-20)", "ЗАЧЁТ", test));
            } else if (selectedDayIndex == 3) { // ЧТ
                list.add(new Lesson("2 пара · 10:40–12:10", "Системы искусственного интеллекта и большие данные", "", "265 (С-20)", "ЗАЧЁТ", test));
            } else if (selectedDayIndex == 4) { // ПТ
                list.add(new Lesson("3 пара · 12:40–14:10", "Иностранный язык", "Ануфриев Олег Сергеевич", "И-320 (В-78)", "ЗАЧЁТ", test));
            }
        }
        if (list.isEmpty()) {
            list.add(new Lesson("--", "Свободный день", "Занятий нет", "", "Отдых", Color.GRAY));
        }
        recyclerView.setAdapter(new LessonAdapter(list));
    }
}