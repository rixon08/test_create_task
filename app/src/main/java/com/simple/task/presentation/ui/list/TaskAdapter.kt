package com.simple.task.presentation.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simple.task.R
import com.simple.task.domain.model.TaskModel

class TaskAdapter(private val onTaskClick: (TaskModel) -> Unit, private val onTaskChecked: (TaskModel) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<TaskModel, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, onTaskClick, onTaskChecked)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTaskName: TextView = itemView.findViewById(R.id.txtTaskName)
        private val txtDeadline: TextView = itemView.findViewById(R.id.txtDeadline)
        private val chkCompleted: CheckBox = itemView.findViewById(R.id.chkCompleted)

        fun bind(task: TaskModel, onTaskClick: (TaskModel) -> Unit, onTaskChecked: (TaskModel) -> Unit) {
            chkCompleted.setOnCheckedChangeListener(null)

            txtTaskName.text = task.name
            txtDeadline.text = task.stringDateFormat
            chkCompleted.isChecked = task.isCompleted

            itemView.setOnClickListener { onTaskClick(task) }

            chkCompleted.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                onTaskChecked(updatedTask)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}