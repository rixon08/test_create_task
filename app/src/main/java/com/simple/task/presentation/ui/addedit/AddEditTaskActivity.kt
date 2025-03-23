package com.simple.task.presentation.ui.addedit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.simple.task.R
import com.simple.task.constants.AddEditTaskKeyConstants
import com.simple.task.domain.model.TaskModel
import com.simple.task.extensions.toDate
import com.simple.task.extensions.toLong
import com.simple.task.extensions.toStringDateFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditTaskActivity : AppCompatActivity() {

//    private lateinit var edtTaskName: EditText
//    private lateinit var edtDeadline: EditText
    private lateinit var edtTaskName: TextInputEditText
    private lateinit var edtDeadline: TextInputEditText
    private lateinit var taskNameInputLayout: TextInputLayout
    private lateinit var deadlineInputLayout: TextInputLayout
    private lateinit var cbIsCompleted: CheckBox
    private lateinit var btnSave: Button

    private val addEditTaskViewModel: AddEditTaskViewModel by viewModel()

    private var selectedDeadline : Date? = null
    private var isValidate: Boolean = false

    companion object{
        private var task: TaskModel? = null

        fun getIntent(context: Context, task: TaskModel? = null): Intent{
            val intent = Intent(context, AddEditTaskActivity::class.java)
            this.task = task
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit_task)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        taskNameInputLayout = findViewById(R.id.taskNameInputLayout)
        deadlineInputLayout = findViewById(R.id.deadlineInputLayout)
        edtTaskName = findViewById(R.id.edtTaskName)
        edtDeadline = findViewById(R.id.edtDeadline)
        cbIsCompleted = findViewById(R.id.cbIsCompleted)
        btnSave = findViewById(R.id.btnSave)

        edtTaskName.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isValidate) {
                    if (s.toString().isNotBlank()) {
                        taskNameInputLayout.error = null
                    } else {
                        taskNameInputLayout.error = getString(R.string.error_task_name_empty)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        edtDeadline.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (s.toString().isNotBlank() && isValidate){
                    deadlineInputLayout.error = null
               }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        if (task != null){
            edtTaskName.setText(task!!.name)
            val dateDeadline = task!!.deadline.toDate()
            if (dateDeadline != null) {
                setDeadline(dateDeadline)
            }
            cbIsCompleted.isChecked = task!!.isCompleted
        }

        edtDeadline.setOnClickListener{
            showDateTimePicker()
        }

        btnSave.setOnClickListener{
            isValidate = true
            addEditTaskViewModel.saveTask(
                edtTaskName.text.toString(),
                selectedDeadline,
                cbIsCompleted.isChecked,
                task
            ){isSuccess ->
                if (isSuccess) {
                    Toast.makeText(this, getString(R.string.task_saved), Toast.LENGTH_SHORT).show()
                    if (task == null) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val intent = Intent()
                        intent.putExtra(AddEditTaskKeyConstants.TASK_ID, task!!.id)
                        intent.putExtra(
                            AddEditTaskKeyConstants.TASK_COMPLETED,
                            cbIsCompleted.isChecked
                        )
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }

        addEditTaskViewModel.isTaskNameError.observe(this){isError ->
            if (isError) {
                taskNameInputLayout.error = getString(R.string.error_task_name_empty)
            } else {
                taskNameInputLayout.error = null
            }
        }

        addEditTaskViewModel.isDeadlineError.observe(this){isError ->
            if (isError) {
                deadlineInputLayout.error = getString(R.string.error_deadline_empty)
            } else {
                deadlineInputLayout.error = null
            }
        }
    }

    private fun setDeadline(date: Date){
        edtDeadline.setText(date.toStringDateFormat())
        selectedDeadline = date
    }

    private fun showDateTimePicker() {
        val selectedCalendar = Calendar.getInstance().apply {
            selectedDeadline?.let { time = it }
        }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                showTimePicker(selectedCalendar)
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(selectedCalendar: Calendar) {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)
                edtDeadline.setText(selectedCalendar.time.toStringDateFormat())
                selectedDeadline = selectedCalendar.time
            },
            selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}