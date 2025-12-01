package com.example.mp_lab_9.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.PeriodicWorkRequest;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.example.mp_lab_9.util.NotificationHelper;

import java.util.concurrent.TimeUnit;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static void testNotificationImmediately(Context context) {
        // Для тестирования используем OneTimeWorkRequest
        OneTimeWorkRequest testRequest =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(0, TimeUnit.SECONDS) // Немедленный запуск
                        .build();

        WorkManager.getInstance(context).enqueue(testRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Отправляем уведомление
            NotificationHelper.showNotification(
                    getApplicationContext(),
                    "🛒 Умный список покупок",
                    "Проверьте ваши списки покупок сегодня!"
            );
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    public static void scheduleReminder(Context context) {
        // Создаем запрос на периодическую работу (минимум 15 минут)
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(
                        ReminderWorker.class,
                        15, // Интервал повторения
                        TimeUnit.MINUTES
                )
                        .setInitialDelay(15, TimeUnit.MINUTES) // Задержка перед первым запуском
                        .build();

        // Планируем работу
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "shopping_reminder",
                ExistingPeriodicWorkPolicy.KEEP, // Не заменять существующую работу
                reminderRequest
        );
    }

    public static void cancelReminder(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork("shopping_reminder");
    }
}