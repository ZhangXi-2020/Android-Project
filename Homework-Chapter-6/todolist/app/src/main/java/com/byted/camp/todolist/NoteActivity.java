package com.byted.camp.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton lowRadio;
    private EditText remindTime;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private Date remind;
    private long remind_long;
    private String time_str;
//    private ClockManager mClockManager = ClockManager.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadio = findViewById(R.id.btn_low);
        lowRadio.setChecked(true);

        remind = new Date();
        remind_long = 0;
        String strDateFormat = "yyyy-MM-dd HH:mm";
        final SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        remindTime = findViewById(R.id.tv_remind_time_picker);
        remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(NoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = year + "-" + StringUtil.getLocalMonth(month) + "-" + StringUtil.getMultiNumber(dayOfMonth) + " "
                                              + StringUtil.getMultiNumber(hourOfDay) + ":" + StringUtil.getMultiNumber(minute);
                                remindTime.setText(time);
                                time_str = String.format("%d-%d-%d %d:%d",year,month+1,dayOfMonth,hourOfDay,minute);
//                                Log.i("timetime", "time_str: " + time_str);
                                try {
                                    remind = sdf.parse(time_str);
                                    remind_long = remind.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
//                                Log.i("timetime", "hourOfDay: " + String.valueOf(hourOfDay));
//                                Log.i("timetime", "minute: " + String.valueOf(minute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
//                        Log.i("timetime", "year: " + String.valueOf(year));
//                        Log.i("timetime", "month: " + String.valueOf(month));
//                        Log.i("timetime", "day: " + String.valueOf(dayOfMonth));

                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

//                long time_now = System.currentTimeMillis();
//                Log.i("timetime", "select: " + String.valueOf(remind_long));
//                Log.i("timetime", "time now: " + String.valueOf(time_now));
                dialog.show();
            }
        });

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                long id = saveNote2Database(content.toString().trim(),
                        getSelectedPriority(), remind_long);
                if (id != -1) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    // 设置闹钟
                    // mClockManager.addAlarm(buildIntent(id), remind);
                    addToCalender();

                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

//    private PendingIntent buildIntent(long id) {
//        Intent intent = new Intent();
//        intent.putExtra(ClockReceiver.EXTRA_EVENT_ID, id);
//        intent.setClass(this, ClockService.class);
//
//        return PendingIntent.getService(this, 0x001, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addToCalender() {
        CalendarReminderUtils.addEvent(this, editText.getText().toString().trim(), remind_long);
    }

    private long saveNote2Database(String content, Priority priority, long remind) {
        if (database == null || TextUtils.isEmpty(content)) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(TodoNote.COLUMN_CONTENT, content);
        values.put(TodoNote.COLUMN_STATE, State.TODO.intValue);
        values.put(TodoNote.COLUMN_DATE, System.currentTimeMillis());
        values.put(TodoNote.COLUMN_REMIND, remind);
        values.put(TodoNote.COLUMN_PRIORITY, priority.intValue);
        long rowId = database.insert(TodoNote.TABLE_NAME, null, values);
        return rowId;
    }

    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.High;
            case R.id.btn_medium:
                return Priority.Medium;
            default:
                return Priority.Low;
        }
    }
}
