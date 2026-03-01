package com.jaydev.awsquiz;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.material.snackbar.Snackbar;

public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static final int REQUEST_CODE_UPDATE = 1001;
    
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    public UpdateManager(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
    }

    /**
     * Verifica se há atualização disponível e inicia o fluxo
     * @param updateType AppUpdateType.FLEXIBLE ou AppUpdateType.IMMEDIATE
     */
    public void checkForUpdate(int updateType) {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                
                Log.d(TAG, "Atualização disponível!");
                startUpdate(appUpdateInfo, updateType);
            } else {
                Log.d(TAG, "Nenhuma atualização disponível");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Erro ao verificar atualização", e);
        });
    }

    /**
     * Inicia o fluxo de atualização
     */
    private void startUpdate(AppUpdateInfo appUpdateInfo, int updateType) {
        try {
            if (updateType == AppUpdateType.FLEXIBLE) {
                setupFlexibleUpdateListener();
            }
            
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateType,
                activity,
                REQUEST_CODE_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Erro ao iniciar atualização", e);
        }
    }

    /**
     * Configura listener para atualização flexível
     */
    private void setupFlexibleUpdateListener() {
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompletedSnackbar();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    /**
     * Mostra snackbar quando download terminar
     */
    private void showUpdateCompletedSnackbar() {
        Snackbar snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "Nova versão baixada! Reinicie para instalar.",
            Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction("REINICIAR", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(0xFF2D6CDF); // Azul do app
        snackbar.show();
    }

    /**
     * Verifica se há atualização em andamento (para retomar após fechar o app)
     */
    public void resumeUpdateIfNeeded() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            // Se atualização foi baixada mas não instalada
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompletedSnackbar();
            }
            
            // Se atualização imediata foi iniciada mas não completada
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        REQUEST_CODE_UPDATE
                    );
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Erro ao retomar atualização", e);
                }
            }
        });
    }

    /**
     * Limpa recursos quando Activity for destruída
     */
    public void cleanup() {
        if (installStateUpdatedListener != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}
