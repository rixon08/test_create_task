# Task List App 📝

A simple Task List app built using **Kotlin, MVVM, and Clean Architecture**. This app allows users to add, edit, delete, and search tasks with deadlines. It also supports local database storage using **Room** and displays notifications for upcoming tasks.

## 📂 Project Structure
The project follows **Clean Architecture** principles:
- `data` → Handles database & repository
- `domain` → Contains use cases & business logic
- `presentation` → UI (Activity + ViewModel)
- `di` → Dependency Injection (Koin setup)
- `utils` → Utility functions (extensions and constants)

## 🚀 Features
✅ Add, Edit, Delete tasks  
✅ Search tasks by name  
✅ Mark tasks as completed  
✅ Swipe to delete with confirmation  
✅ Show notifications for upcoming deadlines  

## 🛠️ Tech Stack
- **Kotlin** (Programming Language)
- **MVVM + Clean Architecture**
- **Room Database** (Local Storage)
- **RecyclerView** (Task List)
- **LiveData + ViewModel** (State Management)
- **AlarmManager** (Notifications)
- **Koin** (Dependency Injection)

## 🔧 How to Run the App
1. Clone this repository:
   ```sh
   git clone https://github.com/your-username/task-list-app.git
2. Open the project in **Android Studio** (latest version recommended).
3. Ensure you have the required dependencies installed.
4. Run the app using an emulator or a physical device.

## Preview
[🎥 Tonton Video](https://raw.githubusercontent.com/rixon08/test_create_task/master/Screen%20Record.mp4)
