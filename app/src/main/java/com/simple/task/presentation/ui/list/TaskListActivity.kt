package com.simple.task.presentation.ui.list

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.simple.task.R
import com.simple.task.utils.constants.AddEditTaskKeyConstants
import com.simple.task.domain.model.TaskModel
import com.simple.task.presentation.ui.addedit.AddEditTaskActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class TaskListActivity : AppCompatActivity() {
    private val taskViewModel: TaskViewModel by viewModel()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtSearch: TextInputEditText

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null){
                if (intent.getBooleanExtra(AddEditTaskKeyConstants.TASK_COMPLETED, false)){
                    removeTaskNotification(intent.getIntExtra(AddEditTaskKeyConstants.TASK_ID, 0))
                }
            }
            taskViewModel.loadTasks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        edtSearch = findViewById(R.id.edtSearch)
        recyclerView = findViewById(R.id.rvTaskList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                taskViewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        taskAdapter = TaskAdapter(onTaskClick = {
            goToAddEditTask(it)
        }, onTaskChecked = {
            taskViewModel.editCompleted(it)
            if (it.isCompleted){
                removeTaskNotification(it.id)
            }
        })
        recyclerView.adapter = taskAdapter

        taskViewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }

        setupSwipeToDelete()

        taskViewModel.loadTasks()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    private fun goToAddEditTask(task: TaskModel? = null){
        recyclerView.requestFocus()
        val intent = AddEditTaskActivity.getIntent(this, task)
        resultLauncher.launch(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_add_task -> {
                goToAddEditTask()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = taskAdapter.currentList[position]

                AlertDialog.Builder(this@TaskListActivity)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah Anda yakin ingin menghapus task ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        taskViewModel.deleteTask(taskToDelete)
                        removeTaskNotification(taskToDelete.id)
                    }
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                        taskAdapter.notifyItemChanged(position)
                    }
                    .show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)!!
                val iconMargin = (itemView.height - icon.intrinsicHeight) / 2

                val paint = Paint().apply { color = Color.RED }
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )

                if (dX < -itemView.width / 4 && isCurrentlyActive) {
                    c.drawRect(background, paint)

                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconBottom = iconTop + icon.intrinsicHeight

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun removeTaskNotification(taskId: Int) {
        val notificationManager =  this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

        }
}